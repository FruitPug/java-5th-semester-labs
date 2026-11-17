package com.example.lab1.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderItemRequest {
    @NotNull
    Long productId;

    @NotNull
    @Min(1)
    Integer quantity;
}
