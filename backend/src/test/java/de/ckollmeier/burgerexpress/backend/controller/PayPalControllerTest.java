package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.dto.PayPalQrCodeResponseDTO;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.service.CustomerSessionService;
import de.ckollmeier.burgerexpress.backend.service.PayPalService;
import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PayPalControllerTest {

    @Mock
    private PayPalService payPalService;

    @Mock
    private CustomerSessionService customerSessionService;

    @InjectMocks
    private PayPalController payPalController;

    @Nested
    @DisplayName("generateQrCode(HttpSession session)")
    class GenerateQrCode {

        @Test
        @DisplayName("Successfully generates QR code with existing PayPal order ID")
        void successfullyGeneratesQrCodeWithExistingPayPalOrderId() {
            // Given
            HttpSession session = mock(HttpSession.class);
            String paypalOrderId = "paypal-order-123";

            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PENDING)
                    .paypalOrderId(paypalOrderId)
                    .build();

            when(customerSessionService.getOrderFromCustomerSession(session))
                    .thenReturn(Optional.of(order));

            String qrCodeBase64 = "base64-encoded-qr-code";
            when(payPalService.generateQrCode(paypalOrderId)).thenReturn(qrCodeBase64);

            // When
            ResponseEntity<PayPalQrCodeResponseDTO> response = payPalController.generateQrCode(session);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().paypalOrderId()).isEqualTo(paypalOrderId);
            assertThat(response.getBody().qrCodeBase64()).isEqualTo(qrCodeBase64);
            assertThat(response.getBody().qrCodeDataUrl()).isEqualTo("data:image/png;base64," + qrCodeBase64);

            verify(customerSessionService).getOrderFromCustomerSession(session);
            verify(payPalService).generateQrCode(paypalOrderId);
            verify(customerSessionService, never()).storeOrder(any(), any(Order.class));
            verify(customerSessionService, never()).renewCustomerSession(session);
        }

        @Test
        @DisplayName("Successfully generates QR code with new PayPal order ID")
        void successfullyGeneratesQrCodeWithNewPayPalOrderId() {
            // Given
            HttpSession session = mock(HttpSession.class);
            String orderId = "order-123";
            String paypalOrderId = "paypal-order-123";

            Order order = Order.builder()
                    .id(orderId)
                    .status(OrderStatus.PENDING)
                    .build();

            when(customerSessionService.getOrderFromCustomerSession(session))
                    .thenReturn(Optional.of(order));

            when(payPalService.createPayPalOrder(order)).thenReturn(paypalOrderId);

            String qrCodeBase64 = "base64-encoded-qr-code";
            when(payPalService.generateQrCode(paypalOrderId)).thenReturn(qrCodeBase64);

            // When
            ResponseEntity<PayPalQrCodeResponseDTO> response = payPalController.generateQrCode(session);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().paypalOrderId()).isEqualTo(paypalOrderId);
            assertThat(response.getBody().qrCodeBase64()).isEqualTo(qrCodeBase64);
            assertThat(response.getBody().qrCodeDataUrl()).isEqualTo("data:image/png;base64," + qrCodeBase64);

            verify(customerSessionService).getOrderFromCustomerSession(session);
            verify(payPalService).createPayPalOrder(order);
            verify(customerSessionService).storeOrder(eq(session), any(Order.class));
            verify(customerSessionService).renewCustomerSession(session);
            verify(payPalService).generateQrCode(paypalOrderId);
        }

        @Test
        @DisplayName("Returns error when no customer session found")
        void returnsErrorWhenNoCustomerSessionFound() {
            // Given
            HttpSession session = mock(HttpSession.class);
            when(customerSessionService.getOrderFromCustomerSession(session))
                    .thenReturn(Optional.empty());

            // When
            ResponseEntity<PayPalQrCodeResponseDTO> response = payPalController.generateQrCode(session);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNull();

            verify(customerSessionService).getOrderFromCustomerSession(session);
            verifyNoInteractions(payPalService);
        }

        @Test
        @DisplayName("Returns error when exception occurs")
        void returnsErrorWhenExceptionOccurs() {
            // Given
            HttpSession session = mock(HttpSession.class);
            when(customerSessionService.getOrderFromCustomerSession(session))
                    .thenThrow(new RuntimeException("Test exception"));

            // When
            ResponseEntity<PayPalQrCodeResponseDTO> response = payPalController.generateQrCode(session);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNull();

            verify(customerSessionService).getOrderFromCustomerSession(session);
        }
    }

    @Nested
    @DisplayName("handleWebhook(String payload)")
    class HandleWebhook {

        @Test
        @DisplayName("Successfully processes webhook")
        void successfullyProcessesWebhook() {
            // Given
            String payload = "{\"event_type\":\"PAYMENT.CAPTURE.COMPLETED\"}";
            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PAID)
                    .build();

            when(payPalService.processWebhook(payload)).thenReturn(Optional.of(order));

            // When
            ResponseEntity<String> response = payPalController.handleWebhook(payload);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo("Webhook processed successfully");

            verify(payPalService).processWebhook(payload);
        }

        @Test
        @DisplayName("Returns bad request when webhook processing fails")
        void returnsBadRequestWhenWebhookProcessingFails() {
            // Given
            String payload = "{\"event_type\":\"UNSUPPORTED_EVENT\"}";
            when(payPalService.processWebhook(payload)).thenReturn(Optional.empty());

            // When
            ResponseEntity<String> response = payPalController.handleWebhook(payload);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isEqualTo("Failed to process webhook");

            verify(payPalService).processWebhook(payload);
        }

        @Test
        @DisplayName("Returns error when exception occurs")
        void returnsErrorWhenExceptionOccurs() {
            // Given
            String payload = "{\"invalid_json\":";
            when(payPalService.processWebhook(payload)).thenThrow(new RuntimeException("Test exception"));

            // When
            ResponseEntity<String> response = payPalController.handleWebhook(payload);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isEqualTo("Error processing webhook");

            verify(payPalService).processWebhook(payload);
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
            String redirectUrl = "https://www.paypal.com/checkoutnow?token=" + paypalOrderId;

            when(payPalService.approvingOrder(paypalOrderId)).thenReturn(redirectUrl);

            // When
            RedirectView result = payPalController.approvingOrder(paypalOrderId);

            // Then
            assertThat(result.getUrl()).isEqualTo(redirectUrl);

            verify(payPalService).approvingOrder(paypalOrderId);
        }
    }
}
