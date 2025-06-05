package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.dto.*;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.DisplayCategory;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConverterService")
class ConverterServiceTest {

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private ConverterService converterService;

    private DishInputDTO dishInputDTO;
    private Dish dish;
    private MenuInputDTO menuInputDTO;
    private Menu menu;
    private DisplayCategoryInputDTO displayCategoryInputDTO;
    private DisplayCategory displayCategory;

    @BeforeEach
    void setUp() {
        // Setup test data
        dishInputDTO = new DishInputDTO(
                "MAIN",
                "Test Dish",
                "9.99",
                Collections.emptyMap(),
                "https://example.com/dish.jpg"
        );

        dish = Dish.builder()
                .id("test-dish-id")
                .name("Test Dish")
                .type(de.ckollmeier.burgerexpress.backend.types.DishType.MAIN)
                .price(new java.math.BigDecimal("9.99"))
                .imageUrl("https://example.com/dish.jpg")
                .build();

        menuInputDTO = new MenuInputDTO(
                "Test Menu",
                "19.99",
                List.of("test-dish-id"),
                Collections.emptyMap()
        );

        menu = Menu.builder()
                .id("test-menu-id")
                .name("Test Menu")
                .price(new java.math.BigDecimal("19.99"))
                .dishes(List.of(dish))
                .build();

        displayCategoryInputDTO = new DisplayCategoryInputDTO(
                "Test Category",
                "Test Description",
                "https://example.com/image.jpg",
                true
        );

        displayCategory = DisplayCategory.builder()
                .id("test-category-id")
                .name("Test Category")
                .description("Test Description")
                .imageUrl("https://example.com/image.jpg")
                .published(true)
                .build();

        // Setup mock behavior
        lenient().when(dishRepository.getReferenceById(anyString())).thenReturn(dish);
    }

    @Nested
    @DisplayName("Dish Conversion")
    class DishConversionTests {

        @Test
        @DisplayName("should convert DishInputDTO to Dish")
        void should_convertDishInputDTOToDish() {
            // Act
            Dish result = converterService.convert(dishInputDTO);

            // Assert
            assertNotNull(result);
            assertEquals(dishInputDTO.name(), result.getName());
            assertEquals(new java.math.BigDecimal(dishInputDTO.price().replace(",", ".")), result.getPrice());
            assertEquals(dishInputDTO.type(), result.getType().name());
            assertEquals(dishInputDTO.imageUrl(), result.getImageUrl());
        }

        @Test
        @DisplayName("should update Dish with DishInputDTO")
        void should_updateDishWithDishInputDTO() {
            // Arrange
            DishInputDTO updateDTO = new DishInputDTO(
                    "MAIN",
                    "Updated Dish",
                    "19.99",
                    Collections.emptyMap(),
                    "https://example.com/updated.jpg"
            );

            // Act
            Dish result = converterService.convert(updateDTO, dish);

            // Assert
            assertNotNull(result);
            assertEquals(updateDTO.name(), result.getName());
            assertEquals(new java.math.BigDecimal(updateDTO.price().replace(",", ".")), result.getPrice());
            assertEquals(updateDTO.type(), result.getType().name());
            assertEquals(updateDTO.imageUrl(), result.getImageUrl());
            assertEquals(dish.getId(), result.getId()); // ID should be preserved
        }

        @Test
        @DisplayName("should convert Dish to DishOutputDTO")
        void should_convertDishToDishOutputDTO() {
            // Act
            DishOutputDTO result = converterService.convert(dish);

            // Assert
            assertNotNull(result);
            assertEquals(dish.getId(), result.id());
            assertEquals(dish.getName(), result.name());
            assertEquals(dish.getPrice().toString(), result.price());
            assertEquals(dish.getType().name().toLowerCase(), result.type());
            assertEquals(dish.getImageUrl(), result.imageUrl());
        }
    }

    @Nested
    @DisplayName("Menu Conversion")
    class MenuConversionTests {

        @Test
        @DisplayName("should convert MenuInputDTO to Menu")
        void should_convertMenuInputDTOToMenu() {
            // Act
            Menu result = converterService.convert(menuInputDTO);

            // Assert
            assertNotNull(result);
            assertEquals(menuInputDTO.name(), result.getName());
            assertEquals(new java.math.BigDecimal(menuInputDTO.price().replace(",", ".")), result.getPrice());
            assertEquals(1, result.getDishes().size());
        }

        @Test
        @DisplayName("should update Menu with MenuInputDTO")
        void should_updateMenuWithMenuInputDTO() {
            // Arrange
            MenuInputDTO updateDTO = new MenuInputDTO(
                    "Updated Menu",
                    "29.99",
                    List.of("test-dish-id"),
                    Collections.emptyMap()
            );

            // Act
            Menu result = converterService.convert(updateDTO, menu);

            // Assert
            assertNotNull(result);
            assertEquals(updateDTO.name(), result.getName());
            assertEquals(new java.math.BigDecimal(updateDTO.price().replace(",", ".")), result.getPrice());
            assertEquals(1, result.getDishes().size());
            assertEquals(menu.getId(), result.getId()); // ID should be preserved
        }

        @Test
        @DisplayName("should convert Menu to MenuOutputDTO")
        void should_convertMenuToMenuOutputDTO() {
            // Act
            MenuOutputDTO result = converterService.convert(menu);

            // Assert
            assertNotNull(result);
            assertEquals(menu.getId(), result.id());
            assertEquals(menu.getName(), result.name());
            assertEquals(menu.getPrice().toString(), result.price());
            assertEquals(menu.getDishes().size(), result.dishes().size());
        }
    }

    @Nested
    @DisplayName("DisplayCategory Conversion")
    class DisplayCategoryConversionTests {

        @Test
        @DisplayName("should convert DisplayCategoryInputDTO to DisplayCategory")
        void should_convertDisplayCategoryInputDTOToDisplayCategory() {
            // Act
            DisplayCategory result = converterService.convert(displayCategoryInputDTO);

            // Assert
            assertNotNull(result);
            assertEquals(displayCategoryInputDTO.name(), result.getName());
            assertEquals(displayCategoryInputDTO.description(), result.getDescription());
            assertEquals(displayCategoryInputDTO.imageUrl(), result.getImageUrl());
            assertEquals(displayCategoryInputDTO.published(), result.isPublished());
        }

        @Test
        @DisplayName("should update DisplayCategory with DisplayCategoryInputDTO")
        void should_updateDisplayCategoryWithDisplayCategoryInputDTO() {
            // Arrange
            DisplayCategoryInputDTO updateDTO = new DisplayCategoryInputDTO(
                    "Updated Category",
                    "Updated Description",
                    "https://example.com/updated.jpg",
                    false
            );

            // Act
            DisplayCategory result = converterService.convert(updateDTO, displayCategory);

            // Assert
            assertNotNull(result);
            assertEquals(updateDTO.name(), result.getName());
            assertEquals(updateDTO.description(), result.getDescription());
            assertEquals(updateDTO.imageUrl(), result.getImageUrl());
            assertEquals(updateDTO.published(), result.isPublished());
            assertEquals(displayCategory.getId(), result.getId()); // ID should be preserved
        }

        @Test
        @DisplayName("should convert DisplayCategory to DisplayCategoryOutputDTO")
        void should_convertDisplayCategoryToDisplayCategoryOutputDTO() {
            // Act
            DisplayCategoryOutputDTO result = converterService.convert(displayCategory);

            // Assert
            assertNotNull(result);
            assertEquals(displayCategory.getId(), result.id());
            assertEquals(displayCategory.getName(), result.name());
            assertEquals(displayCategory.getDescription(), result.description());
            assertEquals(displayCategory.getImageUrl(), result.imageUrl());
            assertEquals(displayCategory.isPublished(), result.published());
        }
    }
}
