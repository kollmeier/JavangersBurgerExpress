package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.OrderOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Order;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Utility class for converting Order objects to OrderDTO objects.
 */
public class OrderOutputDTOConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class should not be instantiated.
     */
    private OrderOutputDTOConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts a single Order to an OrderDTO.
     *
     * @param order the Order to convert
     * @return the converted OrderDTO
     */
    public static OrderOutputDTO convert(final Order order, boolean flattened) {
        return new OrderOutputDTO(
                order.getId() == null ? UUID.randomUUID().toString() : order.getId(),
                order.getOrderNumber() != null ? order.getOrderNumber() : 0,
                flattened ? OrderItemOutputDTOConverter.convertFlattened(order.getItems()) : OrderItemOutputDTOConverter.convert(order.getItems()),
                order.getTotalPrice().toPlainString().replace(".", ",") ,
                order.getCreatedAt() != null ? DATE_TIME_FORMATTER.format(order.getCreatedAt()) : null,
                order.getUpdatedAt() != null ? DATE_TIME_FORMATTER.format(order.getUpdatedAt()) : null,
                order.getStatus().name()
        );
    }

    public static OrderOutputDTO convert(final Order order) {
        return convert(order, false);
    }

    public static OrderOutputDTO convertFlattened(final Order order) {
        return convert(order, true);
    }

    public static List<OrderOutputDTO> convert(final List<Order> orders) {
        return orders.stream().map(OrderOutputDTOConverter::convert).toList();
    }

    public static List<OrderOutputDTO> convertFlattened(List<Order> orders) {
        return orders.stream().map(OrderOutputDTOConverter::convertFlattened).toList();
    }
}
