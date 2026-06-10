package com.mini.customer_service.mapper;

import com.mini.customer_service.dto.CustomerRequest;
import com.mini.customer_service.dto.CustomerResponse;
import com.mini.customer_service.model.Customer;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomerMapper {

    @Autowired
    private final AddressMapper addressMapper;


    public Customer toCustomer(CustomerRequest request) {
        if (request == null){
            return null;

        }
        return Customer.builder()

                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .address(AddressMapper.toAddress(request.getAddress()))
                .build();
    }

    public CustomerResponse fromCustomer(Customer customer) {

        if(customer == null){
            return null;
        }

        return new CustomerResponse(
                customer.getId(),
                customer.getFirstname(),
                customer.getLastname(),
                customer.getEmail(),
                addressMapper.fromAddress(customer.getAddress())
        );
    }
}
