package com.sofkify.productservice.infrastructure.web.mapper;

import com.sofkify.productservice.application.port.in.command.CreateProductCommand;
import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.web.dto.request.CreateProductRequest;
import com.sofkify.productservice.infrastructure.web.dto.response.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper {
    public ProductResponse toDto(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getSku(),
            product.getPrice(),
            product.getStock(),
            product.getStatus()
        );
    }

    public CreateProductCommand toCommand(CreateProductRequest request) {
        return new CreateProductCommand(
            request.name(),
            request.description(),
            request.sku(),
            request.price(),
            request.stock()
        );
    }
}
