package com.sofkify.orderservice.domain.ports.in;

import com.sofkify.orderservice.domain.model.Order;
import java.util.List;
import java.util.UUID;

public interface GetOrdersByCustomerUseCase {
    List<Order> getOrdersByCustomerId(UUID customerId);
}
