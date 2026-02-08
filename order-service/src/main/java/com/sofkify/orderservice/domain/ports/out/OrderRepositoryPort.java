package com.sofkify.orderservice.domain.ports.out;

import com.sofkify.orderservice.domain.model.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(UUID orderId);
    List<Order> findByCustomerId(UUID customerId);
    boolean existsByCartId(UUID cartId);
}
