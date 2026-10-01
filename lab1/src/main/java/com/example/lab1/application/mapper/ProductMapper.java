package com.example.lab1.application.mapper;

import com.example.lab1.application.dto.request.CreateProductRequest;
import com.example.lab1.application.dto.request.UpdateProductRequest;
import com.example.lab1.application.dto.response.ProductDto;
import com.example.lab1.domain.model.Category;
import com.example.lab1.domain.model.Product;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductMapper {
    public static ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory().getId(),
                product.getQuantity()
        );
    }

    public static Product toDomain(CreateProductRequest request, Category category) {
        return new Product(
                UUID.randomUUID(),
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                category,
                request.getQuantity()
        );
    }

    public static Product toDomain(UpdateProductRequest request, UUID id, Category category) {
        return new Product(
                id,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                category,
                request.getQuantity()
        );
    }
}
