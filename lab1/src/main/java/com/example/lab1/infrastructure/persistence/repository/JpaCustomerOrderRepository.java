package com.example.lab1.infrastructure.persistence.repository;

import com.example.lab1.domain.model.MostPurchasedProduct;
import com.example.lab1.domain.model.OrderStatus;
import com.example.lab1.infrastructure.persistence.entity.CustomerOrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
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

    @Query("""
        SELECT
            p.id AS productId,
            p.name AS productName,
            SUM(oi.quantity) AS totalQuantityPurchased
        FROM OrderItemEntity oi
        JOIN oi.product p
        JOIN oi.order o
        WHERE o.status IN :includedStatuses
        GROUP BY p.id, p.name
        ORDER BY SUM(oi.quantity) DESC
        """)
    List<MostPurchasedProduct> findMostPurchasedProducts(
            @Param("includedStatuses") Collection<OrderStatus> includedStatuses
    );
}
