package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class OrderItem implements Serializable {
    @NonNull
    private final OrderableItem item;
    @Builder.Default
    private final int amount = 1;
    private final BigDecimal price;

    public OrderItem(OrderableItem item, int amount) {
        this.item = item;
        this.amount = amount;
        this.price = getSubTotal();
    }

    public BigDecimal getSubTotal() {
        return item.getPrice().multiply(BigDecimal.valueOf(amount));
    }

    public OrderItem withAmount(int amount) {
        return OrderItem.builder()
                .item(this.item)
                .amount(amount)
                .build();
    }

    public OrderItem withItem(@NonNull OrderableItem item) {
        return OrderItem.builder()
                .item(item)
                .amount(this.amount)
                .build();
    }
}
