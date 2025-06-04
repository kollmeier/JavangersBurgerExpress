package de.ckollmeier.burgerexpress.backend.dto;

import lombok.NonNull;

import java.util.List;

public record DisplayItemOutputDTO(
        @NonNull
        String id,
        @NonNull
        String name,
        @NonNull
        String description,
        @NonNull
        List<OrderableItemOutputDTO> orderableItems,
        @NonNull
        String price,
        String oldPrice,
        @NonNull
        Boolean published
) {
}
