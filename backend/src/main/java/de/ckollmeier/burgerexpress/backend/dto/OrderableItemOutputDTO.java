package de.ckollmeier.burgerexpress.backend.dto;

import java.util.List;
import java.util.Map;

public record OrderableItemOutputDTO(
        String id,
        String name,
        String oldPrice,
        String price,
        String type,
        Map<String, List<String>> imageUrls,
        List<String> descriptionForDisplay,
        List<String> descriptionForCart
) {
}
