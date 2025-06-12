package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.OrderInputDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderItemInputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.model.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderConverter")
class OrderConverterTest {

    @Nested
    @DisplayName("convert(OrderInputDTO, Function<String, OrderableItem>)")
    class ConvertOrderInputDTO {

        @Test
        @DisplayName("converts OrderInputDTO to Order")
        void should_convertOrderInputDTO_toOrder() {
            // Given
            OrderInputDTO orderInputDTO = new OrderInputDTO("order-1", List.of(
                    new OrderItemInputDTO("item-1", 2)
            ));

            // Mock OrderableItem resolver
            Function<String, OrderableItem> itemResolver = mock(Function.class);
            OrderableItem orderableItem = mock(OrderableItem.class);
            when(orderableItem.getId()).thenReturn("item-1");
            when(orderableItem.getName()).thenReturn("Test Item");
            when(orderableItem.getPrice()).thenReturn(BigDecimal.valueOf(10.99));
            when(itemResolver.apply("item-1")).thenReturn(orderableItem);

            // Mock OrderItemConverter
            List<OrderItem> convertedItems = List.of(
                    OrderItem.builder()
                            .item(orderableItem)
                            .amount(2)
                            .build()
            );

            try (MockedStatic<OrderItemConverter> orderItemConverterMock = mockStatic(OrderItemConverter.class)) {
                orderItemConverterMock.when(() -> OrderItemConverter.convert(eq(orderInputDTO.items()), eq(itemResolver)))
                        .thenReturn(convertedItems);

                // When
                Order result = OrderConverter.convert(orderInputDTO, itemResolver);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo("order-1");
                orderItemConverterMock.verify(() -> OrderItemConverter.convert(eq(orderInputDTO.items()), eq(itemResolver)));
            }
        }
    }

    @Nested
    @DisplayName("convert(OrderInputDTO, Order, Function<String, OrderableItem>)")
    class ConvertOrderInputDTOWithExistingOrder {

        @Test
        @DisplayName("updates existing Order with OrderInputDTO")
        void should_updateExistingOrder_withOrderInputDTO() {
            // Given
            OrderInputDTO orderInputDTO = new OrderInputDTO("order-1", List.of(
                    new OrderItemInputDTO("item-1", 2)
            ));

            // Create existing Order
            Order existingOrder = Order.builder()
                    .id("order-1")
                    .build();

            // Mock OrderableItem resolver
            Function<String, OrderableItem> itemResolver = mock(Function.class);
            OrderableItem orderableItem = mock(OrderableItem.class);
            when(orderableItem.getId()).thenReturn("item-1");
            when(orderableItem.getName()).thenReturn("Test Item");
            when(orderableItem.getPrice()).thenReturn(BigDecimal.valueOf(10.99));
            when(itemResolver.apply("item-1")).thenReturn(orderableItem);

            // Mock OrderItemConverter
            List<OrderItem> convertedItems = List.of(
                    OrderItem.builder()
                            .item(orderableItem)
                            .amount(2)
                            .build()
            );

            try (MockedStatic<OrderItemConverter> orderItemConverterMock = mockStatic(OrderItemConverter.class)) {
                orderItemConverterMock.when(() -> OrderItemConverter.convert(eq(orderInputDTO.items()), eq(itemResolver)))
                        .thenReturn(convertedItems);

                // When
                Order result = OrderConverter.convert(orderInputDTO, existingOrder, itemResolver);

                // Then
                assertThat(result).isNotNull();
                orderItemConverterMock.verify(() -> OrderItemConverter.convert(eq(orderInputDTO.items()), eq(itemResolver)));
            }
        }

        @Test
        @DisplayName("keeps existing items when OrderInputDTO items are null")
        void should_keepExistingItems_whenOrderInputDTOItemsAreNull() {
            // Given
            OrderInputDTO orderInputDTO = new OrderInputDTO("order-1", null);

            // Create existing Order with items
            OrderableItem orderableItem = mock(OrderableItem.class);
            when(orderableItem.getId()).thenReturn("item-1");
            when(orderableItem.getName()).thenReturn("Test Item");
            when(orderableItem.getPrice()).thenReturn(BigDecimal.valueOf(10.99));

            OrderItem existingItem = OrderItem.builder()
                    .item(orderableItem)
                    .amount(2)
                    .build();

            Order existingOrder = Order.builder()
                    .id("order-1")
                    .items(List.of(existingItem))
                    .build();

            // Mock OrderableItem resolver
            Function<String, OrderableItem> itemResolver = mock(Function.class);

            // When
            Order result = OrderConverter.convert(orderInputDTO, existingOrder, itemResolver);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getItems()).isEqualTo(existingOrder.getItems());
            verifyNoInteractions(itemResolver);
        }
    }
}