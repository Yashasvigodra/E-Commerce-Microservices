package com.mini.order_service.controller;

import com.mini.order_service.exceptions.SignatureVerificationException;
import com.mini.order_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    // This endpoint receives POST from Razorpay via ngrok when payment is completed on the client side and Razorpay calls the webhook URL to notify us about the payment status.
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        log.info("Received Razorpay webhook");

        try {
            paymentService.processWebhook(payload, signature);
            return ResponseEntity.ok("Webhook processed");
        } catch (SignatureVerificationException e) {
            log.error("Invalid webhook signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        } catch (Exception e) {
            log.error("Webhook processing failed: {}", e.getMessage(), e);
            // Return 200 anyway — if you return 4xx/5xx Razorpay retries
            // and you might process the same payment multiple times
            return ResponseEntity.ok("Webhook received");
        }
    }
}