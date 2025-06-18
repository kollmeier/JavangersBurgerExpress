package de.ckollmeier.burgerexpress.backend.dto;

/**
 * DTO for PayPal QR code response
 */
public record PayPalQrCodeResponseDTO(
        String paypalOrderId,
        String qrCodeBase64,
        String qrCodeDataUrl
) {
}