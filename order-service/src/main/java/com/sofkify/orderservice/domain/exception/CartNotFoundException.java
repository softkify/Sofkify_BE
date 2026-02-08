package com.sofkify.orderservice.domain.exception;

import java.util.UUID;

public class CartNotFoundException extends OrderException {
    
    public CartNotFoundException(UUID cartId) {
        super("Cart not found: " + cartId);
    }
    
    public CartNotFoundException(String message) {
        super(message);
    }
}
