package com.example.lab1.presentation.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException{

    public ProductNotFoundException(UUID id) {
        super("Product with id '%s' was not found.".formatted(id));
    }
}
