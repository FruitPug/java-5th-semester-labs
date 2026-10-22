package com.example.lab1.presentation.controller;

import com.example.lab1.application.dto.request.CreateCategoryRequest;
import com.example.lab1.application.dto.response.CategoryDto;
import com.example.lab1.application.service.CategoryService;
import com.example.lab1.presentation.exception.CategoryNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private CategoryController categoryController;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void save_ShouldReturnCreatedCategoryDto() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Electronics");

        CategoryDto expectedDto = CategoryDto.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .build();

        when(categoryService.createCategory(request)).thenReturn(expectedDto);

        ResponseEntity<CategoryDto> response = categoryController.createCategory(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(categoryService).createCategory(request);
    }

    @Test
    void getById_ShouldReturnCategoryDto() {
        UUID categoryId = UUID.randomUUID();
        CategoryDto expectedDto = CategoryDto.builder()
                .id(categoryId)
                .name("Electronics")
                .build();

        when(categoryService.getCategoryById(categoryId)).thenReturn(expectedDto);

        ResponseEntity<CategoryDto> response = categoryController.getCategoryById(categoryId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(categoryService).getCategoryById(categoryId);
    }

    @Test
    void getById_ShouldThrowCategoryNotFoundException_WhenCategoryDoesNotExist() {
        UUID categoryId = UUID.randomUUID();

        when(categoryService.getCategoryById(categoryId))
                .thenThrow(new CategoryNotFoundException(categoryId));

        CategoryNotFoundException exception = assertThrows(
                CategoryNotFoundException.class,
                () -> categoryController.getCategoryById(categoryId)
        );

        assertTrue(exception.getMessage().contains(categoryId.toString()));
        verify(categoryService).getCategoryById(categoryId);
    }
}
