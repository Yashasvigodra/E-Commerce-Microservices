package com.mini.customer_service.mapper;


import com.mini.customer_service.dto.AddressRequest;
import com.mini.customer_service.dto.AddressResponse;
import com.mini.customer_service.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public static Address toAddress(AddressRequest address) {

        if(address == null)return null;

        return Address.builder()
                .street(address.getStreet())
                .zipCode(address.getZipCode())
                .houseNumber(address.getHouseNumber())
                .build();
    }

    public AddressResponse fromAddress(Address address) {

        if(address == null)return null;

        return new AddressResponse(
                address.getStreet(),
                address.getHouseNumber(),
                address.getZipCode()
        );
    }
}
