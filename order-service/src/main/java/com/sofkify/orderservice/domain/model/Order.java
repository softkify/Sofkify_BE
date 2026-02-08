package com.sofkify.orderservice.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final UUID cartId;
    private final UUID customerId;
    private OrderStatus status;
    private final List<OrderItem> items;
    private final BigDecimal totalAmount;
    private final LocalDateTime createdAt;

    public Order(UUID id, UUID cartId, UUID customerId, List<OrderItem> items) {
        this.id = Objects.requireNonNull(id, "Order ID cannot be null");
        this.cartId = Objects.requireNonNull(cartId, "Cart ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.items = new ArrayList<>(Objects.requireNonNull(items, "Order items cannot be null"));
        
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        this.status = OrderStatus.PENDING_PAYMENT;
        this.totalAmount = calculateTotalAmount();
        this.createdAt = LocalDateTime.now();
    }

    private BigDecimal calculateTotalAmount() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updateStatus(OrderStatus newStatus) {
        Objects.requireNonNull(newStatus, "Order status cannot be null");
        
        // Validar transiciones de estado permitidas
        if (status == OrderStatus.CANCELLED && newStatus != OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of cancelled order");
        }
        
        this.status = newStatus;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCartId() { return cartId; }
    public UUID getCustomerId() { return customerId; }
    public OrderStatus getStatus() { return status; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
