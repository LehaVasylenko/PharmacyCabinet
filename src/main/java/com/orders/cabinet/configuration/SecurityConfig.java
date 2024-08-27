package com.orders.cabinet.configuration;

import com.orders.cabinet.model.Role;
import com.orders.cabinet.repository.ShopRepository;
import com.orders.cabinet.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

import static org.springframework.security.config.Customizer.withDefaults;
/**
 * Security configuration class for setting up HTTP security and access controls.
 * <p>
 * This configuration class is responsible for defining security rules and policies for the application.
 * It uses Spring Security to configure HTTP security, including access control, session management, and authentication.
 * </p>
 * <p>
 * The security rules defined here include:
 * <ul>
 *     <li>Disabling CSRF (Cross-Site Request Forgery) protection.</li>
 *     <li>Restricting access to certain API endpoints based on user roles:
 *         <ul>
 *             <li>Endpoints under "/api/admin/**" are accessible only to users with the ADMIN role.</li>
 *             <li>Endpoints under "/api/shops/**" are accessible only to users with the SHOP role.</li>
 *             <li>Endpoints under "/actuator/**" are accessible only to users with the ADMIN role.</li>
 *         </ul>
 *     </li>
 *     <li>Permitting access to "/v3/**" and "/swagger-ui/**" endpoints without authentication.</li>
 *     <li>Permitting access to "/user/login/**" endpoints without authentication.</li>
 *     <li>Requiring authentication for all other requests.</li>
 * </ul>
 * </p>
 * <p>
 * Session management is configured to always create a new session, and HTTP basic authentication is enabled.
 * The user details service used for authentication is provided by the {@link UserService} bean.
 * </p>
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {
    /**
     * Service used for loading user-specific data during authentication.
     */
    UserService userService;
    PasswordEncoder encoder;
    PopOrderProperties prop;
    ShopRepository repo;

    /**
     * Configures HTTP security and access controls.
     * <p>
     * This method sets up security rules for HTTP requests, including access control based on user roles,
     * session management policies, and authentication mechanisms.
     * </p>
     *
     * @param http the {@link HttpSecurity} object used to configure security settings.
     * @return the {@link SecurityFilterChain} bean used to apply the security configuration.
     * @throws Exception if an error occurs while configuring security.
     */
    @Bean
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        log.info("Configuring SecurityFilterChain...");
        DeniedAccessFilter deniedAccessFilter = new DeniedAccessFilter(prop, userService, encoder, repo);
        log.info("DeniedAccessFilter created: {}", deniedAccessFilter);

        http.addFilterAfter(deniedAccessFilter, SwitchUserFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/admin/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/api/shops/**").hasAuthority(Role.SHOP.name())
                        .requestMatchers("/actuator/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/v3/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/swagger-ui/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/swagger**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/user/login/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .httpBasic(withDefaults());
        return http.build();
    }


}

