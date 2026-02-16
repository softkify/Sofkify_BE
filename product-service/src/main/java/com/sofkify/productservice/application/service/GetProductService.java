package com.sofkify.productservice.application.service;

import com.sofkify.productservice.application.port.in.GetProductUseCase;
import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import com.sofkify.productservice.domain.exception.ProductNotFoundException;
import com.sofkify.productservice.domain.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetProductService implements GetProductUseCase {
    private final ProductPersistencePort productPersistencePort;

    @Override
    public Product getProductById(UUID id) {
        log.debug("Searching product by ID: {}", id);
        return productPersistencePort.findById(id)
            .orElseThrow(() -> {
                log.warn("Product not found with ID: {}", id);
                return new ProductNotFoundException("Product not found with ID: " + id);
            });
    }

    @Override
    public List<Product> getAllProducts() {
        log.debug("Fetching all products");
        List<Product> products = productPersistencePort.findAll();
        log.info("Found {} products", products.size());
        return products;
    }

    @Override
    public List<Product> getProductsByStatus(String status) {
        log.debug("Fetching products by status: {}", status);
        List<Product> products = productPersistencePort.findByStatus(status);
        log.info("Found {} products with status: {}", products.size(), status);
        return products;
    }
}
