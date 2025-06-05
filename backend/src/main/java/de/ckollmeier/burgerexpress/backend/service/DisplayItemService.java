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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DisplayItemService {
    private final DisplayItemRepository displayItemRepository;

    private final ValidatedItemService<DisplayItem> validatedDisplayItemService;

    private final ConverterService converterService;

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

        DisplayItem findableItem = null;
        if (id != null) {
            findableItem = validatedDisplayItemService.validatedItemOrThrow(
                    DisplayItem.class,
                    DISPLAY_ITEM,
                    ERROR_PATH_BASE,
                    null,
                    id,
                    item);
        }

        if (displayItem == null) {
            return findableItem;
        }

        return update && findableItem != null ? 
                converterService.convert(displayItem, findableItem) : 
                converterService.convert(displayItem);
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
                        "new", false)
                        .withId(UUID.randomUUID().toString()))
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
