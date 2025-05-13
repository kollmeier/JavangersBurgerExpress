package ckollmeier.de.backend.dto;

import java.util.Map;

public record DishInputDTO(
        String type,
        String name,
        String price,
        Map<String, AdditionalInformationDTO> additionalInformation
) {
}
