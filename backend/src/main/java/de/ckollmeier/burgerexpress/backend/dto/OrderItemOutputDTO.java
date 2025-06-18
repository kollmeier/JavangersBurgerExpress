package de.ckollmeier.burgerexpress.backend.dto;

public record OrderItemOutputDTO(
        String id,
        OrderableItemOutputDTO item,
        int amount,
        String price
) {
}
