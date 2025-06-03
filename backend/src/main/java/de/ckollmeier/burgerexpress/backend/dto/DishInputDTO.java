package de.ckollmeier.burgerexpress.backend.dto;

import de.ckollmeier.burgerexpress.backend.interfaces.NamedDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.PricedDTO;

import java.util.Map;

public record DishInputDTO(
        String type,
        String name,
        String price,
        Map<String, AdditionalInformationDTO> additionalInformation,
        String imageUrl
) implements NamedDTO, PricedDTO {
}
