package com.sofkify.cartservice.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Cart {
    private final UUID id;
    private final UUID customerId;
    private CartStatus status;
    private final List<CartItem> items;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Cart(UUID id, UUID customerId) {
        this.id = Objects.requireNonNull(id, "Cart ID cannot be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID cannot be null");
        this.status = CartStatus.ACTIVE;
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addItem(UUID productId, String productName, BigDecimal productPrice, int quantity) {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(productName, "Product name cannot be null");
        Objects.requireNonNull(productPrice, "Product price cannot be null");
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // Buscar si el producto ya existe en el carrito
        Optional<CartItem> existingItem = findItemByProductId(productId);
        
        if (existingItem.isPresent()) {
            // Si existe, actualizar cantidad
            CartItem item = existingItem.get();
            item.updateQuantity(item.getQuantity() + quantity);
        } else {
            // Si no existe, crear nuevo item
            CartItem newItem = new CartItem(
                UUID.randomUUID(),
                productId,
                productName,
                productPrice,
                quantity
            );
            items.add(newItem);
        }
        
        this.updatedAt = LocalDateTime.now();
    }

    private Optional<CartItem> findItemByProductId(UUID productId) {
        return items.stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst();
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public CartStatus getStatus() { return status; }
    public List<CartItem> getItems() { return Collections.unmodifiableList(items); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(id, cart.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
