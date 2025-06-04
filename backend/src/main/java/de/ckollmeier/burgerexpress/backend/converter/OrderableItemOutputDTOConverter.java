package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.OrderableItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;

import java.util.ArrayList;
import java.util.List;

public class OrderableItemOutputDTOConverter {
    private static final String DESCRIPTION="description";
    private static final String SIZE="size";

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class should not be instantiated.
     */
    private OrderableItemOutputDTOConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static List<String> getDescriptionForDisplay(final OrderableItem orderableItem) {
        List<String> description = new ArrayList<>();

        if (orderableItem.getAdditionalInformation().containsKey(DESCRIPTION)) {
            description.add(orderableItem.getAdditionalInformation().get(DESCRIPTION).displayString());
        }
        if (orderableItem.getAdditionalInformation().containsKey(SIZE)) {
            description.add(orderableItem.getAdditionalInformation().get(SIZE).displayString());
        }
        return description;
    }

    private static List<String> getDescriptionForCart(final OrderableItem orderableItem) {
        List<String> description = new ArrayList<>();

        if (orderableItem.getAdditionalInformation().containsKey(DESCRIPTION)) {
            description.add(orderableItem.getAdditionalInformation().get(DESCRIPTION).shortDisplayString());
        }
        if (orderableItem.getAdditionalInformation().containsKey(SIZE)) {
            description.add(orderableItem.getAdditionalInformation().get(SIZE).shortDisplayString());
        }
        return description;
    }

    public static OrderableItemOutputDTO convert(final OrderableItem orderableItem) {
        return new OrderableItemOutputDTO(
                orderableItem.getId(),
                orderableItem.getName(),
                orderableItem.getOldPrice() != null ? orderableItem.getOldPrice().toPlainString() : null,
                orderableItem.getPrice().toPlainString(),
                orderableItem.getOrderableItemType().name().toLowerCase() ,
                orderableItem.getImageUrls(),
                getDescriptionForDisplay(orderableItem),
                getDescriptionForCart(orderableItem)
        );
    }

    public static List<OrderableItemOutputDTO> convert(final List<OrderableItem> orderableItems) {
        return orderableItems.stream().map(OrderableItemOutputDTOConverter::convert).toList();
    }
}
