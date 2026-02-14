package com.sofkify.productservice.application.port.in;

import com.sofkify.productservice.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetProductUseCase {
    Optional<Product> getProductById(UUID id);

    List<Product> getAllProducts();

    List<Product> getProductsByStatus(String status);
}
