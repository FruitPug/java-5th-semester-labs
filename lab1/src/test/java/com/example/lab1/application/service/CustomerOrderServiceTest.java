package com.example.lab1.application.service;

import com.example.lab1.application.dto.request.CreateOrderItemRequest;
import com.example.lab1.domain.model.Category;
import com.example.lab1.domain.model.CustomerOrder;
import com.example.lab1.domain.model.MostPurchasedProduct;
import com.example.lab1.domain.model.OrderItem;
import com.example.lab1.domain.model.OrderStatus;
import com.example.lab1.domain.model.Product;
import com.example.lab1.domain.repository.CustomerOrderRepository;
import com.example.lab1.domain.repository.ProductRepository;
import com.example.lab1.presentation.exception.OrderNotFoundException;
import com.example.lab1.presentation.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerOrderServiceTest {

    @Mock
    private CustomerOrderRepository customerOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CustomerOrderService customerOrderService;

    @Test
    void createOrder_ShouldSavePendingOrderWithProductPrices() {
        Category category = new Category(1L, "Category");
        Product first = new Product(1L, "First", "Description", new BigDecimal("12.50"), category, 10);
        Product second = new Product(2L, "Second", "Description", new BigDecimal("7.00"), category, 10);
        CreateOrderItemRequest firstRequest = request(1L, 2);
        CreateOrderItemRequest secondRequest = request(2L, 3);
        when(customerOrderRepository.findByOrderNumber("ORD-1")).thenReturn(Optional.empty());
        when(productRepository.findById(1L)).thenReturn(Optional.of(first));
        when(productRepository.findById(2L)).thenReturn(Optional.of(second));
        when(customerOrderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
        LocalDateTime before = LocalDateTime.now();

        CustomerOrder result = customerOrderService.createOrder("ORD-1", List.of(firstRequest, secondRequest));

        LocalDateTime after = LocalDateTime.now();
        ArgumentCaptor<CustomerOrder> captor = ArgumentCaptor.forClass(CustomerOrder.class);
        verify(customerOrderRepository).save(captor.capture());
        assertSame(captor.getValue(), result);
        assertNull(result.getId());
        assertEquals("ORD-1", result.getOrderNumber());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertFalse(result.getCreatedAt().isBefore(before));
        assertFalse(result.getCreatedAt().isAfter(after));
        assertEquals(2, result.getItems().size());
        assertItem(result.getItems().get(0), first, 2);
        assertItem(result.getItems().get(1), second, 3);
    }

    @Test
    void createOrder_ShouldRejectDuplicateBeforeLookingUpProducts() {
        when(customerOrderRepository.findByOrderNumber("ORD-1")).thenReturn(Optional.of(mock(CustomerOrder.class)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerOrderService.createOrder("ORD-1", List.of(request(1L, 1))));

        assertTrue(exception.getMessage().contains("ORD-1"));
        verifyNoInteractions(productRepository);
        verify(customerOrderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldNotSaveWhenProductIsMissing() {
        when(customerOrderRepository.findByOrderNumber("ORD-1")).thenReturn(Optional.empty());
        when(productRepository.findById(42L)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> customerOrderService.createOrder("ORD-1", List.of(request(42L, 1))));

        assertTrue(exception.getMessage().contains("42"));
        verify(customerOrderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldNotSaveAnEmptyOrder() {
        when(customerOrderRepository.findByOrderNumber("ORD-1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> customerOrderService.createOrder("ORD-1", List.of()));

        verifyNoInteractions(productRepository);
        verify(customerOrderRepository, never()).save(any());
    }

    @Test
    void getOrderById_ShouldReturnFoundOrder() {
        CustomerOrder order = mock(CustomerOrder.class);
        when(customerOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertSame(order, customerOrderService.getOrderById(1L));
        verify(customerOrderRepository).findById(1L);
    }

    @Test
    void getOrderById_ShouldThrowWhenMissing() {
        when(customerOrderRepository.findById(42L)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class,
                () -> customerOrderService.getOrderById(42L));
        assertTrue(exception.getMessage().contains("42"));
    }

    @Test
    void getOrderByNumber_ShouldReturnFoundOrder() {
        CustomerOrder order = mock(CustomerOrder.class);
        when(customerOrderRepository.findByOrderNumber("ORD-1")).thenReturn(Optional.of(order));

        assertSame(order, customerOrderService.getOrderByNumber("ORD-1"));
        verify(customerOrderRepository).findByOrderNumber("ORD-1");
    }

    @Test
    void getOrderByNumber_ShouldThrowWhenMissing() {
        when(customerOrderRepository.findByOrderNumber("missing")).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class,
                () -> customerOrderService.getOrderByNumber("missing"));
        assertTrue(exception.getMessage().contains("missing"));
    }

    @Test
    void getMostPurchasedProducts_ShouldReturnRepositoryResultsInOrder() {
        MostPurchasedProduct first = mock(MostPurchasedProduct.class);
        MostPurchasedProduct second = mock(MostPurchasedProduct.class);
        List<MostPurchasedProduct> products = List.of(first, second);
        when(customerOrderRepository.findMostPurchasedProducts()).thenReturn(products);

        assertSame(products, customerOrderService.getMostPurchasedProducts());
        verify(customerOrderRepository).findMostPurchasedProducts();
        verifyNoInteractions(productRepository);
    }

    @Test
    void getMostPurchasedProducts_ShouldReturnEmptyResults() {
        when(customerOrderRepository.findMostPurchasedProducts()).thenReturn(List.of());

        assertTrue(customerOrderService.getMostPurchasedProducts().isEmpty());
        verify(customerOrderRepository).findMostPurchasedProducts();
    }

    private static CreateOrderItemRequest request(Long productId, int quantity) {
        CreateOrderItemRequest request = new CreateOrderItemRequest();
        request.setProductId(productId);
        request.setQuantity(quantity);
        return request;
    }

    private static void assertItem(OrderItem item, Product product, int quantity) {
        assertNull(item.getId());
        assertSame(product, item.getProduct());
        assertEquals(quantity, item.getQuantity());
        assertEquals(product.getPrice(), item.getUnitPrice());
    }
}