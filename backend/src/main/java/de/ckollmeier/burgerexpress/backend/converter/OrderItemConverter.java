package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.OrderItemInputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.OrderItem;

import java.util.List;
import java.util.function.Function;

public final class OrderItemConverter {
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class should not be instantiated.
     */
    private OrderItemConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static OrderItem convert(final OrderItemInputDTO orderItem, final Function<String, OrderableItem> orderableItemResolver) {
        return OrderItem.builder()
                .item(orderableItemResolver.apply(orderItem.item()))
                .amount(orderItem.amount())
                .build();
    }

    public static List<OrderItem> convert(final List<OrderItemInputDTO> orderItems, final Function<String, OrderableItem> orderableItemResolver) {
        return orderItems.stream()
                .map(orderItem -> convert(orderItem, orderableItemResolver))
                .toList();
    }

}
