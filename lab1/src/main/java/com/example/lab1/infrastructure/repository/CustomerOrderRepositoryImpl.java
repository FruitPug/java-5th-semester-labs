package com.example.lab1.infrastructure.repository;

import com.example.lab1.domain.model.CustomerOrder;
import com.example.lab1.domain.model.OrderItem;
import com.example.lab1.domain.repository.CustomerOrderRepository;
import com.example.lab1.infrastructure.mapper.CustomerOrderInfrastructureMapper;
import com.example.lab1.infrastructure.persistence.entity.CustomerOrderEntity;
import com.example.lab1.infrastructure.persistence.entity.OrderItemEntity;
import com.example.lab1.infrastructure.persistence.entity.ProductEntity;
import com.example.lab1.infrastructure.persistence.repository.JpaCustomerOrderRepository;
import com.example.lab1.infrastructure.persistence.repository.JpaProductRepository;
import com.example.lab1.presentation.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerOrderRepositoryImpl implements CustomerOrderRepository {

    private final JpaCustomerOrderRepository jpaCustomerOrderRepository;
    private final JpaProductRepository jpaProductRepository;
    private final CustomerOrderInfrastructureMapper customerOrderMapper;

    @Override
    @Transactional
    public CustomerOrder save(CustomerOrder order) {
        CustomerOrderEntity entity = customerOrderMapper.toEntity(order);

        for (int i = 0; i < order.getItems().size(); i++) {
            OrderItem domainItem = order.getItems().get(i);
            OrderItemEntity entityItem = entity.getItems().get(i);

            ProductEntity product = jpaProductRepository.findById(
                    domainItem.getProduct().getId()
            ).orElseThrow(() ->
                    new ProductNotFoundException(
                            domainItem.getProduct().getId()
                    )
            );

            entityItem.setProduct(product);
            entityItem.setOrder(entity);
        }

        return customerOrderMapper.toDomain(
                jpaCustomerOrderRepository.save(entity)
        );
    }

    @Override
    @Transactional
    public Optional<CustomerOrder> findById(Long id) {
        return jpaCustomerOrderRepository.findById(id)
                .map(customerOrderMapper::toDomain);
    }

    @Override
    @Transactional
    public Optional<CustomerOrder> findByOrderNumber(String orderNumber) {
        return jpaCustomerOrderRepository.findByOrderNumber(orderNumber)
                .map(customerOrderMapper::toDomain);
    }
}