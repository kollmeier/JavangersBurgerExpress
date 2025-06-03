package de.ckollmeier.burgerexpress.backend.dto;

public record DisplayCategoryInputDTO(
        String name,
        String description,
        String imageUrl,
        Boolean published
) {
}
