package com.sofkify.cartservice.application.service;

import com.sofkify.cartservice.domain.exception.CartException;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.model.CartItem;
import com.sofkify.cartservice.domain.ports.in.UpdateItemQuantityUseCase;
import com.sofkify.cartservice.domain.ports.out.CartRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UpdateItemQuantityService implements UpdateItemQuantityUseCase {

    private final CartRepositoryPort cartRepository;

    public UpdateItemQuantityService(CartRepositoryPort cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Cart updateItemQuantity(UUID customerId, UUID cartItemId, int newQuantity) {
        // Obtener el carrito del cliente
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CartException("Cart not found for customer: " + customerId));

        // Buscar el item específico por su ID
        Optional<CartItem> itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst();

        if (itemToUpdate.isEmpty()) {
            throw new CartException("Cart item not found: " + cartItemId);
        }

        // Actualizar la cantidad
        CartItem item = itemToUpdate.get();
        item.updateQuantity(newQuantity);

        // Guardar el carrito actualizado
        return cartRepository.save(cart);
    }
}
