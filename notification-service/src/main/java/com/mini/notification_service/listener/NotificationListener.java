package com.mini.notification_service.listener;


//import com.mini.notification_service.client.CustomerClient;
//import com.mini.notification_service.dto.CustomerResponse;
import com.mini.notification_service.event.OrderPlacedEvent;
import com.mini.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final EmailService emailService;
//    private final CustomerClient customerClient;

    @KafkaListener(topics = "notificationTopic")
    public void handleNotification(OrderPlacedEvent event){

        log.info("Received notification for order: {}, customer: {}",
                event.getOrderNumber(), event.getCustomerName());


        try {


            log.info("sending confirmation to : {}", event.getCustomerEmail());
            emailService.sendOrderConfirmation(event.getCustomerEmail(), event.getCustomerName() ,event.getOrderNumber());
        } catch (Exception e){
            // Don't let a failed email crash the Kafka consumer
            // The offset will be committed and message won't be reprocessed
            log.error("Failed to send notification for order: {}. Reason: {}",
                    event.getOrderNumber(), e.getMessage());

        }
    }
}
