package de.ckollmeier.burgerexpress.backend.dto;

import java.util.List;

/**
 * Test-specific implementation of OrderOutputDTO that provides backward compatibility
 * with tests that use the old constructor signature.
 */
public record OrderOutputDTO(
        String id,
        int orderNumber,
        List<OrderItemOutputDTO> items,
        String totalPrice,
        String createdAt,
        String updatedAt,
        String status
) {
}