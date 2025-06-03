package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.DisplayCategoryConverter;
import de.ckollmeier.burgerexpress.backend.converter.DisplayCategoryOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DisplayCategoryInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DisplayCategoryOutputDTO;
import de.ckollmeier.burgerexpress.backend.exceptions.NotBlankException;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import de.ckollmeier.burgerexpress.backend.repository.DisplayCategoryRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisplayCategoryService {
    private final DisplayCategoryRepository displayCategoryRepository;

    private static final String CATEGORY_NOT_FOUND_MESSAGE_FORMAT = "Kategorie für die Id %s existiert nicht.";
    private static final String NOT_BLANK_MESSAGE_FORMAT = "%s darf nicht leer sein!";

    private static final String PATH_FORMAT = "displayCategory/%s/%s";

    private DisplayCategory validatedDisplayCategoryOrThrow(DisplayCategoryInputDTO displayCategoryInputDTO, String id, String item) {
        return validatedDisplayCategoryOrThrow(displayCategoryInputDTO, id, item, false);
    }

    private DisplayCategory validatedDisplayCategoryOrThrow(DisplayCategoryInputDTO displayCategoryInputDTO, String id, String item, boolean withUpdate) {
        DisplayCategory displayCategory = null;
        if (id != null) {
            displayCategory = displayCategoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                    String.format(CATEGORY_NOT_FOUND_MESSAGE_FORMAT, id),
                    String.format(PATH_FORMAT, item, "")
                )
            );
        }
        if (displayCategoryInputDTO == null) {
            return displayCategory;
        }
        if (displayCategoryInputDTO.name().isBlank()) {
            throw new NotBlankException(
                    String.format(NOT_BLANK_MESSAGE_FORMAT, "Name"),
                    String.format(PATH_FORMAT, item, "name")
            );
        }
        return withUpdate ?
                DisplayCategoryConverter.convert(displayCategoryInputDTO, displayCategory) :
                DisplayCategoryConverter.convert(displayCategoryInputDTO);
    }

    public List<DisplayCategoryOutputDTO> getAllDisplayCategories() {
        return DisplayCategoryOutputDTOConverter.convert(displayCategoryRepository.findAllByOrderByPositionAsc());
    }

    public DisplayCategoryOutputDTO addDisplayCategory(@NonNull DisplayCategoryInputDTO displayCategory) {
        return DisplayCategoryOutputDTOConverter.convert(
            displayCategoryRepository.save(
                validatedDisplayCategoryOrThrow(displayCategory, null, "new"))
        );
    }

    public void removeDisplayCategory(@NonNull String id) {
        validatedDisplayCategoryOrThrow(null, id, "delete");
        displayCategoryRepository.deleteById(id);
    }

    public DisplayCategoryOutputDTO updateDisplayCategory(@NonNull String id, @NonNull DisplayCategoryInputDTO displayCategory) {
        validatedDisplayCategoryOrThrow(displayCategory, id, "update");
        return DisplayCategoryOutputDTOConverter.convert(
                displayCategoryRepository.save(
                        validatedDisplayCategoryOrThrow(displayCategory, id, "update", true)
                )
        );
    }
}
