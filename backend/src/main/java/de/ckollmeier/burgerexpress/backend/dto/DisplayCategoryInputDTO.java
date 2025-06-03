package de.ckollmeier.burgerexpress.backend.dto;

import de.ckollmeier.burgerexpress.backend.interfaces.NamedDTO;

public record DisplayCategoryInputDTO(
        String name,
        String description,
        String imageUrl,
        Boolean published
) implements NamedDTO {
}
