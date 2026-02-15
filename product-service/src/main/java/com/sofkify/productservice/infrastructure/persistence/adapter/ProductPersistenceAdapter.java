package com.sofkify.productservice.infrastructure.persistence.adapter;

import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import com.sofkify.productservice.domain.enums.ProductStatus;
import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.persistence.entity.ProductEntity;
import com.sofkify.productservice.infrastructure.persistence.mapper.ProductMapper;
import com.sofkify.productservice.infrastructure.persistence.repository.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPersistencePort {
    private final JpaProductRepository jpaProductRepository;
    private final ProductMapper mapper;

    @Override
    @Transactional
    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        ProductEntity savedEntity = jpaProductRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(UUID id) {
        return jpaProductRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return jpaProductRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByStatus(String status) {
        ProductStatus productStatus = ProductStatus.valueOf(status.toUpperCase());
        return jpaProductRepository.findByStatus(productStatus)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
