package com.example.lab1.integration;

import com.example.lab1.domain.model.OrderStatus;
import com.example.lab1.infrastructure.persistence.entity.CategoryEntity;
import com.example.lab1.infrastructure.persistence.entity.CustomerOrderEntity;
import com.example.lab1.infrastructure.persistence.entity.OrderItemEntity;
import com.example.lab1.infrastructure.persistence.entity.ProductEntity;
import com.example.lab1.infrastructure.persistence.repository.JpaCustomerOrderRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerOrderIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JpaCustomerOrderRepository orders;

    @Test
    void createsAndRetrievesOrderWithPersistedPriceSnapshot() throws Exception {
        ProductEntity product = createProduct();
        Long productId = product.getId();

        String response = mockMvc.perform(post("/api/orders")
                        .param("orderNumber", "order-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(items(productId, 2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").value("order-1"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.items[0].product.id").value(productId))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].unitPrice").value(12.50))
                .andReturn().getResponse().getContentAsString();

        JsonNode created = objectMapper.readTree(response);
        long orderId = created.get("id").asLong();
        product.setPrice(new BigDecimal("20.00"));
        entityManager.flush();
        entityManager.clear();

        CustomerOrderEntity saved = orders.findById(orderId).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(saved.getItems()).hasSize(1);
        assertThat(saved.getItems().getFirst().getUnitPrice()).isEqualByComparingTo("12.50");
        assertThat(saved.getItems().getFirst().getProduct().getId()).isEqualTo(productId);

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("order-1"))
                .andExpect(jsonPath("$.items[0].unitPrice").value(12.50));
        mockMvc.perform(get("/api/orders/number/{orderNumber}", "order-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void rejectsDuplicateOrderNumberWithoutCreatingAnotherOrder() throws Exception {
        Long productId = createProduct().getId();
        mockMvc.perform(post("/api/orders").param("orderNumber", "duplicate")
                        .contentType(MediaType.APPLICATION_JSON).content(items(productId, 1)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/orders").param("orderNumber", "duplicate")
                        .contentType(MediaType.APPLICATION_JSON).content(items(productId, 1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Order number already exists: duplicate"));

        entityManager.flush();
        assertThat(orders.findAll()).hasSize(1);
    }

    @Test
    void rejectsMissingProductWithoutPersistingOrder() throws Exception {
        Long productId = createProduct().getId();
        mockMvc.perform(post("/api/orders").param("orderNumber", "missing-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"productId\":" + productId + ",\"quantity\":1},"
                                + "{\"productId\":999999999,\"quantity\":1}]"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Product not found"));

        assertThat(orders.findByOrderNumber("missing-product")).isEmpty();
    }

    @Test
    void rejectsInvalidOrderItems() throws Exception {
        Long productId = createProduct().getId();
        for (String body : new String[]{"[]", items(productId, 0), "[{\"quantity\":1}]"}) {
            mockMvc.perform(post("/api/orders").param("orderNumber", "invalid")
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        assertThat(orders.findByOrderNumber("invalid")).isEmpty();
    }

    @Test
    void missingOrderIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", 999999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingOrderNumberReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/number/{orderNumber}", "unknown-order"))
                .andExpect(status().isNotFound());
    }

    @Test
    void persistsAndRemovesOrderItemsThroughEntityRelationship() {
        ProductEntity product = createProduct();
        CustomerOrderEntity order = new CustomerOrderEntity();
        order.setOrderNumber("entity-order");
        order.setStatus(OrderStatus.PENDING);
        OrderItemEntity item = new OrderItemEntity();
        item.setProduct(product);
        item.setQuantity(1);
        item.setUnitPrice(product.getPrice());

        assertThatIllegalArgumentException().isThrownBy(() -> order.addItem(null));
        order.addItem(item);
        order.addItem(item);
        assertThat(order.getItems()).hasSize(1);
        assertThat(item.getOrder()).isSameAs(order);
        entityManager.persist(order);
        entityManager.flush();
        Long orderId = order.getId();
        Long itemId = item.getId();
        assertThat(order.getCreatedAt()).isNotNull();
        entityManager.clear();

        CustomerOrderEntity saved = orders.findById(orderId).orElseThrow();
        assertThat(saved.getItems()).hasSize(1);
        OrderItemEntity savedItem = saved.getItems().getFirst();
        saved.removeItem(savedItem);
        assertThat(savedItem.getOrder()).isNull();
        entityManager.flush();
        entityManager.clear();
        assertThat(entityManager.find(OrderItemEntity.class, itemId)).isNull();
    }

    private ProductEntity createProduct() {
        CategoryEntity category = new CategoryEntity();
        category.setName("Order test category");
        entityManager.persist(category);
        ProductEntity product = new ProductEntity();
        product.setName("Order test product");
        product.setDescription("Test product");
        product.setPrice(new BigDecimal("12.50"));
        product.setQuantity(10);
        product.setCategory(category);
        entityManager.persist(product);
        entityManager.flush();
        return product;
    }

    private String items(Long productId, int quantity) {
        return "[{\"productId\":" + productId + ",\"quantity\":" + quantity + "}]";
    }
}