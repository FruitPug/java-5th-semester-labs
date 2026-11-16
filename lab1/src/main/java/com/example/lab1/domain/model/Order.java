package com.example.lab1.domain.model;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private Long id;
    private Long customerId;
    private List<Product> productList;
    private BigDecimal totalPrice;
}
