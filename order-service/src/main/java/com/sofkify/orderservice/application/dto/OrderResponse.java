package com.sofkify.orderservice.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("cartId")
    UUID cartId,
    @JsonProperty("customerId")
    UUID customerId,
    @JsonProperty("status")
    String status,
    @JsonProperty("items")
    List<OrderItemResponse> items,
    @JsonProperty("totalAmount")
    BigDecimal totalAmount,
    @JsonProperty("createdAt")
    LocalDateTime createdAt
) {}

