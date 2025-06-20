package de.ckollmeier.burgerexpress.backend.listener;

import de.ckollmeier.burgerexpress.backend.model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderListenerTest {

    @InjectMocks
    private OrderListener orderListener;

    @Test
    @DisplayName("Should generate hash for Stripe payment order ID")
    void shouldGenerateHashForStripePaymentOrderId() {
        // Given
        String stripePaymentOrderId = "https://stripe.com/checkout/session-123";
        Order order = Order.builder()
                .id("order-123")
                .stripePaymentOrderId(stripePaymentOrderId)
                .build();

        // When
        Order result = orderListener.onBeforeConvert(order, "collection");

        // Then
        assertThat(result.getStripePaymentOrderIdHash()).isNotNull();
        assertThat(result.getStripePaymentOrderIdHash()).isNotEmpty();
    }

    @Test
    @DisplayName("Should not generate hash when Stripe payment order ID is null")
    void shouldNotGenerateHashWhenStripePaymentOrderIdIsNull() {
        // Given
        Order order = Order.builder()
                .id("order-123")
                .build();

        // When
        Order result = orderListener.onBeforeConvert(order, "collection");

        // Then
        assertThat(result.getStripePaymentOrderIdHash()).isNull();
    }

    @Test
    @DisplayName("Should not overwrite existing hash")
    void shouldNotOverwriteExistingHash() {
        // Given
        String stripePaymentOrderId = "https://stripe.com/checkout/session-123";
        String existingHash = "existing-hash";
        Order order = Order.builder()
                .id("order-123")
                .stripePaymentOrderId(stripePaymentOrderId)
                .build();
        order.setStripePaymentOrderIdHash(existingHash);

        // When
        Order result = orderListener.onBeforeConvert(order, "collection");

        // Then
        assertThat(result.getStripePaymentOrderIdHash()).isEqualTo(existingHash);
    }

    @Test
    @DisplayName("Should generate consistent hash for same input")
    void shouldGenerateConsistentHashForSameInput() {
        // Given
        String stripePaymentOrderId = "https://stripe.com/checkout/session-123";
        Order order1 = Order.builder()
                .id("order-123")
                .stripePaymentOrderId(stripePaymentOrderId)
                .build();
        Order order2 = Order.builder()
                .id("order-456")
                .stripePaymentOrderId(stripePaymentOrderId)
                .build();

        // When
        Order result1 = orderListener.onBeforeConvert(order1, "collection");
        Order result2 = orderListener.onBeforeConvert(order2, "collection");

        // Then
        assertThat(result1.getStripePaymentOrderIdHash()).isEqualTo(result2.getStripePaymentOrderIdHash());
    }
}