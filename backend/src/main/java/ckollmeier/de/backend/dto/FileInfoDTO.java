package ckollmeier.de.backend.dto;

public record FileInfoDTO(
        String id,
        String fileName,
        String contentType,
        String uri
) {
}
