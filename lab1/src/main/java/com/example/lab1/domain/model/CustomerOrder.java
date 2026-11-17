package com.example.lab1.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CustomerOrder {

    private final Long id;
    private final String orderNumber;
    private final LocalDateTime createdAt;
    private final OrderStatus status;
    private final List<OrderItem> items;

    public CustomerOrder(
            Long id,
            String orderNumber,
            LocalDateTime createdAt,
            OrderStatus status,
            List<OrderItem> items
    ) {
        if (orderNumber == null || orderNumber.isBlank()) {
            throw new IllegalArgumentException("Order number must not be blank");
        }

        if (createdAt == null) {
            throw new IllegalArgumentException("Order creation date must not be null");
        }

        if (status == null) {
            throw new IllegalArgumentException("Order status must not be null");
        }

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        this.id = id;
        this.orderNumber = orderNumber;
        this.createdAt = createdAt;
        this.status = status;
        this.items = List.copyOf(items);
    }
}