package com.jvmops.api.email.endpoints.emails.ports;

import com.jvmops.api.email.endpoints.emails.model.EmailMessage;

public interface PendingEmailsQueue {
    void offer(EmailMessage emailMessage);
}
