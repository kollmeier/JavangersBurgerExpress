package de.ckollmeier.burgerexpress.backend.dto;

/**
 * DTO for PayPal QR code response
 */
public record StripePaymentQrCodeResponseDTO(
        String stripePaymentOrderId,
        String qrCodeBase64,
        String qrCodeDataUrl
) {
}