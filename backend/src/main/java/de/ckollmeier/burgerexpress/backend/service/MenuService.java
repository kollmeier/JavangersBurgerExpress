package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.AdditionalInformationConverter;
import de.ckollmeier.burgerexpress.backend.converter.DishConverter;
import de.ckollmeier.burgerexpress.backend.converter.MenuConverter;
import de.ckollmeier.burgerexpress.backend.converter.MenuOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.MenuInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.repository.MenuRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {
    /**
     * The repository for storing and retrieving menus.
     */
    private final MenuRepository menuRepository;

    private final DishRepository dishRepository;

    /**
     * Retrieves all menus.
     *
     * @return A list of all menus as MenuOutputDTOs.
     */
    public List<MenuOutputDTO> getAllMenus() {
        return MenuOutputDTOConverter.convert(menuRepository.findAllByOrderByPositionAsc());
    }

    /**
     * Adds a new menu.
     *
     * @param menu The menu to add.
     * @return The added menu as a MenuOutputDTO.
     */
    public MenuOutputDTO addMenu(final @NonNull Menu menu) {
        if (menu.getName().isEmpty()) {
            throw new IllegalArgumentException("Menu name cannot be empty");
        }
        if (menu.getDishes().isEmpty()) {
            throw new IllegalArgumentException("Menu must contain at least one dish");
        }
        if (menu.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Menu price must be greater than zero");
        }

        return MenuOutputDTOConverter.convert(
            menuRepository.save(menu.withId(UUID.randomUUID().toString()))
        );
    }

    /**
     * Adds a new menu with the specified type.
     *
     * @param menu The menu to add.
     * @return The added menu as a MenuOutputDTO.
     */
    public MenuOutputDTO addMenu(final @NonNull MenuInputDTO menu) {
        if (menu.price().isBlank()) {
            throw new IllegalArgumentException("Menu price must not be blank");
        }
        return addMenu(MenuConverter.convert(menu, dishRepository::getReferenceById));
    }

    /**
     * Removes a menu.
     * @param id The ID of the menu to remove.
     */
    public void removeMenu(final @NonNull String id) {
        if (!menuRepository.existsById(id)) {
            throw new IllegalArgumentException("Menu not found");
        }

        menuRepository.deleteById(id);
    }

    /**
     * Updates an existing menu.
     *
     * @param id             The ID of the menu to update.
     * @param menuInputDTO The updated menu data.
     * @return The updated menu as a MenuOutputDTO. */
    public MenuOutputDTO updateMenu(final @NonNull String id, final @NonNull MenuInputDTO menuInputDTO) {
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Menu not found"));

        if (menuInputDTO.name() != null) {
            menu = menu.withName(menuInputDTO.name());
        }
        if (menuInputDTO.price() != null) {
            menu = menu.withPrice(new BigDecimal(menuInputDTO.price()));
        }
        if (menuInputDTO.additionalInformation() != null && !menuInputDTO.additionalInformation().isEmpty()) {
            menu = menu.withAdditionalInformation(AdditionalInformationConverter.convert(menuInputDTO.additionalInformation()));
        }
        if (menuInputDTO.dishIds() != null) {
            menu = menu.withDishes(DishConverter.convert(menuInputDTO.dishIds(), dishRepository::getReferenceById));
        }
        return updateMenu(menu);
    }

    /**
     * Updates an existing menu in the repository.
     *
     * @param menu The menu to update.
     * @return The updated menu as a MenuOutputDTO.
     * @throws IllegalArgumentException If the menu does not exist.
     */
    public MenuOutputDTO updateMenu(final @NonNull Menu menu) {
        if (!menuRepository.existsById(menu.getId())) {
            throw new IllegalArgumentException("Menu not found");
        }
        if (menu.getDishes().isEmpty()) {
            throw new IllegalArgumentException("Menu must contain at least one dish");
        }
        if (menu.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Menu price must be greater than zero");
        }
        return MenuOutputDTOConverter.convert(menuRepository.save(menu));
    }
}
