package com.example.lab1.presentation.controller;

import com.example.lab1.application.dto.request.CreateOrderItemRequest;
import com.example.lab1.application.service.CustomerOrderService;
import com.example.lab1.domain.model.CustomerOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @PostMapping
    public ResponseEntity<CustomerOrder> createOrder(
            @RequestParam @NotBlank String orderNumber,
            @RequestBody @NotEmpty List<@Valid CreateOrderItemRequest> items
    ) {
        CustomerOrder createdOrder = customerOrderService.createOrder(orderNumber, items);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrder> getOrderById(
            @PathVariable @Positive Long id
    ) {
        return ResponseEntity.ok(
                customerOrderService.getOrderById(id)
        );
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<CustomerOrder> getOrderByNumber(
            @PathVariable @NotBlank String orderNumber
    ) {
        return ResponseEntity.ok(
                customerOrderService.getOrderByNumber(orderNumber)
        );
    }
}
