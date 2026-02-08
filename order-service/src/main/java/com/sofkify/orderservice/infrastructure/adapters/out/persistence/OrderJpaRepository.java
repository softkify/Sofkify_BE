package com.sofkify.orderservice.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {
    
    Optional<OrderJpaEntity> findByCartId(UUID cartId);
    
    List<OrderJpaEntity> findByCustomerId(UUID customerId);
    
    boolean existsByCartId(UUID cartId);
}
