package com.sofkify.productservice.infrastructure.web.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductRequest(
    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    String name,

    @Size(max = 1000, message = "Product description must not exceed 1000 characters")
    String description,

    @NotBlank(message = "Product SKU is required")
    @Size(max = 100, message = "Product SKU must not exceed 100 characters")
    String sku,

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Product price must be greater than zero")
    BigDecimal price,

    @NotNull(message = "Product stock is required")
    @Min(value = 0, message = "Product stock cannot be negative")
    Integer stock
) {
}
