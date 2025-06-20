package de.ckollmeier.burgerexpress.backend.service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import de.ckollmeier.burgerexpress.backend.exceptions.CreateStripeSessionException;
import de.ckollmeier.burgerexpress.backend.exceptions.NotFoundException;
import de.ckollmeier.burgerexpress.backend.exceptions.StripeQrCodeGenerationException;
import de.ckollmeier.burgerexpress.backend.model.Order;
import de.ckollmeier.burgerexpress.backend.repository.OrderRepository;
import de.ckollmeier.burgerexpress.backend.types.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentService {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.secret-sig}")
    private String stripeSecretSig;

    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    private static void setStripeApiKey(String stripeSecretKey) {
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * Creates a Stripe Checkout session for the given order
     * @param order the order
     * @return URL to the hosted checkout page
     */
    public Order createCheckoutSession(Order order) {
        try {
            setStripeApiKey(stripeSecretKey);

            long amountInCents = (long) (order.getTotalPrice().doubleValue() * 100);

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(appBaseUrl + "/payment-return.html")
                    .setCancelUrl(appBaseUrl + "/checkout")
                    .setClientReferenceId(order.getId())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("eur")
                                                    .setUnitAmount(amountInCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Burger Express Bestellung")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);

            return orderService.saveOrder(order.withStripePaymentOrderId(session.getUrl()));
        } catch (StripeException e) {
            log.error("Error creating Stripe Checkout Session", e);
            throw new CreateStripeSessionException("Error creating Stripe Checkout Session", e);
        }
    }

    /**
     * Updates the order status based on Stripe Checkout session
     */
    public void handleCheckoutSessionCompleted(String orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order = order.withStatus(OrderStatus.PAID);
            orderService.saveOrder(order);
        });
    }

    public String generateQrCode(Order order) {
        try {
            int width = 300;
            int height = 300;
            BitMatrix bitMatrix = new MultiFormatWriter().encode(appBaseUrl + "/api/stripe/approving/" + order.getStripePaymentOrderIdHash(), BarcodeFormat.QR_CODE, width, height);

            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", outputStream);

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("Error generating QR code", e);
            throw new StripeQrCodeGenerationException("QR code generation failed", e);
        }
    }

    public void processWebhook(String payload, String sigHeader) throws SignatureVerificationException {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, stripeSecretSig);

            if ("checkout.session.completed".equals(event.getType())) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                if (session != null && session.getClientReferenceId() != null) {
                    handleCheckoutSessionCompleted(session.getClientReferenceId());
                }
            }
        } catch (SignatureVerificationException e) {
            log.error("Ungültige Stripe-Signatur", e);
            throw e;
        } catch (Exception e) {
            log.error("Stripe webhook processing failed", e);
        }
    }

    /**
     * Prüft und gibt die Weiterleitungs-URL für eine Stripe-Zahlungsbestellung zurück.
     * Leitet abhängig vom Zahlungsstatus des Auftrags weiter.
     *
     * @param stripePaymentOrderIdHash die hashed Stripe Checkout Session ID (OrderId)
     * @return Redirect-URL als String
     */
    public String approvingOrder(String stripePaymentOrderIdHash) {
        // Hole die Order anhand der Stripe-Order-ID (Checkout Session ID)
        Order order = orderRepository.findByStripePaymentOrderIdHash(stripePaymentOrderIdHash)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.PAID) {
            log.warn("QR code for paid order {} already exists", stripePaymentOrderIdHash);
            throw new IllegalArgumentException("QR code for paid order already exists");
        }

        Order updatedOrder = order.withStatus(OrderStatus.APPROVING);
        orderService.saveOrder(updatedOrder);

        return order.getStripePaymentOrderId();
    }
}