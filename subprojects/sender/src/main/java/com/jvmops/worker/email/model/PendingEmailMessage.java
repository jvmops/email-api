package com.jvmops.worker.email.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bson.types.ObjectId;

@EqualsAndHashCode(of = "id")
@Value
@Builder
public class PendingEmailMessage {
    ObjectId id;
}
