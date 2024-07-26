package com.orders.cabinet.configuration;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeniedAccessFilter extends OncePerRequestFilter {
    final PopOrderProperties prop;
    final UserService service;
    final PasswordEncoder encoder;
    final ShopRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        log.info("getServletPath(): {}", path);

        // Path to swagger. Shouldn't have any headers
        if (path.contains("/v3/") || path.contains("/swagger-ui/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Path to login. Should have only user-agent
        if (path.contains("/user/login")) {
            log.info("Login path triggered");
            // Not valid user agent - bye-bye
            if (!isUserAgentValid(request, response)) return;
            // Valid user agent - welcome
            filterChain.doFilter(request, response);
            return;
        }

        // All other cases with auth header and user agent
        String authHeaderRaw = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaderRaw == null || !authHeaderRaw.startsWith("Basic ")) {
            log.warn("Bad auth header");
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        String[] credentials = getCredentials(authHeaderRaw);
        String username = credentials[0];
        String password = credentials[1];

        UserDetails userDetails = service.loadUserByUsername(username);
        if (!encoder.matches(password, userDetails.getPassword())) {
            log.warn("{} tried to enter with an invalid password", username);
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        List<String> authority = userDetails.getAuthorities()
                                            .stream()
                                            .map(GrantedAuthority::getAuthority)
                                            .toList();

        if (authority.get(0).equals("SHOP"))
            if (!checkAccess(username, response)) return;

        if (!isUserAgentValid(request, response)) return;

        log.info("{} verified", username);
        filterChain.doFilter(request, response);
    }

    private boolean checkAccess(String addressId, HttpServletResponse response) throws IOException {
        Optional<Shops> shopByShopId = repository.getShopByShopId(addressId);
        if (!shopByShopId.get().isLogged()) {
            log.warn("{} try without logIn", addressId);
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED.value());
            return false;
        }
        return true;
    }

    private String[] getCredentials(String authHeaderRaw) {
        String base64Credentials = authHeaderRaw.substring("Basic ".length()).trim();

        // Decode the Base64 encoded credentials
        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        // Split the decoded string into username and password
        return decodedString.split(":", 2);
    }

    private boolean isUserAgentValid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String header = request.getHeader(HttpHeaders.USER_AGENT);
        if (header == null) {
            log.warn("No User-Agent header found");
            response.sendError(HttpStatus.I_AM_A_TEAPOT.value());
            return false;
        }
        if (header.startsWith("curl")) {
            log.warn("Blocked User-Agent: curl");
            response.sendError(HttpStatus.NOT_ACCEPTABLE.value());
            return false;
        }
        if (!header.equals(prop.getAgent())) {
            log.warn("User-Agent does not match the expected value. Found: " + header);
            response.sendError(HttpStatus.FAILED_DEPENDENCY.value());
            return false;
        }
        log.info("User-Agent is valid: " + header);
        return true;
    }
}
