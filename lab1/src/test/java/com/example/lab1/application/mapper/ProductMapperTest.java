package com.example.lab1.application.mapper;

import com.example.lab1.application.dto.request.CreateProductRequest;
import com.example.lab1.application.dto.request.UpdateProductRequest;
import com.example.lab1.application.dto.response.ProductDto;
import com.example.lab1.domain.model.Category;
import com.example.lab1.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ProductMapperImpl.class)
public class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    void toDto_ShouldMapProductToProductDto() {
        Category category = new Category("Electronics");
        Product product = new Product(
                "Cosmic Laser",
                "A powerful cosmic tool",
                BigDecimal.valueOf(199.99),
                category,
                15
        );

        ProductDto dto = productMapper.toDto(product);

        assertNotNull(dto);
        assertEquals(product.getId(), dto.getId());
        assertEquals(product.getName(), dto.getName());
        assertEquals(product.getDescription(), dto.getDescription());
        assertEquals(product.getPrice(), dto.getPrice());
        assertEquals(category.getId(), dto.getCategoryId());
        assertEquals(product.getQuantity(), dto.getQuantity());
    }

    @Test
    void toDto_ShouldReturnNull_WhenProductIsNull() {
        ProductDto dto = productMapper.toDto(null);

        assertNull(dto);
    }

    @Test
    void toDomain_FromCreateRequest_ShouldMapToProduct() {
        Category category = new Category("Electronics");
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Cosmic Laser");
        request.setDescription("A powerful cosmic tool");
        request.setPrice(BigDecimal.valueOf(199.99));
        request.setCategoryId(category.getId());
        request.setQuantity(15);

        Product product = productMapper.toDomain(request, category);

        assertNotNull(product);
        assertNotNull(product.getId());
        assertEquals(request.getName(), product.getName());
        assertEquals(request.getDescription(), product.getDescription());
        assertEquals(request.getPrice(), product.getPrice());
        assertEquals(category, product.getCategory());
        assertEquals(request.getQuantity(), product.getQuantity());
    }

    @Test
    void toDomain_FromCreateRequest_ShouldReturnNull_WhenRequestAndCategoryAreNull() {
        Product product = productMapper.toDomain(null, null);

        assertNull(product);
    }

    @Test
    void toDomain_FromUpdateRequest_ShouldUpdateExistingProduct() {
        Category initialCategory = new Category("Electronics");
        Category updatedCategory = new Category("Gadgets");
        Product product = new Product(
                "Cosmic Laser",
                "A powerful cosmic tool",
                BigDecimal.valueOf(199.99),
                initialCategory,
                15
        );
        UUID originalProductId = product.getId();

        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("Super Cosmic Laser");
        request.setDescription("An updated powerful cosmic tool");
        request.setPrice(BigDecimal.valueOf(299.99));
        request.setCategoryId(updatedCategory.getId());
        request.setQuantity(20);

        Product updatedProduct = productMapper.toDomain(request, updatedCategory, product);

        assertNotNull(updatedProduct);
        assertEquals(originalProductId, updatedProduct.getId());
        assertEquals(request.getName(), updatedProduct.getName());
        assertEquals(request.getDescription(), updatedProduct.getDescription());
        assertEquals(request.getPrice(), updatedProduct.getPrice());
        assertEquals(updatedCategory, updatedProduct.getCategory());
        assertEquals(request.getQuantity(), updatedProduct.getQuantity());
    }

    @Test
    void toDomain_FromUpdateRequest_ShouldReturnUnmodifiedProduct_WhenRequestAndCategoryAreNull() {
        Category category = new Category("Electronics");
        Product product = new Product(
                "Cosmic Laser",
                "A powerful cosmic tool",
                BigDecimal.valueOf(199.99),
                category,
                15
        );

        Product result = productMapper.toDomain(null, null, product);

        assertSame(product, result);
    }
}
