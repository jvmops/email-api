package com.jvmops.worker.email.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.ErrorHandler;

@EnableConfigurationProperties({RabbitProperties.class, JacksonProperties.class})
@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "host")
@Configuration
@Slf4j
public class RabbitMQ {

    public static final String PENDING_EMAIL_QUEUE = "pendingEmails";

    @Autowired
    private RabbitProperties rabbitProperties;

    @Autowired
    private JacksonProperties jacksonProperties;

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitProperties.getHost(), rabbitProperties.getPort());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        log.info("Creating connection factory with: {}@{}:{}", rabbitProperties.getUsername(), rabbitProperties.getHost(), rabbitProperties.getPort());
        return connectionFactory;
    }

    @Bean
    ObjectMapper emailsObjectMapper() {
        return new Jackson2ObjectMapperBuilder()
                .serializerByType(ObjectId.class, new ToStringSerializer())
                .serializationInclusion(jacksonProperties.getDefaultPropertyInclusion())
                .build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory pendingEmailsListenerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitConnectionFactory());
        factory.setErrorHandler(errorHandler());
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(4);
        factory.setReceiveTimeout(3000L);

        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter(emailsObjectMapper());
        factory.setMessageConverter(messageConverter);

        return factory;
    }

    private ErrorHandler errorHandler() {
        return new ConditionalRejectingErrorHandler();
    }

    @Bean
    Queue emailMessages() {
        return QueueBuilder.durable(PENDING_EMAIL_QUEUE)
                .maxPriority(10)
                .build();
    }
}
