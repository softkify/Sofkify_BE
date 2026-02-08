package com.sofkify.orderservice.domain.model;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    FAILED
}
