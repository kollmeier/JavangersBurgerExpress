package de.ckollmeier.burgerexpress.backend.dto;

public record DisplayCategoryOutputDTO(
        String id,
        String name,
        String description,
        String imageUrl,
        boolean published
) {
}
