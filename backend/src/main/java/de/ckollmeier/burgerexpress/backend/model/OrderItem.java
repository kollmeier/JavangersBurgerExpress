package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class OrderItem implements Serializable {
    @EqualsAndHashCode.Exclude
    private final String id;
    @NonNull
    private final OrderableItem item;
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private final int amount = 1;
    @EqualsAndHashCode.Exclude
    private transient BigDecimal price;

    public OrderItem(String id, OrderableItem item, int amount) {
        this.id = id;
        this.item = item;
        this.amount = amount;
        this.price = getSubTotal();
    }

    public OrderItem(OrderableItem item, int amount) {
        this.id = UUID.randomUUID().toString();
        this.item = item;
        this.amount = amount;
        this.price = getSubTotal();
    }

    public BigDecimal getSubTotal() {
        return item.getPrice().multiply(BigDecimal.valueOf(amount));
    }

    public BigDecimal getPrice() {
        if (price != null) {
            return price;
        }
        return getSubTotal();
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
