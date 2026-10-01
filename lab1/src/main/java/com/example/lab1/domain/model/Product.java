package com.example.lab1.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class Product {

    private final UUID id;

    @Setter
    private String name;

    @Setter
    private String description;

    @Setter
    private BigDecimal price;

    @Setter
    private Category category;

    @Setter
    private Integer quantity;

    public Product(String name, String description, BigDecimal price, Category category, Integer quantity) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
    }

}
