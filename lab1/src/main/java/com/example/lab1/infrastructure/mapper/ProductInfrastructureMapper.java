package com.example.lab1.infrastructure.mapper;

import com.example.lab1.domain.model.Product;
import com.example.lab1.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductInfrastructureMapper {

    Product toDomain(ProductEntity productEntity);

    ProductEntity toEntity(Product product);
}
