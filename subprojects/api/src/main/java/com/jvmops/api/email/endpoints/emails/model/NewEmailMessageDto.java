package com.jvmops.api.email.endpoints.emails.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.beans.Transient;
import java.util.Optional;
import java.util.Set;

@EqualsAndHashCode(of = {"id"})
@Data
@SuperBuilder
@NoArgsConstructor
public class NewEmailMessageDto {
    ObjectId id;

    Priority priority;

    @Email(message = "Sender is not a valid email address")
    @NotEmpty(message = "Missing sender")
    String sender;

    Set<@Email(message = "There is an invalid email address among recipients") String> recipients;

    @NotEmpty(message = "Email topic must be provided")
    @Size(max = 255, message = "Email topic length exceed 255")
    String topic;

    @NotEmpty(message = "Email body must be provided")
    @Size(max = 102000, message = "Email body length exceed 102000")
    String body;

    @Transient
    public Optional<Priority> optionalPrioryty() {
        return Optional.ofNullable(priority);
    }

    @Transient
    public Set<String> nullSafeRecipients() {
        return recipients == null ? Set.of() : recipients;
    }
}
