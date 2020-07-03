package com.jvmops.workers.sender.emails.adapters;

import com.jvmops.workers.sender.emails.model.EmailMessage;
import com.jvmops.workers.sender.emails.ports.SmtpClient;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@Lazy
@Component
@Slf4j
@Profile("gmail")
@AllArgsConstructor
public class GmailClient implements SmtpClient {
    private JavaMailSender emailSender;

    @Override
    public void send(EmailMessage email) {
        try {
            MimeMessageWrapper message = prepareMessage(email.getTopic(), email.getBody());
            sendEmailToReceivers(message, email.getRecipients());
        } catch (MessagingException ex) {
            throw new GmailClientException(email, ex);
        }
    }

    private MimeMessageWrapper prepareMessage(String subject, String content) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        message.setContent(content, "text/html; charset=utf-8");
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setSubject(subject);

        return new MimeMessageWrapper(message, helper);
    }

    private void sendEmailToReceivers(MimeMessageWrapper messageWrapper, Set<String> emails) throws MessagingException {
        if (isEmpty(emails)) {
            return;
        }
        messageWrapper.helper.setTo(emails.toArray(new String[0]));
        emailSender.send(messageWrapper.message);
    }

    @Value
    @AllArgsConstructor
    private static class MimeMessageWrapper {
        MimeMessage message;
        MimeMessageHelper helper;
    }

    static class GmailClientException extends RuntimeException {
        private static final String MSG = "GMail %s account failed in sending email: {}";

        public GmailClientException(EmailMessage emailMessage, Throwable cause) {
            super(String.format(MSG, emailMessage.getId()), cause);
        }
    }
}
