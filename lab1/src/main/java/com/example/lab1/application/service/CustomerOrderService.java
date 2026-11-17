package com.example.lab1.application.service;

import com.example.lab1.application.dto.request.CreateOrderItemRequest;
import com.example.lab1.domain.model.*;
import com.example.lab1.domain.repository.CustomerOrderRepository;
import com.example.lab1.domain.repository.ProductRepository;
import com.example.lab1.presentation.exception.OrderNotFoundException;
import com.example.lab1.presentation.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CustomerOrder createOrder(
            String orderNumber,
            List<CreateOrderItemRequest> requests
    ) {
        if (customerOrderRepository.findByOrderNumber(orderNumber).isPresent()) {
            throw new IllegalArgumentException(
                    "Order number already exists: " + orderNumber
            );
        }

        List<OrderItem> items = new ArrayList<>();

        for (CreateOrderItemRequest request : requests) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

            items.add(new OrderItem(
                    null,
                    product,
                    request.getQuantity(),
                    product.getPrice()
            ));
        }

        CustomerOrder order = new CustomerOrder(
                null,
                orderNumber,
                LocalDateTime.now(),
                OrderStatus.PENDING,
                items
        );

        return customerOrderRepository.save(order);
    }

    @Transactional
    public CustomerOrder getOrderById(Long id) {
        return customerOrderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional
    public CustomerOrder getOrderByNumber(String orderNumber) {
        return customerOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
    }

    @Transactional
    public List<MostPurchasedProduct> getMostPurchasedProducts() {
        return customerOrderRepository.findMostPurchasedProducts();
    }
}
