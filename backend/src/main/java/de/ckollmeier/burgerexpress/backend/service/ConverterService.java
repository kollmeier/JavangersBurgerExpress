package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.*;
import de.ckollmeier.burgerexpress.backend.dto.*;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConverterService {
    private final DishRepository dishRepository;

    public Dish convert(DishInputDTO dishInputDTO) {
        return DishConverter.convert(dishInputDTO);
    }

    public Dish convert(DishInputDTO dishInputDTO, Dish dishToUpdate) {
        return DishConverter.convert(dishInputDTO, dishToUpdate);
    }

    public DishOutputDTO convert(Dish dish) {
        return DishOutputDTOConverter.convert(dish);
    }

    public Menu convert(MenuInputDTO menu) {
        return MenuConverter.convert(menu, dishRepository::getReferenceById);
    }

    public Menu convert(MenuInputDTO menu, Menu menuToUpdate) {
        return MenuConverter.convert(menu, menuToUpdate, dishRepository::getReferenceById);
    }

    public MenuOutputDTO convert(Menu menu) {
        return MenuOutputDTOConverter.convert(menu);
    }

    public DisplayCategory convert(DisplayCategoryInputDTO displayCategory) {
        return DisplayCategoryConverter.convert(displayCategory);
    }

    public DisplayCategory convert(DisplayCategoryInputDTO displayCategory, DisplayCategory displayCategoryToUpdate) {
        return DisplayCategoryConverter.convert(displayCategory, displayCategoryToUpdate);
    }

    public DisplayCategoryOutputDTO convert(DisplayCategory displayCategory) {
        return DisplayCategoryOutputDTOConverter.convert(displayCategory);
    }
}
