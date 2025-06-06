package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.*;
import de.ckollmeier.burgerexpress.backend.dto.*;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.repository.MenuRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for converting between DTOs and domain models.
 * This service provides methods to convert between different types of DTOs and their corresponding domain models.
 */
@Service
@RequiredArgsConstructor
public class ConverterService {
    private final DishRepository dishRepository;
    private final MenuRepository menuRepository;

    /**
     * Converts a DishInputDTO to a new Dish entity.
     *
     * @param dishInputDTO The DTO to convert
     * @return A new Dish entity
     */
    public Dish convert(DishInputDTO dishInputDTO) {
        return DishConverter.convert(dishInputDTO);
    }

    /**
     * Updates an existing Dish entity with data from a DishInputDTO.
     *
     * @param dishInputDTO The DTO containing updated data
     * @param dishToUpdate The existing Dish entity to update
     * @return The updated Dish entity
     */
    public Dish convert(DishInputDTO dishInputDTO, Dish dishToUpdate) {
        return DishConverter.convert(dishInputDTO, dishToUpdate);
    }

    /**
     * Converts a Dish entity to a DishOutputDTO.
     *
     * @param dish The Dish entity to convert
     * @return A DishOutputDTO
     */
    public DishOutputDTO convert(Dish dish) {
        return DishOutputDTOConverter.convert(dish);
    }

    /**
     * Converts a MenuInputDTO to a new Menu entity.
     *
     * @param menu The DTO to convert
     * @return A new Menu entity
     */
    public Menu convert(MenuInputDTO menu) {
        return MenuConverter.convert(menu, dishRepository::getReferenceById);
    }

    /**
     * Updates an existing Menu entity with data from a MenuInputDTO.
     *
     * @param menu The DTO containing updated data
     * @param menuToUpdate The existing Menu entity to update
     * @return The updated Menu entity
     */
    public Menu convert(MenuInputDTO menu, Menu menuToUpdate) {
        return MenuConverter.convert(menu, menuToUpdate, dishRepository::getReferenceById);
    }

    /**
     * Converts a Menu entity to a MenuOutputDTO.
     *
     * @param menu The Menu entity to convert
     * @return A MenuOutputDTO
     */
    public MenuOutputDTO convert(Menu menu) {
        return MenuOutputDTOConverter.convert(menu);
    }

    /**
     * Converts a DisplayCategoryInputDTO to a new DisplayCategory entity.
     *
     * @param displayCategory The DTO to convert
     * @return A new DisplayCategory entity
     */
    public DisplayCategory convert(DisplayCategoryInputDTO displayCategory) {
        return DisplayCategoryConverter.convert(displayCategory);
    }

    /**
     * Updates an existing DisplayCategory entity with data from a DisplayCategoryInputDTO.
     *
     * @param displayCategory The DTO containing updated data
     * @param displayCategoryToUpdate The existing DisplayCategory entity to update
     * @return The updated DisplayCategory entity
     */
    public DisplayCategory convert(DisplayCategoryInputDTO displayCategory, DisplayCategory displayCategoryToUpdate) {
        return DisplayCategoryConverter.convert(displayCategory, displayCategoryToUpdate);
    }

    /**
     * Converts a DisplayCategory entity to a DisplayCategoryOutputDTO.
     *
     * @param displayCategory The DisplayCategory entity to convert
     * @return A DisplayCategoryOutputDTO
     */
    public DisplayCategoryOutputDTO convert(DisplayCategory displayCategory) {
        return DisplayCategoryOutputDTOConverter.convert(displayCategory);
    }

    /**
     * Finds an OrderableItem by its ID.
     * First tries to find a Menu with the given ID, then a Dish.
     *
     * @param id The ID of the item to find
     * @return The found OrderableItem
     * @throws NotFoundException If no item with the given ID exists
     */
    private OrderableItem findItemById(@NonNull String id) {
        OrderableItem orderableItem = menuRepository.findById(id).orElse(null);
        if (orderableItem == null) {
            orderableItem = dishRepository.findById(id).orElseThrow(
                    () -> new NotFoundException("Menu oder Gericht mit der ID " + id + " nicht gefunden!")
            );
        }
        return orderableItem;
    }

    /**
     * Converts a DisplayItemInputDTO to a new DisplayItem entity.
     *
     * @param displayItem The DTO to convert
     * @return A new DisplayItem entity
     * @throws NotFoundException If the referenced orderable item does not exist
     */
    public DisplayItem convert(DisplayItemInputDTO displayItem) {
        return DisplayItemConverter.convert(displayItem, this::findItemById);
    }

    /**
     * Updates an existing DisplayItem entity with data from a DisplayItemInputDTO.
     *
     * @param displayItem The DTO containing updated data
     * @param displayItemToUpdate The existing DisplayItem entity to update
     * @return The updated DisplayItem entity
     * @throws NotFoundException If the referenced orderable item does not exist
     */
    public DisplayItem convert(DisplayItemInputDTO displayItem, DisplayItem displayItemToUpdate) {
        return DisplayItemConverter.convert(displayItem, displayItemToUpdate, this::findItemById);
    }

    /**
     * Converts a DisplayItem entity to a DisplayItemOutputDTO.
     *
     * @param displayItem The DisplayItem entity to convert
     * @return A DisplayItemOutputDTO
     */
    public DisplayItemOutputDTO convert(DisplayItem displayItem) {
        return DisplayItemOutputDTOConverter.convert(displayItem);
    }
}
