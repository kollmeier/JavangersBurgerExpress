package ckollmeier.de.backend.converter;

import ckollmeier.de.backend.dto.DishInputDTO;
import ckollmeier.de.backend.model.Dish;
import ckollmeier.de.backend.types.DishType;

import java.math.BigDecimal;

public final class DishConverter {
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class should not be instantiated.
     */
    private DishConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts a DishInputDTO to a Dish.
     *
     * @param dish the DishInputDTO to convert
     * @return the converted Dish
     */
    public static Dish convert(final DishInputDTO dish) {

        return Dish.builder()
            .type(DishType.valueOf(dish.type().toUpperCase()))
            .name(dish.name())
            .price(new BigDecimal(dish.price().replace(",", ".")))
            .additionalInformation(AdditionalInformationConverter.convert(dish.additionalInformation()))
            .build();
    }
}
