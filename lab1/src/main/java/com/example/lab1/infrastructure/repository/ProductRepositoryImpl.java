package com.example.lab1.infrastructure.repository;

import com.example.lab1.domain.model.Product;
import com.example.lab1.domain.repository.ProductRepository;
import com.example.lab1.infrastructure.mapper.ProductInfrastructureMapper;
import com.example.lab1.infrastructure.persistence.repository.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final ProductInfrastructureMapper productMapper;

    @Override
    public Product save(Product product) {
        return productMapper.toDomain(
                jpaProductRepository.save(productMapper.toEntity(product))
        );
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return jpaProductRepository.findAll(pageable).map(productMapper::toDomain);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaProductRepository.findById(id).map(productMapper::toDomain);
    }

    @Override
    public Product update(Product product) {
        return productMapper.toDomain(
                jpaProductRepository.save(productMapper.toEntity(product))
        );
    }

    @Override
    public void deleteById(Long id) {
        jpaProductRepository.deleteById(id);
    }
}
