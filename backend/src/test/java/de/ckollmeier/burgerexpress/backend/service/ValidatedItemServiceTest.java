package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.dto.DishInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.MenuInputDTO;
import de.ckollmeier.burgerexpress.backend.exceptions.NoValidNumberException;
import de.ckollmeier.burgerexpress.backend.exceptions.NotBlankException;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.repository.GeneralRepository;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ckollmeier.burgerexpress.backend.dto.DisplayCategoryInputDTO;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import java.time.Instant;

class ValidatedItemServiceTest {

    private GeneralRepository<Dish> dishRepository;
    private GeneralRepository<Menu> menuRepository;
    private ConverterService converterService;

    private ValidatedItemService<Dish> dishService;
    private ValidatedItemService<Menu> menuService;

    private GeneralRepository<DisplayCategory> displayCategoryRepository;
    private ValidatedItemService<DisplayCategory> displayCategoryService;

    @BeforeEach
    void setUp() {
        //noinspection unchecked
        dishRepository = mock(GeneralRepository.class);
        //noinspection unchecked
        menuRepository = mock(GeneralRepository.class);
        converterService = mock(ConverterService.class);

        dishService = new ValidatedItemService<>(dishRepository, converterService);
        menuService = new ValidatedItemService<>(menuRepository, converterService);
        // Für DisplayCategory ergänzen:
        //noinspection unchecked
        displayCategoryRepository = mock(GeneralRepository.class);
        displayCategoryService = new ValidatedItemService<>(displayCategoryRepository, converterService);
    }

    @Nested
    class ValidatedItemOrThrowTests {

        @Test
        void validatedItemOrThrow_shouldReturnDish_WhenValidDishInputDTOAndNoId() {
            // Gegeben: Ein gültiger DishInputDTO
            DishInputDTO inputDTO = mock(DishInputDTO.class);
            when(inputDTO.name()).thenReturn("Burger");
            when(inputDTO.price()).thenReturn("4.99");
            Dish expectedDish = Dish.builder()
                    .type(DishType.MAIN)
                    .name("Burger")
                    .price(BigDecimal.TEN).build();
            when(converterService.convert(inputDTO)).thenReturn(expectedDish);

            // Wenn: validatedItemOrThrow aufgerufen wird ohne Id
            Dish result = dishService.validatedItemOrThrow(Dish.class, "Dish", "base", inputDTO, null, "item");

            // Dann: Wird das erwartete Dish-Objekt zurückgegeben
            assertSame(expectedDish, result);
        }

        @Test
        void validatedItemOrThrow_shouldReturnConvertedDishWithUpdate_WhenWithUpdateTrueAndId() {
            // Gegeben: Ein existierendes Dish und ein gültiger DTO
            DishInputDTO inputDTO = mock(DishInputDTO.class);
            Dish existingDish = Dish.builder()
                    .type(DishType.MAIN)
                    .name("Burger")
                    .price(BigDecimal.ONE).build();
            when(inputDTO.name()).thenReturn("Burger");
            when(inputDTO.price()).thenReturn("10.00");
            when(dishRepository.findById("1", Dish.class)).thenReturn(Optional.of(existingDish));
            Dish expectedDish = Dish.builder()
                    .type(DishType.MAIN)
                    .name("Burger")
                    .price(BigDecimal.TEN).build();
            when(converterService.convert(inputDTO, existingDish)).thenReturn(expectedDish);

            // Wenn: validatedItemOrThrow mit withUpdate=true
            Dish result = dishService.validatedItemOrThrow(Dish.class, "Dish", "base", inputDTO, "1", "item", true);

            // Dann: Wird das konvertierte Dish mit Update zurückgegeben
            assertSame(expectedDish, result);
        }

        @Test
        void validatedItemOrThrow_shouldThrowNotFoundException_WhenIdNotFound() {
            // Gegeben: Die Repository liefert kein Objekt zur Id

            // Wenn & Dann: Exception wird geworfen
            NotFoundException ex = assertThrows(NotFoundException.class, () ->
                    dishService.validatedItemOrThrow(Dish.class, "Dish", "base", null, "2", "item")
            );
            assertTrue(ex.getMessage().contains("existiert nicht"));
        }

        @Test
        void validatedItemOrThrow_shouldReturnExistingItem_WhenInputDTOIsNull() {
            // Gegeben: Vorhandenes Dish im Repository
            Dish existingDish = Dish.builder()
                    .type(DishType.MAIN)
                    .name("Burger")
                    .price(BigDecimal.ONE).build();
            when(dishRepository.findById("1", Dish.class)).thenReturn(Optional.of(existingDish));

            // Wenn: inputDTO ist null
            Dish result = dishService.validatedItemOrThrow(Dish.class, "Dish", "base", null, "1", "item");

            // Dann: Wird das vorhandene Dish zurückgegeben
            assertSame(existingDish, result);
        }

        @Test
        void validatedItemOrThrow_shouldThrowNotBlankException_WhenNameIstLeer() {
            // Gegeben: Ein InputDTO mit leerem Namen
            DishInputDTO inputDTO = mock(DishInputDTO.class);
            when(inputDTO.name()).thenReturn("");

            // Wenn & Dann: NotBlankException wird geworfen
            NotBlankException ex = assertThrows(NotBlankException.class, () ->
                    dishService.validatedItemOrThrow(Dish.class, "Dish", "base", inputDTO, null, "item")
            );
            assertTrue(ex.getMessage().contains("darf nicht leer sein"));
        }

        @Test
        void validatedItemOrThrow_shouldThrowNotBlankException_WhenPriceIsBlank() {
            // Gegeben: Preis-String ist leer
            DishInputDTO inputDTO = mock(DishInputDTO.class);
            when(inputDTO.name()).thenReturn("Burger");
            when(inputDTO.price()).thenReturn("");

            // Wenn & Dann: NotBlankException wird geworfen
            NotBlankException ex = assertThrows(NotBlankException.class, () ->
                    dishService.validatedItemOrThrow(Dish.class, "Dish", "base", inputDTO, null, "item")
            );
            assertTrue(ex.getMessage().contains("darf nicht leer sein"));
        }

        @Test
        void validatedItemOrThrow_shouldThrowNoValidNumberException_WhenPriceIsNotANumber() {
            // Gegeben: Preis-String ist keine Zahl
            DishInputDTO inputDTO = mock(DishInputDTO.class);
            when(inputDTO.name()).thenReturn("Burger");
            when(inputDTO.price()).thenReturn("abc");

            // Wenn & Dann: NoValidNumberException wird geworfen
            NoValidNumberException ex = assertThrows(NoValidNumberException.class, () ->
                    dishService.validatedItemOrThrow(Dish.class, "Dish", "base", inputDTO, null, "item")
            );
            assertTrue(ex.getMessage().contains("gültige Zahl"));
        }

        @Test
        void validatedItemOrThrow_shouldThrowNoValidNumberException_WhenPriceIsNegative() {
            // Gegeben: Negativer Preis
            DishInputDTO inputDTO = mock(DishInputDTO.class);
            when(inputDTO.name()).thenReturn("Burger");
            when(inputDTO.price()).thenReturn("-5.00");

            // Wenn & Dann: NoValidNumberException wird geworfen
            NoValidNumberException ex = assertThrows(NoValidNumberException.class, () ->
                    dishService.validatedItemOrThrow(Dish.class, "Dish", "base", inputDTO, null, "item")
            );
            assertTrue(ex.getMessage().contains("positive Zahl"));
        }

        @Test
        void validatedItemOrThrow_shouldThrowIllegalArgumentException_WhenUnknownInputDTOType() {
            // Gegeben: Ein unbekannter Typ eines DTOs
            Object unknownDto = mock(Object.class);

            // Wenn & Dann: IllegalArgumentException wird geworfen
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    dishService.validatedItemOrThrow(Dish.class, "Dish", "base", unknownDto, null, "item")
            );
            assertTrue(ex.getMessage().contains("Unbekannter InputDTO-Typ"));
        }

        @Test
        void validatedItemOrThrow_shouldReturnMenu_WhenValidMenuInputDTO() {
            // Gegeben: Ein gültiger MenuInputDTO
            MenuInputDTO inputDTO = mock(MenuInputDTO.class);
            when(inputDTO.name()).thenReturn("Mittagsmenü");
            when(inputDTO.price()).thenReturn("10,00");
            Menu expectedMenu = Menu.builder()
                    .name("Mittagsmenü")
                    .price(BigDecimal.TEN)
                    .build();
            when(converterService.convert(inputDTO)).thenReturn(expectedMenu);

            // Wenn: validatedItemOrThrow für Menu
            Menu result = menuService.validatedItemOrThrow(Menu.class, "Menu", "base", inputDTO, null, "item");
            assertSame(expectedMenu, result);
        }
    }
    @Nested
    class ReturnValueByTypeOrThrowTests {

        @Test
        void returnValueByTypeOrThrow_shouldReturnDish_WhenInputIsDishInputDTO() {
            // Gegeben: DishInputDTO und kein bestehendes Item
            DishInputDTO inputDTO = mock(DishInputDTO.class);
            when(inputDTO.name()).thenReturn("Burger");
            when(inputDTO.price()).thenReturn("10.00");
            Dish expectedDish = Dish.builder()
                    .type(DishType.MAIN)
                    .name("Burger")
                    .price(BigDecimal.ONE).build();
            when(converterService.convert(inputDTO)).thenReturn(expectedDish);

            // Wenn: Methode direkt über validatedItemOrThrow verwendet wird
            Dish result = dishService.validatedItemOrThrow(Dish.class, "Dish", "base", inputDTO, null, "item");

            // Dann: Dish wird übergeben
            assertSame(expectedDish, result);
        }

        @Test
        void returnValueByTypeOrThrow_shouldReturnDishWithUpdate_WhenInputIsDishInputDTOAndFindableItemGiven() {
            // Gegeben: DTO, bestehendes Dish und withUpdate=true
            DishInputDTO inputDTO = mock(DishInputDTO.class);
            when(inputDTO.name()).thenReturn("Burger");
            when(inputDTO.price()).thenReturn("10.00");
            Dish existingDish = Dish.builder()
                    .type(DishType.MAIN)
                    .name("Burger")
                    .price(BigDecimal.ONE).build();
            when(dishRepository.findById("1", Dish.class)).thenReturn(Optional.of(existingDish));
            Dish expectedDish = Dish.builder()
                    .type(DishType.MAIN)
                    .name("Burger")
                    .price(BigDecimal.ONE).build();
            when(converterService.convert(inputDTO, existingDish)).thenReturn(expectedDish);

            // Wenn: validatedItemOrThrow mit Update
            Dish result = dishService.validatedItemOrThrow(Dish.class, "Dish", "base", inputDTO, "1", "item", true);

            // Dann: convert(DTO, existingDish) wird benutzt
            assertSame(expectedDish, result);
        }

        @Test
        void returnValueByTypeOrThrow_shouldReturnMenu_WhenInputIsMenuInputDTO() {
            // Gegeben: MenuInputDTO und kein bestehendes Item
            MenuInputDTO inputDTO = mock(MenuInputDTO.class);
            when(inputDTO.name()).thenReturn("Mittagsmenü");
            when(inputDTO.price()).thenReturn("10,00");
            Menu expectedMenu = Menu.builder()
                    .name("Mittagsmenü")
                    .price(BigDecimal.TEN)
                    .build();
            when(converterService.convert(inputDTO)).thenReturn(expectedMenu);

            // Wenn: validatedItemOrThrow
            Menu result = menuService.validatedItemOrThrow(Menu.class, "Menu", "base", inputDTO, null, "item");
            assertSame(expectedMenu, result);
        }

        @Test
        void returnValueByTypeOrThrow_shouldReturnMenuWithUpdate_WhenInputIsMenuInputDTOAndFindableItemGiven() {
            // Gegeben: DTO, bestehendes Menu und withUpdate=true
            MenuInputDTO inputDTO = mock(MenuInputDTO.class);
            when(inputDTO.name()).thenReturn("Mittagsmenü");
            when(inputDTO.price()).thenReturn("10,00");
            Menu existingMenu = Menu.builder()
                    .id("3")
                    .name("Mittagsmenü")
                    .price(BigDecimal.TEN)
                    .build();
            when(menuRepository.findById("3", Menu.class)).thenReturn(Optional.of(existingMenu));
            Menu expectedMenu = Menu.builder()
                    .name("Mittagsmenü")
                    .price(BigDecimal.TEN)
                    .build();
            when(converterService.convert(inputDTO, existingMenu)).thenReturn(expectedMenu);

            // Wenn: validatedItemOrThrow mit Update
            Menu result = menuService.validatedItemOrThrow(Menu.class, "Menu", "base", inputDTO, "3", "item", true);
            assertSame(expectedMenu, result);
        }
    }

    @Nested
    class ValidateNameOrThrowAndValidatePriceOrThrow {

        @Test
        void validateNameOrThrow_shouldThrowNotBlankException_whenNameIsBlank() {
            // Gegeben: NamedDTO mit leerem Namen
            DishInputDTO inputDTO = mock(DishInputDTO.class);
            when(inputDTO.name()).thenReturn("");

            // Wenn & Dann: NotBlankException wird geworfen (Methode indirekt über validatedItemOrThrow)
            NotBlankException ex = assertThrows(NotBlankException.class, () ->
                dishService.validatedItemOrThrow(Dish.class, "Dish", "base", inputDTO, null, "item")
            );
            assertTrue(ex.getMessage().contains("darf nicht leer sein"));
        }

        @Test
        void validatePriceOrThrow_shouldThrowNotBlankException_whenPriceIsBlank() {
            // Gegeben: PricedDTO mit leerem Preis
            DishInputDTO inputDTO = mock(DishInputDTO.class);
            when(inputDTO.name()).thenReturn("Testburger");
            when(inputDTO.price()).thenReturn("");

            // Wenn & Dann: NotBlankException wird geworfen
            NotBlankException ex = assertThrows(NotBlankException.class, () ->
                dishService.validatedItemOrThrow(Dish.class, "Dish", "base", inputDTO, null, "item")
            );
            assertTrue(ex.getMessage().contains("darf nicht leer sein"));
        }

        @Test
        void validatePriceOrThrow_shouldThrowNoValidNumberException_whenPriceIsNaN() {
            // Gegeben: PricedDTO mit ungültigem Preis
            DishInputDTO inputDTO = mock(DishInputDTO.class);
            when(inputDTO.name()).thenReturn("Burger");
            when(inputDTO.price()).thenReturn("unfug");

            // Wenn & Dann: NoValidNumberException wird geworfen
            NoValidNumberException ex = assertThrows(NoValidNumberException.class, () ->
                dishService.validatedItemOrThrow(Dish.class, "Dish", "base", inputDTO, null, "item")
            );
            assertTrue(ex.getMessage().contains("gültige Zahl"));
        }

        @Test
        void validatePriceOrThrow_shouldThrowNoValidNumberException_whenPriceIsNegative() {
            // Gegeben: Negativer Preis
            DishInputDTO inputDTO = mock(DishInputDTO.class);
            when(inputDTO.name()).thenReturn("Burger");
            when(inputDTO.price()).thenReturn("-10.00");

            // Wenn & Dann: NoValidNumberException wird geworfen
            NoValidNumberException ex = assertThrows(NoValidNumberException.class, () ->
                dishService.validatedItemOrThrow(Dish.class, "Dish", "base", inputDTO, null, "item")
            );
            assertTrue(ex.getMessage().contains("positive Zahl"));
        }
    }

    @Nested
    class ValidatedItemOrThrowDisplayCategoryTests {

        @Test
        void validatedItemOrThrow_shouldReturnDisplayCategory_WhenValidInputDTOAndNoId() {
            // Gegeben
            DisplayCategoryInputDTO inputDTO = new DisplayCategoryInputDTO(
                    "Snacks", "Kleine Snacks", "http://image", true
            );
            DisplayCategory expected = DisplayCategory.builder()
                    .id("cat1")
                    .name("Snacks")
                    .description("Kleine Snacks")
                    .imageUrl("http://image")
                    .published(true)
                    .createdAt(Instant.now())
                    .build();
            when(converterService.convert(inputDTO)).thenReturn(expected);

            // Wenn
            DisplayCategory result = displayCategoryService.validatedItemOrThrow(
                    DisplayCategory.class, "Kategorie", "categories", inputDTO, null, "display-category"
            );

            // Dann
            assertSame(expected, result);
        }

        @Test
        void validatedItemOrThrow_shouldReturnDisplayCategoryWithUpdate_WhenWithUpdateTrueAndId() {
            // Gegeben
            DisplayCategoryInputDTO inputDTO = new DisplayCategoryInputDTO(
                    "Pasta", "lecker", "http://pasta", false
            );
            DisplayCategory existing = DisplayCategory.builder()
                    .id("123")
                    .name("Old Pasta")
                    .description("alt")
                    .published(false)
                    .createdAt(Instant.now())
                    .build();
            when(displayCategoryRepository.findById("123", DisplayCategory.class)).thenReturn(Optional.of(existing));
            DisplayCategory updated = DisplayCategory.builder()
                    .id("123")
                    .name("Pasta")
                    .description("lecker")
                    .imageUrl("http://pasta")
                    .published(false)
                    .createdAt(existing.getCreatedAt())
                    .build();
            when(converterService.convert(inputDTO, existing)).thenReturn(updated);

            // Wenn
            DisplayCategory result = displayCategoryService.validatedItemOrThrow(
                    DisplayCategory.class, "Kategorie", "categories", inputDTO, "123", "display-category", true
            );

            // Dann
            assertSame(updated, result);
        }

        @Test
        void validatedItemOrThrow_shouldThrowNotFoundException_WhenIdNotFound() {
            // Gegeben: Repository liefert nichts
            when(displayCategoryRepository.findById("42", DisplayCategory.class)).thenReturn(Optional.empty());

            // Wenn & Dann
            NotFoundException ex = assertThrows(NotFoundException.class, () ->
                displayCategoryService.validatedItemOrThrow(
                        DisplayCategory.class, "Kategorie", "categories", null, "42", "display-category"
                )
            );
            assertTrue(ex.getMessage().contains("existiert nicht"));
        }

        @Test
        void validatedItemOrThrow_shouldReturnExistingItem_WhenInputDTOIsNull() {
            // Gegeben
            DisplayCategory existing = DisplayCategory.builder()
                    .id("88")
                    .name("Frühstück")
                    .description("Morgens")
                    .createdAt(Instant.now())
                    .build();
            when(displayCategoryRepository.findById("88", DisplayCategory.class)).thenReturn(Optional.of(existing));

            // Wenn
            DisplayCategory result = displayCategoryService.validatedItemOrThrow(
                    DisplayCategory.class, "Kategorie", "categories", null, "88", "display-category"
            );

            // Dann
            assertSame(existing, result);
        }

        @Test
        void validatedItemOrThrow_shouldThrowNotBlankException_WhenNameIsBlank() {
            // Gegeben
            DisplayCategoryInputDTO inputDTO = new DisplayCategoryInputDTO(
                    "", "desc", "img", true
            );
            // Wenn & Dann
            NotBlankException ex = assertThrows(NotBlankException.class, () ->
                displayCategoryService.validatedItemOrThrow(DisplayCategory.class, "Kategorie", "categories", inputDTO, null, "display-category")
            );
            assertTrue(ex.getMessage().contains("darf nicht leer sein"));
        }
    }
}