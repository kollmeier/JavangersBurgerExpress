package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.OrderItemInputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.OrderItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
                .id(orderItem.id() == null ? UUID.randomUUID().toString() : orderItem.id())
                .item(orderableItemResolver.apply(orderItem.item()))
                .amount(orderItem.amount())
                .build();
    }

    public static List<OrderItem> convert(final List<OrderItemInputDTO> orderItems, final Function<String, OrderableItem> orderableItemResolver) {
        return orderItems.stream()
                .map(orderItem -> convert(orderItem, orderableItemResolver))
                .reduce(new ArrayList<>(), (acc, item) -> {
                   if (acc.contains(item)) {
                       int index = acc.indexOf(item);
                       OrderItem existingItem = acc.remove(index);
                       acc.add(index,existingItem.withAmount(existingItem.getAmount() + item.getAmount()));
                       return acc;
                   }
                   acc.add(item);
                   return acc;
                }, (a, b) -> {
                    a.addAll(b);
                    return a;
                });

    }
}
