package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.DisplayCategoryOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;

import java.util.List;

public class DisplayCategoryOutputDTOConverter {
    private DisplayCategoryOutputDTOConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static DisplayCategoryOutputDTO convert(final DisplayCategory displayCategory) {
        return new DisplayCategoryOutputDTO(
                displayCategory.getId(),
                displayCategory.getName(),
                displayCategory.getDescription(),
                DisplayItemOutputDTOConverter.convert(displayCategory.getDisplayItems()),
                displayCategory.getImageUrl(),
                displayCategory.isPublished()
        );
    }

    public static List<DisplayCategoryOutputDTO> convert(final List<DisplayCategory> displayCategories) {
        return displayCategories.stream().map(DisplayCategoryOutputDTOConverter::convert).toList();
    }
}
