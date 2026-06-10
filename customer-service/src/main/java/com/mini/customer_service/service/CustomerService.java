package com.mini.customer_service.service;

import com.mini.customer_service.dto.CustomerRequest;
import com.mini.customer_service.dto.CustomerResponse;
import com.mini.customer_service.exception.CustomerNotFoundException;
import com.mini.customer_service.mapper.AddressMapper;
import com.mini.customer_service.mapper.CustomerMapper;
import com.mini.customer_service.model.Customer;
import com.mini.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final AddressMapper addressMapper;

    public Long createCustomer(CustomerRequest request) {
        Customer customer = repository.save(mapper.toCustomer(request));
        return customer.getId();
    }

    public void updateCustomer(String id, CustomerRequest request) {
        var customer = repository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Cannot update customer:: No customer found with the provided ID: %s", id)
                ));
        mergeCustomer(customer, request);
        this.repository.save(customer);
    }

    private void mergeCustomer(Customer customer, CustomerRequest request) {
        if (StringUtils.isNotBlank(request.getFirstName())) {
            customer.setFirstname(request.getFirstName());
        }
        if (StringUtils.isNotBlank(request.getEmail())) {
            customer.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            customer.setAddress(addressMapper.toAddress(request.getAddress()));
        }
    }

    public List<CustomerResponse> findAllCustomers() {
        return  repository.findAll()
                .stream()
                .map(this.mapper::fromCustomer)
                .collect(Collectors.toList());
    }

    public CustomerResponse findById(String id) {
        return this.repository.findById(id)
                .map(mapper::fromCustomer)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("No customer found with the provided ID: %s", id)));
    }

    public boolean existsById(String id) {
        return this.repository.findById(id)
                .isPresent();
    }

    public void deleteCustomer(String id) {
        this.repository.deleteById(id);
    }
}
