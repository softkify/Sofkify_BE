package com.sofkify.orderservice.infrastructure.adapters.out.persistence;

import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.model.OrderItem;
import com.sofkify.orderservice.domain.ports.out.OrderRepositoryPort;
import com.sofkify.orderservice.infrastructure.mapper.OrderMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderMapper orderMapper;

    public OrderRepositoryAdapter(OrderJpaRepository orderJpaRepository, OrderMapper orderMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity orderJpaEntity = orderMapper.toJpaEntity(order);
        OrderJpaEntity savedEntity = orderJpaRepository.save(orderJpaEntity);
        return orderMapper.toDomainModel(savedEntity);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return orderJpaRepository.findById(orderId)
                .map(orderMapper::toDomainModel);
    }

    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        return orderJpaRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toDomainModel)
                .toList();
    }

    @Override
    public boolean existsByCartId(UUID cartId) {
        return orderJpaRepository.existsByCartId(cartId);
    }
}
