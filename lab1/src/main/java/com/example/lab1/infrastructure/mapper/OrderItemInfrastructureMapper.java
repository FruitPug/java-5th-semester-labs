package com.example.lab1.infrastructure.mapper;

import com.example.lab1.domain.model.OrderItem;
import com.example.lab1.infrastructure.persistence.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = ProductInfrastructureMapper.class
)
public interface OrderItemInfrastructureMapper {

    OrderItem toDomain(OrderItemEntity entity);

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    OrderItemEntity toEntity(OrderItem item);
}
