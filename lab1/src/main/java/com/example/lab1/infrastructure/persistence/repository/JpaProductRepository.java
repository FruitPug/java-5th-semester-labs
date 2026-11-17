package com.example.lab1.infrastructure.persistence.repository;

import com.example.lab1.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {

    @EntityGraph(attributePaths = "category")
    Page<ProductEntity> findAll(Pageable pageable);
}
