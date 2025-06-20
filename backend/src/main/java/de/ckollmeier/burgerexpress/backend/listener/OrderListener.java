package de.ckollmeier.burgerexpress.backend.listener;

import de.ckollmeier.burgerexpress.backend.model.Order;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class OrderListener implements BeforeConvertCallback<Order> {

    @Override
    @NonNull
    public Order onBeforeConvert(@NonNull Order order, @NonNull String collection) {
        String paymentOrderId = order.getStripePaymentOrderId();
        if (paymentOrderId != null && order.getStripePaymentOrderIdHash() == null) {
            order.setStripePaymentOrderIdHash(sha256Base64(paymentOrderId));
        }
        return order;
    }

    private String sha256Base64(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash).replaceAll("[=/]", "");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 Algorithmus nicht verfügbar", e);
        }
    }
}