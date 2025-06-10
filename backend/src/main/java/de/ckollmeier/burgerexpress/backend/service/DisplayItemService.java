package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.DisplayItemOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DisplayItemInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DisplayItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.SortedInputDTO;
import de.ckollmeier.burgerexpress.backend.exceptions.NotEmptyException;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import de.ckollmeier.burgerexpress.backend.repository.DisplayCategoryRepository;
import de.ckollmeier.burgerexpress.backend.repository.DisplayItemRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisplayItemService {
    private final DisplayItemRepository displayItemRepository;

    private final ValidatedItemService<DisplayItem> validatedDisplayItemService;

    private final ConverterService converterService;

    private final DisplayCategoryRepository displayCategoryRepository;

    private final SortableService<DisplayItem> sortableService;

    private static final String DISPLAY_ITEM = "Anzeigeelement";
    private static final String ERROR_PATH_BASE = "displayItems";

    private DisplayItem validatedDisplayItemOrThrow(
            final DisplayItemInputDTO displayItem,
            final String id,
            final String item,
            final boolean update
    ) {
        if (displayItem != null) {
            validateCategory(displayItem);
            validateOrderableItems(displayItem, id, update);
        }

        DisplayItem findableItem = findExistingItem(id, item);

        if (displayItem == null) {
            return findableItem;
        }

        return update && findableItem != null ? 
                converterService.convert(displayItem, findableItem) : 
                converterService.convert(displayItem);
    }

    private void validateCategory(final DisplayItemInputDTO displayItem) {
        if (displayItem.categoryId() != null && !displayCategoryRepository.existsById(displayItem.categoryId())) {
            throw new NotFoundException("Kategorie mit der ID " + displayItem.categoryId() + " nicht gefunden!");
        }
    }

    private void validateOrderableItems(final DisplayItemInputDTO displayItem, final String id, final boolean update) {
        // Check if this is a category-only update
        boolean isCategoryOnlyUpdate = displayItem.categoryId() != null && 
                                      displayItem.name() == null && 
                                      displayItem.description() == null;

        // Only validate orderableItemIds if this is not a category-only update
        if (!isCategoryOnlyUpdate) {
            if (!update && displayItem.orderableItemIds() == null) {
                throw new NullPointerException("Bestellbare Artikel dürfen nicht null sein!");
            }
            if (displayItem.orderableItemIds() != null && displayItem.orderableItemIds().isEmpty()) {
                throw new NotEmptyException("Bestellbare Artikel dürfen nicht leer sein!", "displayItems/" + id + "/orderableItems");
            }
        }
    }

    private DisplayItem findExistingItem(final String id, final String item) {
        if (id != null) {
            return validatedDisplayItemService.validatedItemOrThrow(
                    DisplayItem.class,
                    DISPLAY_ITEM,
                    ERROR_PATH_BASE,
                    null,
                    id,
                    item);
        }
        return null;
    }

    public List<DisplayItemOutputDTO> getAllDisplayItems() {
        return DisplayItemOutputDTOConverter.convert(displayItemRepository.findAllByOrderByPositionAscCreatedAtDesc());
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

    public List<DisplayItemOutputDTO> updateDisplayItemPositions(@NonNull List<SortedInputDTO> sortedInputDTOS) {
        // Validate that all display items exist
        for (SortedInputDTO sortedInputDTO : sortedInputDTOS) {
            if (!displayItemRepository.existsById(sortedInputDTO.id())) {
                throw new NotFoundException("Anzeigeelement mit der ID " + sortedInputDTO.id() + " nicht gefunden!");
            }

            // Validate that all categories exist
            if (sortedInputDTO.parentId() != null && !displayCategoryRepository.existsById(sortedInputDTO.parentId())) {
                throw new NotFoundException("Kategorie mit der ID " + sortedInputDTO.parentId() + " nicht gefunden!");
            }
        }

        List<DisplayItem> displayItems = sortableService.reorder(DisplayItem.class, sortedInputDTOS);
        Map<String, String> idToCategoryIdMap = sortedInputDTOS.stream()
                .collect(Collectors.toMap(SortedInputDTO::id, SortedInputDTO::parentId));
        return DisplayItemOutputDTOConverter.convert(
            displayItems.stream().map(displayItem -> {
                if (idToCategoryIdMap.containsKey(displayItem.getId()) && idToCategoryIdMap.get(displayItem.getId()) != null) {
                    return displayItemRepository.save(
                            displayItem.withCategoryId(
                                    new ObjectId(idToCategoryIdMap.get(displayItem.getId()))
                            )
                    );
                }
                return displayItem;
            }).toList()
        );
    }
}
