package com.example.lab1.application.dto.request;

import com.example.lab1.presentation.validation.CosmicWordCheck;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateProductRequest {

    @CosmicWordCheck
    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    @NotNull
    private UUID categoryId;

    @Min(0)
    @NotNull
    private Integer quantity;
}
