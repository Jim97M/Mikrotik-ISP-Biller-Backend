package com.notification_service.listener;

import com.notification_service.dto.request.EmailRequestEvent;
import com.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private static final Logger logger  = LoggerFactory.getLogger(EmailEventListener.class);


    private final EmailService emailService;

    @KafkaListener(topics = "email-request-topic", groupId = "notification-group")
    public void handleEmailRequest(EmailRequestEvent event) {
        logger.info("Received email request for: {}", event.getTo());
        emailService.sendSimpleEmail(event.getTo(), event.getSubject(), event.getBody());
        // Optionally produce a success/failure event to another topic
    }
}
