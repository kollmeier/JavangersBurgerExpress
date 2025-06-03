package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.DishConverter;
import de.ckollmeier.burgerexpress.backend.converter.DishOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.converter.MenuConverter;
import de.ckollmeier.burgerexpress.backend.converter.MenuOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DishInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DishOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
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
}
