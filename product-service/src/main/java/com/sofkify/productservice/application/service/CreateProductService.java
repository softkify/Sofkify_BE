package com.sofkify.productservice.application.service;

import com.sofkify.productservice.application.port.in.CreateProductUseCase;
import com.sofkify.productservice.application.port.in.command.CreateProductCommand;
import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import com.sofkify.productservice.domain.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateProductService implements CreateProductUseCase {
    private final ProductPersistencePort productPersistencePort;

    @Override
    public Product createProduct(CreateProductCommand command) {
        log.debug("Creating product with name: {}", command.name());
        Product product = Product.create(
            command.name(),
            command.description(),
            command.price(),
            command.stock()
        );
        Product savedProduct = productPersistencePort.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return savedProduct;
    }
}
