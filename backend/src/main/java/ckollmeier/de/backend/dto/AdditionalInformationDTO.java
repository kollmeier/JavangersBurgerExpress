package ckollmeier.de.backend.dto;

public record AdditionalInformationDTO(
        String type,
        String value,
        String displayString,
        String shortDisplayString
) {
}
