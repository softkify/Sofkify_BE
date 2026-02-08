package com.sofkify.orderservice.domain.ports.in;

import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.model.OrderStatus;
import java.util.UUID;

public interface UpdateOrderStatusUseCase {
    Order updateOrderStatus(UUID orderId, OrderStatus newStatus);
}
