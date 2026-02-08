package com.sofkify.orderservice.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class OrderItem {
    private final UUID id;
    private final UUID productId;
    private final String productName;
    private final BigDecimal productPrice;
    private final int quantity;
    private final BigDecimal subtotal;
    private final LocalDateTime createdAt;

    public OrderItem(UUID id, UUID productId, String productName, BigDecimal productPrice, int quantity) {
        this.id = Objects.requireNonNull(id, "Order item ID cannot be null");
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.productName = Objects.requireNonNull(productName, "Product name cannot be null");
        this.productPrice = Objects.requireNonNull(productPrice, "Product price cannot be null");
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (productPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }
        if (productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        
        this.quantity = quantity;
        this.subtotal = productPrice.multiply(BigDecimal.valueOf(quantity));
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getProductId() { return productId; }
    public String getProductName() { return productName; }
    public BigDecimal getProductPrice() { return productPrice; }
    public int getQuantity() { return quantity; }
    public BigDecimal getSubtotal() { return subtotal; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
