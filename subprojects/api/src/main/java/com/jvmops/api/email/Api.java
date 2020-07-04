package com.jvmops.api.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;
import java.time.ZoneId;
import java.util.List;

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

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(emailsObjectMapper()));
    }
}
