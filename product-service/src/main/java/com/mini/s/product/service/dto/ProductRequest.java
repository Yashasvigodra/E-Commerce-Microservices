package com.mini.s.product.service.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message="Product name cannot be empty")
    private String name;

    @NotBlank(message="Product description cannot be empty")
    private String description;

    @NotNull(message="Product price cannot be null")
    @Positive(message="Product price must be greater than zero")
    private BigDecimal price;
}
