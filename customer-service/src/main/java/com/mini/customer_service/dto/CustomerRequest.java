package com.mini.customer_service.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomerRequest {

    //private String id;

    @NotBlank(message = "First name cannot be null")
    private String firstName;
    @NotBlank(message = "Last name cannot be null")
    private String lastName;

    @NotBlank(message = "Customer Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Address is required")
    @Valid
    private AddressRequest address;



}
