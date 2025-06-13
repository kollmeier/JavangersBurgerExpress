package de.ckollmeier.burgerexpress.backend.dto;

public record CustomerSessionDTO(
        String createdAt,
        String expiresAt,
        long expiresInSeconds,
        boolean expired,
        OrderOutputDTO order
) {
}
