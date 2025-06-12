package de.ckollmeier.burgerexpress.backend.dto;

import java.util.List;

public record OrderOutputDTO(
        String id,
        List<OrderItemOutputDTO> items,
        String totalPrice,
        String createdAt,
        String updatedAt,
        String status
) {
}
