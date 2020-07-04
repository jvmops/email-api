package com.jvmops.api.email.endpoints.emails.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Status {
    ABORT(-1),
    PENDING(0),
    SENT(10);

    final int value;
}