package com.mini.order_service.service;

import com.mini.order_service.exceptions.SignatureVerificationException;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final OrderService orderService;

    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    public void processWebhook(String payload, String signature) {
        // Step 1: Verify this actually came from Razorpay
//        verifySignature(payload, signature);

        // Step 2: Parse the event
        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        log.info("Processing Razorpay webhook event: {}", eventType);

        // Step 3: Handle based on event type
        switch (eventType) {
            case "payment.captured" -> handlePaymentCaptured(event);
            case "payment.failed"   -> handlePaymentFailed(event);
            default -> log.info("Unhandled Razorpay event type: {}", eventType);
        }
    }

    private void handlePaymentCaptured(JSONObject event) {
        JSONObject paymentEntity = event
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayOrderId   = paymentEntity.getString("order_id");   // "order_ABC123"
        String razorpayPaymentId = paymentEntity.getString("id");          // "pay_XYZ789"
        String status            = paymentEntity.getString("status");      // "captured"

        log.info("Payment captured: paymentId={}, orderId={}", razorpayPaymentId, razorpayOrderId);

        if ("captured".equals(status)) {
            orderService.confirmOrderAfterPayment(razorpayOrderId, razorpayPaymentId);
        }
    }

    private void handlePaymentFailed(JSONObject event) {
        JSONObject paymentEntity = event
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayOrderId = paymentEntity.getString("order_id");

        log.warn("Payment failed for Razorpay order: {}", razorpayOrderId);
        orderService.handlePaymentFailure(razorpayOrderId);
    }

    private void verifySignature(String payload, String signature) {
        try {
            Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (RazorpayException e) {
            throw new SignatureVerificationException("Webhook signature verification failed");
        }
    }
}
