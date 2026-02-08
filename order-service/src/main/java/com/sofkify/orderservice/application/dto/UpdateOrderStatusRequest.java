package com.sofkify.orderservice.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sofkify.orderservice.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
    @NotNull(message = "Status cannot be null")
    @JsonProperty("status")
    OrderStatus status
) {}
