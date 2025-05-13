package ckollmeier.de.backend.controller;

import ckollmeier.de.backend.dto.DishInputDTO;
import ckollmeier.de.backend.dto.DishOutputDTO;
import ckollmeier.de.backend.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
     * @return das Hauptgericht
     */
    @PostMapping()
    public ResponseEntity<DishOutputDTO> addDish(final @RequestBody DishInputDTO dish) {
        return new ResponseEntity<>(
                dishService.addDish(dish),
                HttpStatus.CREATED
        );
    }

    /**
     * Updated ein Gericht.
     * @param dishId die ID des Gerichtes
     * @param dish das neue Gericht
     * @return das neue Gericht
     */
    @PutMapping("/{dishId}")
    public ResponseEntity<DishOutputDTO> updateDish(final @PathVariable String dishId, final @RequestBody DishInputDTO dish) {
        return new ResponseEntity<>(
                dishService.updateDish(dishId, dish),
                HttpStatus.OK
        );
    }

    /**
     * Entfernt ein Gericht basierend auf der angegebenen ID.
     * @param dishId die ID des zu entfernenden Gerichts
     * @return No-Content Status
     */
    @DeleteMapping("/{dishId}")
    public ResponseEntity<Void> removeDish(final @PathVariable String dishId) {
        dishService.removeDish(dishId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
