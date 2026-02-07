package com.sofkify.cartservice.infrastructure.mapper;

import com.sofkify.cartservice.domain.model.Cart;
import com.sofkify.cartservice.domain.model.CartItem;
import com.sofkify.cartservice.domain.model.CartStatus;
import com.sofkify.cartservice.infrastructure.adapters.out.persistence.CartJpaEntity;
import com.sofkify.cartservice.infrastructure.adapters.out.persistence.CartItemJpaEntity;
import com.sofkify.cartservice.infrastructure.adapters.out.persistence.CartStatusJpa;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartJpaEntity toJpaEntity(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartJpaEntity jpaEntity = new CartJpaEntity();
        jpaEntity.setId(cart.getId());
        jpaEntity.setCustomerId(cart.getCustomerId());
        jpaEntity.setStatus(CartStatusJpa.ACTIVE);
        jpaEntity.setCreatedAt(cart.getCreatedAt());
        jpaEntity.setUpdatedAt(cart.getUpdatedAt());

        if (cart.getItems() != null) {
            jpaEntity.setItems(cart.getItems().stream()
                    .map(item -> toJpaEntity(item, jpaEntity))
                    .collect(Collectors.toList()));
        }

        return jpaEntity;
    }

    public CartItemJpaEntity toJpaEntity(CartItem cartItem, CartJpaEntity cart) {
        if (cartItem == null) {
            return null;
        }

        CartItemJpaEntity jpaEntity = new CartItemJpaEntity();
        jpaEntity.setId(cartItem.getId());
        jpaEntity.setCart(cart);
        jpaEntity.setProductId(cartItem.getProductId());
        jpaEntity.setProductName(cartItem.getProductName());
        jpaEntity.setProductPrice(cartItem.getProductPrice());
        jpaEntity.setQuantity(cartItem.getQuantity());
        jpaEntity.setCreatedAt(cartItem.getCreatedAt());
        jpaEntity.setUpdatedAt(cartItem.getUpdatedAt());

        return jpaEntity;
    }

    public Cart toDomainEntity(CartJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        Cart cart = new Cart(jpaEntity.getId(), jpaEntity.getCustomerId());
        
        if (jpaEntity.getItems() != null) {
            jpaEntity.getItems().stream()
                    .map(this::toDomainEntity)
                    .forEach(item -> cart.addItem(
                        item.getProductId(),
                        item.getProductName(),
                        item.getProductPrice(),
                        item.getQuantity()
                    ));
        }

        return cart;
    }

    public CartItem toDomainEntity(CartItemJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return new CartItem(
                jpaEntity.getId(),
                jpaEntity.getProductId(),
                jpaEntity.getProductName(),
                jpaEntity.getProductPrice(),
                jpaEntity.getQuantity()
        );
    }
}
