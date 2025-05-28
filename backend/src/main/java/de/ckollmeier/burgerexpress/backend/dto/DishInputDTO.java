package de.ckollmeier.burgerexpress.backend.dto;

import java.util.Map;

public record DishInputDTO(
        String type,
        String name,
        String price,
        Map<String, AdditionalInformationDTO> additionalInformation,
        String imageUrl
) {
}
