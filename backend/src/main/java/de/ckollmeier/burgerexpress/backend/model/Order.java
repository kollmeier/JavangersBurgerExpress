package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@With
@Document(collection = "burger-express-order")
@TypeAlias("burger-express-order")
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Order implements Serializable {
    @Id
    private final String id;
    @Builder.Default
    private final List<OrderItem> items = new ArrayList<>();
    @Builder.Default
    private final Instant createdAt = Instant.now();
    private final Instant updatedAt;
    @Builder.Default
    private final OrderStatus status = OrderStatus.PENDING;
    private final Integer orderNumber;
    @Builder.Default
    private final BigDecimal totalPrice = BigDecimal.ZERO;
    private final String paypalOrderId;
    private final String stripePaymentOrderId;
    @Setter
    private String stripePaymentOrderIdHash;


    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
