package ckollmeier.de.backend.service;

import ckollmeier.de.backend.converter.DishConverter;
import ckollmeier.de.backend.converter.DishOutputDTOConverter;
import ckollmeier.de.backend.dto.DishInputDTO;
import ckollmeier.de.backend.dto.DishOutputDTO;
import ckollmeier.de.backend.model.Dish;
import ckollmeier.de.backend.repository.DishRepository;
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

    /**
     * Retrieves all dishes.
     *
     * @return A list of all dishes as DishOutputDTOs.
     */
    public List<DishOutputDTO> getAllDishes() {
        return DishOutputDTOConverter.convert(dishRepository.findAllByOrderByPositionAsc());
    }

    /**
     * Adds a new dish.
     *
     * @param dish The dish to add.
     * @return The added dish as a DishOutputDTO.
     */
    public DishOutputDTO addDish(final @NonNull Dish dish) {
        if (dish.getName().isEmpty()) {
            throw new IllegalArgumentException("Dish name cannot be empty");
        }
        return DishOutputDTOConverter.convert(
            dishRepository.save(dish.withId(UUID.randomUUID().toString()))
        );
    }

    /**
     * Adds a new dish with the specified type.
     *
     * @param dish The dish to add.
     * @return The added dish as a DishOutputDTO.
     */
    public DishOutputDTO addDish(final @NonNull DishInputDTO dish) {
        return addDish(DishConverter.convert(dish));
    }
}
