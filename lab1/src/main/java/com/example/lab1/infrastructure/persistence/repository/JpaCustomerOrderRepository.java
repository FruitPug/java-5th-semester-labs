package com.example.lab1.infrastructure.persistence.repository;

import com.example.lab1.infrastructure.persistence.entity.CustomerOrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCustomerOrderRepository extends JpaRepository<CustomerOrderEntity, Long> {

    @EntityGraph(attributePaths = {
            "items",
            "items.product",
            "items.product.category"
    })
    Optional<CustomerOrderEntity> findById(Long id);

    @EntityGraph(attributePaths = {
            "items",
            "items.product",
            "items.product.category"
    })
    Optional<CustomerOrderEntity> findByOrderNumber(String orderNumber);
}
