package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.MenuOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.MenuInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.repository.MenuRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {
    /**
     * The repository for storing and retrieving menus.
     */
    private final MenuRepository menuRepository;

    private final ValidatedItemService<Menu> validatedMenuService;

    private static final String MENU = "Menü";
    private static final String ERROR_PATH_BASE = "menus";

    /**
     * Retrieves all menus.
     *
     * @return A list of all menus as MenuOutputDTOs.
     */
    public List<MenuOutputDTO> getAllMenus() {
        return MenuOutputDTOConverter.convert(menuRepository.findAllByOrderByPositionAsc());
    }

    /**
     * Adds a new menu with the specified type.
     *
     * @param menu The menu to add.
     * @return The added menu as a MenuOutputDTO.
     */
    public MenuOutputDTO addMenu(final @NonNull MenuInputDTO menu) {
        return MenuOutputDTOConverter.convert(
                menuRepository.save(
                        validatedMenuService.validatedItemOrThrow(
                            Menu.class,
                            MENU,
                            ERROR_PATH_BASE,
                            menu,
                            null,
                            "add")
                            .withId(UUID.randomUUID().toString()))
        );
    }

    /**
     * Removes a menu.
     * @param id The ID of the menu to remove.
     */
    public void removeMenu(final @NonNull String id) {
        validatedMenuService.validatedItemOrThrow(
                Menu.class,
                MENU,
                ERROR_PATH_BASE,
                null,
                id,
                "delete");
        menuRepository.deleteById(id);
    }

    /**
     * Updates an existing menu.
     *
     * @param id             The ID of the menu to update.
     * @param menuInputDTO The updated menu data.
     * @return The updated menu as a MenuOutputDTO. */
    public MenuOutputDTO updateMenu(final @NonNull String id, final @NonNull MenuInputDTO menuInputDTO) {
        Menu menu = validatedMenuService.validatedItemOrThrow(
                Menu.class,
                MENU,
                ERROR_PATH_BASE,
                menuInputDTO,
                id,
                "update",
                true);

        return MenuOutputDTOConverter.convert(
                menuRepository.save(menu)
        );
    }
}
