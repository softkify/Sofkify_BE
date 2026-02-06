package com.sofkify.productservice.infrastructure.dto.product.response;

import com.sofkify.productservice.domain.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductResponse {

    private UUID productId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private ProductStatus status;

    public ProductResponse() {
    }

    public ProductResponse(UUID productId, String name, String description, BigDecimal price, 
                          Integer stock, ProductStatus status) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

}
