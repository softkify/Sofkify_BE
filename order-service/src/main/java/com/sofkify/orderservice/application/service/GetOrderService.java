package com.sofkify.orderservice.application.service;

import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.ports.in.GetOrderUseCase;
import com.sofkify.orderservice.domain.ports.in.GetOrdersByCustomerUseCase;
import com.sofkify.orderservice.domain.ports.out.OrderRepositoryPort;
import com.sofkify.orderservice.domain.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetOrderService implements GetOrderUseCase, GetOrdersByCustomerUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public GetOrderService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public Order getOrderById(UUID orderId) {
        return orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    public List<Order> getOrdersByCustomerId(UUID customerId) {
        return orderRepositoryPort.findByCustomerId(customerId);
    }
}
