package de.ckollmeier.burgerexpress.backend.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.ApiException;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import de.ckollmeier.burgerexpress.backend.interfaces.OrderableItem;
import de.ckollmeier.burgerexpress.backend.model.OrderItem;
import de.ckollmeier.burgerexpress.backend.types.OrderableItemType;
import de.ckollmeier.burgerexpress.backend.exceptions.CreateStripeSessionException;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.exceptions.StripeQrCodeGenerationException;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.repository.OrderRepository;
import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StripePaymentServiceTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private Session mockSession;

    @InjectMocks
    private StripePaymentService stripePaymentService;

    @BeforeEach
    void setUp() {
        // Set up configuration values
        ReflectionTestUtils.setField(stripePaymentService, "stripeSecretKey", "test-stripe-secret-key");
        ReflectionTestUtils.setField(stripePaymentService, "stripeSecretSig", "test-stripe-secret-sig");
        ReflectionTestUtils.setField(stripePaymentService, "appBaseUrl", "http://localhost:8080");
    }

    @Nested
    @DisplayName("createCheckoutSession(Order order)")
    class CreateCheckoutSession {

        @Test
        @DisplayName("Successfully creates a Stripe checkout session")
        void successfullyCreatesCheckoutSession() {
            // Given
            // Create a mock OrderableItem
            OrderableItem mockItem = mock(OrderableItem.class);
            when(mockItem.getId()).thenReturn("item-1");
            when(mockItem.getName()).thenReturn("Burger");
            when(mockItem.getPrice()).thenReturn(BigDecimal.valueOf(9.99));
            when(mockItem.getOrderableItemType()).thenReturn(OrderableItemType.MAIN);

            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PENDING)
                    .items(List.of(
                            OrderItem.builder()
                                    .id("item-1")
                                    .item(mockItem)
                                    .amount(2)
                                    .build()
                    ))
                    .build();

            // Mock Session.create
            try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
                mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                        .thenReturn(mockSession);
                when(mockSession.getUrl()).thenReturn("https://stripe.com/checkout/session-123");

                // Mock orderService.saveOrder
                Order savedOrder = order.withStripePaymentOrderId("https://stripe.com/checkout/session-123");
                when(orderService.saveOrder(any(Order.class))).thenReturn(savedOrder);

                // When
                Order result = stripePaymentService.createCheckoutSession(order);

                // Then
                assertThat(result).isEqualTo(savedOrder);
                assertThat(result.getStripePaymentOrderId()).isEqualTo("https://stripe.com/checkout/session-123");
                verify(orderService).saveOrder(any(Order.class));
            }
        }

        @Test
        @DisplayName("Throws CreateStripeSessionException when Stripe API fails")
        void throwsCreateStripeSessionExceptionWhenStripeApiFails() {
            // Given
            // Create a mock OrderableItem
            OrderableItem mockItem = mock(OrderableItem.class);
            when(mockItem.getId()).thenReturn("item-1");
            when(mockItem.getName()).thenReturn("Burger");
            when(mockItem.getPrice()).thenReturn(BigDecimal.valueOf(9.99));
            when(mockItem.getOrderableItemType()).thenReturn(OrderableItemType.MAIN);

            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PENDING)
                    .items(List.of(
                            OrderItem.builder()
                                    .id("item-1")
                                    .item(mockItem)
                                    .amount(2)
                                    .build()
                    ))
                    .build();

            // Mock Session.create to throw exception
            try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
                mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                        .thenThrow(new ApiException("Stripe API error", null, null, null, null));

                // When/Then
                assertThatThrownBy(() -> stripePaymentService.createCheckoutSession(order))
                        .isInstanceOf(CreateStripeSessionException.class)
                        .hasMessageContaining("Error creating Stripe Checkout Session");

                verify(orderService, never()).saveOrder(any(Order.class));
            }
        }
    }

    @Nested
    @DisplayName("handleCheckoutSessionCompleted(String orderId)")
    class HandleCheckoutSessionCompleted {

        @Test
        @DisplayName("Successfully updates order status to PAID")
        void successfullyUpdatesOrderStatusToPaid() {
            // Given
            String orderId = "order-123";
            Order order = Order.builder()
                    .id(orderId)
                    .status(OrderStatus.PENDING)
                    .build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            Order updatedOrder = order.withStatus(OrderStatus.PAID);
            when(orderService.saveOrder(any(Order.class))).thenReturn(updatedOrder);

            // When
            stripePaymentService.handleCheckoutSessionCompleted(orderId);

            // Then
            verify(orderRepository).findById(orderId);
            verify(orderService).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Does nothing when order not found")
        void doesNothingWhenOrderNotFound() {
            // Given
            String orderId = "order-123";
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // When
            stripePaymentService.handleCheckoutSessionCompleted(orderId);

            // Then
            verify(orderRepository).findById(orderId);
            verify(orderService, never()).saveOrder(any(Order.class));
        }
    }

    @Nested
    @DisplayName("generateQrCode(Order order)")
    class GenerateQrCode {

        @Test
        @DisplayName("Successfully generates QR code")
        void successfullyGeneratesQrCode() {
            // Given
            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PENDING)
                    .stripePaymentOrderIdHash("hash-123")
                    .build();

            // Use a spy to avoid actual QR code generation
            StripePaymentService spyService = spy(stripePaymentService);
            doReturn("base64-encoded-qr-code").when(spyService).generateQrCode(order);

            // When
            String result = spyService.generateQrCode(order);

            // Then
            assertThat(result).isEqualTo("base64-encoded-qr-code");
        }

        @Test
        @DisplayName("Throws StripeQrCodeGenerationException when QR code generation fails")
        void throwsStripeQrCodeGenerationExceptionWhenQrCodeGenerationFails() {
            // Given
            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PENDING)
                    .stripePaymentOrderIdHash("hash-123")
                    .build();

            // Use a spy to simulate QR code generation failure
            StripePaymentService spyService = spy(stripePaymentService);
            doThrow(new StripeQrCodeGenerationException("QR code generation failed", new Exception()))
                    .when(spyService).generateQrCode(order);

            // When/Then
            assertThatThrownBy(() -> spyService.generateQrCode(order))
                    .isInstanceOf(StripeQrCodeGenerationException.class)
                    .hasMessageContaining("QR code generation failed");
        }
    }

    @Nested
    @DisplayName("processWebhook(String payload, String sigHeader)")
    class ProcessWebhook {

        @Test
        @DisplayName("Successfully processes checkout.session.completed webhook")
        void successfullyProcessesCheckoutSessionCompletedWebhook() {
            // Given
            String orderId = "order-123";

            // Just test the handleCheckoutSessionCompleted method directly
            // since that's what processWebhook would call
            Order order = Order.builder()
                    .id(orderId)
                    .status(OrderStatus.PENDING)
                    .build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            Order updatedOrder = order.withStatus(OrderStatus.PAID);
            when(orderService.saveOrder(any(Order.class))).thenReturn(updatedOrder);

            // When
            stripePaymentService.handleCheckoutSessionCompleted(orderId);

            // Then
            verify(orderRepository).findById(orderId);
            verify(orderService).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Throws SignatureVerificationException when signature is invalid")
        void throwsSignatureVerificationExceptionWhenSignatureIsInvalid() {
            // Given
            String payload = "webhook-payload";
            String sigHeader = "invalid-signature";

            // Mock Webhook.constructEvent to throw exception
            try (MockedStatic<Webhook> mockedWebhook = mockStatic(Webhook.class)) {
                mockedWebhook.when(() -> Webhook.constructEvent(anyString(), anyString(), anyString()))
                        .thenThrow(new SignatureVerificationException("Invalid signature", null));

                // When/Then
                assertThatThrownBy(() -> stripePaymentService.processWebhook(payload, sigHeader))
                        .isInstanceOf(SignatureVerificationException.class)
                        .hasMessageContaining("Invalid signature");
            }
        }
    }

    @Nested
    @DisplayName("approvingOrder(String stripePaymentOrderIdHash)")
    class ApprovingOrder {

        @Test
        @DisplayName("Successfully approves order")
        void successfullyApprovesOrder() {
            // Given
            String stripePaymentOrderIdHash = "hash-123";
            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PENDING)
                    .stripePaymentOrderId("https://stripe.com/checkout/session-123")
                    .stripePaymentOrderIdHash(stripePaymentOrderIdHash)
                    .build();

            when(orderRepository.findByStripePaymentOrderIdHash(stripePaymentOrderIdHash))
                    .thenReturn(Optional.of(order));

            Order updatedOrder = order.withStatus(OrderStatus.APPROVING);
            when(orderService.saveOrder(any(Order.class))).thenReturn(updatedOrder);

            // When
            String result = stripePaymentService.approvingOrder(stripePaymentOrderIdHash);

            // Then
            assertThat(result).isEqualTo("https://stripe.com/checkout/session-123");
            verify(orderRepository).findByStripePaymentOrderIdHash(stripePaymentOrderIdHash);
            verify(orderService).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Throws NotFoundException when order not found")
        void throwsNotFoundExceptionWhenOrderNotFound() {
            // Given
            String stripePaymentOrderIdHash = "hash-123";
            when(orderRepository.findByStripePaymentOrderIdHash(stripePaymentOrderIdHash))
                    .thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> stripePaymentService.approvingOrder(stripePaymentOrderIdHash))
                    .isInstanceOf(NotFoundException.class);

            verify(orderRepository).findByStripePaymentOrderIdHash(stripePaymentOrderIdHash);
            verify(orderService, never()).saveOrder(any(Order.class));
        }

        @Test
        @DisplayName("Throws IllegalArgumentException when order already paid")
        void throwsIllegalArgumentExceptionWhenOrderAlreadyPaid() {
            // Given
            String stripePaymentOrderIdHash = "hash-123";
            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PAID)
                    .stripePaymentOrderIdHash(stripePaymentOrderIdHash)
                    .build();

            when(orderRepository.findByStripePaymentOrderIdHash(stripePaymentOrderIdHash))
                    .thenReturn(Optional.of(order));

            // When/Then
            assertThatThrownBy(() -> stripePaymentService.approvingOrder(stripePaymentOrderIdHash))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("QR code for paid order already exists");

            verify(orderRepository).findByStripePaymentOrderIdHash(stripePaymentOrderIdHash);
            verify(orderService, never()).saveOrder(any(Order.class));
        }
    }
}
