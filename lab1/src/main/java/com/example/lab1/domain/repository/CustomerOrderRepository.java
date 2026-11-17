package com.example.lab1.domain.repository;

import com.example.lab1.domain.model.CustomerOrder;
import com.example.lab1.domain.model.MostPurchasedProduct;

import java.util.List;
import java.util.Optional;

public interface CustomerOrderRepository {

    CustomerOrder save(CustomerOrder order);

    Optional<CustomerOrder> findById(Long id);

    Optional<CustomerOrder> findByOrderNumber(String orderNumber);

    List<MostPurchasedProduct> findMostPurchasedProducts();
}
