package com.example.lab1.infrastructure.repository;

import com.example.lab1.domain.model.Category;
import com.example.lab1.domain.repository.CategoryRepository;
import com.example.lab1.infrastructure.mapper.CategoryInfrastructureMapper;
import com.example.lab1.infrastructure.persistence.repository.JpaCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final JpaCategoryRepository jpaCategoryRepository;
    private final CategoryInfrastructureMapper categoryMapper;

    @Override
    public Optional<Category> findById(Long id) {
        return jpaCategoryRepository.findById(id).map(categoryMapper::toDomain);
    }

    @Override
    public Category save(Category category) {
        return categoryMapper.toDomain(
                jpaCategoryRepository.save(categoryMapper.toEntity(category))
        );
    }
}
