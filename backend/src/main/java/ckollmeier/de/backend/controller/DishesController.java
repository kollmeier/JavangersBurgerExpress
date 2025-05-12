package ckollmeier.de.backend.controller;

import ckollmeier.de.backend.dto.DishInputDTO;
import ckollmeier.de.backend.dto.DishOutputDTO;
import ckollmeier.de.backend.service.DishService;
import ckollmeier.de.backend.types.DishType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller für die Gerichte.
 */
@RestController
@RequestMapping("/api/dishes")
@RequiredArgsConstructor
public class DishesController {
    /**
     * Der Service für die Gerichte.
     */
    private final DishService dishService;

    /**
     * Gibt alle Gerichte zurück.
     * @return Liste aller Gerichte
     */
    @GetMapping
    public List<DishOutputDTO> getAllDishes() {
        return dishService.getAllDishes();
    }

    /**
     * Fügt ein Gericht hinzu.
     * @param dish das Hauptgericht
     * @param type der Typ des Gerichts (main, side, beverage)
     * @return das Hauptgericht
     */
    @PostMapping("/{type}")
    public ResponseEntity<DishOutputDTO> addDish(final @RequestBody DishInputDTO dish, final @PathVariable DishType type) {
        return new ResponseEntity<>(
                dishService.addDish(dish, type),
                HttpStatus.CREATED
        );
    }
}
