package com.sofkify.productservice.application.service;

import com.sofkify.productservice.application.port.in.CreateProductUseCase;
import com.sofkify.productservice.application.port.in.command.CreateProductCommand;
import com.sofkify.productservice.application.port.out.ProductPersistencePort;
import com.sofkify.productservice.domain.exception.DuplicateSkuException;
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
        log.debug("Creating product with name: {} and SKU: {}", command.name(), command.sku());
        
        if (productPersistencePort.existsBySku(command.sku())) {
            throw new DuplicateSkuException("Product with SKU " + command.sku() + " already exists");
        }
        
        Product product = Product.create(
            command.name(),
            command.description(),
            command.sku(),
            command.price(),
            command.stock()
        );
        Product savedProduct = productPersistencePort.save(product);
        log.info("Product created successfully with ID: {} and SKU: {}", savedProduct.getId(), savedProduct.getSku());
        return savedProduct;
    }
}
