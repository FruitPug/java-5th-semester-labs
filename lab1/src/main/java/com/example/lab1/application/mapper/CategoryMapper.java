package com.example.lab1.application.mapper;

import com.example.lab1.application.dto.request.CreateCategoryRequest;
import com.example.lab1.application.dto.response.CategoryDto;
import com.example.lab1.domain.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    Category toDomain(CreateCategoryRequest request);
}
