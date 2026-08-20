package com.notification_service.service;

public interface EmailService {
    public void sendSimpleEmail(String to, String subject, String body);
}
