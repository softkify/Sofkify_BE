package com.sofkify.orderservice.infrastructure.mapper;

import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.model.OrderItem;
import com.sofkify.orderservice.infrastructure.adapters.out.persistence.OrderItemJpaEntity;
import com.sofkify.orderservice.infrastructure.adapters.out.persistence.OrderJpaEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderJpaEntity toJpaEntity(Order order) {
        OrderJpaEntity orderJpaEntity = new OrderJpaEntity();
        orderJpaEntity.setId(order.getId());
        orderJpaEntity.setCartId(order.getCartId());
        orderJpaEntity.setCustomerId(order.getCustomerId());
        orderJpaEntity.setStatus(order.getStatus());
        orderJpaEntity.setTotalAmount(order.getTotalAmount());
        orderJpaEntity.setTotalAmountField(order.getTotalAmount()); // Ambos campos con el mismo valor
        orderJpaEntity.setCreatedAt(order.getCreatedAt());
        orderJpaEntity.setUpdatedAt(order.getCreatedAt()); // Para órdenes inmutables, updatedAt = createdAt

        List<OrderItemJpaEntity> itemEntities = order.getItems().stream()
                .map(this::toJpaEntity)
                .collect(Collectors.toList());
        
        // Set the relationship
        itemEntities.forEach(item -> item.setOrder(orderJpaEntity));
        orderJpaEntity.setItems(itemEntities);

        return orderJpaEntity;
    }

    public OrderItemJpaEntity toJpaEntity(OrderItem orderItem) {
        OrderItemJpaEntity itemJpaEntity = new OrderItemJpaEntity();
        itemJpaEntity.setId(orderItem.getId());
        itemJpaEntity.setProductId(orderItem.getProductId());
        itemJpaEntity.setProductName(orderItem.getProductName());
        itemJpaEntity.setProductPrice(orderItem.getProductPrice());
        itemJpaEntity.setQuantity(orderItem.getQuantity());
        itemJpaEntity.setSubtotal(orderItem.getSubtotal());
        itemJpaEntity.setTotalAmount(orderItem.getSubtotal()); // total_amount = subtotal para items individuales
        itemJpaEntity.setCreatedAt(orderItem.getCreatedAt());
        itemJpaEntity.setUpdatedAt(orderItem.getCreatedAt()); // Para items inmutables, updatedAt = createdAt
        return itemJpaEntity;
    }

    public Order toDomainModel(OrderJpaEntity orderJpaEntity) {
        List<OrderItem> orderItems = orderJpaEntity.getItems().stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList());

        return new Order(
                orderJpaEntity.getId(),
                orderJpaEntity.getCartId(),
                orderJpaEntity.getCustomerId(),
                orderItems
        );
    }

    public OrderItem toDomainModel(OrderItemJpaEntity itemJpaEntity) {
        return new OrderItem(
                itemJpaEntity.getId(),
                itemJpaEntity.getProductId(),
                itemJpaEntity.getProductName(),
                itemJpaEntity.getProductPrice(),
                itemJpaEntity.getQuantity()
        );
    }
}
