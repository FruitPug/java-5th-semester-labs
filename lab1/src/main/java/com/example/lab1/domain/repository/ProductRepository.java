package com.example.lab1.domain.repository;

import com.example.lab1.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(Long id);

    Product update(Product product);

    void deleteById(Long id);
}
