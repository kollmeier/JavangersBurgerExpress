package de.ckollmeier.burgerexpress.backend.dto;

public record OrderItemInputDTO(
        String id,
        String item,
        int amount
) {
}
