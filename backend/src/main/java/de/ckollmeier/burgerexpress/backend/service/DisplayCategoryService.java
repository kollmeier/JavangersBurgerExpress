package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.DisplayCategoryOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DisplayCategoryInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DisplayCategoryOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import de.ckollmeier.burgerexpress.backend.model.DisplayItem;
import de.ckollmeier.burgerexpress.backend.repository.DisplayCategoryRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DisplayCategoryService {
    private final DisplayCategoryRepository displayCategoryRepository;
    private final DisplayItemService displayItemService;

    private final ValidatedItemService<DisplayCategory> validatedDisplayCategoryService;

    private static final String DISPLAY_CATEGORY = "Kategorie";
    private static final String ERROR_PATH_BASE = "displayCategories";

    public List<DisplayCategoryOutputDTO> getAllDisplayCategories() {
        return DisplayCategoryOutputDTOConverter.convert(displayCategoryRepository.findAllByOrderByPositionAsc());
    }

    public DisplayCategoryOutputDTO addDisplayCategory(@NonNull DisplayCategoryInputDTO displayCategory) {
        return DisplayCategoryOutputDTOConverter.convert(
            displayCategoryRepository.save(
                validatedDisplayCategoryService.validatedItemOrThrow(
                        DisplayCategory.class,
                        DISPLAY_CATEGORY,
                        ERROR_PATH_BASE,
                        displayCategory,
                        null,
                        "new")
                        .withId(UUID.randomUUID().toString()))
        );
    }

    public void removeDisplayCategory(@NonNull String id) {
        DisplayCategory displayCategory = validatedDisplayCategoryService.validatedItemOrThrow(
                DisplayCategory.class,
                DISPLAY_CATEGORY,
                ERROR_PATH_BASE,
                null,
                id,
                "delete");

        displayCategory.getDisplayItems().stream()
                .map(DisplayItem::getId)
                .forEach(displayItemService::removeDisplayItem);

        displayCategoryRepository.deleteById(id);
    }

    public DisplayCategoryOutputDTO updateDisplayCategory(@NonNull String id, @NonNull DisplayCategoryInputDTO displayCategory) {
        return DisplayCategoryOutputDTOConverter.convert(
                displayCategoryRepository.save(
                        validatedDisplayCategoryService.validatedItemOrThrow(
                                DisplayCategory.class,
                                DISPLAY_CATEGORY,
                                ERROR_PATH_BASE,
                                displayCategory,
                                id,
                                "update",
                                true)
                )
        );
    }
}
