package de.ckollmeier.burgerexpress.backend.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MethodSecurityConfig")
class MethodSecurityConfigTest {

    private final MethodSecurityConfig methodSecurityConfig = new MethodSecurityConfig();

    @Test
    @DisplayName("authorizationManager should return true for authenticated user")
    void should_returnTrue_when_userIsAuthenticated() {
        // Given
        AuthorizationManager<Object> authorizationManager = methodSecurityConfig.authorizationManager();
        Authentication authentication = new TestingAuthenticationToken("user", "password", "ROLE_USER");
        authentication.setAuthenticated(true);
        Supplier<Authentication> authenticationSupplier = () -> authentication;
        Object object = new Object();

        // When
        AuthorizationResult authorizationResult = authorizationManager.authorize(authenticationSupplier, object);

        // Then
        assertNotNull(authorizationResult);
        assertTrue(authorizationResult.isGranted());
    }

    @Test
    @DisplayName("authorizationManager should return false for unauthenticated user")
    void should_returnFalse_when_userIsNotAuthenticated() {
        // Given
        AuthorizationManager<Object> authorizationManager = methodSecurityConfig.authorizationManager();
        Authentication authentication = new TestingAuthenticationToken("user", "password", "ROLE_USER");
        authentication.setAuthenticated(false);
        Supplier<Authentication> authenticationSupplier = () -> authentication;
        Object object = new Object();

        // When
        AuthorizationResult authorizationResult = authorizationManager.authorize(authenticationSupplier, object);

        // Then
        assertNotNull(authorizationResult);
        assertFalse(authorizationResult.isGranted());
    }

    @Test
    @DisplayName("authorizationManager should return false for null authentication")
    void should_returnFalse_when_authenticationIsNull() {
        // Given
        AuthorizationManager<Object> authorizationManager = methodSecurityConfig.authorizationManager();
        Supplier<Authentication> authenticationSupplier = () -> null;
        Object object = new Object();

        // When
        AuthorizationResult authorizationResult = authorizationManager.authorize(authenticationSupplier, object);

        // Then
        assertNotNull(authorizationResult);
        assertFalse(authorizationResult.isGranted());
    }

    @Test
    @DisplayName("CustomAccessDeniedException can be thrown with a cause")
    void should_beAbleToThrow_customAccessDeniedException() {
        // Given
        String message = "Access Denied";
        AccessDeniedException cause = new AccessDeniedException("Test access denied");

        // When
        MethodSecurityConfig.CustomAccessDeniedException exception = 
            new MethodSecurityConfig.CustomAccessDeniedException(message, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Test access denied", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("CustomAccessDeniedException should extend AccessDeniedException")
    void should_extendAccessDeniedException_when_customAccessDeniedExceptionIsCreated() {
        // Given
        String message = "Test message";
        Throwable cause = new RuntimeException("Test cause");

        // When
        MethodSecurityConfig.CustomAccessDeniedException exception = 
            new MethodSecurityConfig.CustomAccessDeniedException(message, cause);

        // Then
        assertInstanceOf(AccessDeniedException.class, exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
