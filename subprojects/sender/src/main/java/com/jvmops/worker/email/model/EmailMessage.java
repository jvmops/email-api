package com.jvmops.worker.email.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Setter
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class EmailMessage extends PendingEmailMessage {
    String sender;
    Set<String> recipients;
    String topic;
    String body;
}
