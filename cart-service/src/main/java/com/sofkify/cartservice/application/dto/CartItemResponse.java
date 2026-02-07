package com.sofkify.cartservice.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CartItemResponse(
    UUID id,
    UUID productId,
    String productName,
    BigDecimal productPrice,
    int quantity,
    BigDecimal subtotal,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
