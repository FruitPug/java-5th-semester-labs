package com.example.lab1.presentation.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Order with id '%s' was not found.".formatted(id));
    }

    public OrderNotFoundException(String orderNumber) {
        super("Order with number '%s' was not found.".formatted(orderNumber));
    }
}
