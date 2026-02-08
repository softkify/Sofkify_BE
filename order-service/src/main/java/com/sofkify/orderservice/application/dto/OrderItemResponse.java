package com.sofkify.orderservice.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderItemResponse(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("productId")
    UUID productId,
    @JsonProperty("productName")
    String productName,
    @JsonProperty("productPrice")
    BigDecimal productPrice,
    @JsonProperty("quantity")
    int quantity,
    @JsonProperty("subtotal")
    BigDecimal subtotal,
    @JsonProperty("createdAt")
    LocalDateTime createdAt
) {}
