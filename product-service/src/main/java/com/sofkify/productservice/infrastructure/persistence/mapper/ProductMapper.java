package com.sofkify.productservice.infrastructure.persistence.mapper;

import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductEntity toEntity(Product product) {
        return new ProductEntity(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getSku(),
            product.getPrice(),
            product.getStock(),
            product.getStatus()
        );
    }

    public Product toDomain(ProductEntity entity) {
        return Product.reconstitute(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getSku(),
            entity.getPrice(),
            entity.getStock(),
            entity.getStatus()
        );
    }
}
