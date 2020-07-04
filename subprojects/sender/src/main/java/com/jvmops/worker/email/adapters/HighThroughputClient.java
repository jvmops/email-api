package com.jvmops.worker.email.adapters;

import com.jvmops.worker.email.ports.SmtpClient;
import com.jvmops.worker.email.model.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
@Primary
@Profile("prod")
public class HighThroughputClient implements SmtpClient {
    @Override
    public void send(EmailMessage email) {
        throw new UnableToSendEmail(email.getId(), this.getClass());
    }

    @PostConstruct
    @SuppressWarnings("PMD")
    private void log() {
        log.warn("High throughput SMTP client registered!");
    }
}
