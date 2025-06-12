package de.ckollmeier.burgerexpress.backend.dto;

public record OrderItemInputDTO(
        String item,
        int amount
) {
}
