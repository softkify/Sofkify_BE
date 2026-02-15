package com.sofkify.productservice.infrastructure.web.controller;

import com.sofkify.productservice.application.port.in.CreateProductUseCase;
import com.sofkify.productservice.application.port.in.GetProductUseCase;
import com.sofkify.productservice.application.port.in.command.CreateProductCommand;
import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.web.dto.request.CreateProductRequest;
import com.sofkify.productservice.infrastructure.web.dto.response.ProductResponse;
import com.sofkify.productservice.infrastructure.web.mapper.ProductDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final ProductDtoMapper dtoMapper;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        CreateProductCommand command = dtoMapper.toCommand(request);
        Product createdProduct = createProductUseCase.createProduct(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDto(createdProduct));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(dtoMapper.toDto(getProductUseCase.getProductById(id)));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(@RequestParam(required = false) String status) {
        if (status == null) {
            return ResponseEntity.ok(getProductUseCase.getAllProducts().stream().map(dtoMapper::toDto).toList());
        }
        return ResponseEntity.ok(getProductUseCase.getProductsByStatus(status.toUpperCase())
            .stream().map(dtoMapper::toDto).toList());
    }
}
