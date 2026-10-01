package com.example.lab1.application.mapper;

import com.example.lab1.application.dto.request.CreateCategoryRequest;
import com.example.lab1.application.dto.response.CategoryDto;
import com.example.lab1.domain.model.Category;

import java.util.UUID;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toDomain(CreateCategoryRequest request) {
        return Category.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .build();
    }
}
