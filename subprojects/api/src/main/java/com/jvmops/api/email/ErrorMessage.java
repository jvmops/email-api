package com.jvmops.api.email;

import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

@Value
@Builder
public class ErrorMessage {
    String message;
    int statusCode;
    String error;
    String path;
    OffsetDateTime timestamp;
}