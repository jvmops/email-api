package com.jvmops.api.email.endpoints.emails

import com.jvmops.api.email.endpoints.emails.model.EmailMessage
import com.jvmops.api.email.endpoints.emails.ports.PendingEmailsQueue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfig {
    @Bean
    PendingEmailsQueue dummyEmailsQueue() {
        return new DummyEmailsQueue()
    }

    class DummyEmailsQueue implements PendingEmailsQueue {
        @Override
        void offer(EmailMessage emailMessage) {
        }
    }
}
