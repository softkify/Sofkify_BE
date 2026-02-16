package com.sofkify.productservice.infrastructure.persistence.repository;

import com.sofkify.productservice.domain.enums.ProductStatus;
import com.sofkify.productservice.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findByStatus(ProductStatus status);
    
    boolean existsBySku(String sku);
}
