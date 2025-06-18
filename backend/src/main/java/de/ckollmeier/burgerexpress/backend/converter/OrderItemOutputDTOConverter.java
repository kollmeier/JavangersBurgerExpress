package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.OrderItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.OrderItem;

import java.util.List;

/**
 * Utility class for converting OrderItem objects to OrderItemDTO objects.
 */
public class OrderItemOutputDTOConverter {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class should not be instantiated.
     */
    private OrderItemOutputDTOConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts a single OrderItem to an OrderItemDTO.
     *
     * @param orderItem the OrderItem to convert
     * @return the converted OrderItemDTO
     */
    public static OrderItemOutputDTO convert(final OrderItem orderItem) {
        return new OrderItemOutputDTO(
                orderItem.getId(),
                OrderableItemOutputDTOConverter.convert(orderItem.getItem()),
                orderItem.getAmount(),
                orderItem.getPrice().toPlainString().replace(".", ",")
        );
    }

    /**
     * Converts a list of OrderItems to a list of OrderItemDTOs.
     *
     * @param orderItems the list of OrderItems to convert
     * @return the list of converted OrderItemDTOs
     */
    public static List<OrderItemOutputDTO> convert(final List<OrderItem> orderItems) {
        return orderItems.stream().map(OrderItemOutputDTOConverter::convert).toList();
    }
}
