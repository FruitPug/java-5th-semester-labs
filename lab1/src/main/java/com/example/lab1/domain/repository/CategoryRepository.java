package com.example.lab1.domain.repository;

import com.example.lab1.domain.model.Category;

import java.util.Optional;

public interface CategoryRepository {

    Optional<Category> findById(Long id);

    Category save(Category category);
}
