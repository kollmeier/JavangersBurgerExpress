package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.converter.MenuOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.MenuInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.SortedInputDTO;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.service.MenuService;
import de.ckollmeier.burgerexpress.backend.service.SortableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller für die Menüs.
 */
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenusController {
    /**
     * Der Service für die Menüs.
     */
    private final MenuService menuService;

    /**
     * Der Service für die Sortierung.
     */
    private final SortableService<Menu> sortableService;

    /**
     * Gibt alle Menüs zurück.
     * @return Liste aller Menüs
     */
    @GetMapping
    public List<MenuOutputDTO> getAllMenus() {
        return menuService.getAllMenus();
    }

    /**
     * Fügt ein Menü hinzu.
     * @param menu das Hauptmenü
     * @return das Hauptmenü
     */
    @PostMapping()
    public ResponseEntity<MenuOutputDTO> addMenu(final @RequestBody MenuInputDTO menu) {
        return new ResponseEntity<>(
                menuService.addMenu(menu),
                HttpStatus.CREATED
        );
    }

    /**
     * Updated ein Menü.
     * @param menuId die ID des Menüss
     * @param menu das neue Menü
     * @return das neue Menü
     */
    @PutMapping("/{menuId}")
    public ResponseEntity<MenuOutputDTO> updateMenu(final @PathVariable String menuId, final @RequestBody MenuInputDTO menu) {
        return new ResponseEntity<>(
                menuService.updateMenu(menuId, menu),
                HttpStatus.OK
        );
    }

    /**
     * Updated die Positionen der Menüs.
     * @param sortedInputDTOs Liste der neuen Reihenfolge
     * @return die Menüs in der neuen Reihenfolge
     */
    @PutMapping("/positions")
    public ResponseEntity<List<MenuOutputDTO>> updateMenuPositions(final @RequestBody List<SortedInputDTO> sortedInputDTOs) {
        return new ResponseEntity<>(
                MenuOutputDTOConverter.convert(sortableService.reorderAndSave(Menu.class, sortedInputDTOs)),
                HttpStatus.OK
        );
    }

    /**
     * Entfernt ein Menü basierend auf der angegebenen ID.
     * @param menuId die ID des zu entfernenden Menüs
     * @return No-Content Status
     */
    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> removeMenu(final @PathVariable String menuId) {
        menuService.removeMenu(menuId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
