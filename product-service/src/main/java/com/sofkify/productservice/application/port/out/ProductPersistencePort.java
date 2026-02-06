package com.sofkify.productservice.application.port.out;

import com.sofkify.productservice.domain.model.Product;

public interface ProductPersistencePort {
    Product save(Product product);
}
