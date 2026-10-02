package com.example.lab1.application.mapper;

import com.example.lab1.application.dto.request.CreateProductRequest;
import com.example.lab1.application.dto.request.UpdateProductRequest;
import com.example.lab1.application.dto.response.ProductDto;
import com.example.lab1.domain.model.Category;
import com.example.lab1.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    ProductDto toDto(Product product);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "name", source = "request.name")
    Product toDomain(CreateProductRequest request, Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "name", source = "request.name")
    Product toDomain(
            UpdateProductRequest request,
            Category category,
            @MappingTarget Product product
    );
}
