package de.ckollmeier.burgerexpress.backend.exceptionhandler;

import de.ckollmeier.burgerexpress.backend.dto.ErrorDTO;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("catchNotFoundException should return ErrorDTO with NOT_FOUND status")
    void should_returnErrorDTOWithNotFoundStatus_when_catchNotFoundException() {
        // Given
        String errorMessage = "Test not found";
        String path = "test/path";
        NotFoundException exception = new NotFoundException(errorMessage, path);

        // When
        ErrorDTO errorDTO = exceptionHandler.catchNotFoundException(exception);

        // Then
        assertNotNull(errorDTO);
        assertEquals("NotFoundException", errorDTO.error());
        assertEquals(errorMessage, errorDTO.message());
        assertEquals(HttpStatus.NOT_FOUND.name(), errorDTO.status());
    }

    @Test
    @DisplayName("catchNullPointerException should return ErrorDTO with BAD_REQUEST status")
    void should_returnErrorDTOWithBadRequestStatus_when_catchNullPointerException() {
        // Given
        String errorMessage = "Test null pointer";
        NullPointerException exception = new NullPointerException(errorMessage);

        // When
        ErrorDTO errorDTO = exceptionHandler.catchNullPointerException(exception);

        // Then
        assertNotNull(errorDTO);
        assertEquals("NullPointerException", errorDTO.error());
        assertEquals(errorMessage, errorDTO.message());
        assertEquals(HttpStatus.BAD_REQUEST.name(), errorDTO.status());
    }

    @Test
    @DisplayName("catchIllegalArgumentException should return ErrorDTO with BAD_REQUEST status")
    void should_returnErrorDTOWithBadRequestStatus_when_catchIllegalArgumentException() {
        // Given
        String errorMessage = "Test illegal argument";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // When
        ErrorDTO errorDTO = exceptionHandler.catchIllegalArgumentException(exception);

        // Then
        assertNotNull(errorDTO);
        assertEquals("IllegalArgumentException", errorDTO.error());
        assertEquals(errorMessage, errorDTO.message());
        assertEquals(HttpStatus.BAD_REQUEST.name(), errorDTO.status());
    }

    @Test
    @DisplayName("catchAll should return ErrorDTO with INTERNAL_SERVER_ERROR status")
    void should_returnErrorDTOWithInternalServerErrorStatus_when_catchAll() {
        // Given
        String errorMessage = "Test generic exception";
        Exception exception = new Exception(errorMessage);

        // When
        ErrorDTO errorDTO = exceptionHandler.catchAll(exception);

        // Then
        assertNotNull(errorDTO);
        assertEquals("Exception", errorDTO.error());
        assertEquals(errorMessage, errorDTO.message());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.name(), errorDTO.status());
    }

    @Test
    @DisplayName("catchAll should handle exceptions with null message")
    void should_handleNullMessage_when_catchAll() {
        // Given
        Exception exception = new Exception();

        // When
        ErrorDTO errorDTO = exceptionHandler.catchAll(exception);

        // Then
        assertNotNull(errorDTO);
        assertEquals("Exception", errorDTO.error());
        assertEquals("Exception", errorDTO.message()); // Default to class name when message is null
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.name(), errorDTO.status());
    }

    @Test
    @DisplayName("catchAll should handle exceptions with cause")
    void should_includeCauseInfo_when_exceptionHasCause() {
        // Given
        IllegalArgumentException cause = new IllegalArgumentException("Cause message");
        Exception exception = new Exception("Wrapper message", cause);

        // When
        ErrorDTO errorDTO = exceptionHandler.catchAll(exception);

        // Then
        assertNotNull(errorDTO);
        assertEquals("Exception", errorDTO.error());
        assertEquals("Wrapper message", errorDTO.message());
        assertEquals("IllegalArgumentException", errorDTO.cause());
        assertEquals("Cause message", errorDTO.causeMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.name(), errorDTO.status());
    }
}
