package com.example.lab1.presentation.controller;

import com.example.lab1.application.dto.request.CreateOrderItemRequest;
import com.example.lab1.application.service.CustomerOrderService;
import com.example.lab1.domain.model.CustomerOrder;
import com.example.lab1.presentation.exception.OrderNotFoundException;
import com.example.lab1.presentation.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerOrderControllerTest {

    @InjectMocks
    private CustomerOrderController customerOrderController;

    @Mock
    private CustomerOrderService customerOrderService;

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        String orderNumber = "ORDER-1";
        List<CreateOrderItemRequest> items = orderItems();
        CustomerOrder order = mock(CustomerOrder.class);
        when(customerOrderService.createOrder(orderNumber, items)).thenReturn(order);

        ResponseEntity<CustomerOrder> response = customerOrderController.createOrder(orderNumber, items);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(order, response.getBody());
        verify(customerOrderService).createOrder(orderNumber, items);
    }

    @Test
    void createOrder_ShouldPropagateDuplicateOrderNumber() {
        String orderNumber = "ORDER-1";
        List<CreateOrderItemRequest> items = orderItems();
        IllegalArgumentException failure = new IllegalArgumentException("Order number already exists: " + orderNumber);
        when(customerOrderService.createOrder(orderNumber, items)).thenThrow(failure);

        assertSame(failure, assertThrows(IllegalArgumentException.class,
                () -> customerOrderController.createOrder(orderNumber, items)));
        verify(customerOrderService).createOrder(orderNumber, items);
    }

    @Test
    void createOrder_ShouldPropagateProductNotFound() {
        String orderNumber = "ORDER-1";
        List<CreateOrderItemRequest> items = orderItems();
        ProductNotFoundException failure = new ProductNotFoundException(1L);
        when(customerOrderService.createOrder(orderNumber, items)).thenThrow(failure);

        assertSame(failure, assertThrows(ProductNotFoundException.class,
                () -> customerOrderController.createOrder(orderNumber, items)));
        verify(customerOrderService).createOrder(orderNumber, items);
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        Long id = 1L;
        CustomerOrder order = mock(CustomerOrder.class);
        when(customerOrderService.getOrderById(id)).thenReturn(order);

        ResponseEntity<CustomerOrder> response = customerOrderController.getOrderById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(order, response.getBody());
        verify(customerOrderService).getOrderById(id);
    }

    @Test
    void getOrderById_ShouldPropagateOrderNotFound() {
        Long id = 1L;
        OrderNotFoundException failure = new OrderNotFoundException(id);
        when(customerOrderService.getOrderById(id)).thenThrow(failure);

        assertSame(failure, assertThrows(OrderNotFoundException.class,
                () -> customerOrderController.getOrderById(id)));
        verify(customerOrderService).getOrderById(id);
    }

    @Test
    void getOrderByNumber_ShouldReturnOrder() {
        String orderNumber = "ORDER-1";
        CustomerOrder order = mock(CustomerOrder.class);
        when(customerOrderService.getOrderByNumber(orderNumber)).thenReturn(order);

        ResponseEntity<CustomerOrder> response = customerOrderController.getOrderByNumber(orderNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(order, response.getBody());
        verify(customerOrderService).getOrderByNumber(orderNumber);
    }

    @Test
    void getOrderByNumber_ShouldPropagateOrderNotFound() {
        String orderNumber = "ORDER-1";
        OrderNotFoundException failure = new OrderNotFoundException(orderNumber);
        when(customerOrderService.getOrderByNumber(orderNumber)).thenThrow(failure);

        assertSame(failure, assertThrows(OrderNotFoundException.class,
                () -> customerOrderController.getOrderByNumber(orderNumber)));
        verify(customerOrderService).getOrderByNumber(orderNumber);
    }

    private List<CreateOrderItemRequest> orderItems() {
        CreateOrderItemRequest item = new CreateOrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);
        return List.of(item);
    }
}
