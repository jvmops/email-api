package com.jvmops.worker.email.ports;

import com.jvmops.worker.email.model.EmailMessage;
import com.jvmops.worker.email.model.PendingEmailMessage;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.util.Optional;

public interface EmailMessageRepository {
    Optional<EmailMessage> findById(ObjectId id);
    EmailMessage emailSent(EmailMessage message);
    void error(PendingEmailMessage emailMessage, Throwable throwable);

    @Getter
    class NuSuchEmailMessage extends RuntimeException {
        private static final String MSG = "There is no email message with such id: %s";

        private ObjectId objectId;

        public NuSuchEmailMessage(ObjectId objectId) {
            super(String.format(MSG, objectId.toString()));
            this.objectId = objectId;
        }
    }
}
