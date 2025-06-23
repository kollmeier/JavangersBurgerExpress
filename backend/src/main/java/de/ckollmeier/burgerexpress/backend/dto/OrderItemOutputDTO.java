package de.ckollmeier.burgerexpress.backend.dto;

import lombok.With;

public record OrderItemOutputDTO(
        String id,
        OrderableItemOutputDTO item,
        @With
        int amount,
        String price
) {
}
