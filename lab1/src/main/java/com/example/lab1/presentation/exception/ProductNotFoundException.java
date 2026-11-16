package com.example.lab1.presentation.exception;

public class ProductNotFoundException extends RuntimeException{

    public ProductNotFoundException(Long id) {
        super("Product with id '%s' was not found.".formatted(id));
    }
}
