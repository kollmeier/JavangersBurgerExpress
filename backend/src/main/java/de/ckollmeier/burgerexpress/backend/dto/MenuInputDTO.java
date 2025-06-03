package de.ckollmeier.burgerexpress.backend.dto;

import de.ckollmeier.burgerexpress.backend.interfaces.NamedDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.PricedDTO;

import java.util.List;
import java.util.Map;

public record MenuInputDTO(
        String name,
        String price,
        List<String> dishIds,
        Map<String, AdditionalInformationDTO> additionalInformation
) implements NamedDTO, PricedDTO {
}
