package com.example.lab1.infrastructure.repository;

import com.example.lab1.domain.model.Category;
import com.example.lab1.domain.model.Product;
import com.example.lab1.presentation.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ProductRepositoryInMemoryImplTest {

    private ProductRepositoryInMemoryImpl productRepository;
    private Category category;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepositoryInMemoryImpl();
        category = new Category("Electronics");
    }

    private Product createProduct(String name, String description, BigDecimal price, int quantity) {
        return new Product(name, description, price, category, quantity);
    }

    @Test
    void save_ShouldStoreProductAndReturnIt() {
        Product product = createProduct("Cosmic Laser", "Powerful laser", BigDecimal.valueOf(99.99), 10);

        Product saved = productRepository.save(product);

        assertNotNull(saved);
        assertEquals(product.getId(), saved.getId());
        assertEquals("Cosmic Laser", saved.getName());

        Optional<Product> found = productRepository.findById(product.getId());
        assertTrue(found.isPresent());
        assertEquals(product, found.get());
    }

    @Test
    void findById_ShouldReturnProduct_WhenProductExists() {
        Product product = createProduct("Cosmic Laser", "Powerful laser", BigDecimal.valueOf(99.99), 10);
        productRepository.save(product);

        Optional<Product> found = productRepository.findById(product.getId());

        assertTrue(found.isPresent());
        assertEquals(product, found.get());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenProductDoesNotExist() {
        UUID randomId = UUID.randomUUID();

        Optional<Product> found = productRepository.findById(randomId);

        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_ShouldReturnEmptyPage_WhenRepositoryIsEmpty() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> page = productRepository.findAll(pageable);

        assertNotNull(page);
        assertTrue(page.getContent().isEmpty());
        assertEquals(0, page.getTotalElements());
        assertEquals(0, page.getTotalPages());
    }

    @Test
    void findAll_ShouldReturnAllProducts_WhenPageSizeIsLargerThanTotal() {
        Product product1 = createProduct("Product 1", "Desc 1", BigDecimal.valueOf(10.0), 5);
        Product product2 = createProduct("Product 2", "Desc 2", BigDecimal.valueOf(20.0), 10);
        productRepository.save(product1);
        productRepository.save(product2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = productRepository.findAll(pageable);

        assertNotNull(page);
        assertEquals(2, page.getTotalElements());
        assertEquals(2, page.getContent().size());
        assertEquals(product1, page.getContent().get(0));
        assertEquals(product2, page.getContent().get(1));
    }

    @Test
    void findAll_ShouldReturnPagedProducts_WhenMultiplePagesExist() {
        Product product1 = createProduct("Product 1", "Desc 1", BigDecimal.valueOf(10.0), 5);
        Product product2 = createProduct("Product 2", "Desc 2", BigDecimal.valueOf(20.0), 10);
        Product product3 = createProduct("Product 3", "Desc 3", BigDecimal.valueOf(30.0), 15);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        Pageable firstPageable = PageRequest.of(0, 2);
        Page<Product> firstPage = productRepository.findAll(firstPageable);

        assertEquals(3, firstPage.getTotalElements());
        assertEquals(2, firstPage.getContent().size());
        assertEquals(product1, firstPage.getContent().get(0));
        assertEquals(product2, firstPage.getContent().get(1));

        Pageable secondPageable = PageRequest.of(1, 2);
        Page<Product> secondPage = productRepository.findAll(secondPageable);

        assertEquals(3, secondPage.getTotalElements());
        assertEquals(1, secondPage.getContent().size());
        assertEquals(product3, secondPage.getContent().getFirst());
    }

    @Test
    void findAll_ShouldReturnEmptyPage_WhenOffsetExceedsTotalElements() {
        Product product = createProduct("Product 1", "Desc 1", BigDecimal.valueOf(10.0), 5);
        productRepository.save(product);

        Pageable pageable = PageRequest.of(5, 10);
        Page<Product> page = productRepository.findAll(pageable);

        assertNotNull(page);
        assertTrue(page.getContent().isEmpty());
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void update_ShouldUpdateAndReturnProduct_WhenProductExists() {
        Product product = createProduct("Product 1", "Desc 1", BigDecimal.valueOf(10.0), 5);
        productRepository.save(product);

        product.setName("Updated Product");
        product.setPrice(BigDecimal.valueOf(25.0));

        Product updated = productRepository.update(product);

        assertNotNull(updated);
        assertEquals("Updated Product", updated.getName());
        assertEquals(BigDecimal.valueOf(25.0), updated.getPrice());

        Optional<Product> found = productRepository.findById(product.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated Product", found.get().getName());
    }

    @Test
    void update_ShouldThrowProductNotFoundException_WhenProductDoesNotExist() {
        Product product = createProduct("Product 1", "Desc 1", BigDecimal.valueOf(10.0), 5);

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productRepository.update(product)
        );

        assertTrue(exception.getMessage().contains(product.getId().toString()));
    }

    @Test
    void deleteById_ShouldRemoveProduct_WhenProductExists() {
        Product product = createProduct("Product 1", "Desc 1", BigDecimal.valueOf(10.0), 5);
        productRepository.save(product);

        productRepository.deleteById(product.getId());

        Optional<Product> found = productRepository.findById(product.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void deleteById_ShouldThrowProductNotFoundException_WhenProductDoesNotExist() {
        UUID randomId = UUID.randomUUID();

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productRepository.deleteById(randomId)
        );

        assertTrue(exception.getMessage().contains(randomId.toString()));
    }
}
