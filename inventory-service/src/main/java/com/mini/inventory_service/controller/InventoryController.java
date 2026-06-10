package com.mini.inventory_service.controller;


import com.mini.inventory_service.dto.InventoryCheckRequest;
import com.mini.inventory_service.dto.InventoryRequest;
import com.mini.inventory_service.dto.InventoryResponse;
import com.mini.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // Get inventory quantity
    @GetMapping("/{skuCode}")
    public InventoryResponse getInventory(@PathVariable String skuCode) {
        return inventoryService.getInventory(skuCode);
    }

    // Check if product is in stock
    @PostMapping("/instock")
    public List<InventoryResponse> isInStock(@RequestBody List<InventoryCheckRequest> requests) {
        return inventoryService.isInStock(requests);
    }

    // Add stock (admin)
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addInventory(@RequestBody InventoryRequest request) {
        inventoryService.addInventory(request);
    }

    // Reduce stock (order service)
    @PutMapping("/reduce/{skuCode}")
    public void reduceStock(@PathVariable String skuCode,
                            @RequestParam Integer quantity) {

        inventoryService.reduceStock(skuCode, quantity);
    }

    // Get low stock products
    @GetMapping("/low-stock")
    public List<InventoryResponse> getLowStockProducts() {
        return inventoryService.getLowStockProducts();
    }
}
