package com.sofkify.productservice.application.port.in;

import com.sofkify.productservice.domain.model.Product;

import java.math.BigDecimal;

public interface CreateProductUseCase {
    Product createProduct(String name, String description, BigDecimal price, int stock);
}
