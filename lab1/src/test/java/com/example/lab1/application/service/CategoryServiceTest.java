package com.example.lab1.application.service;

import com.example.lab1.application.dto.request.CreateCategoryRequest;
import com.example.lab1.application.dto.response.CategoryDto;
import com.example.lab1.application.mapper.CategoryMapper;
import com.example.lab1.domain.model.Category;
import com.example.lab1.domain.repository.CategoryRepository;
import com.example.lab1.presentation.exception.CategoryNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CategoryService.class)
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private CategoryMapper categoryMapper;

    @Test
    void createCategory_ShouldReturnCategoryDto() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Electronics");

        Category category = new Category("Electronics");
        CategoryDto expectedDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();

        when(categoryMapper.toDomain(request)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expectedDto);

        CategoryDto actualDto = categoryService.createCategory(request);

        assertEquals(actualDto, expectedDto);
    }

    @Test
    void getCategoryById_ShouldReturnCategoryDto() {
        Category category = new Category("Electronics");
        CategoryDto expectedDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedDto);

        CategoryDto actualDto = categoryService.getCategoryById(category.getId());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void getCategoryById_ShouldThrowCategoryNotFoundException_WhenCategoryDoesNotExist() {
        UUID randomUUID = UUID.randomUUID();

        when(categoryRepository.findById(randomUUID)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getCategoryById(randomUUID)
        );

        assertTrue(exception.getMessage().contains(randomUUID.toString()));
    }
}
