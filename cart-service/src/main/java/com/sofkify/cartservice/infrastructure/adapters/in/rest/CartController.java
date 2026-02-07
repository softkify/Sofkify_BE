package com.sofkify.cartservice.infrastructure.adapters.in.rest;

import com.sofkify.cartservice.application.dto.AddItemRequest;
import com.sofkify.cartservice.application.dto.CartItemResponse;
import com.sofkify.cartservice.application.dto.CartResponse;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.model.CartItem;
import com.sofkify.cartservice.domain.ports.in.AddItemToCartUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final AddItemToCartUseCase addItemToCartUseCase;

    public CartController(AddItemToCartUseCase addItemToCartUseCase) {
        this.addItemToCartUseCase = addItemToCartUseCase;
    }

    /**
     * POST /api/carts/items
     * 
     * Headers:
     * X-Customer-Id: UUID (required) - ID del cliente existente en user-service
     * Content-Type: application/json
     * 
     * Body:
     * {
     *   "productId": "UUID (required) - ID del producto existente en product-service",
     *   "quantity": "Integer (required) - Cantidad mayor a 0"
     * }
     * 
     * Responses:
     * 201 - Item agregado exitosamente
     * 400 - UUID inválido, cantidad inválida, usuario/producto no encontrado o inactivo
     * 500 - Error interno del servidor
     */
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@RequestHeader("X-Customer-Id") UUID customerId,
                                           @Valid @RequestBody AddItemRequest request) {
        Cart cart = addItemToCartUseCase.addItem(
            customerId,
            request.productId(),
            request.quantity()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toCartResponse(cart));
    }

    private CartResponse toCartResponse(Cart cart) {
        return new CartResponse(
            cart.getId(),
            cart.getCustomerId(),
            cart.getStatus().name(),
            cart.getItems().stream()
                .map(this::toCartItemResponse)
                .toList(),
            cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add),
            cart.getCreatedAt(),
            cart.getUpdatedAt()
        );
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        return new CartItemResponse(
            item.getId(),
            item.getProductId(),
            item.getProductName(),
            item.getProductPrice(),
            item.getQuantity(),
            item.getSubtotal(),
            item.getCreatedAt(),
            item.getUpdatedAt()
        );
    }
}
