package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.DishOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.DishInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.DishOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private ValidatedItemService<Dish> validatedDishService;

    @InjectMocks
    private DishService dishService;

    @Nested
    @DisplayName("getAllDishes()")
    class GetAllDishes {

        @Test
        @DisplayName("Gibt sortierte Liste von DishOutputDTOs zurück, wenn Gerichte existieren")
        void returnsSortedDTOs() {
            // Given
            Dish dish1 = Dish.builder()
                    .id("1")
                    .name("Burger")
                    .type(DishType.MAIN)
                    .price(new BigDecimal("5.99"))
                    .position(0)
                    .build();
            Dish dish2 = Dish.builder()
                    .id("2")
                    .name("Fries")
                    .type(DishType.MAIN)
                    .price(new BigDecimal("2.49"))
                    .position(1)
                    .build();
            List<Dish> dishes = List.of(dish1, dish2);

            when(dishRepository.findAllByOrderByPositionAscCreatedAtDesc()).thenReturn(dishes);

            List<DishOutputDTO> expectedDTOs = List.of(
                    new DishOutputDTO("1",
                            "Burger",
                            "5,99",
                            "main", // ggf. Typ oder Extras, siehe DishOutputDTO
                            Map.of(),
                            null),
                    new DishOutputDTO("2",
                            "Fries",
                            "2,49",
                            "main",
                            Map.of(),
                            null)
            );

            try (MockedStatic<DishOutputDTOConverter> converterMock = mockStatic(DishOutputDTOConverter.class)) {
                converterMock.when(() -> DishOutputDTOConverter.convert(dishes)).thenReturn(expectedDTOs);

                // When
                List<DishOutputDTO> result = dishService.getAllDishes();

                // Then
                assertThat(result).isEqualTo(expectedDTOs);
                verify(dishRepository).findAllByOrderByPositionAscCreatedAtDesc();
                converterMock.verify(() -> DishOutputDTOConverter.convert(dishes));
            }
        }

        @Test
        @DisplayName("Gibt leere Liste zurück, wenn keine Gerichte existieren")
        void returnsEmptyList() {
            // Given
            when(dishRepository.findAllByOrderByPositionAscCreatedAtDesc()).thenReturn(Collections.emptyList());

            try (MockedStatic<DishOutputDTOConverter> converterMock = mockStatic(DishOutputDTOConverter.class)) {
                converterMock.when(() -> DishOutputDTOConverter.convert(Collections.emptyList()))
                        .thenReturn(Collections.emptyList());

                // When
                List<DishOutputDTO> result = dishService.getAllDishes();

                // Then
                assertThat(result).isEmpty();
                verify(dishRepository).findAllByOrderByPositionAscCreatedAtDesc();
                converterMock.verify(() -> DishOutputDTOConverter.convert(Collections.emptyList()));
            }
        }
    }

    @Nested
    @DisplayName("addDish(final DishInputDTO)")
    class AddDish {

        @Test
        @DisplayName("Konvertiert DishInputDTO und speichert Gericht, gibt DTO zurück")
        void addDishByDTO_returnsDTO() {
            // Given
            DishInputDTO input = new DishInputDTO("Burger", "5,99", null, Map.of(), null);
            Dish validated = Dish.builder()
                    .id(null)
                    .name("Burger")
                    .type(DishType.MAIN)
                    .price(new BigDecimal("5.99"))
                    .position(0)
                    .build();
            Dish dishWithId = Dish.builder()
                    .id("uuid-1")
                    .name("Burger")
                    .type(DishType.MAIN)
                    .price(new BigDecimal("5.99"))
                    .position(0)
                    .build();

            when(validatedDishService.validatedItemOrThrow(eq(Dish.class), any(), any(), eq(input), isNull(), eq("add")))
                    .thenReturn(validated);

            when(dishRepository.save(any(Dish.class))).thenReturn(dishWithId);

            DishOutputDTO dto = new DishOutputDTO("uuid-1", "Burger", "5,99", null, Map.of(), null);

            try (MockedStatic<DishOutputDTOConverter> converterMock = mockStatic(DishOutputDTOConverter.class)) {
                converterMock.when(() -> DishOutputDTOConverter.convert(dishWithId)).thenReturn(dto);

                // When
                DishOutputDTO result = dishService.addDish(input);

                // Then
                assertThat(result).isEqualTo(dto);
                verify(dishRepository).save(argThat(d -> d.getName().equals("Burger")));
                converterMock.verify(() -> DishOutputDTOConverter.convert(dishWithId));
            }
        }

        @Test
        @DisplayName("Wirft Exception, wenn validatedDishService wirft")
        void addDishByDTO_throwsException() {
            // Given
            DishInputDTO input = new DishInputDTO("Burger", "5,99", null, Map.of(), null);

            when(validatedDishService.validatedItemOrThrow(any(), any(), any(), any(), any(), eq("add")))
                    .thenThrow(new IllegalArgumentException("Test-Fehler"));

            // Then
            assertThatThrownBy(() -> dishService.addDish(input))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Test-Fehler");
        }
    }

    @Nested
    @DisplayName("updateDish(final String, final DishInputDTO)")
    class UpdateDish {

        @Test
        @DisplayName("Aktualisiert Gericht anhand ID und DishInputDTO und gibt DishOutputDTO zurück")
        void updatesAndReturnsDTO() {
            // Given
            String id = "1";
            DishInputDTO input = new DishInputDTO("Veggie Burger", "6,99", null, Map.of(), null);
            Dish validated = Dish.builder()
                    .id(id)
                    .name("Veggie Burger")
                    .type(DishType.MAIN)
                    .price(new BigDecimal("6.99"))
                    .position(1)
                    .build();

            Dish saved = Dish.builder()
                    .id(id)
                    .name("Veggie Burger")
                    .type(DishType.MAIN)
                    .price(new BigDecimal("6.99"))
                    .position(1)
                    .build();

            when(validatedDishService.validatedItemOrThrow(eq(Dish.class), any(), any(), eq(input), eq(id), eq("update"), eq(true)))
                    .thenReturn(validated);

            when(dishRepository.save(any(Dish.class))).thenReturn(saved);

            DishOutputDTO dto = new DishOutputDTO(id, "Veggie Burger", "6,99", null, Map.of(), null);

            try (MockedStatic<DishOutputDTOConverter> converterMock = mockStatic(DishOutputDTOConverter.class)) {
                converterMock.when(() -> DishOutputDTOConverter.convert(saved)).thenReturn(dto);

                // When
                DishOutputDTO result = dishService.updateDish(id, input);

                // Then
                assertThat(result).isEqualTo(dto);
                verify(dishRepository).save(validated);
                converterMock.verify(() -> DishOutputDTOConverter.convert(saved));
            }
        }

        @Test
        @DisplayName("Wirft Exception, wenn validatedDishService throws")
        void updateDishByIdAndInputDto_nonexistent_throwsException() {
            // Given
            String id = "1";
            DishInputDTO input = new DishInputDTO("x", "1,99", null, Map.of(), null);

            when(validatedDishService.validatedItemOrThrow(any(), any(), any(), any(), any(), eq("update"), eq(true)))
                    .thenThrow(new NoSuchElementException("Not found"));

            // Then
            assertThatThrownBy(() -> dishService.updateDish(id, input))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Not found");
        }
    }

    @Nested
    @DisplayName("removeDish(final String)")
    class RemoveDish {

        @Test
        @DisplayName("Entfernt ein existierendes Gericht erfolgreich")
        void shouldRemoveDish_whenDishExists() {
            // Given
            String id = "1";
            when(validatedDishService.validatedItemOrThrow(eq(Dish.class), any(), any(), isNull(), eq(id), eq("delete")))
                    .thenReturn(Dish.builder()
                            .id(id)
                            .name("Test")
                            .type(DishType.MAIN)
                            .price(BigDecimal.TEN)
                            .build());

            // When
            dishService.removeDish(id);

            // Then
            verify(dishRepository).deleteById(id);
        }

        @Test
        @DisplayName("Wirft Exception, wenn validatedDishService wirft")
        void shouldThrowException_whenDishDoesNotExist() {
            // Given
            String id = "1";
            when(validatedDishService.validatedItemOrThrow(any(), any(), any(), isNull(), eq(id), eq("delete")))
                    .thenThrow(new NoSuchElementException("Nicht gefunden"));

            // Then
            assertThatThrownBy(() -> dishService.removeDish(id))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Nicht gefunden");
        }
    }
}