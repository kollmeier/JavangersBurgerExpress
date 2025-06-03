package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.MenuInputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.Menu;

import java.math.BigDecimal;
import java.util.function.Function;

public final class MenuConverter {
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class should not be instantiated.
     */
    private MenuConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts a MenuInputDTO to a Menu.
     *
     * @param menu the MenuInputDTO to convert
     * @return the converted Menu
     */
    public static Menu convert(final MenuInputDTO menu, final Function<String, Dish> dishResolver) {

        return Menu.builder()
            .name(menu.name())
            .price(new BigDecimal(menu.price().replace(",", ".")))
            .dishes(DishConverter.convert(menu.dishIds(), dishResolver))
            .additionalInformation(AdditionalInformationConverter.convert(menu.additionalInformation()))
            .build();
    }

    public static Menu convert(final MenuInputDTO menu, final Menu existingMenu, final Function<String, Dish> dishResolver) {
        return Menu.builder()
            .id(existingMenu.getId())
            .name(menu.name() != null ? menu.name() : existingMenu.getName())
            .price(menu.price() != null ? new BigDecimal(menu.price().replace(",", ".")) : existingMenu.getPrice())
            .dishes(DishConverter.convert(menu.dishIds(), dishResolver))
            .additionalInformation(AdditionalInformationConverter.convert(menu.additionalInformation(), existingMenu.getAdditionalInformation()))
            .build();
    }
}
