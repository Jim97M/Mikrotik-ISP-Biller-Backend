package com.notification_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestEvent {
    private String to;
    private String subject;
    private String body;
    // optionally add: from, attachments, etc.
}
