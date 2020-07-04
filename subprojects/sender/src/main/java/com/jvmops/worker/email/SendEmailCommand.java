package com.jvmops.worker.email;

import com.jvmops.worker.email.model.EmailMessage;
import com.jvmops.worker.email.model.PendingEmailMessage;
import com.jvmops.worker.email.ports.EmailMessageRepository;
import com.jvmops.worker.email.ports.EmailMessageRepository.NuSuchEmailMessage;
import com.jvmops.worker.email.ports.SmtpClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
class SendEmailCommand {
    private SmtpClient smtpClient;
    private EmailMessageRepository emailMessageRepository;

    @RabbitListener(queues = "pendingEmails", containerFactory = "pendingEmailsListenerFactory")
    public void send(PendingEmailMessage pendingMessage) {
        geyById().andThen(send())
                .apply(pendingMessage);
    }

    private Function<PendingEmailMessage, EmailMessage> geyById() {
        return pendingMessage -> emailMessageRepository.findById(pendingMessage.getId())
                .orElseThrow(() -> new NuSuchEmailMessage(pendingMessage.getId()));
    }

    // It's a Consumer, but how to compose this type here?
    // If I had stream of data I could have use of that but otherwise I can't?
    private Function<EmailMessage, EmailMessage> send() {
        return email -> {
                smtpClient.send(email);
                return emailMessageRepository.emailSent(email);
        };
    }
}
