package com.sofkify.productservice.infrastructure.web;

import com.sofkify.productservice.application.port.in.CreateProductUseCase;
import com.sofkify.productservice.application.port.in.GetProductUseCase;
import com.sofkify.productservice.domain.model.Product;
import com.sofkify.productservice.infrastructure.dto.product.request.CreateProductRequest;
import com.sofkify.productservice.infrastructure.dto.product.response.ProductResponse;
import com.sofkify.productservice.infrastructure.persistence.mapper.ProductMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase, GetProductUseCase getProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
    }

    // POST /products - Create new product
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

    // GET /products - List all products (optional: ?status=ACTIVE|INACTIVE|DELETED)
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String status) {
        
        List<Product> products;
        if (status != null && !status.trim().isEmpty()) {
            products = getProductUseCase.getProductsByStatus(status.toUpperCase());
        } else {
            products = getProductUseCase.getAllProducts();
        }

        List<ProductResponse> response = products.stream()
                .map(ProductMapper::toResponse)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    // GET /products/{id} - Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        return getProductUseCase.getProductById(id)
                .map(product -> ResponseEntity.ok(ProductMapper.toResponse(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    // TODO: PUT /products/{id} - Update product details
    // TODO: PATCH /products/{id}/stock - Add stock to product
    // TODO: DELETE /products/{id} - Soft delete product
    // TODO: PATCH /products/{id}/status - Change product status
}
