package com.example.lab1.infrastructure.mapper;

import com.example.lab1.domain.model.Category;
import com.example.lab1.infrastructure.persistence.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryInfrastructureMapper {

    Category toDomain(CategoryEntity categoryEntity);

    CategoryEntity toEntity(Category category);
}
