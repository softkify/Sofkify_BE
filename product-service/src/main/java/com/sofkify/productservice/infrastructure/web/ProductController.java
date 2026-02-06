package com.sofkify.productservice.infrastructure.web;

import com.sofkify.productservice.application.port.in.CreateProductUseCase;
import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.dto.product.request.CreateProductRequest;
import com.sofkify.productservice.infrastructure.dto.product.response.ProductResponse;
import com.sofkify.productservice.infrastructure.persistence.mapper.ProductMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase) {
        this.createProductUseCase = createProductUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product createdProduct = createProductUseCase.createProduct(
            request.getName(),
            request.getDescription(),
            request.getPrice(),
            request.getStock()
        );

        ProductResponse response = ProductMapper.toResponse(createdProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
