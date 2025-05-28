package de.ckollmeier.burgerexpress.backend.dto;

public record FileInfoDTO(
        String id,
        String fileName,
        String contentType,
        String uri
) {
}
