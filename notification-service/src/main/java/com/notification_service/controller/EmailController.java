package com.notification_service.controller;

import com.notification_service.dto.request.EmailRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailRequestEvent event) {
        kafkaTemplate.send("email-request-topic", event);
        return "Email request published to Kafka";
    }
}
