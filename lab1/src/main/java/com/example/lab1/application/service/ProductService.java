package com.example.lab1.application.service;

import com.example.lab1.application.dto.request.CreateProductRequest;
import com.example.lab1.application.dto.request.UpdateProductRequest;
import com.example.lab1.application.dto.response.ProductDto;
import com.example.lab1.application.mapper.ProductMapper;
import com.example.lab1.domain.model.Category;
import com.example.lab1.domain.model.Product;
import com.example.lab1.domain.repository.CategoryRepository;
import com.example.lab1.domain.repository.ProductRepository;
import com.example.lab1.presentation.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductDto createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(request.getCategoryId()));

        Product product = ProductMapper.toDomain(request, category);
        return ProductMapper.toDto(productRepository.save(product));
    }

    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductMapper::toDto);
    }

    public ProductDto getProductById(UUID id) {
        return productRepository.findById(id).map(ProductMapper::toDto).orElse(null);
    }

    public ProductDto updateProduct(UpdateProductRequest updateProductRequest) {
        Category category = categoryRepository.findById(updateProductRequest.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(updateProductRequest.getCategoryId()));

        Product product = ProductMapper.toDomain(updateProductRequest, category);
        return ProductMapper.toDto(productRepository.update(product));
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }
}
