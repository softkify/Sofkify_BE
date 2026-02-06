package com.sofkify.productservice.domain.model;

import com.sofkify.productservice.domain.enums.ProductStatus;
import com.sofkify.productservice.domain.exception.InvalidProductPriceException;
import com.sofkify.productservice.domain.exception.InvalidProductStockException;

import java.math.BigDecimal;
import java.util.UUID;

public class Product {
    private final UUID id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final int stock;
    private final ProductStatus status;

    private Product(UUID id, String name, String description, BigDecimal price, int stock, 
                    ProductStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

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

    public static Product restore(UUID id, String name, String description, BigDecimal price, 
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

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public ProductStatus getStatus() {
        return status;
    }


    public boolean isActive() {
        return ProductStatus.ACTIVE.equals(status);
    }
}
