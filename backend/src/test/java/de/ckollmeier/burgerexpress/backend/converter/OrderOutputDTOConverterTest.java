package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.OrderItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderDTOConverter")
class OrderOutputDTOConverterTest {

    @Test
    @DisplayName("convert(Order) converts Order to OrderOutputDTO")
    void should_convertOrder_toOrderOutputDTO() {
        // Given
        Instant now = Instant.now();
        Instant updated = now.plusSeconds(60);

        Order order = Order.builder()
                .id("order-1")
                .items(List.of())
                .createdAt(now)
                .updatedAt(updated)
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItemOutputDTO> convertedItems = List.of();

        try (MockedStatic<OrderItemOutputDTOConverter> orderItemDTOConverterMock = mockStatic(OrderItemOutputDTOConverter.class)) {
            orderItemDTOConverterMock.when(() -> OrderItemOutputDTOConverter.convert(eq(order.getItems())))
                    .thenReturn(convertedItems);

            // When
            OrderOutputDTO result = OrderOutputDTOConverter.convert(order);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo("order-1");
            assertThat(result.items()).isEqualTo(convertedItems);
            assertThat(result.totalPrice()).isEqualTo("0");
            assertThat(result.createdAt()).isEqualTo(DateTimeFormatter.ISO_INSTANT.format(now));
            assertThat(result.updatedAt()).isEqualTo(DateTimeFormatter.ISO_INSTANT.format(updated));
            assertThat(result.status()).isEqualTo("PENDING");

            orderItemDTOConverterMock.verify(() -> OrderItemOutputDTOConverter.convert(eq(order.getItems())));
        }
    }

    @Test
    @DisplayName("convert(Order) handles null createdAt and updatedAt")
    void should_handleNullDates() {
        // Given
        Order order = Order.builder()
                .id("order-1")
                .items(List.of())
                .createdAt(null)
                .updatedAt(null)
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItemOutputDTO> convertedItems = List.of();

        try (MockedStatic<OrderItemOutputDTOConverter> orderItemDTOConverterMock = mockStatic(OrderItemOutputDTOConverter.class)) {
            orderItemDTOConverterMock.when(() -> OrderItemOutputDTOConverter.convert(eq(order.getItems())))
                    .thenReturn(convertedItems);

            // When
            OrderOutputDTO result = OrderOutputDTOConverter.convert(order);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo("order-1");
            assertThat(result.items()).isEqualTo(convertedItems);
            assertThat(result.totalPrice()).isEqualTo("0");
            assertThat(result.createdAt()).isNull();
            assertThat(result.updatedAt()).isNull();
            assertThat(result.status()).isEqualTo("PENDING");

            orderItemDTOConverterMock.verify(() -> OrderItemOutputDTOConverter.convert(eq(order.getItems())));
        }
    }

    @Nested
    @DisplayName("convertFlattened(Order)")
    class ConvertFlattenedOrder {

        @Test
        @DisplayName("convertFlattened(Order) converts Order to OrderOutputDTO with flattened items")
        void should_convertOrderFlattened_toOrderOutputDTO() {
            // Given
            Instant now = Instant.now();
            Instant updated = now.plusSeconds(60);

            Order order = Order.builder()
                    .id("order-1")
                    .items(List.of())
                    .createdAt(now)
                    .updatedAt(updated)
                    .status(OrderStatus.PENDING)
                    .build();

            List<OrderItemOutputDTO> flattenedItems = List.of();

            try (MockedStatic<OrderItemOutputDTOConverter> orderItemDTOConverterMock = mockStatic(OrderItemOutputDTOConverter.class)) {
                orderItemDTOConverterMock.when(() -> OrderItemOutputDTOConverter.convertFlattened(eq(order.getItems())))
                        .thenReturn(flattenedItems);

                // When
                OrderOutputDTO result = OrderOutputDTOConverter.convertFlattened(order);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.id()).isEqualTo("order-1");
                assertThat(result.items()).isEqualTo(flattenedItems);
                assertThat(result.totalPrice()).isEqualTo("0");
                assertThat(result.createdAt()).isEqualTo(DateTimeFormatter.ISO_INSTANT.format(now));
                assertThat(result.updatedAt()).isEqualTo(DateTimeFormatter.ISO_INSTANT.format(updated));
                assertThat(result.status()).isEqualTo("PENDING");

                orderItemDTOConverterMock.verify(() -> OrderItemOutputDTOConverter.convertFlattened(eq(order.getItems())));
            }
        }
    }

    @Nested
    @DisplayName("convert(List<Order>)")
    class ConvertListOfOrders {

        @Test
        @DisplayName("convert(List<Order>) converts list of Orders to list of OrderOutputDTOs")
        void should_convertListOfOrders_toListOfOrderOutputDTOs() {
            // Given
            Instant now = Instant.now();

            Order order1 = Order.builder()
                    .id("order-1")
                    .items(List.of())
                    .createdAt(now)
                    .updatedAt(now)
                    .status(OrderStatus.PENDING)
                    .build();

            Order order2 = Order.builder()
                    .id("order-2")
                    .items(List.of())
                    .createdAt(now)
                    .updatedAt(now)
                    .status(OrderStatus.PAID)
                    .build();

            List<Order> orders = List.of(order1, order2);

            OrderOutputDTO dto1 = new OrderOutputDTO("order-1", 0, List.of(), "0", 
                    DateTimeFormatter.ISO_INSTANT.format(now), 
                    DateTimeFormatter.ISO_INSTANT.format(now), "PENDING");

            OrderOutputDTO dto2 = new OrderOutputDTO("order-2", 0, List.of(), "0", 
                    DateTimeFormatter.ISO_INSTANT.format(now), 
                    DateTimeFormatter.ISO_INSTANT.format(now), "PAID");

            try (MockedStatic<OrderOutputDTOConverter> converterMock = mockStatic(OrderOutputDTOConverter.class)) {
                // Need to call the real method for convert(List<Order>)
                converterMock.when(() -> OrderOutputDTOConverter.convert(any(List.class)))
                        .thenCallRealMethod();

                // Mock the convert(Order) method
                converterMock.when(() -> OrderOutputDTOConverter.convert(order1))
                        .thenReturn(dto1);
                converterMock.when(() -> OrderOutputDTOConverter.convert(order2))
                        .thenReturn(dto2);

                // When
                List<OrderOutputDTO> result = OrderOutputDTOConverter.convert(orders);

                // Then
                assertThat(result).isNotNull();
                assertThat(result).hasSize(2);
                assertThat(result).containsExactly(dto1, dto2);

                converterMock.verify(() -> OrderOutputDTOConverter.convert(order1));
                converterMock.verify(() -> OrderOutputDTOConverter.convert(order2));
            }
        }
    }

    @Nested
    @DisplayName("convertFlattened(List<Order>)")
    class ConvertFlattenedListOfOrders {

        @Test
        @DisplayName("convertFlattened(List<Order>) converts list of Orders to list of flattened OrderOutputDTOs")
        void should_convertListOfOrdersFlattened_toListOfOrderOutputDTOs() {
            // Given
            Instant now = Instant.now();

            Order order1 = Order.builder()
                    .id("order-1")
                    .items(List.of())
                    .createdAt(now)
                    .updatedAt(now)
                    .status(OrderStatus.PENDING)
                    .build();

            Order order2 = Order.builder()
                    .id("order-2")
                    .items(List.of())
                    .createdAt(now)
                    .updatedAt(now)
                    .status(OrderStatus.PAID)
                    .build();

            List<Order> orders = List.of(order1, order2);

            OrderOutputDTO flattenedDto1 = new OrderOutputDTO("order-1", 0, List.of(), "0", 
                    DateTimeFormatter.ISO_INSTANT.format(now), 
                    DateTimeFormatter.ISO_INSTANT.format(now), "PENDING");

            OrderOutputDTO flattenedDto2 = new OrderOutputDTO("order-2", 0, List.of(), "0", 
                    DateTimeFormatter.ISO_INSTANT.format(now), 
                    DateTimeFormatter.ISO_INSTANT.format(now), "PAID");

            try (MockedStatic<OrderOutputDTOConverter> converterMock = mockStatic(OrderOutputDTOConverter.class)) {
                // Need to call the real method for convertFlattened(List<Order>)
                converterMock.when(() -> OrderOutputDTOConverter.convertFlattened(any(List.class)))
                        .thenCallRealMethod();

                // Mock the convertFlattened(Order) method
                converterMock.when(() -> OrderOutputDTOConverter.convertFlattened(order1))
                        .thenReturn(flattenedDto1);
                converterMock.when(() -> OrderOutputDTOConverter.convertFlattened(order2))
                        .thenReturn(flattenedDto2);

                // When
                List<OrderOutputDTO> result = OrderOutputDTOConverter.convertFlattened(orders);

                // Then
                assertThat(result).isNotNull();
                assertThat(result).hasSize(2);
                assertThat(result).containsExactly(flattenedDto1, flattenedDto2);

                converterMock.verify(() -> OrderOutputDTOConverter.convertFlattened(order1));
                converterMock.verify(() -> OrderOutputDTOConverter.convertFlattened(order2));
            }
        }
    }
}
