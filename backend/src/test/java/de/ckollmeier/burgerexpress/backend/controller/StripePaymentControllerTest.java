package de.ckollmeier.burgerexpress.backend.controller;

import com.stripe.exception.SignatureVerificationException;
import de.ckollmeier.burgerexpress.backend.dto.StripePaymentQrCodeResponseDTO;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.service.CustomerSessionService;
import de.ckollmeier.burgerexpress.backend.service.StripePaymentService;
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
class StripePaymentControllerTest {

    @Mock
    private StripePaymentService stripePaymentService;

    @Mock
    private CustomerSessionService customerSessionService;

    @InjectMocks
    private StripePaymentController stripePaymentController;

    @Nested
    @DisplayName("generateQrCode(HttpSession session)")
    class GenerateQrCode {

        @Test
        @DisplayName("Successfully generates QR code with existing Stripe payment order ID")
        void successfullyGeneratesQrCodeWithExistingStripePaymentOrderId() {
            // Given
            HttpSession session = mock(HttpSession.class);
            String stripePaymentOrderId = "https://stripe.com/checkout/session-123";

            Order order = Order.builder()
                    .id("order-123")
                    .status(OrderStatus.PENDING)
                    .stripePaymentOrderId(stripePaymentOrderId)
                    .build();

            when(customerSessionService.getOrderFromCustomerSession(session))
                    .thenReturn(Optional.of(order));

            String qrCodeBase64 = "base64-encoded-qr-code";
            when(stripePaymentService.generateQrCode(order)).thenReturn(qrCodeBase64);

            // When
            ResponseEntity<StripePaymentQrCodeResponseDTO> response = stripePaymentController.generateQrCode(session);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().stripePaymentOrderId()).isEqualTo(stripePaymentOrderId);
            assertThat(response.getBody().qrCodeBase64()).isEqualTo(qrCodeBase64);
            assertThat(response.getBody().qrCodeDataUrl()).isEqualTo("data:image/png;base64," + qrCodeBase64);

            verify(customerSessionService).getOrderFromCustomerSession(session);
            verify(stripePaymentService).generateQrCode(order);
            verify(customerSessionService, never()).storeOrder(any(), any(Order.class));
            verify(customerSessionService, never()).renewCustomerSession(session);
        }

        @Test
        @DisplayName("Successfully generates QR code with new Stripe payment order ID")
        void successfullyGeneratesQrCodeWithNewStripePaymentOrderId() {
            // Given
            HttpSession session = mock(HttpSession.class);
            String orderId = "order-123";
            String stripePaymentOrderId = "https://stripe.com/checkout/session-123";

            Order order = Order.builder()
                    .id(orderId)
                    .status(OrderStatus.PENDING)
                    .build();

            Order updatedOrder = order.withStripePaymentOrderId(stripePaymentOrderId);

            when(customerSessionService.getOrderFromCustomerSession(session))
                    .thenReturn(Optional.of(order));

            when(stripePaymentService.createCheckoutSession(order)).thenReturn(updatedOrder);

            String qrCodeBase64 = "base64-encoded-qr-code";
            when(stripePaymentService.generateQrCode(updatedOrder)).thenReturn(qrCodeBase64);

            // When
            ResponseEntity<StripePaymentQrCodeResponseDTO> response = stripePaymentController.generateQrCode(session);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().stripePaymentOrderId()).isEqualTo(stripePaymentOrderId);
            assertThat(response.getBody().qrCodeBase64()).isEqualTo(qrCodeBase64);
            assertThat(response.getBody().qrCodeDataUrl()).isEqualTo("data:image/png;base64," + qrCodeBase64);

            verify(customerSessionService).getOrderFromCustomerSession(session);
            verify(stripePaymentService).createCheckoutSession(order);
            verify(customerSessionService).storeOrder(eq(session), any(Order.class));
            verify(customerSessionService).renewCustomerSession(session);
            verify(stripePaymentService).generateQrCode(updatedOrder);
        }

        @Test
        @DisplayName("Returns error when no customer session found")
        void returnsErrorWhenNoCustomerSessionFound() {
            // Given
            HttpSession session = mock(HttpSession.class);
            when(customerSessionService.getOrderFromCustomerSession(session))
                    .thenReturn(Optional.empty());

            // When
            ResponseEntity<StripePaymentQrCodeResponseDTO> response = stripePaymentController.generateQrCode(session);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNull();

            verify(customerSessionService).getOrderFromCustomerSession(session);
            verifyNoInteractions(stripePaymentService);
        }

        @Test
        @DisplayName("Returns error when exception occurs")
        void returnsErrorWhenExceptionOccurs() {
            // Given
            HttpSession session = mock(HttpSession.class);
            when(customerSessionService.getOrderFromCustomerSession(session))
                    .thenThrow(new RuntimeException("Test exception"));

            // When
            ResponseEntity<StripePaymentQrCodeResponseDTO> response = stripePaymentController.generateQrCode(session);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNull();

            verify(customerSessionService).getOrderFromCustomerSession(session);
        }
    }

    @Nested
    @DisplayName("handleWebhook(String payload, String sigHeader)")
    class HandleWebhook {

        @Test
        @DisplayName("Successfully processes webhook")
        void successfullyProcessesWebhook() throws SignatureVerificationException {
            // Given
            String payload = "{\"event_type\":\"checkout.session.completed\"}";
            String sigHeader = "valid-signature";

            doNothing().when(stripePaymentService).processWebhook(payload, sigHeader);

            // When
            ResponseEntity<String> response = stripePaymentController.handleWebhook(payload, sigHeader);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNull();

            verify(stripePaymentService).processWebhook(payload, sigHeader);
        }

        @Test
        @DisplayName("Returns bad request when signature is invalid")
        void returnsBadRequestWhenSignatureIsInvalid() throws SignatureVerificationException {
            // Given
            String payload = "{\"event_type\":\"checkout.session.completed\"}";
            String sigHeader = "invalid-signature";

            doThrow(new SignatureVerificationException("Invalid signature", null))
                    .when(stripePaymentService).processWebhook(payload, sigHeader);

            // When
            ResponseEntity<String> response = stripePaymentController.handleWebhook(payload, sigHeader);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isEqualTo("Invalid signature");

            verify(stripePaymentService).processWebhook(payload, sigHeader);
        }

        @Test
        @DisplayName("Returns error when exception occurs")
        void returnsErrorWhenExceptionOccurs() throws SignatureVerificationException {
            // Given
            String payload = "{\"invalid_json\":";
            String sigHeader = "valid-signature";

            doThrow(new RuntimeException("Test exception"))
                    .when(stripePaymentService).processWebhook(payload, sigHeader);

            // When
            ResponseEntity<String> response = stripePaymentController.handleWebhook(payload, sigHeader);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isEqualTo("Error processing webhook");

            verify(stripePaymentService).processWebhook(payload, sigHeader);
        }
    }

    @Nested
    @DisplayName("approvingOrder(String hash)")
    class ApprovingOrder {

        @Test
        @DisplayName("Successfully approves order")
        void successfullyApprovesOrder() {
            // Given
            String hash = "hash-123";
            String redirectUrl = "https://stripe.com/checkout/session-123";

            when(stripePaymentService.approvingOrder(hash)).thenReturn(redirectUrl);

            // When
            RedirectView result = stripePaymentController.approvingOrder(hash);

            // Then
            assertThat(result.getUrl()).isEqualTo(redirectUrl);

            verify(stripePaymentService).approvingOrder(hash);
        }
    }
}
