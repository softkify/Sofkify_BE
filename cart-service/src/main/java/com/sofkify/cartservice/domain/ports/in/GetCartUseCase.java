package com.sofkify.cartservice.domain.ports.in;

import com.sofkify.cartservice.domain.model.Cart;

import java.util.UUID;

public interface GetCartUseCase {
    
    Cart getCartByCustomerId(UUID customerId);
    Cart getCartById(UUID cartId);
}
