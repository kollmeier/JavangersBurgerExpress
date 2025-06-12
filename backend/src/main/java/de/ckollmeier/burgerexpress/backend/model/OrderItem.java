package de.ckollmeier.burgerexpress.backend.model;

import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;

import java.math.BigDecimal;

@Builder
@With
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class OrderItem {
    private OrderableItem item;
    private int amount;
    private BigDecimal price;
}
