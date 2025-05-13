package ckollmeier.de.backend.dto;

import lombok.With;

import java.util.Map;

@With
public record DishOutputDTO(
        String id,
        String name,
        String price,
        String type,
        Map<String, AdditionalInformationDTO> additionalInformation
) {
}
