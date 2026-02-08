package com.sofkify.orderservice.domain.exception;

import java.util.UUID;

public class InvalidCartException extends OrderException {
    
    public InvalidCartException(UUID cartId, String reason) {
        super("Invalid cart " + cartId + ": " + reason);
    }
    
    public InvalidCartException(String message) {
        super(message);
    }
}
