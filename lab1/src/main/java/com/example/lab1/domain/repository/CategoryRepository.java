package com.example.lab1.domain.repository;

import com.example.lab1.domain.model.Category;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    Optional<Category> findById(UUID id);

    Category save(Category category);
}
