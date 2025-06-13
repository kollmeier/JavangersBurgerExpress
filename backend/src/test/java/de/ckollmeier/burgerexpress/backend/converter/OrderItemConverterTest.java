package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.OrderItemInputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderItemConverter")
class OrderItemConverterTest {

    @Nested
    @DisplayName("convert(OrderItemInputDTO, Function<String, OrderableItem>)")
    class ConvertSingleOrderItemInputDTO {

        @Test
        @DisplayName("converts OrderItemInputDTO to OrderItem")
        void should_convertOrderItemInputDTO_toOrderItem() {
            // Given
            OrderItemInputDTO orderItemInputDTO = new OrderItemInputDTO(null, "item-1", 2);

            // Mock OrderableItem resolver
            Function<String, OrderableItem> itemResolver = mock(Function.class);
            OrderableItem orderableItem = mock(OrderableItem.class);
            when(itemResolver.apply("item-1")).thenReturn(orderableItem);

            // When
            OrderItem result = OrderItemConverter.convert(orderItemInputDTO, itemResolver);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getItem()).isEqualTo(orderableItem);
            assertThat(result.getAmount()).isEqualTo(2);
            verify(itemResolver).apply("item-1");
        }
    }

    @Nested
    @DisplayName("convert(List<OrderItemInputDTO>, Function<String, OrderableItem>)")
    class ConvertListOfOrderItemInputDTO {

        @Test
        @DisplayName("converts list of OrderItemInputDTO to list of OrderItem")
        void should_convertListOfOrderItemInputDTO_toListOfOrderItem() {
            // Given
            List<OrderItemInputDTO> orderItemInputDTOs = List.of(
                    new OrderItemInputDTO(null, "item-1", 2),
                    new OrderItemInputDTO(null, "item-2", 3)
            );

            // Mock OrderableItem resolver
            Function<String, OrderableItem> itemResolver = mock(Function.class);

            OrderableItem orderableItem1 = mock(OrderableItem.class);
            when(itemResolver.apply("item-1")).thenReturn(orderableItem1);

            OrderableItem orderableItem2 = mock(OrderableItem.class);
            when(itemResolver.apply("item-2")).thenReturn(orderableItem2);

            // When
            List<OrderItem> result = OrderItemConverter.convert(orderItemInputDTOs, itemResolver);

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasSize(2);

            assertThat(result.get(0).getItem()).isEqualTo(orderableItem1);
            assertThat(result.get(0).getAmount()).isEqualTo(2);

            assertThat(result.get(1).getItem()).isEqualTo(orderableItem2);
            assertThat(result.get(1).getAmount()).isEqualTo(3);

            verify(itemResolver).apply("item-1");
            verify(itemResolver).apply("item-2");
        }
    }
}
