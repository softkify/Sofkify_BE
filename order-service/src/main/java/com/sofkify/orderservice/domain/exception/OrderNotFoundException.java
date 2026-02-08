package com.sofkify.orderservice.domain.exception;

import java.util.UUID;

public class OrderNotFoundException extends OrderException {
    
    public OrderNotFoundException(UUID orderId) {
        super("Order not found: " + orderId);
    }
    
    public OrderNotFoundException(String message) {
        super(message);
    }
}
