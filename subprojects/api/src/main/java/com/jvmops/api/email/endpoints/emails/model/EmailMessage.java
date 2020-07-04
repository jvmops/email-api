package com.jvmops.api.email.endpoints.emails.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "emails")
@Value
@EqualsAndHashCode(of = "id")
@Builder
public class EmailMessage {
    @Id
    ObjectId id;
    String sender;
    Set<String> recipients;
    String topic;
    String body;
    @Indexed
    Status status;
    Priority priority;
    @Version
    Long version;
}
