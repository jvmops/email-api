package com.jvmops.services.emails.model;

import com.jvmops.api.emails.model.Priority;
import com.jvmops.api.emails.model.Status;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "emails")
@Value
@EqualsAndHashCode(of = "id")
public class PendingEmailMessage {
    @Id
    ObjectId id;
    @Indexed
    Status status;
    Priority priority;
}
