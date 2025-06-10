package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.DishOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DishInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DishOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DishService {
    /**
     * The repository for storing and retrieving dishes.
     */
    private final DishRepository dishRepository;
    
    private final ValidatedItemService<Dish> validatedDishService;

    private static final String DISH = "Gericht";
    private static final String ERROR_PATH_BASE = "dishes";

    /**
     * Retrieves all dishes.
     *
     * @return A list of all dishes as DishOutputDTOs.
     */
    public List<DishOutputDTO> getAllDishes() {
        return DishOutputDTOConverter.convert(dishRepository.findAllByOrderByPositionAscCreatedAtDesc());
    }

    /**
     * Adds a new dish with the specified type.
     *
     * @param dish The dish to add.
     * @return The added dish as a DishOutputDTO.
     */
    public DishOutputDTO addDish(final @NonNull DishInputDTO dish) {
        return DishOutputDTOConverter.convert(
                dishRepository.save(
                        validatedDishService.validatedItemOrThrow(
                            Dish.class,
                            DISH,
                            ERROR_PATH_BASE,
                            dish, 
                            null, 
                            "add")
                        .withId(UUID.randomUUID().toString()))
        );
    }

    /**
     * Removes a dish.
     * @param id The ID of the dish to remove.
     */
    public void removeDish(final @NonNull String id) {
        validatedDishService.validatedItemOrThrow(
                Dish.class,
                DISH,
                ERROR_PATH_BASE,
                null, 
                id, 
                "delete");
        dishRepository.deleteById(id);
    }

    /**
     * Updates an existing dish.
     *
     * @param id             The ID of the dish to update.
     * @param dishInputDTO The updated dish data.
     * @return The updated dish as a DishOutputDTO. */
    public DishOutputDTO updateDish(final @NonNull String id, final @NonNull DishInputDTO dishInputDTO) {
        Dish dish = validatedDishService.validatedItemOrThrow(
                Dish.class,
                DISH,
                ERROR_PATH_BASE,
                dishInputDTO, 
                id, 
                "update", 
                true);

        return DishOutputDTOConverter.convert(
                dishRepository.save(dish)
        );
    }
}
