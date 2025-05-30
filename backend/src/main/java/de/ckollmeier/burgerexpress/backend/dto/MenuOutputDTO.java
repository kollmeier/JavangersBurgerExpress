package de.ckollmeier.burgerexpress.backend.dto;

import lombok.With;

import java.util.List;
import java.util.Map;

@With
public record MenuOutputDTO(
        String id,
        String name,
        String price,
        List<DishOutputDTO> dishes,
        Map<String, AdditionalInformationDTO> additionalInformation
) {
}
