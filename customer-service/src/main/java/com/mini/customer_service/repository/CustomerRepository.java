package com.mini.customer_service.repository;

import com.mini.customer_service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer , String> {
}
