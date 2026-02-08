package com.sofkify.cartservice.infrastructure.adapters.in.rest;

import com.sofkify.cartservice.application.dto.AddItemRequest;
import com.sofkify.cartservice.application.dto.CartItemResponse;
import com.sofkify.cartservice.application.dto.CartResponse;
import com.sofkify.cartservice.application.dto.UpdateQuantityRequest;
import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.model.CartItem;
import com.sofkify.cartservice.domain.ports.in.AddItemToCartUseCase;
import com.sofkify.cartservice.domain.ports.in.GetCartUseCase;
import com.sofkify.cartservice.domain.ports.in.RemoveItemFromCartUseCase;
import com.sofkify.cartservice.domain.ports.in.UpdateItemQuantityUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final AddItemToCartUseCase addItemToCartUseCase;
    private final GetCartUseCase getCartUseCase;
    private final UpdateItemQuantityUseCase updateItemQuantityUseCase;
    private final RemoveItemFromCartUseCase removeItemFromCartUseCase;

    public CartController(AddItemToCartUseCase addItemToCartUseCase, 
                         GetCartUseCase getCartUseCase,
                         UpdateItemQuantityUseCase updateItemQuantityUseCase,
                         RemoveItemFromCartUseCase removeItemFromCartUseCase) {
        this.addItemToCartUseCase = addItemToCartUseCase;
        this.getCartUseCase = getCartUseCase;
        this.updateItemQuantityUseCase = updateItemQuantityUseCase;
        this.removeItemFromCartUseCase = removeItemFromCartUseCase;
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

    /**
     * GET /api/carts
     * 
     * Headers:
     * X-Customer-Id: UUID (required) - ID del cliente existente en user-service
     * 
     * Responses:
     * 200 - Carrito encontrado exitosamente
     * 404 - Carrito no encontrado para el cliente
     * 500 - Error interno del servidor
     */
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("X-Customer-Id") UUID customerId) {
        Cart cart = getCartUseCase.getCartByCustomerId(customerId);
        return ResponseEntity.ok(toCartResponse(cart));
    }

    /**
     * GET /api/carts/{cartId}
     * 
     * Path variable:
     * cartId: UUID (required) - ID del carrito a consultar
     * 
     * Responses:
     * 200 - Carrito encontrado exitosamente
     * 404 - Carrito no encontrado
     * 500 - Error interno del servidor
     */
    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponse> getCartById(@PathVariable UUID cartId) {
        Cart cart = getCartUseCase.getCartById(cartId);
        return ResponseEntity.ok(toCartResponse(cart));
    }

    /**
     * PUT /api/carts/items/{cartItemId}
     * 
     * Headers:
     * X-Customer-Id: UUID (required) - ID del cliente existente en user-service
     * Content-Type: application/json
     * 
     * Path variable:
     * cartItemId: UUID (required) - ID del item del carrito a actualizar
     * 
     * Body:
     * {
     *   "quantity": "Integer (required) - Nueva cantidad mayor a 0"
     * }
     * 
     * Responses:
     * 200 - Cantidad actualizada exitosamente
     * 400 - UUID inválido, cantidad inválida, carrito o item no encontrado
     * 500 - Error interno del servidor
     */
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateItemQuantity(@RequestHeader("X-Customer-Id") UUID customerId,
                                                           @PathVariable UUID cartItemId,
                                                           @Valid @RequestBody UpdateQuantityRequest request) {
        Cart cart = updateItemQuantityUseCase.updateItemQuantity(customerId, cartItemId, request.quantity());
        return ResponseEntity.ok(toCartResponse(cart));
    }

    /**
     * DELETE /api/carts/items/{cartItemId}
     * 
     * Headers:
     * X-Customer-Id: UUID (required) - ID del cliente existente en user-service
     * 
     * Path variable:
     * cartItemId: UUID (required) - ID del item del carrito a eliminar
     * 
     * Responses:
     * 200 - Item eliminado exitosamente
     * 400 - UUID inválido, carrito o item no encontrado
     * 500 - Error interno del servidor
     */
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(@RequestHeader("X-Customer-Id") UUID customerId,
                                                          @PathVariable UUID cartItemId) {
        Cart cart = removeItemFromCartUseCase.removeItemFromCart(customerId, cartItemId);
        return ResponseEntity.ok(toCartResponse(cart));
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
