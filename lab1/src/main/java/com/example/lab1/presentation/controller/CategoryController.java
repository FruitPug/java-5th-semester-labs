package com.example.lab1.presentation.controller;

import com.example.lab1.application.dto.request.CreateCategoryRequest;
import com.example.lab1.application.dto.response.CategoryDto;
import com.example.lab1.application.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return new ResponseEntity<>(categoryService.createCategory(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable UUID id) {
        return new ResponseEntity<>(categoryService.getCategoryById(id), HttpStatus.OK);
    }
}
