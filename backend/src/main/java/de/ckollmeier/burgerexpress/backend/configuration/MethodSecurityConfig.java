package de.ckollmeier.burgerexpress.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {

    @Bean
    public AuthorizationManager<Object> authorizationManager() {
        return (authentication, object) -> {
            try {
                // Check if authentication is not null and authenticated
                boolean isAuthorized = authentication != null && 
                                      authentication.get() != null && 
                                      authentication.get().isAuthenticated();
                return new AuthorizationDecision(isAuthorized);
            } catch (AccessDeniedException e) {
                // Convert AccessDeniedException to a 403 response
                throw new CustomAccessDeniedException("Access Denied", e);
            }
        };
    }

    // Custom exception that will be handled by our exception handler
    public static class CustomAccessDeniedException extends AccessDeniedException {
        public CustomAccessDeniedException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }
}
