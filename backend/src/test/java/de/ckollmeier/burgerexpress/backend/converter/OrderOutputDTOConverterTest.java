package de.ckollmeier.burgerexpress.backend.converter;

import de.ckollmeier.burgerexpress.backend.dto.OrderItemOutputDTO;
import de.ckollmeier.burgerexpress.backend.dto.OrderOutputDTO;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
}