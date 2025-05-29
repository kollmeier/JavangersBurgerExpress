package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.DishInputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.types.DishType;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

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
            .imageUrl(dish.imageUrl())
            .build();
    }

    /**
     * Converts a id to a Dish Reference.
     *
     * @param id the id of the Dish to convert
     * @return the converted Dish as Reference
     */
    public static Dish convert(final String id, final Function<String, Dish> dishResolver) {
        return dishResolver.apply(id);
    }

    /**
     * Converts multiple ids to Dish References.
     *
     * @param ids the ids of the Dishes to convert
     * @return the List of converted Dishes as Reference
     */
    public static List<Dish> convert(final List<String> ids, final Function<String, Dish> dishResolver) {
        return ids.stream().map(id -> convert(id, dishResolver)).toList();
    }
}
