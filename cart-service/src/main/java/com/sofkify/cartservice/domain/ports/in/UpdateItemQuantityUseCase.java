package com.sofkify.cartservice.domain.ports.in;

import com.sofkify.cartservice.domain.model.Cart;

import java.util.UUID;

public interface UpdateItemQuantityUseCase {
    
    Cart updateItemQuantity(UUID customerId, UUID cartItemId, int newQuantity);
}
