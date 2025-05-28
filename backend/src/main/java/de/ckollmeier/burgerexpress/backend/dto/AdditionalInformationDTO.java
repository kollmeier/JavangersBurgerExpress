package de.ckollmeier.burgerexpress.backend.dto;

public record AdditionalInformationDTO(
        String type,
        String value,
        String displayString,
        String shortDisplayString
) {
}
