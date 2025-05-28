package de.ckollmeier.burgerexpress.backend.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public final class FilesDTO {
    @EqualsAndHashCode.Exclude
    private final byte[] data;
    private final String contentType;
    private final String fileName;
}
