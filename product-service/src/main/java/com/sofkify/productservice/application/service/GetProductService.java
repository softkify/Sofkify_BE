package com.sofkify.productservice.application.service;

import com.sofkify.productservice.application.port.in.GetProductUseCase;
import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import com.sofkify.productservice.domain.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetProductService implements GetProductUseCase {

    private final ProductPersistencePort productPersistencePort;

    public GetProductService(ProductPersistencePort productPersistencePort) {
        this.productPersistencePort = productPersistencePort;
    }

    @Override
    public Optional<Product> getProductById(UUID id) {
        return productPersistencePort.findById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productPersistencePort.findAll();
    }

    @Override
    public List<Product> getProductsByStatus(String status) {
        return productPersistencePort.findByStatus(status);
    }
}
