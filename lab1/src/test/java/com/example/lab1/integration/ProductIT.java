package com.example.lab1.integration;

import com.example.lab1.infrastructure.persistence.entity.CategoryEntity;
import com.example.lab1.infrastructure.persistence.entity.ProductEntity;
import com.example.lab1.infrastructure.persistence.repository.JpaCategoryRepository;
import com.example.lab1.infrastructure.persistence.repository.JpaProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaCategoryRepository categories;

    @Autowired
    private JpaProductRepository products;

    @Autowired
    private EntityManager entityManager;

    @Test
    void createReadUpdateAndDeleteProduct() throws Exception {
        CategoryEntity firstCategory = category("First category");
        CategoryEntity secondCategory = category("Second category");

        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("Star lamp", "Original", "12.50", firstCategory.getId(), 3)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Star lamp"))
                .andExpect(jsonPath("$.categoryId").value(firstCategory.getId()))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(response).get("id").asLong();

        entityManager.flush();
        entityManager.clear();
        ProductEntity created = products.findById(id).orElseThrow();
        assertThat(created.getDescription()).isEqualTo("Original");
        assertThat(created.getPrice()).isEqualByComparingTo("12.50");
        assertThat(created.getQuantity()).isEqualTo(3);
        assertThat(created.getCategory().getId()).isEqualTo(firstCategory.getId());

        mockMvc.perform(get("/api/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.price").value(12.50))
                .andExpect(jsonPath("$.quantity").value(3));

        mockMvc.perform(put("/api/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("Moon lamp", "Updated", "25.75", secondCategory.getId(), 0)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Moon lamp"))
                .andExpect(jsonPath("$.categoryId").value(secondCategory.getId()));

        entityManager.flush();
        entityManager.clear();
        ProductEntity updated = products.findById(id).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Moon lamp");
        assertThat(updated.getDescription()).isEqualTo("Updated");
        assertThat(updated.getPrice()).isEqualByComparingTo("25.75");
        assertThat(updated.getQuantity()).isZero();
        assertThat(updated.getCategory().getId()).isEqualTo(secondCategory.getId());

        mockMvc.perform(delete("/api/products/{id}", id)).andExpect(status().isNoContent());
        entityManager.flush();
        entityManager.clear();
        assertThat(products.findById(id)).isEmpty();
        mockMvc.perform(get("/api/products/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void listProductsHonorsPaginationAndSorting() throws Exception {
        CategoryEntity category = category("Page category");
        product(category, "Star alpha");
        product(category, "Star beta");
        product(category, "Star gamma");
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/products")
                        .param("page", "1").param("size", "1").param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Star beta"))
                .andExpect(jsonPath("$.content[0].categoryId").value(category.getId()));
    }

    @Test
    void missingProductsAndCategoriesReturnNotFoundWithoutChangingData() throws Exception {
        CategoryEntity category = category("Existing category");
        ProductEntity product = product(category, "Star original");
        entityManager.flush();
        long missingId = Long.MAX_VALUE;

        mockMvc.perform(get("/api/products/{id}", missingId))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.title").value("Product not found"));
        mockMvc.perform(delete("/api/products/{id}", missingId))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.title").value("Product not found"));
        mockMvc.perform(put("/api/products/{id}", missingId).contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("Star new", "New", "1.00", category.getId(), 1)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.title").value("Product not found"));
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("Star new", "New", "1.00", missingId, 1)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.title").value("Category not found"));
        mockMvc.perform(put("/api/products/{id}", product.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("Star new", "New", "1.00", missingId, 1)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.title").value("Category not found"));

        entityManager.flush();
        entityManager.clear();
        assertThat(products.count()).isEqualTo(1);
        assertThat(products.findById(product.getId()).orElseThrow().getName()).isEqualTo("Star original");
    }

    @Test
    void invalidRequestsAreRejectedWithoutChangingData() throws Exception {
        CategoryEntity category = category("Validation category");
        ProductEntity product = product(category, "Star original");
        entityManager.flush();

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content(productJson("Ordinary lamp", "Invalid", "0.00", category.getId(), -1)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.title").value("Validation failed"));
        mockMvc.perform(put("/api/products/{id}", product.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"price\":null,\"categoryId\":null,\"quantity\":null}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.title").value("Validation failed"));

        entityManager.flush();
        entityManager.clear();
        assertThat(products.count()).isEqualTo(1);
        assertThat(products.findById(product.getId()).orElseThrow().getName()).isEqualTo("Star original");
    }

    private CategoryEntity category(String name) {
        CategoryEntity category = new CategoryEntity();
        category.setName(name);
        return categories.save(category);
    }

    private ProductEntity product(CategoryEntity category, String name) {
        ProductEntity product = new ProductEntity();
        product.setName(name);
        product.setDescription("Test product");
        product.setPrice(new BigDecimal("10.00"));
        product.setQuantity(2);
        product.setCategory(category);
        return products.save(product);
    }

    private String productJson(String name, String description, String price, long categoryId, int quantity) {
        return """
                {"name":"%s","description":"%s","price":%s,"categoryId":%d,"quantity":%d}
                """.formatted(name, description, price, categoryId, quantity);
    }
}