package com.sofkify.orderservice.domain.exception;

import java.util.UUID;

public class OrderAlreadyExistsException extends OrderException {
    
    public OrderAlreadyExistsException(UUID cartId) {
        super("Order already exists for cart: " + cartId);
    }
    
    public OrderAlreadyExistsException(String message) {
        super(message);
    }
}
