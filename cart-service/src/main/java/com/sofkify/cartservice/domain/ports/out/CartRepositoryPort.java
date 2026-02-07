package com.sofkify.cartservice.domain.ports.out;

import com.sofkify.cartservice.domain.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepositoryPort {
    
    Cart save(Cart cart);
    
    Optional<Cart> findByCustomerId(UUID customerId);
}
