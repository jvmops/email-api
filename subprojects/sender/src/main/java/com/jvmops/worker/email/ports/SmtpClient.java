package com.jvmops.worker.email.ports;

import com.jvmops.worker.email.model.EmailMessage;
import com.jvmops.worker.email.model.Status;
import org.bson.types.ObjectId;

import java.util.function.Function;

public interface SmtpClient {
    void send(EmailMessage email);

    default Function<EmailMessage, EmailMessage> messageSent() {
        return emailMessage -> emailMessage.toBuilder()
                .status(Status.SENT)
                .build();
    }

    class UnableToSendEmail extends RuntimeException {
        private static final String MSG = "%s was unable to send email: %s";

        public UnableToSendEmail(ObjectId emailMessageId, Class implementation, Exception cause) {
            super(getMessage(emailMessageId, implementation), cause);
        }

        public UnableToSendEmail(ObjectId emailMessageId, Class implementation) {
            super(getMessage(emailMessageId, implementation));
        }

        private static String getMessage(ObjectId emailMessageId, Class implementation) {
            return String.format(MSG, emailMessageId, implementation.getSimpleName());
        }
    }
}
