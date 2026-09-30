package com.example.lab1.domain.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Order {
    private UUID id;
    private Long customerId;
    private List<Product> productList;
    private BigDecimal totalPrice;
}
