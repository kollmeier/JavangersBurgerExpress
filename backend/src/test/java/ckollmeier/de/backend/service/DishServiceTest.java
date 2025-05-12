package ckollmeier.de.backend.service;

import ckollmeier.de.backend.converter.DishConverter;
import ckollmeier.de.backend.converter.DishOutputDTOConverter;
import ckollmeier.de.backend.dto.DishInputDTO;
import ckollmeier.de.backend.dto.DishOutputDTO;
import ckollmeier.de.backend.model.Dish;
import ckollmeier.de.backend.repository.DishRepository;
import ckollmeier.de.backend.types.DishType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DishServiceTest {

    private final DishRepository dishRepository = mock(DishRepository.class);
    private final DishService dishService = new DishService(dishRepository);

    @Test
    @DisplayName("Returns sorted list of DishOutputDTOs when dishes exist")
    void getAllDishes_returnsSortedDTOs() {
        // Given
        Dish dish1 = Dish.builder().id("1").name("Pizza").price(new BigDecimal("10.99")).type(DishType.MAIN).additionalInformation(Collections.emptyMap()).build();
        Dish dish2 = Dish.builder().id("2").name("Fries").price(new BigDecimal("3.50")).type(DishType.SIDE).additionalInformation(Collections.emptyMap()).build();
        List<Dish> dishes = List.of(dish1, dish2);

        when(dishRepository.findAllByOrderByPositionAsc()).thenReturn(dishes);

        List<DishOutputDTO> expectedDTOs = List.of(
                new DishOutputDTO("1", "Pizza", "10,99", DishType.MAIN.toString().toLowerCase(), Collections.emptyMap()),
                new DishOutputDTO("2", "Fries", "3,50", DishType.SIDE.toString().toLowerCase(), Collections.emptyMap())
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
        Dish inputDish = Dish.builder().name("Lemonade").price(new BigDecimal("2.50")).type(DishType.BEVERAGE).additionalInformation(Collections.emptyMap()).build();
        Dish savedDish = inputDish.withId("unique-id-111");
        when(dishRepository.save(any(Dish.class))).thenReturn(savedDish);

        DishOutputDTO expectedDTO = new DishOutputDTO("unique-id-111", "Lemonade", "2,50", DishType.BEVERAGE.toString().toLowerCase(), Collections.emptyMap());

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
        DishInputDTO inputDTO = new DishInputDTO(DishType.BEVERAGE.name(), "Cola", "2.20", Collections.emptyMap());
        DishType type = DishType.BEVERAGE;

        Dish convertedDish = Dish.builder()
                .name("Cola")
                .price(new BigDecimal("2.20"))
                .type(DishType.MAIN)
                .additionalInformation(Collections.emptyMap())
                .build();
        Dish withType = convertedDish.withType(type);
        Dish savedDish = withType.withId("drink-id-212");

        DishOutputDTO expectedDTO = new DishOutputDTO("drink-id-212", "Cola", "2.20", DishType.BEVERAGE.toString().toLowerCase(), Collections.emptyMap());

        try (
            MockedStatic<DishConverter> dishConverterMock = mockStatic(DishConverter.class);
            MockedStatic<DishOutputDTOConverter> dishOutputConverterMock = mockStatic(DishOutputDTOConverter.class)
        ) {
            dishConverterMock.when(() -> DishConverter.convert(inputDTO)).thenReturn(convertedDish);
            when(dishRepository.save(any(Dish.class))).thenReturn(savedDish);
            dishOutputConverterMock.when(() -> DishOutputDTOConverter.convert(savedDish)).thenReturn(expectedDTO);

            // When
            DishOutputDTO result = dishService.addDish(inputDTO, type);

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
        DishInputDTO inputDTO = new DishInputDTO(DishType.MAIN.name(), "", "8.40", Collections.emptyMap());
        DishType type = DishType.MAIN;

        Dish convertedDish = Dish.builder()
                .name("")
                .price(new BigDecimal("8.40"))
                .type(DishType.MAIN)
                .additionalInformation(Collections.emptyMap())
                .build();

        try (MockedStatic<DishConverter> dishConverterMock = mockStatic(DishConverter.class)) {
            dishConverterMock.when(() -> DishConverter.convert(inputDTO)).thenReturn(convertedDish);

            // When / Then
            assertThatThrownBy(() -> dishService.addDish(inputDTO, type))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Dish name cannot be empty");
            verifyNoInteractions(dishRepository);
            dishConverterMock.verify(() -> DishConverter.convert(inputDTO));
        }
    }
}