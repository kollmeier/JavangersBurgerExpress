package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.MenuInputDTO;
import de.ckollmeier.burgerexpress.backend.model.Menu;

import java.math.BigDecimal;

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
    public static Menu convert(final MenuInputDTO menu) {

        return Menu.builder()
            .name(menu.name())
                // Dishes must be added by service
            .price(new BigDecimal(menu.price().replace(",", ".")))
            .additionalInformation(AdditionalInformationConverter.convert(menu.additionalInformation()))
            .build();
    }
}
