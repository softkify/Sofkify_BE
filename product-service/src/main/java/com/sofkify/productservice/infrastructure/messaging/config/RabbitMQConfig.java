package com.sofkify.productservice.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchanges.order}")
    private String orderExchange;

    @Value("${rabbitmq.queues.stock-decrement}")
    private String stockDecrementQueue;

    @Value("${rabbitmq.routing-keys.order-created}")
    private String orderCreatedRoutingKey;

    @Value("${rabbitmq.routing-keys.stock-decremented}")
    private String stockDecrementedRoutingKey;

    @Value("${rabbitmq.routing-keys.order-failed}")
    private String orderFailedRoutingKey;

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(orderExchange);
    }

    @Bean
    public Queue stockDecrementQueue() {
        return QueueBuilder.durable(stockDecrementQueue).build();
    }

    @Bean
    public Binding stockDecrementBinding() {
        return BindingBuilder
                .bind(stockDecrementQueue())
                .to(orderExchange())
                .with(orderCreatedRoutingKey);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
