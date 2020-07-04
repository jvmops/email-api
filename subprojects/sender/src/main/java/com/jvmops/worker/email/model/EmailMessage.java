package com.jvmops.worker.email.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document("emails")
@EqualsAndHashCode(of = "id")
@Value
@Builder(toBuilder=true)
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
}
