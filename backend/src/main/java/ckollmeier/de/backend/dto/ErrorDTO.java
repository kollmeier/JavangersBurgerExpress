package ckollmeier.de.backend.dto;

public record ErrorDTO(
        String error,
        String message,
        String status
) {
}
