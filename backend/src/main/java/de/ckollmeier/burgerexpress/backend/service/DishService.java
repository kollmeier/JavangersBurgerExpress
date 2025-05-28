package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.AdditionalInformationConverter;
import de.ckollmeier.burgerexpress.backend.converter.DishConverter;
import de.ckollmeier.burgerexpress.backend.converter.DishOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DishInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DishOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    /**
     * Removes a dish.
     * @param id The ID of the dish to remove.
     */
    public void removeDish(final @NonNull String id) {
        if (!dishRepository.existsById(id)) {
            throw new IllegalArgumentException("Dish not found");
        }

        dishRepository.deleteById(id);
    }

    /**
     * Updates an existing dish.
     *
     * @param id             The ID of the dish to update.
     * @param dishInputDTO The updated dish data.
     * @return The updated dish as a DishOutputDTO. */
    public DishOutputDTO updateDish(final @NonNull String id, final @NonNull DishInputDTO dishInputDTO) {
        Dish dish = dishRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Dish not found"));

        if (dishInputDTO.name() != null) {
            dish = dish.withName(dishInputDTO.name());
        }
        if (dishInputDTO.price() != null) {
            dish = dish.withPrice(new BigDecimal(dishInputDTO.price()));
        }
        if (dishInputDTO.additionalInformation() != null && !dishInputDTO.additionalInformation().isEmpty()) {
            dish = dish.withAdditionalInformation(AdditionalInformationConverter.convert(dishInputDTO.additionalInformation()));
        }
        if (dishInputDTO.imageUrl() != null && !dishInputDTO.imageUrl().equals(dish.getImageUrl())) {
            dish = dish.withImageUrl(dishInputDTO.imageUrl());
        }
        return updateDish(dish);
    }

    /**
     * Updates an existing dish in the repository.
     *
     * @param dish The dish to update.
     * @return The updated dish as a DishOutputDTO.
     * @throws IllegalArgumentException If the dish does not exist.
     */
    public DishOutputDTO updateDish(final @NonNull Dish dish) {
        if (!dishRepository.existsById(dish.getId())) {
            throw new IllegalArgumentException("Dish not found");
        }
        return DishOutputDTOConverter.convert(dishRepository.save(dish));
    }
}
