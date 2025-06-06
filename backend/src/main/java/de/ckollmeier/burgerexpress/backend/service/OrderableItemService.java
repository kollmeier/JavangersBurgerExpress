package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.OrderableItemOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.OrderableItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.repository.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderableItemService {
    private final DishRepository dishRepository;
    private final MenuRepository menuRepository;

    public OrderableItemService(DishRepository dishRepository, MenuRepository menuRepository) {
        this.dishRepository = dishRepository;
        this.menuRepository = menuRepository;
    }

    /**
     * Returns all Menus and Dishes as a list of OrderableItemOutputDTOs.
     *
     * @return List of OrderableItemOutputDTOs
     */
    public List<OrderableItemOutputDTO> getAllOrderableItems() {
        List<OrderableItem> orderableItems = new ArrayList<>();
        orderableItems.addAll(dishRepository.findAll());
        orderableItems.addAll(menuRepository.findAll());
        return OrderableItemOutputDTOConverter.convert(orderableItems);
    }

    /**
     * Returns all Menus as a list of OrderableItemOutputDTOs.
     *
     * @return List of OrderableItemOutputDTOs
     */
    public List<OrderableItemOutputDTO> getAllMenus() {
        List<OrderableItem> orderableItems = new ArrayList<>(menuRepository.findAll());
        return OrderableItemOutputDTOConverter.convert(orderableItems);
    }

    /**
     * Returns all Dishes as a list of OrderableItemOutputDTOs.
     *
     * @return List of OrderableItemOutputDTOs
     */
    public List<OrderableItemOutputDTO> getAllDishes() {
        List<OrderableItem> orderableItems = new ArrayList<>(dishRepository.findAll());
        return OrderableItemOutputDTOConverter.convert(orderableItems);
    }
}
