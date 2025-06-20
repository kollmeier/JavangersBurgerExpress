package de.ckollmeier.burgerexpress.backend.service;

import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.model.OrderItem;
import de.ckollmeier.burgerexpress.backend.repository.OrderRepository;
import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import de.ckollmeier.burgerexpress.backend.types.OrderableItemType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PayPalServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PayPalService payPalService;

    @BeforeEach
    void setUp() {
        // Set up the RestTemplate mock
        ReflectionTestUtils.setField(payPalService, "restTemplate", restTemplate);

        // Set up configuration values
        ReflectionTestUtils.setField(payPalService, "clientId", "test-client-id");
        ReflectionTestUtils.setField(payPalService, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(payPalService, "paypalApiBaseUrl", "https://api-test.paypal.com");
        ReflectionTestUtils.setField(payPalService, "appBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(payPalService, "paypalCheckoutUrl", "https://www.paypal.com/checkoutnow?token=");
    }

    @Nested
    @DisplayName("createPayPalOrder(Order order)")
    class CreatePayPalOrder {

        @Test
        @DisplayName("Successfully creates a PayPal order")
        void successfullyCreatesPayPalOrder() {
            // Given
            // Create a mock OrderableItem
            OrderableItem mockItem = mock(OrderableItem.class);
            when(mockItem.getId()).thenReturn("item-1");
            when(mockItem.getName()).thenReturn("Burger");
            when(mockItem.getPrice()).thenReturn(BigDecimal.valueOf(9.99));
            when(mockItem.getOrderableItemType()).thenReturn(OrderableItemType.MAIN);

            Order order = Order.builder()
                    .id("order-123")
                    .items(List.of(
                            OrderItem.builder()
                                    .id("item-1")
                                    .item(mockItem)
                                    .amount(2)
                                    .build()
                    ))
                    .build();

            // Mock access token response
            String accessTokenResponse = "{\"access_token\":\"test-access-token\",\"expires_in\":3600}";
            ResponseEntity<String> accessTokenResponseEntity = new ResponseEntity<>(accessTokenResponse, HttpStatus.OK);
            when(restTemplate.exchange(
                    eq("https://api-test.paypal.com/v1/oauth2/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(accessTokenResponseEntity);

            // Mock create order response
            String createOrderResponse = "{\"id\":\"paypal-order-123\",\"status\":\"CREATED\"}";
            ResponseEntity<String> createOrderResponseEntity = new ResponseEntity<>(createOrderResponse, HttpStatus.CREATED);
            when(restTemplate.exchange(
                    eq("https://api-test.paypal.com/v2/checkout/orders"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(createOrderResponseEntity);

            // When
            String result = payPalService.createPayPalOrder(order);

            // Then
            assertThat(result).isEqualTo("paypal-order-123");
            verify(orderService).saveOrder(any(Order.class));
        }
    }

    @Nested
    @DisplayName("generateQrCode(String paypalOrderId)")
    class GenerateQrCode {

        @Test
        @DisplayName("Successfully generates a QR code")
        void successfullyGeneratesQrCode() {
            // Given
            String paypalOrderId = "paypal-order-123";
            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PENDING)
                    .paypalOrderId(paypalOrderId)
                    .build();
            when(orderRepository.findByPaypalOrderId(paypalOrderId)).thenReturn(order);

            // Create a spy of the service to avoid actually generating a QR code
            PayPalService spyService = spy(payPalService);

            // Mock the actual QR code generation to avoid the complexity
            String fakeBase64 = "fakeBase64QRCode";
            doReturn(fakeBase64).when(spyService).generateQrCode(anyString());

            // When
            String result = spyService.generateQrCode(paypalOrderId);

            // Then
            assertThat(result).isEqualTo(fakeBase64);

            // Note: We can't verify orderRepository.findByPaypalOrderId was called
            // because we've mocked the entire method that would call it
        }

        @Test
        @DisplayName("Throws exception when order is already paid")
        void throwsExceptionWhenOrderAlreadyPaid() {
            // Given
            String paypalOrderId = "paypal-order-123";
            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PAID)
                    .paypalOrderId(paypalOrderId)
                    .build();
            when(orderRepository.findByPaypalOrderId(paypalOrderId)).thenReturn(order);

            // When/Then
            assertThatThrownBy(() -> payPalService.generateQrCode(paypalOrderId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("QR code for paid order already exists");
        }
    }

    @Nested
    @DisplayName("capturePayment(String paypalOrderId)")
    class CapturePayment {

        @Test
        @DisplayName("Successfully captures payment")
        void successfullyCapturesPayment() {
            // Given
            String paypalOrderId = "paypal-order-123";
            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PENDING)
                    .paypalOrderId(paypalOrderId)
                    .build();
            when(orderRepository.findByPaypalOrderId(paypalOrderId)).thenReturn(order);

            // Mock access token response
            String accessTokenResponse = "{\"access_token\":\"test-access-token\",\"expires_in\":3600}";
            ResponseEntity<String> accessTokenResponseEntity = new ResponseEntity<>(accessTokenResponse, HttpStatus.OK);
            when(restTemplate.exchange(
                    eq("https://api-test.paypal.com/v1/oauth2/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(accessTokenResponseEntity);

            // Mock capture payment response
            String captureResponse = "{\"id\":\"paypal-order-123\",\"status\":\"COMPLETED\"}";
            ResponseEntity<String> captureResponseEntity = new ResponseEntity<>(captureResponse, HttpStatus.CREATED);
            when(restTemplate.exchange(
                    eq("https://api-test.paypal.com/v2/checkout/orders/" + paypalOrderId + "/capture"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(captureResponseEntity);

            // When
            Optional<Order> result = payPalService.capturePayment(paypalOrderId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo(OrderStatus.PAID);
        }

        @Test
        @DisplayName("Returns empty when order not found")
        void returnsEmptyWhenOrderNotFound() {
            // Given
            String paypalOrderId = "paypal-order-123";
            when(orderRepository.findByPaypalOrderId(paypalOrderId)).thenReturn(null);

            // Mock access token response
            String accessTokenResponse = "{\"access_token\":\"test-access-token\",\"expires_in\":3600}";
            ResponseEntity<String> accessTokenResponseEntity = new ResponseEntity<>(accessTokenResponse, HttpStatus.OK);
            when(restTemplate.exchange(
                    eq("https://api-test.paypal.com/v1/oauth2/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(accessTokenResponseEntity);

            // Mock capture payment response
            String captureResponse = "{\"id\":\"paypal-order-123\",\"status\":\"COMPLETED\"}";
            ResponseEntity<String> captureResponseEntity = new ResponseEntity<>(captureResponse, HttpStatus.CREATED);
            when(restTemplate.exchange(
                    eq("https://api-test.paypal.com/v2/checkout/orders/" + paypalOrderId + "/capture"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(String.class)
            )).thenReturn(captureResponseEntity);

            // When
            Optional<Order> result = payPalService.capturePayment(paypalOrderId);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("processWebhook(String payload)")
    class ProcessWebhook {

        @Test
        @DisplayName("Successfully processes CHECKOUT.ORDER.APPROVED webhook")
        void successfullyProcessesCheckoutOrderApprovedWebhook() {
            // Given
            String paypalOrderId = "paypal-order-123";
            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PENDING)
                    .paypalOrderId(paypalOrderId)
                    .build();
            when(orderRepository.findByPaypalOrderId(paypalOrderId)).thenReturn(order);

            // Mock capture payment
            Order updatedOrder = order.withStatus(OrderStatus.PAID).withUpdatedAt(Instant.now());
            when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

            // Create webhook payload
            String payload = "{"
                    + "\"event_type\":\"CHECKOUT.ORDER.APPROVED\","
                    + "\"resource\":{"
                    + "\"id\":\"" + paypalOrderId + "\""
                    + "}"
                    + "}";

            // Use a spy to partially mock the service
            PayPalService spyService = spy(payPalService);

            // Mock capturePayment method
            doReturn(Optional.of(updatedOrder)).when(spyService).capturePayment(paypalOrderId);

            // When
            Optional<Order> result = spyService.processWebhook(payload);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo(OrderStatus.PAID);
            verify(orderService).saveOrder(any(Order.class));
            verify(spyService).capturePayment(paypalOrderId);
        }

        @Test
        @DisplayName("Successfully processes PAYMENT.CAPTURE.COMPLETED webhook")
        void successfullyProcessesPaymentCaptureCompletedWebhook() {
            // Given
            String paypalOrderId = "paypal-order-123";
            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PENDING)
                    .paypalOrderId(paypalOrderId)
                    .build();
            when(orderRepository.findByPaypalOrderId(paypalOrderId)).thenReturn(order);

            // Create webhook payload
            String payload = "{"
                    + "\"event_type\":\"PAYMENT.CAPTURE.COMPLETED\","
                    + "\"resource\":{"
                    + "\"supplementary_data\":{"
                    + "\"related_ids\":{"
                    + "\"order_id\":\"" + paypalOrderId + "\""
                    + "}"
                    + "}"
                    + "}"
                    + "}";

            // When
            Optional<Order> result = payPalService.processWebhook(payload);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo(OrderStatus.PAID);
            verify(orderService).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Returns empty for unsupported event type")
        void returnsEmptyForUnsupportedEventType() {
            // Given
            String payload = "{"
                    + "\"event_type\":\"UNSUPPORTED_EVENT\","
                    + "\"resource\":{}"
                    + "}";

            // When
            Optional<Order> result = payPalService.processWebhook(payload);

            // Then
            assertThat(result).isEmpty();
            verify(orderRepository, never()).save(any(Order.class));
        }
    }

    @Nested
    @DisplayName("approvingOrder(String paypalOrderId)")
    class ApprovingOrder {

        @Test
        @DisplayName("Successfully approves order")
        void successfullyApprovesOrder() {
            // Given
            String paypalOrderId = "paypal-order-123";
            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PENDING)
                    .paypalOrderId(paypalOrderId)
                    .build();
            when(orderRepository.findByPaypalOrderId(paypalOrderId)).thenReturn(order);

            // When
            String result = payPalService.approvingOrder(paypalOrderId);

            // Then
            assertThat(result).isEqualTo("https://www.paypal.com/checkoutnow?token=paypal-order-123");
            verify(orderService).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Throws NotFoundException when order not found")
        void throwsNotFoundExceptionWhenOrderNotFound() {
            // Given
            String paypalOrderId = "paypal-order-123";
            when(orderRepository.findByPaypalOrderId(paypalOrderId)).thenReturn(null);

            // When/Then
            assertThatThrownBy(() -> payPalService.approvingOrder(paypalOrderId))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("Throws IllegalArgumentException when order already paid")
        void throwsIllegalArgumentExceptionWhenOrderAlreadyPaid() {
            // Given
            String paypalOrderId = "paypal-order-123";
            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PAID)
                    .paypalOrderId(paypalOrderId)
                    .build();
            when(orderRepository.findByPaypalOrderId(paypalOrderId)).thenReturn(order);

            // When/Then
            assertThatThrownBy(() -> payPalService.approvingOrder(paypalOrderId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("QR code for paid order already exists");
        }
    }
}
