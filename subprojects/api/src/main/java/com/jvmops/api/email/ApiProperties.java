package com.jvmops.api.email;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

@ConfigurationProperties(prefix = "email.api")
@Setter
@Getter
@Slf4j
public class ApiProperties {
    private String timeOffset = "+2";

    @PostConstruct
    void log() {
        log.info("Time offset: {}", timeOffset);
    }
}