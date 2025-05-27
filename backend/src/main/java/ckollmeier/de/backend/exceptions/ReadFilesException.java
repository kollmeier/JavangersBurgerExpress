package ckollmeier.de.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ReadFilesException extends RuntimeException {
    public ReadFilesException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
