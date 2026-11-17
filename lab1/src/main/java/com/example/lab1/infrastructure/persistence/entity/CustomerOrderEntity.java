package com.example.lab1.infrastructure.persistence.entity;

import com.example.lab1.domain.model.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_orders")

@Getter
@Setter
@NoArgsConstructor
public class CustomerOrderEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_order_generator"
    )
    @SequenceGenerator(
            name = "customer_order_generator",
            sequenceName = "customer_order_seq",
            allocationSize = 1
    )
    private Long id;

    @NaturalId
    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private OrderStatus status;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItemEntity> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void addItem(OrderItemEntity item) {
        if (item == null) {
            throw new IllegalArgumentException("Order item must not be null");
        }

        if (!items.contains(item)) {
            items.add(item);
            item.setOrder(this);
        }
    }

    public void removeItem(OrderItemEntity item) {
        items.remove(item);
        item.setOrder(null);
    }
}
