package de.ckollmeier.burgerexpress.backend.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RestExceptionHandler")
class RestExceptionHandlerTest {

    private final RestExceptionHandler exceptionHandler = new RestExceptionHandler();

    @Test
    @DisplayName("handleAccessDeniedException should return 403 with error details for AccessDeniedException")
    void should_return403WithErrorDetails_when_handlingAccessDeniedException() {
        // Given
        AccessDeniedException exception = new AccessDeniedException("Access Denied Test");

        // When
        ResponseEntity<Object> response = exceptionHandler.handleAccessDeniedException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("AccessDeniedException", body.get("error"));
        assertEquals("Access Denied", body.get("message"));
        assertEquals("FORBIDDEN", body.get("status"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("handleAccessDeniedException should return 403 with error details for AuthorizationDeniedException")
    void should_return403WithErrorDetails_when_handlingAuthorizationDeniedException() {
        // Given
        AuthorizationDeniedException exception = new AuthorizationDeniedException("Authorization Denied Test");

        // When
        ResponseEntity<Object> response = exceptionHandler.handleAccessDeniedException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("AuthorizationDeniedException", body.get("error"));
        assertEquals("Access Denied", body.get("message"));
        assertEquals("FORBIDDEN", body.get("status"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("handleAccessDeniedException should return 403 with error details for MethodSecurityConfig.CustomAccessDeniedException")
    void should_return403WithErrorDetails_when_handlingCustomAccessDeniedException() {
        // Given
        MethodSecurityConfig.CustomAccessDeniedException exception = 
            new MethodSecurityConfig.CustomAccessDeniedException("Custom Access Denied", new RuntimeException());

        // When
        ResponseEntity<Object> response = exceptionHandler.handleAccessDeniedException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("CustomAccessDeniedException", body.get("error"));
        assertEquals("Access Denied", body.get("message"));
        assertEquals("FORBIDDEN", body.get("status"));
        assertNotNull(body.get("timestamp"));
    }
}