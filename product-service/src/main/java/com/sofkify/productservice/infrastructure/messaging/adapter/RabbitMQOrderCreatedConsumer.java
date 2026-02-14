package com.sofkify.productservice.infrastructure.messaging.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofkify.productservice.application.dto.OrderCreatedEventDTO;
import com.sofkify.productservice.domain.ports.in.HandleOrderCreatedUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQOrderCreatedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQOrderCreatedConsumer.class);

    private final ObjectMapper objectMapper;
    private final HandleOrderCreatedUseCase handleOrderCreatedUseCase;

    @RabbitListener(queues = "${rabbitmq.queues.stock-decrement}")
    public void handleOrderCreated(String message) {
        logger.info("Received OrderCreatedEvent message: {}", message);

        try {
            OrderCreatedEventDTO event = objectMapper.readValue(message, OrderCreatedEventDTO.class);
            logger.info("Parsed OrderCreatedEvent for order: {}", event.orderId());

            handleOrderCreatedUseCase.handleOrderCreated(event);

            logger.info("Successfully processed OrderCreatedEvent for order: {}", event.orderId());

        } catch (JsonProcessingException e) {
            logger.error("Error parsing OrderCreatedEvent message: {}", message, e);
            // Aquí podríamos mover el mensaje a una Dead Letter Queue
            throw new RuntimeException("Failed to parse OrderCreatedEvent", e);
        } catch (Exception e) {
            logger.error("Error processing OrderCreatedEvent", e);
            throw new RuntimeException("Failed to process OrderCreatedEvent", e);
        }
    }
}
