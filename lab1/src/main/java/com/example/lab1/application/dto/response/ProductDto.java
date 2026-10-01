package com.example.lab1.application.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductDto {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private UUID categoryId;
    private Integer quantity;

    public ProductDto(UUID id, String name, String description, BigDecimal price, UUID categoryId, Integer quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.quantity = quantity;
    }
}
