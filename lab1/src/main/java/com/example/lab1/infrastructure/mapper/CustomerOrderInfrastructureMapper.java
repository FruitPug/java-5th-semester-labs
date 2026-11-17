package com.example.lab1.infrastructure.mapper;

import com.example.lab1.domain.model.CustomerOrder;
import com.example.lab1.infrastructure.persistence.entity.CustomerOrderEntity;
import com.example.lab1.infrastructure.persistence.entity.OrderItemEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = OrderItemInfrastructureMapper.class
)
public interface CustomerOrderInfrastructureMapper {

    CustomerOrder toDomain(CustomerOrderEntity entity);

    CustomerOrderEntity toEntity(CustomerOrder order);

    @AfterMapping
    default void linkOrderItems(
            @MappingTarget CustomerOrderEntity entity
    ) {
        if (entity.getItems() != null) {
            for (OrderItemEntity item : entity.getItems()) {
                item.setOrder(entity);
            }
        }
    }
}
