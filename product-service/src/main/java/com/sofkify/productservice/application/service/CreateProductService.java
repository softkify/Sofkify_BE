package com.sofkify.productservice.application.service;

import com.sofkify.productservice.application.port.in.CreateProductUseCase;
import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import com.sofkify.productservice.domain.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CreateProductService implements CreateProductUseCase {

    private final ProductPersistencePort productPersistencePort;

    public CreateProductService(ProductPersistencePort productPersistencePort) {
        this.productPersistencePort = productPersistencePort;
    }

    @Override
    public Product createProduct(String name, String description, BigDecimal price, int stock) {
        Product product = Product.create(name, description, price, stock);
        return productPersistencePort.save(product);
    }
}
