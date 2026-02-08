package com.sofkify.orderservice.application.service;

import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.model.OrderStatus;
import com.sofkify.orderservice.domain.ports.in.UpdateOrderStatusUseCase;
import com.sofkify.orderservice.domain.ports.out.OrderRepositoryPort;
import com.sofkify.orderservice.domain.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UpdateOrderStatusService implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public UpdateOrderStatusService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public Order updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.updateStatus(newStatus);

        return orderRepositoryPort.save(order);
    }
}
