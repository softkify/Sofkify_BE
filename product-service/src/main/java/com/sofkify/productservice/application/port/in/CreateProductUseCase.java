package com.sofkify.productservice.application.port.in;

import com.sofkify.productservice.application.port.in.command.CreateProductCommand;
import com.sofkify.productservice.domain.model.Product;

public interface CreateProductUseCase {
    Product createProduct(CreateProductCommand command);
}
