package de.ckollmeier.burgerexpress.backend.dto;

import java.util.List;

public record OrderInputDTO(
        String id,
        List<OrderItemInputDTO> items
) {
}
