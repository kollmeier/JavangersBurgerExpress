package de.ckollmeier.burgerexpress.backend.dto;

import java.util.List;
import java.util.Map;

public record MenuInputDTO(
        String name,
        String price,
        List<String> dishIds,
        Map<String, AdditionalInformationDTO> additionalInformation
) {
}
