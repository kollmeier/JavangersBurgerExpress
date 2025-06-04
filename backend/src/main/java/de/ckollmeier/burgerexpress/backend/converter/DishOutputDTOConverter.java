package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.DishOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;

import java.util.List;

public final class DishOutputDTOConverter {
    private DishOutputDTOConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts a Dish entity to a DishOutputDTO.
     *
     * @param dish The Dish entity to convert.
     * @return The corresponding DishOutputDTO.
     */
    public static DishOutputDTO convert(final Dish dish) {
        return new DishOutputDTO(
                dish.getId(),
                dish.getName(),
                dish.getPrice().toString(),
                dish.getOrderableItemType().name().toLowerCase(),
                AdditionalInformationDTOConverter.convert(dish.getAdditionalInformation()),
                dish.getImageUrl()
        );
    }
    /**
     * Converts a list of Dish entities to a list of DishOutputDTOs.
     *
     * @param dishes The list of Dish entities to convert.
     * @return The corresponding list of DishOutputDTOs.
     */
    public static List<DishOutputDTO> convert(final List<Dish> dishes) {
        return dishes.stream().map(DishOutputDTOConverter::convert).toList();
    }
}
