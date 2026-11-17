package com.example.lab1.integration;

import com.example.lab1.application.dto.request.CreateCategoryRequest;
import com.example.lab1.infrastructure.persistence.entity.CategoryEntity;
import com.example.lab1.infrastructure.persistence.repository.JpaCategoryRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CategoryIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void createCategory() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Test Category");

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Test Category"));

        entityManager.flush();
        entityManager.clear();

        List<CategoryEntity> categories = jpaCategoryRepository.findAll();
        assertThat(categories).hasSize(1);

        CategoryEntity category = categories.getFirst();
        assertThat(category.getName()).isEqualTo("Test Category");

        mockMvc.perform(get("/api/categories/{id}", category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value("Test Category"));
    }

    @Test
    void invalidCategoryIsRejected() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("ab");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"));

        assertThat(jpaCategoryRepository.count()).isZero();
    }

    @Test
    void missingCategoryReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Category not found"));
    }
}
