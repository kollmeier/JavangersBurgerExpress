package de.ckollmeier.burgerexpress.backend.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
@RequiredArgsConstructor
public class NotFoundException extends IllegalArgumentException implements WithPathInterface {
    private final String path;

    public NotFoundException(final String message, final String path) {
        super(message);
        this.path = path;
    }
}

