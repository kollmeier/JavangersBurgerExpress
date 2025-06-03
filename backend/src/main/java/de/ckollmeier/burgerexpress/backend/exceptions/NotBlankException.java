package de.ckollmeier.burgerexpress.backend.exceptions;

import lombok.Getter;

@Getter
public class NotBlankException extends IllegalArgumentException implements WithPathInterface {
    private final String path;

    public NotBlankException(String message, String path) {
        super(message);
        this.path = path;
    }
}
