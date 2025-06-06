package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.converter.OrderableItemOutputDTOConverter;
import de.ckollmeier.burgerexpress.backend.dto.OrderableItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.Dish;
import de.ckollmeier.burgerexpress.backend.model.Menu;
import de.ckollmeier.burgerexpress.backend.repository.DishRepository;
import de.ckollmeier.burgerexpress.backend.repository.MenuRepository;
import de.ckollmeier.burgerexpress.backend.types.DishType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderableItemServiceTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private OrderableItemService orderableItemService;

    @Nested
    @DisplayName("getAllOrderableItems()")
    class GetAllOrderableItems {

        @Test
        @DisplayName("Returns list of OrderableItemOutputDTOs when items exist")
        void returnsOrderableItemDTOs() {
            // Given
            Dish dish = Dish.builder()
                    .id("1")
                    .name("Burger")
                    .type(DishType.MAIN)
                    .price(new BigDecimal("5.99"))
                    .position(0)
                    .build();

            Menu menu = Menu.builder()
                    .id("2")
                    .name("Burger Menu")
                    .price(new BigDecimal("8.99"))
                    .position(0)
                    .build();

            when(dishRepository.findAll()).thenReturn(List.of(dish));
            when(menuRepository.findAll()).thenReturn(List.of(menu));

            List<OrderableItem> orderableItems = new ArrayList<>();
            orderableItems.add(dish);
            orderableItems.add(menu);

            List<OrderableItemOutputDTO> expectedDTOs = List.of(
                    new OrderableItemOutputDTO("1", "Burger", null, "5.99", "main", Map.of(), List.of(), List.of()),
                    new OrderableItemOutputDTO("2", "Burger Menu", "0", "8.99", "menu", Map.of(), List.of(), List.of())
            );

            try (MockedStatic<OrderableItemOutputDTOConverter> converterMock = mockStatic(OrderableItemOutputDTOConverter.class)) {
                converterMock.when(() -> OrderableItemOutputDTOConverter.convert(any(List.class))).thenReturn(expectedDTOs);

                // When
                List<OrderableItemOutputDTO> result = orderableItemService.getAllOrderableItems();

                // Then
                assertThat(result).isEqualTo(expectedDTOs);
                verify(dishRepository).findAll();
                verify(menuRepository).findAll();
                converterMock.verify(() -> OrderableItemOutputDTOConverter.convert(any(List.class)));
            }
        }

        @Test
        @DisplayName("Returns empty list when no items exist")
        void returnsEmptyList() {
            // Given
            when(dishRepository.findAll()).thenReturn(Collections.emptyList());
            when(menuRepository.findAll()).thenReturn(Collections.emptyList());

            try (MockedStatic<OrderableItemOutputDTOConverter> converterMock = mockStatic(OrderableItemOutputDTOConverter.class)) {
                converterMock.when(() -> OrderableItemOutputDTOConverter.convert(Collections.emptyList()))
                        .thenReturn(Collections.emptyList());

                // When
                List<OrderableItemOutputDTO> result = orderableItemService.getAllOrderableItems();

                // Then
                assertThat(result).isEmpty();
                verify(dishRepository).findAll();
                verify(menuRepository).findAll();
                converterMock.verify(() -> OrderableItemOutputDTOConverter.convert(Collections.emptyList()));
            }
        }
    }

    @Nested
    @DisplayName("getAllMenus()")
    class GetAllMenus {

        @Test
        @DisplayName("Returns list of OrderableItemOutputDTOs for menus when menus exist")
        void returnsMenuDTOs() {
            // Given
            Menu menu = Menu.builder()
                    .id("2")
                    .name("Burger Menu")
                    .price(new BigDecimal("8.99"))
                    .position(0)
                    .build();

            when(menuRepository.findAll()).thenReturn(List.of(menu));

            List<OrderableItemOutputDTO> expectedDTOs = List.of(
                    new OrderableItemOutputDTO("2", "Burger Menu", "0", "8.99", "menu", Map.of(), List.of(), List.of())
            );

            try (MockedStatic<OrderableItemOutputDTOConverter> converterMock = mockStatic(OrderableItemOutputDTOConverter.class)) {
                converterMock.when(() -> OrderableItemOutputDTOConverter.convert(any(List.class))).thenReturn(expectedDTOs);

                // When
                List<OrderableItemOutputDTO> result = orderableItemService.getAllMenus();

                // Then
                assertThat(result).isEqualTo(expectedDTOs);
                verify(menuRepository).findAll();
                converterMock.verify(() -> OrderableItemOutputDTOConverter.convert(any(List.class)));
            }
        }

        @Test
        @DisplayName("Returns empty list when no menus exist")
        void returnsEmptyList() {
            // Given
            when(menuRepository.findAll()).thenReturn(Collections.emptyList());

            try (MockedStatic<OrderableItemOutputDTOConverter> converterMock = mockStatic(OrderableItemOutputDTOConverter.class)) {
                converterMock.when(() -> OrderableItemOutputDTOConverter.convert(Collections.emptyList()))
                        .thenReturn(Collections.emptyList());

                // When
                List<OrderableItemOutputDTO> result = orderableItemService.getAllMenus();

                // Then
                assertThat(result).isEmpty();
                verify(menuRepository).findAll();
                converterMock.verify(() -> OrderableItemOutputDTOConverter.convert(Collections.emptyList()));
            }
        }
    }

    @Nested
    @DisplayName("getAllDishes()")
    class GetAllDishes {

        @Test
        @DisplayName("Returns list of OrderableItemOutputDTOs for dishes when dishes exist")
        void returnsDishDTOs() {
            // Given
            Dish dish = Dish.builder()
                    .id("1")
                    .name("Burger")
                    .type(DishType.MAIN)
                    .price(new BigDecimal("5.99"))
                    .position(0)
                    .build();

            when(dishRepository.findAll()).thenReturn(List.of(dish));

            List<OrderableItemOutputDTO> expectedDTOs = List.of(
                    new OrderableItemOutputDTO("1", "Burger", null, "5.99", "main", Map.of(), List.of(), List.of())
            );

            try (MockedStatic<OrderableItemOutputDTOConverter> converterMock = mockStatic(OrderableItemOutputDTOConverter.class)) {
                converterMock.when(() -> OrderableItemOutputDTOConverter.convert(any(List.class))).thenReturn(expectedDTOs);

                // When
                List<OrderableItemOutputDTO> result = orderableItemService.getAllDishes();

                // Then
                assertThat(result).isEqualTo(expectedDTOs);
                verify(dishRepository).findAll();
                converterMock.verify(() -> OrderableItemOutputDTOConverter.convert(any(List.class)));
            }
        }

        @Test
        @DisplayName("Returns empty list when no dishes exist")
        void returnsEmptyList() {
            // Given
            when(dishRepository.findAll()).thenReturn(Collections.emptyList());

            try (MockedStatic<OrderableItemOutputDTOConverter> converterMock = mockStatic(OrderableItemOutputDTOConverter.class)) {
                converterMock.when(() -> OrderableItemOutputDTOConverter.convert(Collections.emptyList()))
                        .thenReturn(Collections.emptyList());

                // When
                List<OrderableItemOutputDTO> result = orderableItemService.getAllDishes();

                // Then
                assertThat(result).isEmpty();
                verify(dishRepository).findAll();
                converterMock.verify(() -> OrderableItemOutputDTOConverter.convert(Collections.emptyList()));
            }
        }
    }
}
