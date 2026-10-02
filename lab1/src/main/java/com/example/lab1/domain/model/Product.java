package com.example.lab1.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class Product {

    private final UUID id;

    private String name;

    private String description;

    private BigDecimal price;

    private Category category;

    private Integer quantity;

    public Product(String name, String description, BigDecimal price, Category category, Integer quantity) {
        this.id = UUID.randomUUID();
        setName(name);
        setDescription(description);
        setPrice(price);
        setCategory(category);
        setQuantity(quantity);
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }

        if (name.length() < 2 || name.length() > 50) {
            throw new IllegalArgumentException(
                    "Product name must contain between 2 and 50 characters"
            );
        }

        this.name = name;
    }

    public void setDescription(String description) {
        if (description == null || description.length() > 1000) {
            throw new IllegalArgumentException(
                    "Product description must not exceed 1000 characters"
            );
        }

        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Product price must not be null");
        }

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Product price must be greater than 0"
            );
        }

        this.price = price;
    }

    public void setCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Product category must not be null");
        }

        this.category = category;
    }

    public void setQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Product quantity must not be negative");
        }

        this.quantity = quantity;
    }
}
