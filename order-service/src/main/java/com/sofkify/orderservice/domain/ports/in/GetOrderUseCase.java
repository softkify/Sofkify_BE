package com.sofkify.orderservice.domain.ports.in;

import com.sofkify.orderservice.domain.model.Order;
import java.util.UUID;

public interface GetOrderUseCase {
    Order getOrderById(UUID orderId);
}
