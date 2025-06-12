package de.ckollmeier.burgerexpress.backend.dto;

public record OrderItemOutputDTO(
        OrderableItemOutputDTO item,
        int amount,
        String price
) {
}
