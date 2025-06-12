package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.OrderInputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.Order;
import lombok.NonNull;

import java.util.function.Function;

public class OrderConverter {
    // Utility class
    private OrderConverter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Order convert(final @NonNull OrderInputDTO order, Function<String, OrderableItem> itemResolver) {
        return Order.builder()
                .id(order.id())
                .items(OrderItemConverter.convert(order.items(), itemResolver))
                .build();
    }

    public static Order convert(final @NonNull OrderInputDTO order, final Order orderToUpdate, Function<String, OrderableItem> itemResolver) {
        return orderToUpdate
                .withItems(order.items() == null ? orderToUpdate.getItems() : OrderItemConverter.convert(order.items(), itemResolver));
    }
}
