package com.example.lab1.application.service;

import com.example.lab1.application.dto.request.CreateProductRequest;
import com.example.lab1.application.dto.request.UpdateProductRequest;
import com.example.lab1.application.dto.response.ProductDto;
import com.example.lab1.application.mapper.ProductMapper;
import com.example.lab1.domain.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDto createProduct(CreateProductRequest productDto) {
        return ProductMapper.toDto(productRepository.save(ProductMapper.toDomain(productDto)));
    }

    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductMapper::toDto);
    }

    public ProductDto getProductById(UUID id) {
        return productRepository.findById(id).map(ProductMapper::toDto).orElse(null);
    }

    public ProductDto updateProduct(UpdateProductRequest updateProductRequest) {
        return ProductMapper.toDto(productRepository.update(ProductMapper.toDomain(updateProductRequest)));
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }
}
