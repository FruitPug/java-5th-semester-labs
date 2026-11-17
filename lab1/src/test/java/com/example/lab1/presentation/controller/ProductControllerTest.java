package com.example.lab1.presentation.controller;

import com.example.lab1.application.dto.request.CreateProductRequest;
import com.example.lab1.application.dto.request.UpdateProductRequest;
import com.example.lab1.application.dto.response.ProductDto;
import com.example.lab1.application.service.ProductService;
import com.example.lab1.presentation.exception.CategoryNotFoundException;
import com.example.lab1.presentation.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @Test
    void save_ShouldReturnCreatedProductDto() {
        Long categoryId = 1L;
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Cosmic Laser");
        request.setDescription("A powerful cosmic tool");
        request.setPrice(BigDecimal.valueOf(199.99));
        request.setCategoryId(categoryId);
        request.setQuantity(15);

        ProductDto expectedDto = ProductDto.builder()
                .id(1L)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .categoryId(request.getCategoryId())
                .quantity(request.getQuantity())
                .build();

        when(productService.createProduct(request)).thenReturn(expectedDto);

        ResponseEntity<ProductDto> response = productController.save(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(productService).createProduct(request);
    }

    @Test
    void save_ShouldThrowCategoryNotFoundException_WhenCategoryDoesNotExist() {
        Long categoryId = 1L;
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Cosmic Laser");
        request.setDescription("A powerful cosmic tool");
        request.setPrice(BigDecimal.valueOf(199.99));
        request.setCategoryId(categoryId);
        request.setQuantity(15);

        when(productService.createProduct(request))
                .thenThrow(new CategoryNotFoundException(categoryId));

        CategoryNotFoundException exception = assertThrows(
                CategoryNotFoundException.class,
                () -> productController.save(request)
        );

        assertTrue(exception.getMessage().contains(categoryId.toString()));
        verify(productService).createProduct(request);
    }

    @Test
    void getAll_ShouldReturnPageOfProductDto() {
        Long categoryId = 1L;
        ProductDto product1 = ProductDto.builder()
                .id(1L)
                .name("Cosmic Laser")
                .description("A powerful cosmic tool")
                .price(BigDecimal.valueOf(199.99))
                .categoryId(categoryId)
                .quantity(15)
                .build();

        ProductDto product2 = ProductDto.builder()
                .id(2L)
                .name("Galaxy Vacuum")
                .description("Description")
                .price(BigDecimal.valueOf(250))
                .categoryId(categoryId)
                .quantity(10)
                .build();

        Page<ProductDto> expectedPage = new PageImpl<>(List.of(product1, product2));
        Pageable pageable = PageRequest.of(0, 10);

        when(productService.getAllProducts(pageable)).thenReturn(expectedPage);

        ResponseEntity<Page<ProductDto>> response = productController.getAll(pageable);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
        verify(productService).getAllProducts(pageable);
    }

    @Test
    void getAll_ShouldReturnRequestedPage() {
        Long categoryId = 1L;

        ProductDto product = ProductDto.builder()
                .id(1L)
                .name("Galaxy Vacuum")
                .description("Description")
                .price(BigDecimal.valueOf(250))
                .categoryId(categoryId)
                .quantity(10)
                .build();

        Pageable pageable = PageRequest.of(1, 1);
        Page<ProductDto> expectedPage =
                new PageImpl<>(List.of(product), pageable, 2);

        when(productService.getAllProducts(pageable)).thenReturn(expectedPage);

        ResponseEntity<Page<ProductDto>> response =
                productController.getAll(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedPage, response.getBody());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getNumber());
        assertEquals(1, response.getBody().getSize());
        assertEquals(2, response.getBody().getTotalElements());

        verify(productService).getAllProducts(pageable);
    }

    @Test
    void getById_ShouldReturnProductDto() {
        Long productId = 1L;
        ProductDto expectedDto = ProductDto.builder()
                .id(productId)
                .name("Cosmic Laser")
                .description("A powerful cosmic tool")
                .price(BigDecimal.valueOf(199.99))
                .categoryId(1L)
                .quantity(15)
                .build();

        when(productService.getProductById(productId)).thenReturn(expectedDto);

        ResponseEntity<ProductDto> response = productController.getById(productId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(productService).getProductById(productId);
    }

    @Test
    void getById_ShouldThrowProductNotFoundException_WhenProductDoesNotExist() {
        Long productId = 1L;

        when(productService.getProductById(productId))
                .thenThrow(new ProductNotFoundException(productId));

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productController.getById(productId)
        );

        assertTrue(exception.getMessage().contains(productId.toString()));
        verify(productService).getProductById(productId);
    }

    @Test
    void update_ShouldReturnProductDto() {
        Long productId = 1L;
        Long categoryId = 2L;
        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Updated Name");
        request.setDescription("An updated description");
        request.setPrice(BigDecimal.valueOf(299.99));
        request.setCategoryId(categoryId);
        request.setQuantity(20);

        ProductDto expectedDto = ProductDto.builder()
                .id(productId)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .categoryId(categoryId)
                .quantity(request.getQuantity())
                .build();

        when(productService.updateProduct(request, productId)).thenReturn(expectedDto);

        ResponseEntity<ProductDto> response = productController.update(request, productId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(productService).updateProduct(request, productId);
    }

    @Test
    void update_ShouldThrowProductNotFoundException_WhenProductDoesNotExist() {
        Long productId = 1L;
        Long categoryId = 2L;
        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Updated Name");
        request.setDescription("An updated description");
        request.setPrice(BigDecimal.valueOf(299.99));
        request.setCategoryId(categoryId);
        request.setQuantity(20);

        when(productService.updateProduct(request, productId))
                .thenThrow(new ProductNotFoundException(productId));

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productController.update(request, productId)
        );

        assertTrue(exception.getMessage().contains(productId.toString()));
        verify(productService).updateProduct(request, productId);
    }

    @Test
    void update_ShouldThrowCategoryNotFoundException_WhenCategoryDoesNotExist() {
        Long productId = 1L;
        Long categoryId = 2L;
        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Updated Name");
        request.setDescription("An updated description");
        request.setPrice(BigDecimal.valueOf(299.99));
        request.setCategoryId(categoryId);
        request.setQuantity(20);

        when(productService.updateProduct(request, productId))
                .thenThrow(new CategoryNotFoundException(categoryId));

        CategoryNotFoundException exception = assertThrows(
                CategoryNotFoundException.class,
                () -> productController.update(request, productId)
        );

        assertTrue(exception.getMessage().contains(categoryId.toString()));
        verify(productService).updateProduct(request, productId);
    }

    @Test
    void deleteById_ShouldReturnNoContent() {
        Long productId = 1L;

        doNothing().when(productService).deleteProduct(productId);

        ResponseEntity<Void> response = productController.deleteById(productId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(productService).deleteProduct(productId);
    }

    @Test
    void deleteById_ShouldThrowProductNotFoundException_WhenProductDoesNotExist() {
        Long productId = 1L;

        doThrow(new ProductNotFoundException(productId))
                .when(productService).deleteProduct(productId);

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productController.deleteById(productId)
        );

        assertTrue(exception.getMessage().contains(productId.toString()));
        verify(productService).deleteProduct(productId);
    }
}
