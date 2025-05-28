package ckollmeier.de.backend.exceptionhandler;

import ckollmeier.de.backend.dto.ErrorDTO;
import ckollmeier.de.backend.exceptions.NotFoundException;
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
        return new ErrorDTO(
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                HttpStatus.NOT_FOUND.name()
        );
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
        return new ErrorDTO(
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    /**
     * Catches a IllegalArgumentException and returns an ErrorDTO with the exception details.
     * @param exception The exception that was thrown.
     * @return An ErrorDTO containing the exception's class name, message, and HTTP status.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO catchIllegalArgumentException(final IllegalArgumentException exception) {
        return new ErrorDTO(
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
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
        return new ErrorDTO(
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.name()
        );
    }
}
