package com.example.lab1.application.service;

import com.example.lab1.application.dto.request.CreateCategoryRequest;
import com.example.lab1.application.dto.response.CategoryDto;
import com.example.lab1.application.mapper.CategoryMapper;
import com.example.lab1.domain.model.Category;
import com.example.lab1.domain.repository.CategoryRepository;
import com.example.lab1.presentation.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryDto createCategory(CreateCategoryRequest request) {
        Category category = CategoryMapper.toDomain(request);
        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    public CategoryDto getCategoryById(UUID id) {
        return CategoryMapper.toDto(categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id))
        );
    }
}
