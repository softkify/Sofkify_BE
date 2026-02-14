package com.sofkify.productservice.infrastructure.persistence.adapter;

import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import com.sofkify.productservice.domain.enums.ProductStatus;
import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.persistence.entity.ProductEntity;
import com.sofkify.productservice.infrastructure.persistence.mapper.ProductMapper;
import com.sofkify.productservice.infrastructure.persistence.repository.JpaProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaProductRepository.findById(id).map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll()
            .stream()
            .map(ProductMapper::toDomain)
            .toList();
    }

    @Override
    public List<Product> findByStatus(String status) {
        ProductStatus productStatus = ProductStatus.valueOf(status.toUpperCase());
        return jpaProductRepository.findByStatus(productStatus)
            .stream()
            .map(ProductMapper::toDomain)
            .toList();
    }
}
