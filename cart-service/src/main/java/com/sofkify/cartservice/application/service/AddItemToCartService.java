package com.sofkify.cartservice.application.service;

import com.sofkify.cartservice.domain.exception.CartException;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.ports.in.AddItemToCartUseCase;
import com.sofkify.cartservice.domain.ports.out.CartRepositoryPort;
import com.sofkify.cartservice.domain.ports.out.ProductServicePort;
import com.sofkify.cartservice.domain.ports.out.UserServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AddItemToCartService implements AddItemToCartUseCase {

    private final CartRepositoryPort cartRepository;
    private final ProductServicePort productServicePort;
    private final UserServicePort userServicePort;

    public AddItemToCartService(CartRepositoryPort cartRepository, 
                             ProductServicePort productServicePort,
                             UserServicePort userServicePort) {
        this.cartRepository = cartRepository;
        this.productServicePort = productServicePort;
        this.userServicePort = userServicePort;
    }

    @Override
    public Cart addItem(UUID customerId, UUID productId, int quantity) {
        
        // Validar usuario
        if (!userServicePort.validateUser(customerId)) {
            throw new CartException("Invalid or inactive customer: " + customerId);
        }
        
        // Validar producto
        ProductServicePort.ProductInfo productInfo = productServicePort.getProduct(productId);
        
        if (!productInfo.active()) {
            throw new CartException("Product is not active: " + productId);
        }
        
        if (!productServicePort.validateStock(productId, quantity)) {
            throw new CartException("Insufficient stock for product: " + productId);
        }
        
        // Obtener o crear carrito
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> new Cart(UUID.randomUUID(), customerId));
        
        // Agregar item al carrito
        cart.addItem(
            productInfo.id(),
            productInfo.name(),
            productInfo.price(),
            quantity
        );
        
        // Guardar carrito actualizado
        return cartRepository.save(cart);
    }
}
