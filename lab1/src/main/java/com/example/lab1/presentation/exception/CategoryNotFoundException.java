package com.example.lab1.presentation.exception;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(UUID id) {
        super("Category with id '%s' was not found.".formatted(id));
    }
}
