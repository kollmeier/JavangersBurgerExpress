package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.converter.DisplayCategoryOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DisplayCategoryInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DisplayCategoryOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.SortedInputDTO;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import de.ckollmeier.burgerexpress.backend.service.DisplayCategoryService;
import de.ckollmeier.burgerexpress.backend.service.SortableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller für die DisplayCategories.
 */
@RestController
@RequestMapping("/api/displayCategories")
@RequiredArgsConstructor
public class DisplayCategoryController {
    /**
     * Der Service für die DisplayCategories.
     */
    private final DisplayCategoryService displayCategoryService;

    /**
     * Der Service für die Sortierung.
     */
    private final SortableService<DisplayCategory> sortableService;

    /**
     * Gibt alle DisplayCategories zurück.
     * @return Liste aller DisplayCategories
     */
    @GetMapping
    public List<DisplayCategoryOutputDTO> getAllDisplayCategories() {
        return displayCategoryService.getAllDisplayCategories();
    }

    /**
     * Fügt eine DisplayCategory hinzu.
     * @param displayCategory die DisplayCategory
     * @return das HauptdisplayCategory
     */
    @PostMapping()
    public ResponseEntity<DisplayCategoryOutputDTO> addDisplayCategory(final @RequestBody DisplayCategoryInputDTO displayCategory) {
        return new ResponseEntity<>(
                displayCategoryService.addDisplayCategory(displayCategory),
                HttpStatus.CREATED
        );
    }

    /**
     * Updated eine DisplayCategory.
     * @param displayCategoryId die ID der DisplayCategory
     * @param displayCategory das neue DisplayCategory
     * @return das neue DisplayCategory
     */
    @PutMapping("/{displayCategoryId}")
    public ResponseEntity<DisplayCategoryOutputDTO> updateDisplayCategory(final @PathVariable String displayCategoryId, final @RequestBody DisplayCategoryInputDTO displayCategory) {
        return new ResponseEntity<>(
                displayCategoryService.updateDisplayCategory(displayCategoryId, displayCategory),
                HttpStatus.OK
        );
    }

    /**
     * Updated die Positionen der DisplayCategories.
     * @param sortedInputDTOs Liste der neuen Reihenfolge
     * @return die DisplayCategories in der neuen Reihenfolge
     */
    @PutMapping("/positions")
    public ResponseEntity<List<DisplayCategoryOutputDTO>> updateDisplayCategoryPositions(final @RequestBody List<SortedInputDTO> sortedInputDTOs) {
        return new ResponseEntity<>(
                DisplayCategoryOutputDTOConverter.convert(sortableService.reorder(DisplayCategory.class, sortedInputDTOs)),
                HttpStatus.OK
        );
    }

    /**
     * Entfernt eine DisplayCategory basierend auf der angegebenen ID.
     * @param displayCategoryId die ID der zu entfernenden DisplayCategory
     * @return No-Content Status
     */
    @DeleteMapping("/{displayCategoryId}")
    public ResponseEntity<Void> removeDisplayCategory(final @PathVariable String displayCategoryId) {
        displayCategoryService.removeDisplayCategory(displayCategoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
