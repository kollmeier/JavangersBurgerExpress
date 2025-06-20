package de.ckollmeier.burgerexpress.backend.controller;

import com.stripe.exception.SignatureVerificationException;
import de.ckollmeier.burgerexpress.backend.dto.StripePaymentQrCodeResponseDTO;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.service.CustomerSessionService;
import de.ckollmeier.burgerexpress.backend.service.StripePaymentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for StripePayment integration
 */
@RestController
@RequestMapping("/api/stripe")
@PreAuthorize("permitAll()")
@RequiredArgsConstructor
@Slf4j
public class StripePaymentController {

    private final StripePaymentService stripePaymentService;
    private final CustomerSessionService customerSessionService;

    /**
     * Generates a QR code for StripePayment payment
     * @return The QR code as a base64 encoded image
     */
    @GetMapping("/qr-code")
    public ResponseEntity<StripePaymentQrCodeResponseDTO> generateQrCode(HttpSession session) {
        try {
            // Get the order from session
            Order order = customerSessionService.getOrderFromCustomerSession(session)
                    .orElseThrow(() -> new IllegalStateException("No customer session found"));

            // If the order already has a StripePayment order ID, use it
            String stripePaymentOrderId = order.getStripePaymentOrderId();
            if (stripePaymentOrderId == null) {
                // Create a StripePayment order
                order = stripePaymentService.createCheckoutSession(order);
                customerSessionService.storeOrder(session, order);
                customerSessionService.renewCustomerSession(session);
            }

            // Generate QR code
            String qrCodeBase64 = stripePaymentService.generateQrCode(order);

            // Create response
            StripePaymentQrCodeResponseDTO response = new StripePaymentQrCodeResponseDTO(
                    order.getStripePaymentOrderId(),
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
     * Handles StripePayment webhooks
     * @param payload The webhook payload
     * @return Success or failure
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            stripePaymentService.processWebhook(payload, sigHeader);
            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }

    /**
     * Bestätigt eine Stripe-Zahlungsbestellung anhand der Order-ID und leitet weiter.
     * Ruft StripePaymentService.approvingOrder auf.
     */
    @GetMapping("/approving/{hash}")
    public RedirectView approvingOrder(@PathVariable String hash) {
        return new RedirectView(stripePaymentService.approvingOrder(hash));
    }
}