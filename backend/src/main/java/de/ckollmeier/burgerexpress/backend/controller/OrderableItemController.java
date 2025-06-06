package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.dto.OrderableItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.service.OrderableItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orderable-items")
public class OrderableItemController {
    private final OrderableItemService orderableItemService;

    public OrderableItemController(OrderableItemService orderableItemService) {
        this.orderableItemService = orderableItemService;
    }

    /**
     * Returns all Menus and Dishes as a list of OrderableItemOutputDTOs.
     *
     * @return ResponseEntity with a list of OrderableItemOutputDTOs
     */
    @GetMapping
    public ResponseEntity<List<OrderableItemOutputDTO>> getAllOrderableItems() {
        return ResponseEntity.ok(orderableItemService.getAllOrderableItems());
    }

    /**
     * Returns all Menus as a list of OrderableItemOutputDTOs.
     *
     * @return ResponseEntity with a list of OrderableItemOutputDTOs
     */
    @GetMapping("/menus")
    public ResponseEntity<List<OrderableItemOutputDTO>> getAllMenus() {
        return ResponseEntity.ok(orderableItemService.getAllMenus());
    }

    /**
     * Returns all Dishes as a list of OrderableItemOutputDTOs.
     *
     * @return ResponseEntity with a list of OrderableItemOutputDTOs
     */
    @GetMapping("/dishes")
    public ResponseEntity<List<OrderableItemOutputDTO>> getAllDishes() {
        return ResponseEntity.ok(orderableItemService.getAllDishes());
    }
}