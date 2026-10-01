package com.example.lab1.domain.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Category {
    private final UUID id;
    private final String name;

    public Category(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }
}
