package com.sofkify.cartservice.domain.ports.in;

import com.sofkify.cartservice.domain.model.Cart;

import java.util.UUID;

public interface AddItemToCartUseCase {
    
    Cart addItem(UUID customerId, UUID productId, int quantity);
}
