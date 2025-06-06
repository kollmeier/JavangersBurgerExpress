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

    /**
     * Validates that the name field of the DTO is not blank.
     *
     * @param inputDTO The DTO to validate
     * @param base The base path for error message
     * @param item The item name for error message
     * @throws NotBlankException If the name is blank
     */
    private void validateNameOrThrow(NamedDTO inputDTO, String base, String item) {
        if (inputDTO.name().isBlank()) {
            throw new NotBlankException(
                    String.format(NOT_BLANK_MESSAGE_FORMAT, NAME),
                    String.format(PATH_FORMAT, base, item, NAME_FIELD)
            );
        }
    }

    /**
     * Validates that the price field of the DTO is not blank and is a valid positive number.
     *
     * @param inputDTO The DTO to validate
     * @param base The base path for error message
     * @param item The item name for error message
     * @throws NotBlankException If the price is blank
     * @throws NoValidNumberException If the price is not a valid number or is negative
     */
    private void validatePriceOrThrow(PricedDTO inputDTO, String base, String item) {
        if (inputDTO.price().isBlank()) {
            throw new NotBlankException(
                    String.format(NOT_BLANK_MESSAGE_FORMAT, PRICE),
                    String.format(PATH_FORMAT, base, item, PRICE_FIELD)
            );
        }
        try {
            BigDecimal price = new BigDecimal(inputDTO.price().replace(",", "."));
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

    /**
     * Returns a Dish object based on the input DTO and existing item.
     *
     * @param findableItem The existing item, if any
     * @param dishInputDTO The input DTO containing dish data
     * @param withUpdate Whether to update an existing item or create a new one
     * @return A Dish object
     */
    private Dish returnDish(FindableItem findableItem, DishInputDTO dishInputDTO, boolean withUpdate) {
        return (withUpdate && findableItem instanceof Dish dish ?
                converterService.convert(dishInputDTO, dish) :
                converterService.convert(dishInputDTO));
    }

    /**
     * Returns a Menu object based on the input DTO and existing item.
     *
     * @param findableItem The existing item, if any
     * @param menuInputDTO The input DTO containing menu data
     * @param withUpdate Whether to update an existing item or create a new one
     * @return A Menu object
     */
    private Menu returnMenu(FindableItem findableItem, MenuInputDTO menuInputDTO, boolean withUpdate) {
        return (withUpdate && findableItem instanceof Menu menu ?
                converterService.convert(menuInputDTO, menu) :
                converterService.convert(menuInputDTO));
    }

    /**
     * Returns a DisplayCategory object based on the input DTO and existing item.
     *
     * @param findableItem The existing item, if any
     * @param displayCategoryInputDTO The input DTO containing display category data
     * @param withUpdate Whether to update an existing item or create a new one
     * @return A DisplayCategory object
     */
    private DisplayCategory returnDisplayCategory(FindableItem findableItem, DisplayCategoryInputDTO displayCategoryInputDTO, boolean withUpdate) {
        return (withUpdate && findableItem instanceof DisplayCategory displayCategory ?
                converterService.convert(displayCategoryInputDTO, displayCategory) :
                converterService.convert(displayCategoryInputDTO));
    }

    /**
     * Returns a value of type T based on the input DTO type and existing item.
     *
     * @param findableItem The existing item, if any
     * @param inputDTO The input DTO containing item data
     * @param withUpdate Whether to update an existing item or create a new one
     * @return An object of type T
     * @throws IllegalArgumentException If the input DTO type is unknown
     */
    @SuppressWarnings("unchecked")
    private T returnValueByTypeOrThrow(FindableItem findableItem, Object inputDTO, boolean withUpdate) throws IllegalArgumentException {
        if (inputDTO instanceof DishInputDTO dishInputDTO && (findableItem instanceof Dish || findableItem == null)) {
            return (T) returnDish(findableItem, dishInputDTO, withUpdate);
        }
        if (inputDTO instanceof MenuInputDTO menuInputDTO && (findableItem == null || findableItem instanceof Menu)) {
            return (T) returnMenu(findableItem, menuInputDTO, withUpdate);
        }
        if (inputDTO instanceof DisplayCategoryInputDTO displayCategoryInputDTO && (findableItem == null || findableItem instanceof DisplayCategory)) {
            return (T) returnDisplayCategory(findableItem, displayCategoryInputDTO, withUpdate);
        }
        throw new IllegalArgumentException("Unbekannter InputDTO-Typ!");
    }

    /**
     * Validates the given inputDTO and returns the corresponding item.
     * This is a convenience method that calls {@link #validatedItemOrThrow(Class, String, String, Object, String, String, boolean)}
     * with withUpdate set to false.
     *
     * @param theClass     The class of the item to validate.
     * @param itemName     The name of the item to validate.
     * @param base         The base path of the item.
     * @param inputDTO     The inputDTO to validate.
     * @param id           The ID of the item to validate.
     * @param item         The item identifier for error messages.
     * @return             The validated item.
     * @throws IllegalArgumentException If the given inputDTO is invalid.
     * @throws NotFoundException If the item with the given ID does not exist.
     */
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
     * @param item         The item identifier for error messages.
     * @param withUpdate   Whether to update an existing item or create a new one.
     * @return             The validated item.
     * @throws IllegalArgumentException If the given inputDTO is invalid.
     * @throws NotFoundException If the item with the given ID does not exist.
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
