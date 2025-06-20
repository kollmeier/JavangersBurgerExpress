package de.ckollmeier.burgerexpress.backend.exceptions;

public class CreatePaypalOrderException extends RuntimeException {
    public CreatePaypalOrderException(String message, Exception e) {
        super(message, e);
    }
}
