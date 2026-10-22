package com.example.lab1.application.mapper;

import com.example.lab1.application.dto.request.CreateCategoryRequest;
import com.example.lab1.application.dto.response.CategoryDto;
import com.example.lab1.domain.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CategoryMapperImpl.class)
public class CategoryMapperTest {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    void toDto_ShouldMapCategoryToCategoryDto() {
        Category category = new Category("Electronics");

        CategoryDto dto = categoryMapper.toDto(category);

        assertNotNull(dto);
        assertEquals(category.getId(), dto.getId());
        assertEquals(category.getName(), dto.getName());
    }

    @Test
    void toDto_ShouldReturnNull_WhenCategoryIsNull() {
        CategoryDto dto = categoryMapper.toDto(null);

        assertNull(dto);
    }

    @Test
    void toDomain_ShouldMapCreateCategoryRequestToCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Electronics");

        Category category = categoryMapper.toDomain(request);

        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals(request.getName(), category.getName());
    }

    @Test
    void toDomain_ShouldReturnNull_WhenRequestIsNull() {
        Category category = categoryMapper.toDomain(null);

        assertNull(category);
    }
}
