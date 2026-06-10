package com.mini.customer_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AddressRequest {

    @NotBlank(message = "Street cannot be empty")
    private String street;

    @NotBlank(message = "House number cannot be empty")
    private String houseNumber;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "\\d{5,6}", message = "Zip code must be 5 or 6 digits")
    private String zipCode;
}
