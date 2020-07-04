package com.jvmops.api.emails.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.jvmops.api.emails.model.Priority;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;
import java.time.ZoneId;
import java.util.List;

import static com.jvmops.api.emails.model.Priority.LOW;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration
@Order(HIGHEST_PRECEDENCE)
@EnableWebMvc
@EnableConfigurationProperties({ApiProperties.class, JacksonProperties.class})
@Slf4j
class Api implements WebMvcConfigurer {

    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private JacksonProperties jacksonProperties;

    @Bean
    Clock clock() {
        return Clock.system(ZoneId.of(apiProperties.getTimeOffset()));
    }

    @Bean
    ObjectMapper emailsObjectMapper() {
        return new Jackson2ObjectMapperBuilder()
                .serializerByType(ObjectId.class, new ToStringSerializer())
                .serializationInclusion(jacksonProperties.getDefaultPropertyInclusion())
                .build();
    }

    // TODO: make mongo save int to db
    @Bean
    Converter<Priority, Integer> priorityToNumber() {
        log.info("priorityToNumber");
        return Priority::getNumber;
    }

    @Bean
    Converter<Integer, Priority> numberToPriority() {
        log.info("numberToPriority");
        return value -> Priority.with(value)
                .orElse(LOW);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(emailsObjectMapper()));
    }
}
