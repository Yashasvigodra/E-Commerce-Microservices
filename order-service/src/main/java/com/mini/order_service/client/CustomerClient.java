package com.mini.order_service.client;

import com.mini.order_service.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    // mirrors GET /api/v1/customers/exists/{customer-id} in CustomerController
    @GetMapping("/api/v1/customers/exists/{customerId}")
    Boolean existsById(@PathVariable("customerId") String customerId);

    @GetMapping("/api/v1/customers/{customer-id}")          // ← add this
    CustomerResponse findById(@PathVariable("customer-id") String customerId);
}
