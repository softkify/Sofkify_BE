package com.sofkify.productservice.infrastructure.persistence.adapter;

import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.persistence.entity.ProductEntity;
import com.sofkify.productservice.infrastructure.persistence.mapper.ProductMapper;
import com.sofkify.productservice.infrastructure.persistence.repository.JpaProductRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceAdapter implements ProductPersistencePort {

    private final JpaProductRepository jpaProductRepository;

    public ProductPersistenceAdapter(JpaProductRepository jpaProductRepository) {
        this.jpaProductRepository = jpaProductRepository;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = ProductMapper.toEntity(product);
        ProductEntity savedEntity = jpaProductRepository.save(entity);
        return ProductMapper.toDomain(savedEntity);
    }
}
