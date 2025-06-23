package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.repository.OrderRepository;
import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderNumberingTest {

    @Mock
    private CustomerSessionService customerSessionService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private OrderService orderService;

    private record OrderNumberTestCase(
            String displayName,
            int maxOrderNumber,
            int expectedOrderNumber
    ) {}

    private static Stream<OrderNumberTestCase> orderNumberTestCases() {
        return Stream.of(
                new OrderNumberTestCase(
                        "Order number starts at 101 when no orders exist in the last day",
                        0,
                        101
                ),
                new OrderNumberTestCase(
                        "Order number increments from the maximum order number in the last day",
                        105,
                        106
                ),
                new OrderNumberTestCase(
                        "Order number resets to 101 after one day if no orders exist",
                        0,
                        101
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("orderNumberTestCases")
    void testOrderNumbering(OrderNumberTestCase testCase) {
        // Given
        Order order = Order.builder()
                .id("order-123")
                .status(OrderStatus.PENDING)
                .build();

        when(customerSessionService.getOrderFromCustomerSession(httpSession))
                .thenReturn(Optional.of(order));

        when(orderRepository.findTopByUpdatedAtAfterOrderByOrderNumberDesc(any(Instant.class)))
                .thenReturn(Optional.of(order.withOrderNumber(testCase.maxOrderNumber())));

        Order savedOrder = order.withStatus(OrderStatus.CHECKOUT)
                .withUpdatedAt(Instant.now())
                .withOrderNumber(testCase.expectedOrderNumber());
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        Order result = orderService.placeOrder(httpSession);

        // Then
        assertThat(result.getOrderNumber()).isEqualTo(testCase.expectedOrderNumber());
        verify(orderRepository).findTopByUpdatedAtAfterOrderByOrderNumberDesc(any(Instant.class));
    }
}
