package com.example.lab1.application.service;


import com.example.lab1.application.dto.request.CreateProductRequest;
import com.example.lab1.application.dto.request.UpdateProductRequest;
import com.example.lab1.application.dto.response.ProductDto;
import com.example.lab1.application.mapper.ProductMapper;
import com.example.lab1.domain.model.Category;
import com.example.lab1.domain.model.Product;
import com.example.lab1.domain.repository.CategoryRepository;
import com.example.lab1.domain.repository.ProductRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Test
    void createProduct_ShouldReturnProductDto() {
        Category category = new Category("Electronics");
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Cosmic Laser");
        request.setDescription("A powerful cosmic tool");
        request.setPrice(BigDecimal.valueOf(199.99));
        request.setCategoryId(category.getId());
        request.setQuantity(15);

        Product product = new Product("Cosmic Laser", "A powerful cosmic tool", BigDecimal.valueOf(199.99), category, 15);

        ProductDto expectedDto = ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategory().getId())
                .quantity(product.getQuantity())
                .build();

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(productMapper.toDomain(request, category)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(expectedDto);

        ProductDto actualDto = productService.createProduct(request);

        assertEquals(expectedDto, actualDto);
        verify(categoryRepository).findById(category.getId());
        verify(productMapper).toDomain(request, category);
        verify(productRepository).save(product);
        verify(productMapper).toDto(product);
    }

    @Test
    void createProduct_ShouldThrowCategoryNotFoundException_WhenCategoryDoesNotExist() {
        UUID categoryId = UUID.randomUUID();
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Cosmic Laser");
        request.setDescription("A powerful cosmic tool");
        request.setPrice(BigDecimal.valueOf(199.99));
        request.setCategoryId(categoryId);
        request.setQuantity(15);

        CategoryNotFoundException exception = assertThrows(
                CategoryNotFoundException.class,
                () -> productService.createProduct(request)
        );

        assertTrue(exception.getMessage().contains(categoryId.toString()));
    }

    @Test
    void getAllProducts_ShouldReturnPageOfProductDto() {
        Category category = new Category("Electronics");
        Product product1 = new Product("Cosmic Laser", "A powerful cosmic tool", BigDecimal.valueOf(199.99), category, 15);
        Product product2 = new Product("Galaxy Vacuum", "Description", BigDecimal.valueOf(250), category, 10);

        ProductDto expectedProduct1Dto = ProductDto.builder()
                .id(product1.getId())
                .name(product1.getName())
                .description(product1.getDescription())
                .price(product1.getPrice())
                .categoryId(product1.getCategory().getId())
                .quantity(product1.getQuantity())
                .build();

        ProductDto expectedProduct2Dto = ProductDto.builder()
                .id(product2.getId())
                .name(product2.getName())
                .description(product2.getDescription())
                .price(product2.getPrice())
                .categoryId(product2.getCategory().getId())
                .quantity(product2.getQuantity())
                .build();

        Page<Product> products = new PageImpl<>(List.of(product1, product2));
        Page<ProductDto> expectedProductsDto = new PageImpl<>(List.of(expectedProduct1Dto, expectedProduct2Dto));

        when(productRepository.findAll(any(Pageable.class))).thenReturn(products);
        when(productMapper.toDto(any(Product.class))).thenReturn(expectedProduct1Dto, expectedProduct2Dto);

        Page<ProductDto> actualProductsDto = productService.getAllProducts(PageRequest.of(0, 10));

        assertEquals(expectedProductsDto, actualProductsDto);
        verify(productRepository).findAll(any(Pageable.class));
        verify(productMapper, times(2)).toDto(any(Product.class));
    }

    @Test
    void getProductById_ShouldReturnProductDto() {
        Category category = new Category("Electronics");
        Product product = new Product("Cosmic Laser", "A powerful cosmic tool", BigDecimal.valueOf(199.99), category, 15);

        ProductDto expectedProductDto = ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategory().getId())
                .quantity(product.getQuantity())
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productMapper.toDto(any(Product.class))).thenReturn(expectedProductDto);

        ProductDto actualProductDto = productService.getProductById(product.getId());

        assertEquals(expectedProductDto, actualProductDto);
        verify(productRepository).findById(product.getId());
        verify(productMapper).toDto(any(Product.class));
    }

    @Test
    void getProductById_ShouldThrowProductNotFoundException_WhenProductDoesNotExist() {
        UUID randomUUID = UUID.randomUUID();

        when(productRepository.findById(randomUUID)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(randomUUID)
        );

        assertTrue(exception.getMessage().contains(randomUUID.toString()));
        verify(productRepository).findById(randomUUID);
    }

    @Test
    void updateProduct_ShouldReturnProductDto() {
        Category category = new Category("Electronics");
        Product product = new Product("Cosmic Laser", "A powerful cosmic tool", BigDecimal.valueOf(199.99), category, 15);
        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Updated Name");
        request.setDescription("An updated description");
        request.setPrice(BigDecimal.valueOf(299.99));
        request.setCategoryId(category.getId());
        request.setQuantity(20);

        Product updatedProduct = new Product("Updated Name", "An updated description", BigDecimal.valueOf(299.99), category, 20);
        ProductDto expectedDto = ProductDto.builder()
                .id(updatedProduct.getId())
                .name(updatedProduct.getName())
                .description(updatedProduct.getDescription())
                .price(updatedProduct.getPrice())
                .categoryId(updatedProduct.getCategory().getId())
                .quantity(updatedProduct.getQuantity())
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(productMapper.toDomain(request, category, product)).thenReturn(updatedProduct);
        when(productRepository.update(updatedProduct)).thenReturn(updatedProduct);
        when(productMapper.toDto(updatedProduct)).thenReturn(expectedDto);

        ProductDto actualDto = productService.updateProduct(request, product.getId());

        assertEquals(expectedDto, actualDto);
        verify(productRepository).findById(product.getId());
        verify(categoryRepository).findById(category.getId());
        verify(productMapper).toDomain(request, category, product);
        verify(productRepository).update(updatedProduct);
        verify(productMapper).toDto(updatedProduct);
    }

    @Test
    void updateProduct_ShouldThrowProductNotFoundException_WhenProductDoesNotExist() {
        UUID productId = UUID.randomUUID();
        Category category = new Category("Electronics");
        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Updated Name");
        request.setDescription("An updated description");
        request.setPrice(BigDecimal.valueOf(299.99));
        request.setCategoryId(category.getId());
        request.setQuantity(20);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(request, productId)
        );

        assertTrue(exception.getMessage().contains(productId.toString()));
        verify(productRepository).findById(productId);
    }

    @Test
    void updateProduct_ShouldThrowCategoryNotFoundException_WhenCategoryDoesNotExist() {
        UUID categoryId = UUID.randomUUID();
        Category category = new Category("Electronics");
        Product product = new Product("Cosmic Laser", "A powerful cosmic tool", BigDecimal.valueOf(199.99), category, 15);
        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Updated Name");
        request.setDescription("An updated description");
        request.setPrice(BigDecimal.valueOf(299.99));
        request.setCategoryId(categoryId);
        request.setQuantity(20);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(
                CategoryNotFoundException.class,
                () -> productService.updateProduct(request, product.getId())
        );

        assertTrue(exception.getMessage().contains(categoryId.toString()));
        verify(productRepository).findById(product.getId());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void deleteProduct_ShouldNotThrowAnything() {
        Category category = new Category("Electronics");
        Product product = new Product("Cosmic Laser", "A powerful cosmic tool", BigDecimal.valueOf(199.99), category, 15);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(product.getId());

        productService.deleteProduct(product.getId());

        verify(productRepository).deleteById(product.getId());
    }

    @Test
    void deleteProduct_ShouldThrowProductNotFoundException_WhenProductDoesNotExist() {
        UUID productId = UUID.randomUUID();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(productId)
        );

        assertTrue(exception.getMessage().contains(productId.toString()));
        verify(productRepository).findById(productId);
    }
}
