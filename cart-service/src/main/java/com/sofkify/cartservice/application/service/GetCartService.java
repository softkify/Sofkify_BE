package com.sofkify.cartservice.application.service;

import com.sofkify.cartservice.domain.exception.CartException;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.ports.in.GetCartUseCase;
import com.sofkify.cartservice.domain.ports.out.CartRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetCartService implements GetCartUseCase {
    private final CartRepositoryPort cartRepository;
    
    public GetCartService(CartRepositoryPort cartRepository) {
        this.cartRepository = cartRepository;
    }
    
    @Override
    public Cart getCartByCustomerId(UUID customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CartException("Cart not found for customer: " + customerId));
    }
    
    @Override
    public Cart getCartById(UUID cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new CartException("Cart not found: " + cartId));
    }
}
