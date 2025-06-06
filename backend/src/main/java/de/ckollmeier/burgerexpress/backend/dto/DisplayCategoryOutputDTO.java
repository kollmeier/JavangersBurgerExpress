package de.ckollmeier.burgerexpress.backend.dto;

import java.util.List;

public record DisplayCategoryOutputDTO(
        String id,
        String name,
        String description,
        List<DisplayItemOutputDTO> displayItems,
        String imageUrl,
        boolean published
) {
}
