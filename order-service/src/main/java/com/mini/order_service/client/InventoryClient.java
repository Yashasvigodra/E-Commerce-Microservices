package com.mini.order_service.client;

import com.mini.order_service.dto.InventoryCheckRequest;
import com.mini.order_service.dto.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PostMapping("/api/inventory/instock")
    List<InventoryResponse> checkStock(@RequestBody List<InventoryCheckRequest> requests);

    @PutMapping("/api/inventory/reduce/{skuCode}")
    void reduceStock(@PathVariable("skuCode") String skuCode,
                     @RequestParam("quantity") Integer quantity);
}
