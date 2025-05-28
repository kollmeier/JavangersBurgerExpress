package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.DishConverter;
import de.ckollmeier.burgerexpress.backend.converter.DishOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DishInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DishOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DishServiceTest {

    private final DishRepository dishRepository = mock(DishRepository.class);
    private final DishService dishService = new DishService(dishRepository);

    @Test
    @DisplayName("Returns sorted list of DishOutputDTOs when dishes exist")
    void getAllDishes_returnsSortedDTOs() {
        // Given
        Dish dish1 = Dish.builder().id("1").name("Pizza").price(new BigDecimal("10.99")).type(DishType.MAIN).additionalInformation(Collections.emptyMap()).imageUrl("test.jpg").build();
        Dish dish2 = Dish.builder().id("2").name("Fries").price(new BigDecimal("3.50")).type(DishType.SIDE).additionalInformation(Collections.emptyMap()).build();
        List<Dish> dishes = List.of(dish1, dish2);

        when(dishRepository.findAllByOrderByPositionAsc()).thenReturn(dishes);

        List<DishOutputDTO> expectedDTOs = List.of(
                new DishOutputDTO("1", "Pizza", "10,99", DishType.MAIN.toString().toLowerCase(), Collections.emptyMap(), "test.jpg"),
                new DishOutputDTO("2", "Fries", "3,50", DishType.SIDE.toString().toLowerCase(), Collections.emptyMap(), null)
        );

        try (MockedStatic<DishOutputDTOConverter> converterMock = mockStatic(DishOutputDTOConverter.class)) {
            converterMock.when(() -> DishOutputDTOConverter.convert(dishes)).thenReturn(expectedDTOs);

            // When
            List<DishOutputDTO> result = dishService.getAllDishes();

            // Then
            assertThat(result).isEqualTo(expectedDTOs);
            verify(dishRepository).findAllByOrderByPositionAsc();
            converterMock.verify(() -> DishOutputDTOConverter.convert(dishes));
        }
    }

    @Test
    @DisplayName("Returns empty list if no dishes exist in the repository")
    void getAllDishes_returnsEmptyList() {
        // Given
        when(dishRepository.findAllByOrderByPositionAsc()).thenReturn(Collections.emptyList());

        try (MockedStatic<DishOutputDTOConverter> converterMock = mockStatic(DishOutputDTOConverter.class)) {
            converterMock.when(() -> DishOutputDTOConverter.convert(Collections.emptyList())).thenReturn(Collections.emptyList());

            // When
            List<DishOutputDTO> result = dishService.getAllDishes();

            // Then
            assertThat(result).isEmpty();
            verify(dishRepository).findAllByOrderByPositionAsc();
            converterMock.verify(() -> DishOutputDTOConverter.convert(Collections.emptyList()));
        }
    }

    @Test
    @DisplayName("Saves a valid dish and returns the DishOutputDTO")
    void addDish_savesDishAndReturnsDTO() {
        // Given
        Dish inputDish = Dish.builder().name("Lemonade").price(new BigDecimal("2.50")).type(DishType.BEVERAGE).additionalInformation(Collections.emptyMap()).imageUrl("test.jpg").build();
        Dish savedDish = inputDish.withId("unique-id-111");
        when(dishRepository.save(any(Dish.class))).thenReturn(savedDish);

        DishOutputDTO expectedDTO = new DishOutputDTO("unique-id-111", "Lemonade", "2,50", DishType.BEVERAGE.toString().toLowerCase(), Collections.emptyMap(), "test.jpg");

        try (MockedStatic<DishOutputDTOConverter> converterMock = mockStatic(DishOutputDTOConverter.class)) {
            converterMock.when(() -> DishOutputDTOConverter.convert(savedDish)).thenReturn(expectedDTO);

            // When
            DishOutputDTO result = dishService.addDish(inputDish);

            // Then
            verify(dishRepository).save(any(Dish.class));
            assertThat(result).isEqualTo(expectedDTO);
            converterMock.verify(() -> DishOutputDTOConverter.convert(savedDish));
        }
    }

    @Test
    @DisplayName("Throws exception when trying to save a dish with empty name")
    void addDish_emptyName_throwsException() {
        // Given
        Dish inputDish = Dish.builder().name("").price(new BigDecimal("5.00")).type(DishType.MAIN).additionalInformation(Collections.emptyMap()).build();

        // When / Then
        assertThatThrownBy(() -> dishService.addDish(inputDish))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Dish name cannot be empty");
        verifyNoInteractions(dishRepository);
    }

    @Test
    @DisplayName("Converts DishInputDTO and stores dish with correct type, returns DTO")
    void addDishByDTOAndType_returnsDTO() {
        // Given
        DishInputDTO inputDTO = new DishInputDTO(DishType.BEVERAGE.name(), "Cola", "2.20", Collections.emptyMap(), null);
        DishType type = DishType.BEVERAGE;

        Dish convertedDish = Dish.builder()
                .name("Cola")
                .price(new BigDecimal("2.20"))
                .type(DishType.BEVERAGE)
                .additionalInformation(Collections.emptyMap())
                .build();
        Dish withType = convertedDish.withType(type);
        Dish savedDish = withType.withId("drink-id-212");

        DishOutputDTO expectedDTO = new DishOutputDTO("drink-id-212", "Cola", "2.20", DishType.BEVERAGE.toString().toLowerCase(), Collections.emptyMap(), null);

        try (
            MockedStatic<DishConverter> dishConverterMock = mockStatic(DishConverter.class);
            MockedStatic<DishOutputDTOConverter> dishOutputConverterMock = mockStatic(DishOutputDTOConverter.class)
        ) {
            dishConverterMock.when(() -> DishConverter.convert(inputDTO)).thenReturn(convertedDish);
            when(dishRepository.save(any(Dish.class))).thenReturn(savedDish);
            dishOutputConverterMock.when(() -> DishOutputDTOConverter.convert(savedDish)).thenReturn(expectedDTO);

            // When
            DishOutputDTO result = dishService.addDish(inputDTO);

            // Then
            verify(dishRepository).save(argThat(d -> d.getType() == type && d.getName().equals("Cola")));
            assertThat(result).isEqualTo(expectedDTO);
            dishConverterMock.verify(() -> DishConverter.convert(inputDTO));
            dishOutputConverterMock.verify(() -> DishOutputDTOConverter.convert(savedDish));
        }
    }

    @Test
    @DisplayName("Throws exception if name from DishInputDTO is empty")
    void addDishByDTOAndType_emptyName_throwsException() {
        // Given
        DishInputDTO inputDTO = new DishInputDTO(DishType.MAIN.name(), "", "8.40", Collections.emptyMap(), null);

        Dish convertedDish = Dish.builder()
                .name("")
                .price(new BigDecimal("8.40"))
                .type(DishType.MAIN)
                .additionalInformation(Collections.emptyMap())
                .build();

        try (MockedStatic<DishConverter> dishConverterMock = mockStatic(DishConverter.class)) {
            dishConverterMock.when(() -> DishConverter.convert(inputDTO)).thenReturn(convertedDish);

            // When / Then
            assertThatThrownBy(() -> dishService.addDish(inputDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Dish name cannot be empty");
            verifyNoInteractions(dishRepository);
            dishConverterMock.verify(() -> DishConverter.convert(inputDTO));
        }
    }

    @Test
    @DisplayName("Aktualisiert existierendes Dish und gibt aktualisiertes DishOutputDTO zurück")
    void updateDish_updatesExistingDishAndReturnsDTO() {
        // Given
        Dish existingDish = Dish.builder().id("dish-123").name("Burger").price(new BigDecimal("6.00")).type(DishType.MAIN).additionalInformation(Collections.emptyMap()).build();
        Dish updatedDish = existingDish.withName("Vegan Burger").withPrice(new BigDecimal("7.00"));

        when(dishRepository.existsById("dish-123")).thenReturn(true);
        when(dishRepository.save(updatedDish)).thenReturn(updatedDish);

        DishOutputDTO expectedDTO = new DishOutputDTO("dish-123", "Vegan Burger", "7,00", DishType.MAIN.toString().toLowerCase(), Collections.emptyMap(), null);

        try (MockedStatic<DishOutputDTOConverter> converterMock = mockStatic(DishOutputDTOConverter.class)) {
            converterMock.when(() -> DishOutputDTOConverter.convert(updatedDish)).thenReturn(expectedDTO);

            // When
            DishOutputDTO result = dishService.updateDish(updatedDish);

            // Then
            verify(dishRepository).existsById("dish-123");
            verify(dishRepository).save(updatedDish);
            assertThat(result).isEqualTo(expectedDTO);
            converterMock.verify(() -> DishOutputDTOConverter.convert(updatedDish));
        }
    }

    @Test
    @DisplayName("Wirft Exception, wenn Dish zu aktualisieren nicht existiert")
    void updateDish_nonexistentDish_throwsException() {
        // Given
        Dish nonExistingDish = Dish.builder().id("notfound-001").name("Rice").price(new BigDecimal("2.00")).type(DishType.SIDE).additionalInformation(Collections.emptyMap()).build();

        when(dishRepository.existsById("notfound-001")).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> dishService.updateDish(nonExistingDish))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Dish not found");
        verify(dishRepository).existsById("notfound-001");
        verify(dishRepository, never()).save(any());
    }

    @Test
    @DisplayName("Aktualisiert Dish anhand ID und DishInputDTO und gibt DishOutputDTO zurück")
    void updateDishByIdAndInputDto_updatesAndReturnsDTO() {
        // Given
        String id = "test-4711";
        DishInputDTO inputDTO = new DishInputDTO(DishType.MAIN.name(), "Wrap", "9.95", Collections.emptyMap(), "image-url-123.jpg");
        Dish convertedDish = Dish.builder()
                .id(id)
                .name("Wrap")
                .price(new BigDecimal("9.95"))
                .type(DishType.MAIN)
                .additionalInformation(Collections.emptyMap())
                .imageUrl("image-url-123.jpg")
                .build();

        when(dishRepository.findById(id)).thenReturn(Optional.of(convertedDish));
        when(dishRepository.existsById(id)).thenReturn(true);
        when(dishRepository.save(convertedDish)).thenReturn(convertedDish);

        DishOutputDTO expectedDTO = new DishOutputDTO(id, "Wrap", "9,95", DishType.MAIN.toString().toLowerCase(), Collections.emptyMap(), "image-url-123.jpg");

        try (
                MockedStatic<DishOutputDTOConverter> converterMock = mockStatic(DishOutputDTOConverter.class)
        ) {
            converterMock.when(() -> DishOutputDTOConverter.convert(convertedDish)).thenReturn(expectedDTO);

            // When
            DishOutputDTO result = dishService.updateDish(id, inputDTO);

            // Then
            verify(dishRepository).findById(id);
            verify(dishRepository).save(convertedDish);
            assertThat(result).isEqualTo(expectedDTO);
            converterMock.verify(() -> DishOutputDTOConverter.convert(convertedDish));
        }
    }

    @Test
    @DisplayName("Wirft Exception, wenn zu aktualisierendes Dish anhand ID und DTO nicht existiert")
    void updateDishByIdAndInputDto_nonexistent_throwsException() {
        // Given
        String id = "notexists-789";
        DishInputDTO inputDTO = new DishInputDTO(DishType.SIDE.name(), "Pommes", "3.90", Collections.emptyMap(), null);

        when(dishRepository.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> dishService.updateDish(id, inputDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Dish not found");
        verify(dishRepository).findById(id);
        verify(dishRepository, never()).save(any());
    }


    @Test
    @DisplayName("Entfernt ein existierendes Dish erfolgreich")
    void removeDish_shouldRemoveDish_whenDishExists() {
        // Given
        String id = "existing-dish-id";
        when(dishRepository.existsById(id)).thenReturn(true);

        // When
        dishService.removeDish(id);

        // Then
        verify(dishRepository).existsById(id);
        verify(dishRepository).deleteById(id);
    }

    @Test
    @DisplayName("Wirft Exception, wenn zu löschendes Dish nicht existiert")
    void removeDish_shouldThrowException_whenDishDoesNotExist() {
        // Given
        String id = "nonexistent-dish-id";
        when(dishRepository.existsById(id)).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> dishService.removeDish(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Dish not found");
        verify(dishRepository).existsById(id);
        verify(dishRepository, never()).deleteById(any());
    }
}