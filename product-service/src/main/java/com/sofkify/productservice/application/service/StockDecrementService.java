package com.sofkify.productservice.application.service;

import com.sofkify.productservice.application.dto.OrderCreatedEventDTO;
import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import com.sofkify.productservice.domain.ports.in.HandleOrderCreatedUseCase;
import com.sofkify.productservice.domain.exception.InsufficientStockException;
import com.sofkify.productservice.domain.exception.ProductNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class StockDecrementService implements HandleOrderCreatedUseCase {

    private static final Logger logger = LoggerFactory.getLogger(StockDecrementService.class);

    private final ProductPersistencePort productPersistencePort;

    public StockDecrementService(ProductPersistencePort productPersistencePort) {
        this.productPersistencePort = productPersistencePort;
    }

    @Override
    public void handleOrderCreated(OrderCreatedEventDTO event) {
        logger.info("Handling OrderCreatedEvent for order: {}", event.orderId());

        try {
            for (OrderCreatedEventDTO.OrderItemEventDTO item : event.items()) {
                decrementStock(item.productId(), item.quantity(), event.orderId());
            }

            logger.info("Successfully decremented stock for order: {}", event.orderId());

        } catch (Exception e) {
            logger.error("Error handling OrderCreatedEvent for order: {}", event.orderId(), e);

            // Aquí podríamos publicar un evento de error
            // orderEventPublisher.publishOrderFailed(event.orderId(), e.getMessage());

            throw new RuntimeException("Failed to process order: " + event.orderId(), e);
        }
    }

    private void decrementStock(UUID productId, int quantity, UUID orderId) {
        logger.debug("Decrementing {} units for product: {} (order: {})", quantity, productId, orderId);

        var product = productPersistencePort.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

        if (product.getStock() < quantity) {
            throw new InsufficientStockException(
                String.format("Insufficient stock for product %s. Available: %d, Required: %d",
                    productId, product.getStock(), quantity)
            );
        }

        product.decrementStock(quantity);
        productPersistencePort.save(product);

        logger.debug("Stock decremented successfully for product: {}. New stock: {}",
            productId, product.getStock());
    }
}
