package com.sofkify.orderservice.application.service;

import com.sofkify.orderservice.application.dto.CartItemResponse;
import com.sofkify.orderservice.application.dto.CartResponse;
import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.model.OrderItem;
import com.sofkify.orderservice.domain.ports.in.CreateOrderFromCartUseCase;
import com.sofkify.orderservice.domain.ports.out.CartServicePort;
import com.sofkify.orderservice.domain.ports.out.OrderRepositoryPort;
import com.sofkify.orderservice.domain.exception.CartNotFoundException;
import com.sofkify.orderservice.domain.exception.InvalidCartException;
import com.sofkify.orderservice.domain.exception.OrderAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CreateOrderService implements CreateOrderFromCartUseCase {

    private final CartServicePort cartServicePort;
    private final OrderRepositoryPort orderRepositoryPort;

    public CreateOrderService(CartServicePort cartServicePort, OrderRepositoryPort orderRepositoryPort) {
        this.cartServicePort = cartServicePort;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public Order createOrderFromCart(UUID cartId) {
        // Verificar si ya existe una orden para este carrito
        if (orderRepositoryPort.existsByCartId(cartId)) {
            throw new OrderAlreadyExistsException(cartId);
        }

        // Obtener y validar el carrito
        CartResponse cartResponse = cartServicePort.getCartById(cartId);
        
        if (cartResponse == null) {
            throw new CartNotFoundException(cartId);
        }

        if (cartResponse.items().isEmpty()) {
            throw new InvalidCartException(cartId, "Cart is empty");
        }

        // Convertir items del carrito a items de orden
        List<OrderItem> orderItems = cartResponse.items().stream()
                .map(this::cartItemToOrderItem)
                .collect(Collectors.toList());

        // Crear y guardar la orden
        Order order = new Order(
                UUID.randomUUID(),
                cartId,
                cartResponse.customerId(),
                orderItems
        );

        return orderRepositoryPort.save(order);
    }

    private OrderItem cartItemToOrderItem(CartItemResponse cartItem) {
        return new OrderItem(
                UUID.randomUUID(),
                cartItem.productId(),
                cartItem.productName(),
                cartItem.productPrice(),
                cartItem.quantity()
        );
    }
}
