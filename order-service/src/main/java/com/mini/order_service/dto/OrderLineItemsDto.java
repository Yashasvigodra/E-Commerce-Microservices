package com.mini.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemsDto {

    @NotBlank(message = "SKU code is required")
    private String skuCode;

    @NotNull
    @Positive(message = "Price must be a positive value")
    private BigDecimal price;

    @NotNull
    @Positive(message = "quantity must be a positive value")
    private Integer quantity;
}
