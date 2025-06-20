package de.ckollmeier.burgerexpress.backend.exceptions;

public class PayPalQrCodeGenerationException extends RuntimeException {
    public PayPalQrCodeGenerationException(Exception e) {
        super(e);
    }
}
