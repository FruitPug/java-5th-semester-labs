package com.example.lab1.domain.model;

import lombok.Getter;

@Getter
public class Category {
    private final Long id;
    private final String name;

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
