package com.sofkify.orderservice.infrastructure.adapters.in.rest;

import com.sofkify.orderservice.application.dto.OrderItemResponse;
import com.sofkify.orderservice.application.dto.OrderResponse;
import com.sofkify.orderservice.application.dto.UpdateOrderStatusRequest;
import com.sofkify.orderservice.domain.model.Order;
import com.sofkify.orderservice.domain.ports.in.CreateOrderFromCartUseCase;
import com.sofkify.orderservice.domain.ports.in.GetOrderUseCase;
import com.sofkify.orderservice.domain.ports.in.GetOrdersByCustomerUseCase;
import com.sofkify.orderservice.domain.ports.in.UpdateOrderStatusUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderFromCartUseCase createOrderFromCartUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetOrdersByCustomerUseCase getOrdersByCustomerUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    public OrderController(CreateOrderFromCartUseCase createOrderFromCartUseCase,
                           GetOrderUseCase getOrderUseCase,
                           GetOrdersByCustomerUseCase getOrdersByCustomerUseCase,
                           UpdateOrderStatusUseCase updateOrderStatusUseCase) {
        this.createOrderFromCartUseCase = createOrderFromCartUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.getOrdersByCustomerUseCase = getOrdersByCustomerUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
    }

    /**
     * POST /api/orders/from-cart/{cartId}
     * 
     * Path variable:
     * cartId: UUID (required) - ID del carrito desde el cual crear la orden
     * 
     * Responses:
     * 201 - Orden creada exitosamente
     * 400 - Carrito inválido (vacío o ya procesado)
     * 404 - Carrito no encontrado
     * 409 - Ya existe una orden para este carrito
     * 500 - Error interno del servidor
     */
    @PostMapping("/from-cart/{cartId}")
    public ResponseEntity<OrderResponse> createOrderFromCart(@PathVariable UUID cartId) {
        Order order = createOrderFromCartUseCase.createOrderFromCart(cartId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toOrderResponse(order));
    }

    /**
     * GET /api/orders/{orderId}
     * 
     * Path variable:
     * orderId: UUID (required) - ID de la orden a consultar
     * 
     * Responses:
     * 200 - Orden encontrada exitosamente
     * 404 - Orden no encontrada
     * 500 - Error interno del servidor
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        Order order = getOrderUseCase.getOrderById(orderId);
        return ResponseEntity.ok(toOrderResponse(order));
    }

    /**
     * GET /api/orders/customer/{customerId}
     * 
     * Path variable:
     * customerId: UUID (required) - ID del cliente
     * 
     * Responses:
     * 200 - Lista de órdenes del cliente
     * 500 - Error interno del servidor
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(@PathVariable UUID customerId) {
        List<Order> orders = getOrdersByCustomerUseCase.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders.stream()
                .map(this::toOrderResponse)
                .toList());
    }

    /**
     * PUT /api/orders/{orderId}/status
     * 
     * Path variable:
     * orderId: UUID (required) - ID de la orden a actualizar
     * 
     * Body:
     * {
     *   "status": "PENDING|CONFIRMED|SHIPPED|DELIVERED|CANCELLED"
     * }
     * 
     * Responses:
     * 200 - Estado actualizado exitosamente
     * 400 - Estado inválido o transición no permitida
     * 404 - Orden no encontrada
     * 500 - Error interno del servidor
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable UUID orderId,
                                                           @Valid @RequestBody UpdateOrderStatusRequest request) {
        Order order = updateOrderStatusUseCase.updateOrderStatus(orderId, request.status());
        return ResponseEntity.ok(toOrderResponse(order));
    }

    private OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCartId(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getItems().stream()
                        .map(this::toOrderItemResponse)
                        .toList(),
                order.getTotalAmount(),
                order.getCreatedAt()
        );
    }

    private OrderItemResponse toOrderItemResponse(com.sofkify.orderservice.domain.model.OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getProductPrice(),
                item.getQuantity(),
                item.getSubtotal(),
                item.getCreatedAt()
        );
    }
}
