package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.DisplayItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;

import java.util.List;

public class DisplayItemOutputDTOConverter {
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class should not be instantiated.
     */
    private DisplayItemOutputDTOConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static DisplayItemOutputDTO convert(final DisplayItem displayItem) {
        return new DisplayItemOutputDTO(
                displayItem.getId(),
                displayItem.getCategoryId().toString(),
                displayItem.getName(),
                displayItem.getDescription(),
                OrderableItemOutputDTOConverter.convert(displayItem.getOrderableItems()),
                displayItem.getPrice().toPlainString(),
                displayItem.getOldPrice() != null ? displayItem.getOldPrice().toPlainString() : null,
                displayItem.isPublished()
        );
    }

    public static List<DisplayItemOutputDTO> convert(final List<DisplayItem> displayItems) {
        return displayItems.stream()
                .map(DisplayItemOutputDTOConverter::convert)
                .toList();
    }

}
