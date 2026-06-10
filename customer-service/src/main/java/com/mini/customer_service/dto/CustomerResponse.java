package com.mini.customer_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomerResponse {

      private Long id;
      private String firstName;
        private String lastName;
        private String email;
        private AddressResponse address;

}
