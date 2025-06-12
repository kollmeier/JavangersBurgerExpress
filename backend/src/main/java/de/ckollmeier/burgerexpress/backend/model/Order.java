package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Builder
@With
@Document(collection = "burger-express-order")
@TypeAlias("burger-express-order")
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class Order {
    @Id
    private final String id;
    private final String sessionId;
    private final List<OrderItem> items;
    private final BigDecimal totalPrice;
    @Builder.Default
    private final OrderStatus status = OrderStatus.PENDING;
}
