package de.ckollmeier.burgerexpress.backend.exceptions;

public class StripeQrCodeGenerationException extends RuntimeException {
    public StripeQrCodeGenerationException(String qrCodeGenerationFailed, Exception e) {
        super(qrCodeGenerationFailed, e);
    }
}
