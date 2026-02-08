package com.sofkify.orderservice.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CartResponse(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("customerId")
    UUID customerId,
    @JsonProperty("status")
    String status,
    @JsonProperty("items")
    List<CartItemResponse> items,
    @JsonProperty("totalAmount")
    BigDecimal totalAmount,
    @JsonProperty("createdAt")
    LocalDateTime createdAt,
    @JsonProperty("updatedAt")
    LocalDateTime updatedAt
) {}
