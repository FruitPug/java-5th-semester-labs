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
import com.example.lab1.presentation.exception.ProductNotFoundException;
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
    private final ProductMapper productMapper;

    public ProductDto createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(request.getCategoryId()));

        Product product = productMapper.toDomain(request, category);
        return productMapper.toDto(productRepository.save(product));
    }

    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    public ProductDto getProductById(UUID id) {
        return productRepository.findById(id).map(productMapper::toDto)
                .orElseThrow(() ->
                        new ProductNotFoundException(id));
    }

    public ProductDto updateProduct(UpdateProductRequest request, UUID id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(request.getCategoryId()));

        productMapper.toDomain(request, category, existingProduct);

        return productMapper.toDto(productRepository.update(existingProduct));
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }
}
