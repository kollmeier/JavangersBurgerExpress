package de.ckollmeier.burgerexpress.backend.dto;

import java.util.List;

public record DisplayItemInputDTO(
        String name,
        String description,
        Boolean hasActualPrice,
        String actualPrice,
        List<String> orderableItemIds,
        Boolean published,
        String categoryId
) {
}
