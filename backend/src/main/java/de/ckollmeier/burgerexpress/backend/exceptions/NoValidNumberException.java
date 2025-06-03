package de.ckollmeier.burgerexpress.backend.exceptions;

import lombok.Getter;

@Getter
public class NoValidNumberException extends IllegalArgumentException implements WithPathInterface {
    private final String path;

    public NoValidNumberException(String message, String path) {
        super(message);
        this.path = path;
    }
}
