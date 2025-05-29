package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.MenuOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Menu;

import java.util.List;

public final class MenuOutputDTOConverter {
    private MenuOutputDTOConverter() {
        // Utility class
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts a Menu entity to a MenuOutputDTO.
     *
     * @param menu The Menu entity to convert.
     * @return The corresponding MenuOutputDTO.
     */
    public static MenuOutputDTO convert(final Menu menu) {
        return new MenuOutputDTO(
                menu.getId(),
                menu.getName(),
                menu.getPrice().toString(),
                DishOutputDTOConverter.convert(menu.getMainDishes()),
                DishOutputDTOConverter.convert(menu.getSideDishes()),
                DishOutputDTOConverter.convert(menu.getBeverages()),
                AdditionalInformationDTOConverter.convert(menu.getAdditionalInformation())
        );
    }
    /**
     * Converts a list of Menu entities to a list of MenuOutputDTOs.
     *
     * @param menus The list of Menu entities to convert.
     * @return The corresponding list of MenuOutputDTOs.
     */
    public static List<MenuOutputDTO> convert(final List<Menu> menus) {
        return menus.stream().map(MenuOutputDTOConverter::convert).toList();
    }
}
