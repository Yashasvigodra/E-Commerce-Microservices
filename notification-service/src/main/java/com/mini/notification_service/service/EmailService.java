package com.mini.notification_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendOrderConfirmation(String receiverEmail,String customerName ,  String orderNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(receiverEmail);
        message.setSubject("Order Confirmation - " + orderNumber);
        message.setText("Hii"+ customerName +" Your order with order number " + orderNumber + " has been placed successfully. Thank you for shopping with us!");

        mailSender.send(message);

    }
}
