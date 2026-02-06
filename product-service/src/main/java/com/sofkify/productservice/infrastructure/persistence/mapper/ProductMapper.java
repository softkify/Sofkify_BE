package com.sofkify.productservice.infrastructure.persistence.mapper;

import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.domain.enums.ProductStatus;
import com.sofkify.productservice.infrastructure.dto.product.response.ProductResponse;
import com.sofkify.productservice.infrastructure.persistence.entity.ProductEntity;

public class ProductMapper {

    public static ProductEntity toEntity(Product product) {
        return new ProductEntity(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getStatus()
        );
    }

    public static Product toDomain(ProductEntity entity) {
        return Product.restore(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getPrice(),
            entity.getStock(),
            entity.getStatus()
        );
    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getStatus()
        );
    }
}
