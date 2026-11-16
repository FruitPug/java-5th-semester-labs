package com.example.lab1.presentation.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("Category with id '%s' was not found.".formatted(id));
    }
}
