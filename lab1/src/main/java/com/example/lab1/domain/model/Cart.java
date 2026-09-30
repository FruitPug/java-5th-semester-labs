package com.example.lab1.domain.model;

import java.util.List;
import java.util.UUID;

public class Cart {
    private UUID id;
    private Long customerId;
    private List<Product> productList;
}
