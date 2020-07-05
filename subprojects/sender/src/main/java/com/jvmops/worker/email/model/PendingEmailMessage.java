package com.jvmops.worker.email.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;

@EqualsAndHashCode(of = {"id"})
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class PendingEmailMessage {
    @Id
    ObjectId id;

    transient Priority priority;

    @Indexed
    Status status;
    @Version
    Long version;
}
