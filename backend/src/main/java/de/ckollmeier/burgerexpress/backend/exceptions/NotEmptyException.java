package de.ckollmeier.burgerexpress.backend.exceptions;

import lombok.Getter;

@Getter
public class NotEmptyException extends IllegalArgumentException implements WithPathInterface {
    private final String path;

    public NotEmptyException(String message, String path) {
        super(message);
        this.path = path;
    }
}
