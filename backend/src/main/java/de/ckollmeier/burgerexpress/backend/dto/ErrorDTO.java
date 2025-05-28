package de.ckollmeier.burgerexpress.backend.dto;

public record ErrorDTO(
        String error,
        String message,
        String status
) {
}
