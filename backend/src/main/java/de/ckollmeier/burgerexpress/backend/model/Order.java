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
@Document(collection = "burger-express-order")
@TypeAlias("burger-express-order")
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Order implements Serializable {
    @Id
    private final String id;
    @Builder.Default
    private final List<OrderItem> items = new ArrayList<>();
    @Builder.Default
    private final Instant createdAt = Instant.now();
    private final Instant updatedAt;
    @Builder.Default
    private final BigDecimal totalPrice = BigDecimal.ZERO;
    @Builder.Default
    private final OrderStatus status = OrderStatus.PENDING;

    public Order(
            String id,
            @NonNull
            List<OrderItem> items
    ) {
        this.id = id;
        this.items = new ArrayList<>(items);
        this.totalPrice = items.stream()
                .map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.createdAt = Instant.now();
        this.updatedAt = null;
        this.status = OrderStatus.PENDING;
    }

    public Order withItems(List<OrderItem> items) {
        return Order.builder()
                .id(this.id)
                .items(items)
                .createdAt(this.createdAt)
                .updatedAt(Instant.now())
                .totalPrice(items.stream()
                        .map(OrderItem::getSubTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .status(this.status)
                .build();
    }

    public Order withStatus(@NonNull OrderStatus status) {
        return Order.builder()
                .id(this.id)
                .items(items)
                .createdAt(this.createdAt)
                .updatedAt(Instant.now())
                .totalPrice(this.totalPrice)
                .status(status)
                .build();
    }
}
