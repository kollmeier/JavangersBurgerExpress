package de.ckollmeier.burgerexpress.backend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ckollmeier.burgerexpress.backend.dto.LoginResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_KITCHEN = "KITCHEN";
    public static final String ROLE_CASHIER = "CASHIER";
    public static final String CONTENT_TYPE_JSON = "application/json";

    private final ObjectMapper objectMapper;

    @Value("${burgerexpress.password.manager}")
    private String managerPassword;

    @Value("${burgerexpress.password.kitchen}")
    private String kitchenPassword;

    @Value("${burgerexpress.password.cashier}")
    private String cashierPassword;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails managerUser = User.builder()
                .username("manager")
                .password(passwordEncoder().encode(managerPassword))
                .roles(ROLE_MANAGER)
                .build();

        UserDetails kitchenUser = User.builder()
                .username("kitchen")
                .password(passwordEncoder().encode(kitchenPassword))
                .roles(ROLE_KITCHEN)
                .build();

        UserDetails cashierUser = User.builder()
                .username("cashier")
                .password(passwordEncoder().encode(cashierPassword))
                .roles(ROLE_CASHIER)
                .build();

        return new InMemoryUserDetailsManager(managerUser, kitchenUser, cashierUser);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Protected API endpoints that require authentication
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/displayCategories/**").authenticated()
                        .requestMatchers("/api/dishes/**").authenticated()
                        .requestMatchers("/api/menus/**").authenticated()
                        .requestMatchers("/api/files/upload").authenticated()
                        .requestMatchers("/api/orders/kitchen").authenticated()
                        // Any other request is permitted for SPA application
                        .anyRequest().permitAll()
                )
                .httpBasic(httpBasic -> httpBasic.realmName("Burger Express"))
                .formLogin(formLogin -> formLogin
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(200);
                            response.setContentType(CONTENT_TYPE_JSON);
                            response.getWriter().write(
                                    objectMapper.writeValueAsString(
                                            new LoginResponseDTO(true, null)
                                    ));
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(401);
                            response.setContentType(CONTENT_TYPE_JSON);
                            response.getWriter().write(
                                    objectMapper.writeValueAsString(
                                            new LoginResponseDTO(false, exception.getMessage())
                                    )
                            );
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                            response.setContentType(CONTENT_TYPE_JSON);
                            response.getWriter().write("{\"message\":\"Logout successful\"}");
                        })
                        .permitAll()
                )
                .exceptionHandling(exceptionHandling -> 
                        exceptionHandling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType(CONTENT_TYPE_JSON);
                            response.getWriter().write("{\"error\":\"Access Denied\",\"status\":\"FORBIDDEN\"}");
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            // For API requests, return JSON response instead of redirecting to login page
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(401);
                                response.setContentType(CONTENT_TYPE_JSON);
                                response.getWriter().write("{\"error\":\"Unauthorized\",\"status\":\"UNAUTHORIZED\"}");
                            } else {
                                // For non-API requests, redirect to login page (default behavior)
                                response.sendRedirect("/index.html");
                            }
                        })
                );

        return http.build();
    }
}
