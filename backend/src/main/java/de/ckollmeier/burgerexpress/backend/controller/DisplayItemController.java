package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.converter.DisplayItemOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DisplayItemInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DisplayItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.SortedInputDTO;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import de.ckollmeier.burgerexpress.backend.service.DisplayItemService;
import de.ckollmeier.burgerexpress.backend.service.SortableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller für die DisplayItems.
 */
@RestController
@RequestMapping("/api/displayItems")
@RequiredArgsConstructor
public class DisplayItemController {
    /**
     * Der Service für die DisplayItems.
     */
    private final DisplayItemService displayItemService;

    /**
     * Der Service für die Sortierung.
     */
    private final SortableService<DisplayItem> sortableService;

    /**
     * Gibt alle DisplayItems zurück.
     * @return Liste aller DisplayItems
     */
    @GetMapping
    public List<DisplayItemOutputDTO> getAllDisplayItems() {
        return displayItemService.getAllDisplayItems();
    }

    /**
     * Fügt ein DisplayItem hinzu.
     * @param displayItem die DisplayItem
     * @return das HauptdisplayItem
     */
    @PostMapping()
    public ResponseEntity<DisplayItemOutputDTO> addDisplayItem(final @RequestBody DisplayItemInputDTO displayItem) {
        return new ResponseEntity<>(
                displayItemService.addDisplayItem(displayItem),
                HttpStatus.CREATED
        );
    }

    /**
     * Updated ein DisplayItem.
     * @param displayItemId die ID der DisplayItem
     * @param displayItem das neue DisplayItem
     * @return das neue DisplayItem
     */
    @PutMapping("/{displayItemId}")
    public ResponseEntity<DisplayItemOutputDTO> updateDisplayItem(final @PathVariable String displayItemId, final @RequestBody DisplayItemInputDTO displayItem) {
        return new ResponseEntity<>(
                displayItemService.updateDisplayItem(displayItemId, displayItem),
                HttpStatus.OK
        );
    }

    /**
     * Updated die Positionen der DisplayItems.
     * @param sortedInputDTOs Liste der neuen Reihenfolge
     * @return die DisplayItems in der neuen Reihenfolge
     */
    @PutMapping("/positions")
    public ResponseEntity<List<DisplayItemOutputDTO>> updateDisplayItemPositions(final @RequestBody List<SortedInputDTO> sortedInputDTOs) {
        return new ResponseEntity<>(
                DisplayItemOutputDTOConverter.convert(sortableService.reorder(DisplayItem.class, sortedInputDTOs)),
                HttpStatus.OK
        );
    }

    /**
     * Entfernt ein DisplayItem basierend auf der angegebenen ID.
     * @param displayItemId die ID der zu entfernenden DisplayItem
     * @return No-Content Status
     */
    @DeleteMapping("/{displayItemId}")
    public ResponseEntity<Void> removeDisplayItem(final @PathVariable String displayItemId) {
        displayItemService.removeDisplayItem(displayItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
