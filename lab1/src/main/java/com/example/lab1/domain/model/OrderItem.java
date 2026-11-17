package com.example.lab1.domain.model;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class OrderItem {

    private final Long id;
    private final Product product;
    private final Integer quantity;
    private final BigDecimal unitPrice;

    public OrderItem(
            Long id,
            Product product,
            Integer quantity,
            BigDecimal unitPrice
    ) {
        if (product == null) {
            throw new IllegalArgumentException("Order item product must not be null");
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Order item quantity must be greater than 0");
        }

        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order item price must be greater than 0");
        }

        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
