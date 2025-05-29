package de.ckollmeier.burgerexpress.backend.dto;

import lombok.With;

import java.util.List;
import java.util.Map;

@With
public record MenuOutputDTO(
        String id,
        String name,
        String price,
        List<DishOutputDTO> mainDishes,
        List<DishOutputDTO> sideDishes,
        List<DishOutputDTO> beverages,
        Map<String, AdditionalInformationDTO> additionalInformation
) {
}
