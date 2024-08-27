package com.orders.cabinet.configuration;

import com.orders.cabinet.event.Timed;
import com.orders.cabinet.model.db.Shops;
import com.orders.cabinet.repository.ShopRepository;
import com.orders.cabinet.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeniedAccessFilter extends OncePerRequestFilter {
    final PopOrderProperties prop;
    final UserService service;
    final PasswordEncoder encoder;
    final ShopRepository repository;

    UserDetails userDetails;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        log.info("Request path: {}", path);

        // Efficient path checks
        if (isSwaggerOrLoginPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!isValidAuthHeader(authHeader)) {
            log.warn("Invalid auth header");
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        String[] credentials = getCredentials(authHeader);
        String username = credentials[0];
        String password = credentials[1];
        userDetails = service.loadUserByUsername(username);

        if (!isValidUser(username, password)) {
            log.warn("Invalid credentials for user {}", username);
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        if (isShopUser(username) && !checkAccess(username, response)) {
            return;
        }

        if (!isUserAgentValid(request, response)) {
            return;
        }

        log.info("User {} verified", username);
        filterChain.doFilter(request, response);
    }

    private boolean isSwaggerOrLoginPath(String path) {
        return path.startsWith("/v3") || path.startsWith("/swagger") || path.startsWith("/user/login");
    }

    private boolean isValidAuthHeader(String authHeader) {
        return authHeader != null && authHeader.startsWith("Basic ");
    }

    private String[] getCredentials(String authHeader) {
        try {
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            return decodedString.split(":", 2);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            log.warn("Failed to decode credentials", e);
            return new String[]{"", ""};
        }
    }

    private boolean isValidUser(String username, String password) {
        try {
            return encoder.matches(password, userDetails.getPassword());
        } catch (Exception e) {
            log.warn("User validation failed for user {}", username, e);
            return false;
        }
    }

    private boolean isShopUser(String username) {
        // Cache user details if possible to avoid repeated database access
        return userDetails.getAuthorities()
                .stream()
                .anyMatch(authority -> "SHOP".equals(authority.getAuthority()));
    }

    @Cacheable("shopCache")
    private boolean checkAccess(String addressId, HttpServletResponse response) throws IOException {
        Optional<Shops> shopOpt = repository.getShopByShopId(addressId);
        if (shopOpt.isEmpty() || !shopOpt.get().isLogged()) {
            log.warn("Shop {} access denied", addressId);
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED.value());
            return false;
        }
        return true;
    }

    private boolean isUserAgentValid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        if (userAgent == null) {
            log.warn("No User-Agent header found");
            response.sendError(HttpStatus.I_AM_A_TEAPOT.value());
            return false;
        }
        if (userAgent.startsWith("curl")) {
            log.warn("Blocked User-Agent: curl");
            response.sendError(HttpStatus.NOT_ACCEPTABLE.value());
            return false;
        }
        if (!userAgent.equals(prop.getAgent())) {
            log.warn("User-Agent does not match expected value. Found: {}", userAgent);
            response.sendError(HttpStatus.FAILED_DEPENDENCY.value());
            return false;
        }
        log.info("User-Agent is valid: {}", userAgent);
        return true;
    }
}
