package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.DisplayItemOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DisplayItemInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DisplayItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.exceptions.NotEmptyException;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import de.ckollmeier.burgerexpress.backend.repository.DisplayItemRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisplayItemService {
    private final DisplayItemRepository displayItemRepository;

    private final ValidatedItemService<DisplayItem> validatedDisplayItemService;

    private static final String DISPLAY_ITEM = "Anzeigeelement";
    private static final String ERROR_PATH_BASE = "displayItems";

    private DisplayItem validatedDisplayItemOrThrow(
            final DisplayItemInputDTO displayItem,
            final String id,
            final String item,
            final boolean update
    ) {
        if (displayItem != null) {
            if (!update && displayItem.orderableItemIds() == null) {
                throw new NullPointerException("Bestellbare Artikel dürfen nicht null sein!");
            }
            if (displayItem.orderableItemIds() != null && displayItem.orderableItemIds().isEmpty()) {
                throw new NotEmptyException("Bestellbare Artikel dürfen nicht leer sein!", "displayItems/" + id + "/orderableItems");
            }
        }

        return validatedDisplayItemService.validatedItemOrThrow(
                DisplayItem.class,
                DISPLAY_ITEM,
                ERROR_PATH_BASE,
                displayItem,
                id,
                item, update);
    }

    public List<DisplayItemOutputDTO> getAllDisplayItems() {
        return DisplayItemOutputDTOConverter.convert(displayItemRepository.findAllByOrderByPositionAsc());
    }

    public DisplayItemOutputDTO addDisplayItem(@NonNull DisplayItemInputDTO displayItem) {
        return DisplayItemOutputDTOConverter.convert(
            displayItemRepository.save(
                validatedDisplayItemOrThrow(
                        displayItem,
                        null,
                        "new", false))
        );
    }

    public void removeDisplayItem(@NonNull String id) {
        validatedDisplayItemOrThrow(
                null,
                id,
                "delete", false);
        displayItemRepository.deleteById(id);
    }

    public DisplayItemOutputDTO updateDisplayItem(@NonNull String id, @NonNull DisplayItemInputDTO displayItem) {
        return DisplayItemOutputDTOConverter.convert(
                displayItemRepository.save(
                        validatedDisplayItemOrThrow(
                                displayItem,
                                id,
                                "update",
                                true)
                )
        );
    }
}
