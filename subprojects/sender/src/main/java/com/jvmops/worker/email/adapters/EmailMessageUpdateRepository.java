package com.jvmops.worker.email.adapters;

import com.jvmops.worker.email.model.EmailMessage;
import com.jvmops.worker.email.model.PendingEmailMessage;
import com.jvmops.worker.email.model.Status;
import com.jvmops.worker.email.ports.EmailMessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
@Slf4j
class EmailMessageUpdateRepository implements EmailMessageRepository {
    private MongoTemplate mongoTemplate;

    @Override
    public Optional<EmailMessage> findById(ObjectId id) {
        return Optional.ofNullable(
                mongoTemplate.findById(id, EmailMessage.class));
    }

    @Override
    public EmailMessage emailSent(EmailMessage emailMessage) {
        mongoTemplate.updateFirst(
                new Query(Criteria
                        .where("id").is(emailMessage.getId())),
                new Update()
                        .set("status", Status.SENT),
                EmailMessage.class
        );

        return emailMessage.toBuilder()
                .status(Status.SENT)
                .build();
    }

    @Override
    public void error(PendingEmailMessage emailMessage, Throwable throwable) {
        log.warn("Error events are not persisted yet");
    }
}
