package com.sofkify.productservice.infrastructure.web.dto.response;

import com.sofkify.productservice.domain.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
    UUID productId,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    ProductStatus status
) {
}
