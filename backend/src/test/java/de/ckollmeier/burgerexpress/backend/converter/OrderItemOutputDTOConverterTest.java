package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.OrderItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderableItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderItemDTOConverter")
class OrderItemOutputDTOConverterTest {

    @Nested
    @DisplayName("convert(OrderItem)")
    class ConvertSingleOrderItem {

        @Test
        @DisplayName("converts OrderItem to OrderItemOutputDTO")
        void should_convertOrderItem_toOrderItemOutputDTO() {
            // Given
            OrderableItem orderableItem = mock(OrderableItem.class);
            when(orderableItem.getPrice()).thenReturn(BigDecimal.valueOf(10.99));

            OrderItem orderItem = OrderItem.builder()
                    .item(orderableItem)
                    .amount(2)
                    .build();

            OrderableItemOutputDTO orderableItemOutputDTO = new OrderableItemOutputDTO(
                    "item-1",
                    "Test Item",
                    null,
                    "10.99",
                    "dish",
                    null,
                    List.of("Description"),
                    List.of("Short Description")
            );

            try (MockedStatic<OrderableItemOutputDTOConverter> converterMock = mockStatic(OrderableItemOutputDTOConverter.class)) {
                converterMock.when(() -> OrderableItemOutputDTOConverter.convert(orderableItem))
                        .thenReturn(orderableItemOutputDTO);

                // When
                OrderItemOutputDTO result = OrderItemOutputDTOConverter.convert(orderItem);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.id()).isNull();
                assertThat(result.item()).isEqualTo(orderableItemOutputDTO);
                assertThat(result.amount()).isEqualTo(2);
                assertThat(result.price()).isEqualTo("21,98");

                converterMock.verify(() -> OrderableItemOutputDTOConverter.convert(orderableItem));
            }
        }
    }

    @Nested
    @DisplayName("convert(List<OrderItem>)")
    class ConvertListOfOrderItems {

        @Test
        @DisplayName("converts list of OrderItems to list of OrderItemOutputDTOs")
        void should_convertListOfOrderItems_toListOfOrderItemOutputDTOs() {
            // Given
            OrderableItem orderableItem1 = mock(OrderableItem.class);
            when(orderableItem1.getPrice()).thenReturn(BigDecimal.valueOf(10.99));

            OrderableItem orderableItem2 = mock(OrderableItem.class);
            when(orderableItem2.getPrice()).thenReturn(BigDecimal.valueOf(15.99));

            OrderItem orderItem1 = OrderItem.builder()
                    .item(orderableItem1)
                    .amount(2)
                    .build();
            OrderItem orderItem2 = OrderItem.builder()
                    .item(orderableItem2)
                    .amount(3)
                    .build();

            List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

            OrderableItemOutputDTO orderableItemOutputDTO1 = new OrderableItemOutputDTO(
                    "item-1",
                    "Test Item 1",
                    null,
                    "10.99",
                    "dish",
                    null,
                    List.of("Description 1"),
                    List.of("Short Description 1")
            );

            OrderableItemOutputDTO orderableItemOutputDTO2 = new OrderableItemOutputDTO(
                    "item-2",
                    "Test Item 2",
                    null,
                    "15.99",
                    "dish",
                    null,
                    List.of("Description 2"),
                    List.of("Short Description 2")
            );

            OrderItemOutputDTO orderItemOutputDTO1 = new OrderItemOutputDTO(
                    null,
                    orderableItemOutputDTO1,
                    2,
                    "21,98"
            );

            OrderItemOutputDTO orderItemOutputDTO2 = new OrderItemOutputDTO(
                    null,
                    orderableItemOutputDTO2,
                    3,
                    "47,97"
            );

            try (MockedStatic<OrderItemOutputDTOConverter> converterMock = mockStatic(OrderItemOutputDTOConverter.class, CALLS_REAL_METHODS)) {
                converterMock.when(() -> OrderItemOutputDTOConverter.convert(orderItem1))
                        .thenReturn(orderItemOutputDTO1);
                converterMock.when(() -> OrderItemOutputDTOConverter.convert(orderItem2))
                        .thenReturn(orderItemOutputDTO2);

                // When
                List<OrderItemOutputDTO> result = OrderItemOutputDTOConverter.convert(orderItems);

                // Then
                assertThat(result).isNotNull();
                assertThat(result).hasSize(2);
                assertThat(result).containsExactly(orderItemOutputDTO1, orderItemOutputDTO2);

                converterMock.verify(() -> OrderItemOutputDTOConverter.convert(orderItem1));
                converterMock.verify(() -> OrderItemOutputDTOConverter.convert(orderItem2));
            }
        }
    }
}
