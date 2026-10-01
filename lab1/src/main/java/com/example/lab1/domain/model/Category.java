package com.example.lab1.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Category {
    private UUID id;
    private String name;
}
