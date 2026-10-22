package com.example.lab1.infrastructure.repository;

import com.example.lab1.domain.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryRepositoryInMemoryImplTest {

    private CategoryRepositoryInMemoryImpl categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository = new CategoryRepositoryInMemoryImpl();
    }

    @Test
    void save_ShouldStoreCategoryAndReturnIt() {
        Category category = new Category("Electronics");

        Category savedCategory = categoryRepository.save(category);

        assertNotNull(savedCategory);
        assertEquals(category.getId(), savedCategory.getId());
        assertEquals("Electronics", savedCategory.getName());

        Optional<Category> foundCategory = categoryRepository.findById(category.getId());
        assertTrue(foundCategory.isPresent());
        assertEquals(category, foundCategory.get());
    }

    @Test
    void findById_ShouldReturnCategory_WhenCategoryExists() {
        Category category = new Category("Books");
        categoryRepository.save(category);

        Optional<Category> foundCategory = categoryRepository.findById(category.getId());

        assertTrue(foundCategory.isPresent());
        assertEquals(category, foundCategory.get());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenCategoryDoesNotExist() {
        UUID randomId = UUID.randomUUID();

        Optional<Category> foundCategory = categoryRepository.findById(randomId);

        assertTrue(foundCategory.isEmpty());
    }
}
