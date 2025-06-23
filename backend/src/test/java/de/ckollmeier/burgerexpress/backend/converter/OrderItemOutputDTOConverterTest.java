package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.OrderItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderableItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.interfaces.BaseAdditionalInformation;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.OrderItem;
import de.ckollmeier.burgerexpress.backend.types.OrderableItemType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

    @Nested
    @DisplayName("convertFlattened(List<OrderItem>)")
    class ConvertFlattenedListOfOrderItems {

        /**
         * Test implementation of OrderableItem for testing the convertFlattened method.
         */
        static class TestOrderableItem implements OrderableItem {
            private final String id;
            private final String name;
            private final BigDecimal price;
            private final OrderableItemType type;
            private final List<TestOrderableItem> subItems;

            TestOrderableItem(String id, String name, BigDecimal price, OrderableItemType type, List<TestOrderableItem> subItems) {
                this.id = id;
                this.name = name;
                this.price = price;
                this.type = type;
                this.subItems = subItems;
            }

            @Override
            public String getId() { return id; }

            @Override
            public String getName() { return name; }

            @Override
            public Map<String, BaseAdditionalInformation> getAdditionalInformation() { return Collections.emptyMap(); }

            @Override
            public Map<String, List<String>> getImageUrls() { return Collections.emptyMap(); }

            @Override
            public BigDecimal getPrice() { return price; }

            @Override
            public BigDecimal getOldPrice() { return null; }

            @Override
            public OrderableItemType getOrderableItemType() { return type; }

            @Override
            public List<? extends OrderableItem> getSubItems() { return subItems; }
        }

        @Test
        @DisplayName("converts list of OrderItems with no sub-items to flattened list of OrderItemOutputDTOs")
        void should_convertListOfOrderItemsWithNoSubItems_toFlattenedList() {
            // Given
            TestOrderableItem item1 = new TestOrderableItem(
                    "item-1", 
                    "Burger", 
                    BigDecimal.valueOf(5.99), 
                    OrderableItemType.MAIN, 
                    Collections.emptyList()
            );

            TestOrderableItem item2 = new TestOrderableItem(
                    "item-2", 
                    "Fries", 
                    BigDecimal.valueOf(2.99), 
                    OrderableItemType.SIDE, 
                    Collections.emptyList()
            );

            OrderItem orderItem1 = OrderItem.builder()
                    .id("order-item-1")
                    .item(item1)
                    .amount(2)
                    .build();

            OrderItem orderItem2 = OrderItem.builder()
                    .id("order-item-2")
                    .item(item2)
                    .amount(1)
                    .build();

            List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

            OrderableItemOutputDTO itemOutputDTO1 = new OrderableItemOutputDTO(
                    "item-1",
                    "Burger",
                    null,
                    "5.99",
                    "main",
                    Collections.emptyMap(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );

            OrderableItemOutputDTO itemOutputDTO2 = new OrderableItemOutputDTO(
                    "item-2",
                    "Fries",
                    null,
                    "2.99",
                    "side",
                    Collections.emptyMap(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );

            try (MockedStatic<OrderableItemOutputDTOConverter> converterMock = mockStatic(OrderableItemOutputDTOConverter.class)) {
                converterMock.when(() -> OrderableItemOutputDTOConverter.convert(item1))
                        .thenReturn(itemOutputDTO1);
                converterMock.when(() -> OrderableItemOutputDTOConverter.convert(item2))
                        .thenReturn(itemOutputDTO2);

                // When
                List<OrderItemOutputDTO> result = OrderItemOutputDTOConverter.convertFlattened(orderItems);

                // Then
                assertThat(result).isNotNull();
                assertThat(result).hasSize(2);

                // Verify item1 is in the result with amount 2
                assertThat(result).anySatisfy(dto -> {
                    assertThat(dto.id()).isEqualTo("item-1");
                    assertThat(dto.item()).isEqualTo(itemOutputDTO1);
                    assertThat(dto.amount()).isEqualTo(2);
                    assertThat(dto.price()).isEqualTo("5,99");
                });

                // Verify item2 is in the result with amount 1
                assertThat(result).anySatisfy(dto -> {
                    assertThat(dto.id()).isEqualTo("item-2");
                    assertThat(dto.item()).isEqualTo(itemOutputDTO2);
                    assertThat(dto.amount()).isEqualTo(1);
                    assertThat(dto.price()).isEqualTo("2,99");
                });

                converterMock.verify(() -> OrderableItemOutputDTOConverter.convert(item1), times(1));
                converterMock.verify(() -> OrderableItemOutputDTOConverter.convert(item2), times(1));
            }
        }

        @Test
        @DisplayName("converts list of OrderItems with sub-items to flattened list of OrderItemOutputDTOs")
        void should_convertListOfOrderItemsWithSubItems_toFlattenedList() {
            // Given
            TestOrderableItem subItem1 = new TestOrderableItem(
                    "sub-item-1", 
                    "Cheese", 
                    BigDecimal.valueOf(1.00), 
                    OrderableItemType.SIDE, 
                    Collections.emptyList()
            );

            TestOrderableItem subItem2 = new TestOrderableItem(
                    "sub-item-2", 
                    "Bacon", 
                    BigDecimal.valueOf(1.50), 
                    OrderableItemType.SIDE, 
                    Collections.emptyList()
            );

            TestOrderableItem mainItem = new TestOrderableItem(
                    "main-item", 
                    "Burger with toppings", 
                    BigDecimal.valueOf(7.99), 
                    OrderableItemType.MAIN, 
                    List.of(subItem1, subItem2)
            );

            OrderItem orderItem = OrderItem.builder()
                    .id("order-item-1")
                    .item(mainItem)
                    .amount(1)
                    .build();

            List<OrderItem> orderItems = List.of(orderItem);

            OrderableItemOutputDTO subItemOutputDTO1 = new OrderableItemOutputDTO(
                    "sub-item-1",
                    "Cheese",
                    null,
                    "1.00",
                    "side",
                    Collections.emptyMap(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );

            OrderableItemOutputDTO subItemOutputDTO2 = new OrderableItemOutputDTO(
                    "sub-item-2",
                    "Bacon",
                    null,
                    "1.50",
                    "side",
                    Collections.emptyMap(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );

            try (MockedStatic<OrderableItemOutputDTOConverter> converterMock = mockStatic(OrderableItemOutputDTOConverter.class)) {
                converterMock.when(() -> OrderableItemOutputDTOConverter.convert(subItem1))
                        .thenReturn(subItemOutputDTO1);
                converterMock.when(() -> OrderableItemOutputDTOConverter.convert(subItem2))
                        .thenReturn(subItemOutputDTO2);

                // When
                List<OrderItemOutputDTO> result = OrderItemOutputDTOConverter.convertFlattened(orderItems);

                // Then
                assertThat(result).isNotNull();
                assertThat(result).hasSize(2);

                // Create expected DTOs
                OrderItemOutputDTO expectedDTO1 = new OrderItemOutputDTO(
                        "sub-item-1",
                        subItemOutputDTO1,
                        1,
                        "1,0"
                );

                OrderItemOutputDTO expectedDTO2 = new OrderItemOutputDTO(
                        "sub-item-2",
                        subItemOutputDTO2,
                        1,
                        "1,5"
                );

                // Verify result contains both expected DTOs, regardless of order
                assertThat(result).containsExactlyInAnyOrder(expectedDTO1, expectedDTO2);

                converterMock.verify(() -> OrderableItemOutputDTOConverter.convert(subItem1));
                converterMock.verify(() -> OrderableItemOutputDTOConverter.convert(subItem2));
            }
        }

        @Test
        @DisplayName("groups items with the same ID in the flattened list")
        void should_groupItemsWithSameId_inFlattenedList() {
            // Given
            TestOrderableItem item1 = new TestOrderableItem(
                    "item-1", 
                    "Burger", 
                    BigDecimal.valueOf(5.99), 
                    OrderableItemType.MAIN, 
                    Collections.emptyList()
            );

            TestOrderableItem item2 = new TestOrderableItem(
                    "item-1", // Same ID as item1
                    "Burger", 
                    BigDecimal.valueOf(5.99), 
                    OrderableItemType.MAIN, 
                    Collections.emptyList()
            );

            OrderItem orderItem1 = OrderItem.builder()
                    .id("order-item-1")
                    .item(item1)
                    .amount(2)
                    .build();

            OrderItem orderItem2 = OrderItem.builder()
                    .id("order-item-2")
                    .item(item2)
                    .amount(3)
                    .build();

            List<OrderItem> orderItems = List.of(orderItem1, orderItem2);

            OrderableItemOutputDTO itemOutputDTO = new OrderableItemOutputDTO(
                    "item-1",
                    "Burger",
                    null,
                    "5.99",
                    "main",
                    Collections.emptyMap(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );

            try (MockedStatic<OrderableItemOutputDTOConverter> converterMock = mockStatic(OrderableItemOutputDTOConverter.class)) {
                converterMock.when(() -> OrderableItemOutputDTOConverter.convert(item1))
                        .thenReturn(itemOutputDTO);
                converterMock.when(() -> OrderableItemOutputDTOConverter.convert(item2))
                        .thenReturn(itemOutputDTO);

                // When
                List<OrderItemOutputDTO> result = OrderItemOutputDTOConverter.convertFlattened(orderItems);

                // Then
                assertThat(result).isNotNull();
                assertThat(result).hasSize(1);

                // Verify the items are grouped with a total amount of 5 (2 + 3)
                assertThat(result.getFirst().id()).isEqualTo("item-1");
                assertThat(result.getFirst().item()).isEqualTo(itemOutputDTO);
                assertThat(result.getFirst().amount()).isEqualTo(5);
                assertThat(result.getFirst().price()).isEqualTo("5,99");

                // Verify that convert was called at least once
                converterMock.verify(() -> OrderableItemOutputDTOConverter.convert(any(OrderableItem.class)), atLeastOnce());
            }
        }
    }
}
