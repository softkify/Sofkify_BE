package com.sofkify.productservice.domain.model;

import com.sofkify.productservice.domain.enums.ProductStatus;
import com.sofkify.productservice.domain.exception.InvalidProductPriceException;
import com.sofkify.productservice.domain.exception.InvalidProductStockException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {
    private final UUID id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private int stock;
    private final ProductStatus status;

    public static Product create(String name, String description, BigDecimal price, int stock) {
        validatePrice(price);
        validateStock(stock);
        return new Product(
            UUID.randomUUID(),
            name,
            description,
            price,
            stock,
            ProductStatus.ACTIVE
        );
    }

    public static Product reconstitute(UUID id, String name, String description, BigDecimal price,
                                       int stock, ProductStatus status) {
        return new Product(id, name, description, price, stock, status);
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductPriceException("Product price must be greater than zero");
        }
    }

    private static void validateStock(int stock) {
        if (stock < 0) {
            throw new InvalidProductStockException("Product stock cannot be negative");
        }
    }

    public void decrementStock(int quantity) {
        if (quantity <= 0) {
            throw new InvalidProductStockException("Quantity to decrement must be greater than zero");
        }
        if (this.stock < quantity) {
            throw new InvalidProductStockException(
                String.format("Insufficient stock. Available: %d, Required: %d", this.stock, quantity)
            );
        }
        this.stock -= quantity;
    }
}
