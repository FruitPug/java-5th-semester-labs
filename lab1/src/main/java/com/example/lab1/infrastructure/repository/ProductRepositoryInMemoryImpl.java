package com.example.lab1.infrastructure.repository;

import com.example.lab1.domain.model.Product;
import com.example.lab1.domain.repository.ProductRepository;
import com.example.lab1.presentation.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ProductRepositoryInMemoryImpl implements ProductRepository {

    private final Map<UUID, Product> products = new LinkedHashMap<>();

    @Override
    public Product save(Product product) {
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        List<Product> allProducts = products.values().stream().toList();

        int totalElements = allProducts.size();
        int start = (int) pageable.getOffset();

        if (start >= totalElements) {
            return new PageImpl<>(
                    List.of(),
                    pageable,
                    totalElements
            );
        }

        int end = Math.min(start + pageable.getPageSize(), totalElements);

        List<Product> pageContent = allProducts.subList(start, end);

        return new PageImpl<>(
                pageContent,
                pageable,
                totalElements
        );
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public Product update(Product product) {
        findById(product.getId())
                .orElseThrow(() ->
                        new ProductNotFoundException(product.getId()));

        products.put(product.getId(), product);
        return product;
    }

    @Override
    public void deleteById(UUID id) {
        products.remove(id);
    }
}
