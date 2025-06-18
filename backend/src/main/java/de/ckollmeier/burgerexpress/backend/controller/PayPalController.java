package de.ckollmeier.burgerexpress.backend.controller;

import de.ckollmeier.burgerexpress.backend.dto.PayPalQrCodeResponseDTO;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.service.CustomerSessionService;
import de.ckollmeier.burgerexpress.backend.service.PayPalService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

/**
 * Controller for PayPal integration
 */
@RestController
@RequestMapping("/api/paypal")
@PreAuthorize("permitAll()")
@RequiredArgsConstructor
@Slf4j
public class PayPalController {

    private final PayPalService payPalService;
    private final CustomerSessionService customerSessionService;

    /**
     * Generates a QR code for PayPal payment
     * @return The QR code as a base64 encoded image
     */
    @GetMapping("/qr-code")
    public ResponseEntity<PayPalQrCodeResponseDTO> generateQrCode(HttpSession session) {
        try {
            // Get the order from session
            Order order = customerSessionService.getOrderFromCustomerSession(session)
                    .orElseThrow(() -> new IllegalStateException("No customer session found"));

            // If the order already has a PayPal order ID, use it
            String paypalOrderId;
            if (order.getPaypalOrderId() != null) {
                paypalOrderId = order.getPaypalOrderId();
            } else {
                // Create a PayPal order
                paypalOrderId = payPalService.createPayPalOrder(order);
                customerSessionService.storeOrder(session, order.withPaypalOrderId(paypalOrderId));
                customerSessionService.renewCustomerSession(session);
            }
            
            // Generate QR code
            String qrCodeBase64 = payPalService.generateQrCode(paypalOrderId);
            
            // Create response
            PayPalQrCodeResponseDTO response = new PayPalQrCodeResponseDTO(
                    paypalOrderId,
                    qrCodeBase64,
                    "data:image/png;base64," + qrCodeBase64
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating QR code", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Handles PayPal webhooks
     * @param payload The webhook payload
     * @return Success or failure
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload) {

        try {
            // Process webhook
            Optional<Order> order = payPalService.processWebhook(payload);
            if (order.isPresent()) {
                return ResponseEntity.ok("Webhook processed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to process webhook");
            }
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }

    @GetMapping("/approving/{paypalOrderId}")
    public RedirectView approvingOrder(final @PathVariable String paypalOrderId) {
        return new RedirectView(payPalService.approvingOrder(paypalOrderId));
    }
}