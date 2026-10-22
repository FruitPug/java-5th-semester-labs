package com.example.lab1.infrastructure.repository;

import com.example.lab1.domain.model.Category;
import com.example.lab1.domain.repository.CategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CategoryRepositoryInMemoryImpl implements CategoryRepository {

    private final Map<UUID, Category> categories = new HashMap<>();

    @Override
    public Optional<Category> findById(UUID id) {
        return Optional.ofNullable(categories.get(id));
    }

    @Override
    public Category save(Category category) {
        categories.put(category.getId(), category);
        return category;
    }
}
