package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.DisplayCategoryInputDTO;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import lombok.NonNull;

public class DisplayCategoryConverter {
    // Utility class
    private DisplayCategoryConverter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static DisplayCategory convert(final @NonNull DisplayCategoryInputDTO displayCategory) {
        return DisplayCategory.builder()
                .name(displayCategory.name())
                .description(displayCategory.description())
                .imageUrl(displayCategory.imageUrl())
                .published(displayCategory.published())
                .build();
    }

    public static DisplayCategory convert(final @NonNull DisplayCategoryInputDTO displayCategory, final DisplayCategory displayCategoryToUpdate) {
        return DisplayCategory.builder()
                .name(displayCategory.name() != null ? displayCategory.name() : displayCategoryToUpdate.getName())
                .description(displayCategory.description() != null ? displayCategory.description() : displayCategoryToUpdate.getDescription())
                .imageUrl(displayCategory.imageUrl() != null ? displayCategory.imageUrl() : displayCategoryToUpdate.getImageUrl())
                .published(displayCategory.published() != null ? displayCategory.published() : displayCategoryToUpdate.isPublished())
                .build();
    }
}
