package de.ckollmeier.burgerexpress.backend.exceptionhandler;

import de.ckollmeier.burgerexpress.backend.dto.ErrorDTO;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO catchNotFoundException(final NotFoundException exception) {
        return ErrorDTO.fromException(exception).withStatus(HttpStatus.NOT_FOUND.name());
    }

    /**
     * Catches a NullPointerException and returns an ErrorDTO with the exception details.
     *
     * @param exception The exception that was thrown.
     * @return An ErrorDTO containing the exception's class name, message, and HTTP status.
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO catchNullPointerException(final NullPointerException exception) {
        return ErrorDTO.fromException(exception);
    }

    /**
     * Catches a IllegalArgumentException and returns an ErrorDTO with the exception details.
     * @param exception The exception that was thrown.
     * @return An ErrorDTO containing the exception's class name, message, and HTTP status.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO catchIllegalArgumentException(final IllegalArgumentException exception) {
        return ErrorDTO.fromException(exception);
    }

    /**
     * Behandelt IllegalStateException und gibt ein ErrorDTO zurück,
     * das Details zur Ausnahme enthält.
     *
     * @param exception Die geworfene IllegalStateException.
     * @return Ein ErrorDTO, das die Klasse, Nachricht und den Status der Ausnahme enthält.
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO catchIllegalStateException(final IllegalStateException exception) {
        return ErrorDTO.fromException(exception);
    }

    /**
     * Catches all unhandled exceptions and returns an ErrorDTO with the exception details.
     *
     * @param exception The exception that was thrown.
     * @return An ErrorDTO containing the exception's class name, message, and HTTP status.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO catchAll(final Exception exception) {
        return ErrorDTO.fromException(exception)
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
    }
}
