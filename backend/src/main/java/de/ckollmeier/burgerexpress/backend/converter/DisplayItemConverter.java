package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.DisplayItemInputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.function.Function;

public final class DisplayItemConverter {
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class should not be instantiated.
     */
    private DisplayItemConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts a DisplayItemInputDTO to a DisplayItem.
     *
     * @param displayItem the DisplayItemInputDTO to convert
     * @return the converted DisplayItem
     */
    public static DisplayItem convert(final @NonNull DisplayItemInputDTO displayItem, final @NonNull Function<String, OrderableItem> itemResolver) {
        return DisplayItem.builder()
            .name(displayItem.name())
            .description(displayItem.description())
            .categoryId(new org.bson.types.ObjectId(displayItem.categoryId()))
            .actualPrice(Boolean.TRUE.equals(displayItem.hasActualPrice()) ? new BigDecimal(displayItem.actualPrice().replace(",", ".")) : null)
            .orderableItems(displayItem.orderableItemIds().stream()
                    .map(itemResolver)
                    .toList())
            .published(Boolean.TRUE.equals(displayItem.published()))
            .build();
    }

    public static DisplayItem convert(final @NonNull DisplayItemInputDTO displayItem, final @NonNull DisplayItem existingDisplayItem, final @NonNull Function<String, OrderableItem> itemResolver) {
        BigDecimal actualPrice = existingDisplayItem.getActualPrice();
        if (displayItem.hasActualPrice() != null) {
            actualPrice = displayItem.actualPrice() != null ? new BigDecimal(displayItem.actualPrice().replace(",", ".")) : null;
        }
        return DisplayItem.builder()
            .id(existingDisplayItem.getId())
            .name(displayItem.name() == null ? existingDisplayItem.getName() : displayItem.name())
            .description(displayItem.description() == null ? existingDisplayItem.getDescription() : displayItem.description())
            .categoryId(displayItem.categoryId() != null ? new org.bson.types.ObjectId(displayItem.categoryId()) : existingDisplayItem.getCategoryId())
            .actualPrice(actualPrice)
            .orderableItems(displayItem.orderableItemIds().stream()
                    .map(itemResolver)
                    .toList())
            .published(displayItem.published() != null ? displayItem.published() : existingDisplayItem.isPublished())
            .build();
    }
}
