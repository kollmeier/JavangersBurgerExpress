package de.ckollmeier.burgerexpress.backend.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NotFoundException")
class NotFoundExceptionTest {

    @Test
    @DisplayName("Constructor with message and path should set both properties")
    void should_setMessageAndPath_when_constructedWithMessageAndPath() {
        // Given
        String message = "Test message";
        String path = "test/path";

        // When
        NotFoundException exception = new NotFoundException(message, path);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(path, exception.getPath());
    }

    @Test
    @DisplayName("Constructor with only path should set path and have null message")
    void should_setPathAndHaveNullMessage_when_constructedWithOnlyPath() {
        // Given
        String path = "test/path";

        // When
        NotFoundException exception = new NotFoundException(path);

        // Then
        assertNull(exception.getMessage());
        assertEquals(path, exception.getPath());
    }

    @Test
    @DisplayName("NotFoundException should implement WithPathInterface")
    void should_implementWithPathInterface() {
        // Given
        NotFoundException exception = new NotFoundException("test/path");

        // Then
        assertInstanceOf(WithPathInterface.class, exception);
        assertEquals("test/path", (exception).getPath());
    }

    @Test
    @DisplayName("NotFoundException should extend IllegalArgumentException")
    void should_extendIllegalArgumentException() {
        // Given
        NotFoundException exception = new NotFoundException("test/path");

        // Then
        assertInstanceOf(IllegalArgumentException.class, exception);
    }
}