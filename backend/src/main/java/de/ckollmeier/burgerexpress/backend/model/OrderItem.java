package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class OrderItem implements Serializable {
    @EqualsAndHashCode.Exclude
    @With
    private final String id;
    private final OrderableItem item;
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private final int amount = 1;

    public BigDecimal getSubTotal() {
        return item.getPrice().multiply(BigDecimal.valueOf(amount));
    }

    public BigDecimal getPrice() {
        return getSubTotal();
    }

    public OrderItem withAmount(int amount) {
        return OrderItem.builder()
                .id(this.id)
                .item(this.item)
                .amount(amount)
                .build();
    }
}
