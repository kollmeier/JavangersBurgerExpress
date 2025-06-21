package de.ckollmeier.burgerexpress.backend.dto;

public record LoginResponseDTO(
        boolean success,
        String error
) {
}
