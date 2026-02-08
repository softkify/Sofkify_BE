package com.sofkify.cartservice.infrastructure.adapters.out.persistence;

import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.ports.out.CartRepositoryPort;
import com.sofkify.cartservice.infrastructure.mapper.CartMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CartRepositoryAdapter implements CartRepositoryPort {

    private final CartJpaRepository cartJpaRepository;
    private final CartMapper cartMapper;

    public CartRepositoryAdapter(CartJpaRepository cartJpaRepository, CartMapper cartMapper) {
        this.cartJpaRepository = cartJpaRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    public Cart save(Cart cart) {
        CartJpaEntity jpaEntity = cartMapper.toJpaEntity(cart);
        CartJpaEntity savedEntity = cartJpaRepository.save(jpaEntity);
        return cartMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Cart> findByCustomerId(UUID customerId) {
        return cartJpaRepository.findByCustomerId(customerId)
                .map(cartMapper::toDomainEntity);
    }

    @Override
    public Optional<Cart> findById(UUID cartId) {
        return cartJpaRepository.findById(cartId)
                .map(cartMapper::toDomainEntity);
    }
}
