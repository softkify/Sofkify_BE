package com.sofkify.cartservice.domain.ports.out;

import java.util.UUID;

public interface ProductServicePort {
    
    ProductInfo getProduct(UUID productId);
    
    boolean validateStock(UUID productId, int requiredQuantity);
    
    record ProductInfo(
        UUID id,
        String name,
        java.math.BigDecimal price,
        int stock,
        boolean active
    ) {}
}
