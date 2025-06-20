package de.ckollmeier.burgerexpress.backend.exceptions;

import com.stripe.exception.StripeException;

public class CreateStripeSessionException extends RuntimeException {
    public CreateStripeSessionException(String errorCreatingStripeCheckoutSession, StripeException e) {
        super(errorCreatingStripeCheckoutSession, e);
    }
}
