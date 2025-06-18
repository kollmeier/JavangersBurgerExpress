package de.ckollmeier.burgerexpress.backend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.repository.OrderRepository;
import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayPalService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${paypal.client.id:your-client-id}")
    private String clientId;

    @Value("${paypal.client.secret:your-client-secret}")
    private String clientSecret;

    @Value("${paypal.api.base-url:https://api-m.sandbox.paypal.com}")
    private String paypalApiBaseUrl;

    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    @Value("${paypal.api.checkout-url:https://www.paypal.com/checkoutnow?token=}")
    private String paypalCheckoutUrl;

    /**
     * Creates a PayPal order for the given burger-express order
     * @param order The burger-express order
     * @return The PayPal order ID
     */
    public String createPayPalOrder(Order order) {
        try {
            // Get access token
            String accessToken = getAccessToken();

            // Create order request body
            Map<String, Object> orderRequest = new HashMap<>();
            orderRequest.put("intent", "CAPTURE");

            Map<String, Object> applicationContext = new HashMap<>();
            applicationContext.put("brand_name", "Burger Express");
            applicationContext.put("user_action", "PAY_NOW");
            orderRequest.put("application_context", applicationContext);

            Map<String, Object> redirectUrls = new HashMap<>();
            redirectUrls.put("return_url", appBaseUrl + "/payment-return.html");
            redirectUrls.put("cancel_url", appBaseUrl + "/checkout");
            orderRequest.put("redirect_urls", redirectUrls);

            Map<String, Object> amount = new HashMap<>();
            amount.put("currency_code", "EUR");
            amount.put("value", order.getTotalPrice().toString());

            Map<String, Object> purchaseUnit = new HashMap<>();
            purchaseUnit.put("reference_id", order.getId());
            purchaseUnit.put("description", "Burger Express Bestellung");
            purchaseUnit.put("amount", amount);

            orderRequest.put("purchase_units", new Object[]{purchaseUnit});

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            // Make API call
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(orderRequest, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    paypalApiBaseUrl + "/v2/checkout/orders",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Parse response
            JSONObject jsonResponse = new JSONObject(response.getBody());
            String orderId = jsonResponse.getString("id");

            // Update order with PayPal order ID
            Order updatedOrder = order.withPaypalOrderId(orderId);
            orderRepository.save(updatedOrder);

            return orderId;
        } catch (Exception e) {
            log.error("Error creating PayPal order", e);
            throw new RuntimeException("Error creating PayPal order", e);
        }
    }

    /**
     * Generates a QR code for the given PayPal order ID
     * @param paypalOrderId The PayPal order ID
     * @return Base64 encoded QR code image
     */
    public String generateQrCode(String paypalOrderId) {
        try {
            Order order = orderRepository.findByPaypalOrderId(paypalOrderId);

            if (order != null && order.getStatus() == OrderStatus.PAID) {
                log.warn("QR code for paid order {} already exists", paypalOrderId);
                throw new RuntimeException("QR code for paid order already exists");
            }
            // Create QR code content - PayPal payment URL
            String qrCodeContent = appBaseUrl + "/api/paypal/approving/" + paypalOrderId;

            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, 300, 300);

            // Convert to image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            // Convert to Base64
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code", e);
            throw new RuntimeException("Error generating QR code", e);
        }
    }

    /**
     * Captures a PayPal payment
     * @param paypalOrderId The PayPal order ID
     * @return Order if the payment was captured successfully
     */
    public Optional<Order> capturePayment(String paypalOrderId) {
        try {
            // Get access token
            String accessToken = getAccessToken();

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            // Make API call
            HttpEntity<String> entity = new HttpEntity<>("{}", headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    paypalApiBaseUrl + "/v2/checkout/orders/" + paypalOrderId + "/capture",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Check if successful
            boolean isSuccessful = response.getStatusCode() == HttpStatus.CREATED || 
                                  response.getStatusCode() == HttpStatus.OK;

            if (isSuccessful) {
                // Update order status
                Order order = orderRepository.findByPaypalOrderId(paypalOrderId);
                if (order != null) {
                    Order updatedOrder = order.withStatus(OrderStatus.PAID)
                                             .withUpdatedAt(Instant.now());
                    return Optional.of(updatedOrder);
                }
            }

            return Optional.empty();
        } catch (Exception e) {
            log.error("Error capturing PayPal payment", e);
            return Optional.empty();
        }
    }

    /**
     * Processes a PayPal webhook event
     * @param payload The webhook payload
     * @return changed order if successful
     */
    public Optional<Order> processWebhook(String payload) {
        try {
            JSONObject jsonPayload = new JSONObject(payload);
            String eventType = jsonPayload.getString("event_type");

            // Handle PAYMENT.CAPTURE.COMPLETED event
            if ("CHECKOUT.ORDER.APPROVED".equals(eventType)) {
                JSONObject resource = jsonPayload.getJSONObject("resource");
                String paypalOrderId = resource.getString("id");

                // Update order status
                Order order = orderRepository.findByPaypalOrderId(paypalOrderId);
                if (order != null) {
                    Order updatedOrder = order.withStatus(OrderStatus.APPROVED)
                                             .withUpdatedAt(Instant.now());
                    orderRepository.save(updatedOrder);
                    return capturePayment(paypalOrderId);
                }
            }
            // Handle PAYMENT.CAPTURE.COMPLETED event
            if ("PAYMENT.CAPTURE.COMPLETED".equals(eventType)) {
                JSONObject resource = jsonPayload.getJSONObject("resource");
                JSONObject supplementaryData = resource.getJSONObject("supplementary_data");
                JSONObject relatedIds = supplementaryData.getJSONObject("related_ids");
                String paypalOrderId = relatedIds.getString("order_id");

                // Update order status
                Order order = orderRepository.findByPaypalOrderId(paypalOrderId);
                if (order != null) {
                    Order updatedOrder = order.withStatus(OrderStatus.PAID)
                                             .withUpdatedAt(Instant.now());
                    orderRepository.save(updatedOrder);
                    return Optional.of(updatedOrder);
                }
            }

            return Optional.empty();
        } catch (Exception e) {
            log.error("Error processing PayPal webhook", e);
            return Optional.empty();
        }
    }

    /**
     * Gets an access token from PayPal
     * @return The access token
     */
    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                paypalApiBaseUrl + "/v1/oauth2/token",
                HttpMethod.POST,
                entity,
                String.class
        );

        JSONObject jsonResponse = new JSONObject(response.getBody());
        return jsonResponse.getString("access_token");
    }

    public String approvingOrder(final String paypalOrderId) {
        Order order = orderRepository.findByPaypalOrderId(paypalOrderId);

        if (order == null) {
            throw new NotFoundException("Order not found");
        }

        if (order.getStatus() == OrderStatus.PAID) {
            log.warn("QR code for paid order {} already exists", paypalOrderId);
            throw new IllegalArgumentException("QR code for paid order already exists");
        }

        Order updatedOrder = order.withStatus(OrderStatus.APPROVING)
                .withUpdatedAt(Instant.now());
        orderRepository.save(updatedOrder);

        return paypalCheckoutUrl + order.getPaypalOrderId();
    }
}
