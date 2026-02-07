package com.sofkify.cartservice.infrastructure.adapters.out.messaging;

import com.sofkify.cartservice.domain.ports.out.ProductServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class ProductServiceAdapter implements ProductServicePort {

    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public ProductServiceAdapter(RestTemplate restTemplate,
                             @Value("${product.service.url:http://localhost:8082}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
    }

    @Override
    public ProductInfo getProduct(UUID productId) {
        try {
            String url = productServiceUrl + "/products/" + productId;
            ProductResponse response = restTemplate.getForObject(url, ProductResponse.class);
            
            if (response == null) {
                throw new RuntimeException("Product not found: " + productId);
            }
            
            return new ProductInfo(
                response.getProductId(),
                response.getName(),
                response.getPrice(),
                response.getStock(),
                response.getStatus().equals("ACTIVE")
            );
        } catch (Exception e) {
            throw new RuntimeException("Error fetching product: " + productId, e);
        }
    }

    @Override
    public boolean validateStock(UUID productId, int requiredQuantity) {
        try {
            ProductInfo product = getProduct(productId);
            return product.stock() >= requiredQuantity;
        } catch (Exception e) {
            return false;
        }
    }

    // DTO para respuesta del product-service
    private static class ProductResponse {
        private UUID productId;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private String status;

        // Getters and Setters
        public UUID getProductId() { return productId; }
        public void setProductId(UUID productId) { this.productId = productId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
