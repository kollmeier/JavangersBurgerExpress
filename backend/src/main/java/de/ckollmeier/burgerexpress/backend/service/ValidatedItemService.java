package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.dto.DishInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DisplayCategoryInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuInputDTO;
import de.ckollmeier.burgerexpress.backend.exceptions.NoValidNumberException;
import de.ckollmeier.burgerexpress.backend.exceptions.NotBlankException;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.interfaces.FindableItem;
import de.ckollmeier.burgerexpress.backend.interfaces.NamedDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.PricedDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.repository.GeneralRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ValidatedItemService<T extends FindableItem> {
    private static final String ITEM_NOT_FOUND_MESSAGE_FORMAT = "%s für die Id %s existiert nicht.";
    private static final String NOT_BLANK_MESSAGE_FORMAT = "%s darf nicht leer sein!";
    private static final String NO_VALID_NUMBER_MESSAGE_FORMAT = "%s muss eine gültige Zahl sein!";
    private static final String NO_NEGATIVE_NUMBER_MESSAGE_FORMAT = "%s muss eine positive Zahl sein!";
    private static final String PRICE = "Preis";
    private static final String PRICE_FIELD = "price";
    private static final String NAME = "Name";
    private static final String NAME_FIELD = "name";

    private static final String PATH_FORMAT = "%s/%s/%s";

    private final GeneralRepository<T> repository;

    private final ConverterService converterService;

    private void validateNameOrThrow(NamedDTO inputDTO, String base, String item) {
        if (inputDTO.name().isBlank()) {
            throw new NotBlankException(
                    String.format(NOT_BLANK_MESSAGE_FORMAT, NAME),
                    String.format(PATH_FORMAT, base, item, NAME_FIELD)
            );
        }
    }

    private void validatePriceOrThrow(PricedDTO inputDTO, String base, String item) {
        if ((inputDTO).price().isBlank()) {
            throw new NotBlankException(
                    String.format(NOT_BLANK_MESSAGE_FORMAT, PRICE),
                    String.format(PATH_FORMAT, base, item, PRICE_FIELD)
            );
        }
        try {
            BigDecimal price = new BigDecimal((inputDTO).price().replace(",", "."));
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new NoValidNumberException(
                        String.format(NO_NEGATIVE_NUMBER_MESSAGE_FORMAT, PRICE),
                        String.format(PATH_FORMAT, base, item, PRICE_FIELD)
                );
            }
        } catch (NumberFormatException e) {
            throw new NoValidNumberException(
                    String.format(NO_VALID_NUMBER_MESSAGE_FORMAT, PRICE),
                    String.format(PATH_FORMAT, base, item, PRICE_FIELD)
            );
        }
    }

    private Dish returnDish(FindableItem findableItem, DishInputDTO dishInputDTO, boolean withUpdate) {
        return (withUpdate && findableItem instanceof Dish dish ?
                converterService.convert(dishInputDTO, dish) :
                converterService.convert(dishInputDTO));
    }

    private Menu returnMenu(FindableItem findableItem, MenuInputDTO menuInputDTO, boolean withUpdate) {
        return (withUpdate ?
                converterService.convert(menuInputDTO, (Menu) findableItem) :
                converterService.convert(menuInputDTO));
    }

    private DisplayCategory returnDisplayCategory(FindableItem findableItem, DisplayCategoryInputDTO displayCategoryInputDTO, boolean withUpdate) {
        return (withUpdate ?
                converterService.convert(displayCategoryInputDTO, (DisplayCategory) findableItem) :
                converterService.convert(displayCategoryInputDTO));
    }

    private T returnValueByTypeOrThrow(FindableItem findableItem, Object inputDTO, boolean withUpdate) throws IllegalArgumentException {
        if (inputDTO instanceof DishInputDTO dishInputDTO && (findableItem instanceof Dish || findableItem == null)) {
            //noinspection unchecked
            return (T) returnDish(findableItem, dishInputDTO, withUpdate);
        }
        if (inputDTO instanceof MenuInputDTO && (findableItem == null || findableItem instanceof Menu)) {
            //noinspection unchecked
            return (T) returnMenu(findableItem, (MenuInputDTO) inputDTO, withUpdate);
        }
        if (inputDTO instanceof DisplayCategoryInputDTO && (findableItem == null || findableItem instanceof DisplayCategory)) {
            //noinspection unchecked
            return (T) returnDisplayCategory(findableItem, (DisplayCategoryInputDTO) inputDTO, withUpdate);
        }
        throw new IllegalArgumentException("Unbekannter InputDTO-Typ!");
    }

    public T validatedItemOrThrow(Class<T> theClass, String itemName, String base, Object inputDTO, String id, String item) {
        return validatedItemOrThrow(theClass, itemName, base, inputDTO, id, item, false);
    }

    /**
     * Validates the given inputDTO and returns the corresponding item.
     *
     * @param theClass     The class of the item to validate.
     * @param itemName     The name of the item to validate.
     * @param base         The base path of the item.
     * @param inputDTO     The inputDTO to validate.
     * @param id           The ID of the item to validate.
     * @throws IllegalArgumentException If the given inputDTO is invalid.
    */
    public T validatedItemOrThrow(Class<T> theClass, String itemName, String base, Object inputDTO, String id, String item, boolean withUpdate) throws IllegalArgumentException {
        T findableItem = null;
        if (id != null) {
            findableItem = repository.findById(id, theClass).orElseThrow(
                    () -> new NotFoundException(
                            String.format(ITEM_NOT_FOUND_MESSAGE_FORMAT, itemName, id),
                            String.format(PATH_FORMAT, base, item, "")
                    )
            );
        }
        if (inputDTO == null) {
            return findableItem;
        }
        if (inputDTO instanceof NamedDTO namedDTO) {
            validateNameOrThrow(namedDTO, base, item);
        }
        if (inputDTO instanceof PricedDTO pricedDTO) {
            validatePriceOrThrow(pricedDTO, base, item);
        }
        return returnValueByTypeOrThrow(findableItem, inputDTO, withUpdate);
    }


}
