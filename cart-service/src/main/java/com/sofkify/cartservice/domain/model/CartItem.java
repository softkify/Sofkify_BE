package com.sofkify.cartservice.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class CartItem {
    private final UUID id;
    private final UUID productId;
    private final String productName;
    private final BigDecimal productPrice;
    private int quantity;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CartItem(UUID id, UUID productId, String productName, BigDecimal productPrice, int quantity) {
        this.id = Objects.requireNonNull(id, "Cart item ID cannot be null");
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.productName = Objects.requireNonNull(productName, "Product name cannot be null");
        this.productPrice = Objects.requireNonNull(productPrice, "Product price cannot be null");
        setQuantity(quantity);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        validate();
    }

    private void validate() {
        if (productPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
    }

    public void updateQuantity(int newQuantity) {
        setQuantity(newQuantity);
        this.updatedAt = LocalDateTime.now();
    }

    private void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getProductId() { return productId; }
    public String getProductName() { return productName; }
    public BigDecimal getProductPrice() { return productPrice; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(id, cartItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
