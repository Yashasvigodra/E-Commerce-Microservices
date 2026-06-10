package com.mini.inventory_service.service;

import com.mini.inventory_service.dto.InventoryCheckRequest;
import com.mini.inventory_service.dto.InventoryRequest;
import com.mini.inventory_service.dto.InventoryResponse;
import com.mini.inventory_service.exceptions.InsufficientStockException;
import com.mini.inventory_service.exceptions.SkuNotFoundException;
import com.mini.inventory_service.model.Inventory;
import com.mini.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    @Value("${inventory.low-stock-threshold:5}")
    private int lowStockThreshold;

    private final InventoryRepository inventoryRepository;

    //check Quantity of product in stock
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(String skuCode){

        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() ->  new SkuNotFoundException("SKU not found: " + skuCode));

        return InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .quantity(inventory.getQuantity())
                .build();
    }



//  //cehck if in stock or not
//    @Transactional(readOnly = true)
////    @SneakyThrows //exception handling for Thread.sleep not to be used in production
//    public List<InventoryResponse> isInStock(List<String> skuCode) {
//        // Implement logic to check if the product with the given SKU code is in stock
//        // This could involve querying the database or an external inventory system
//
////        log.info("wait started");
////        Thread.sleep(10000);// Simulate a delay of 10 seconds
////        log.info("wait ended");
//
//        List<InventoryResponse> inventory = inventoryRepository.findBySkuCodeIn(skuCode).stream()
//                .map(inv->
//                    InventoryResponse.builder()
//                            .skuCode(inv.getSkuCode())
//                            .isInStock(inv.getQuantity()>0)
//                            .build()
//
//                ).toList();
//                //.orElseThrow(() ->  new SkuNotFoundException("SKU not found: " + skuCode));
//
//        return inventory;
//    }

@Transactional(readOnly = true)
public List<InventoryResponse> isInStock(List<InventoryCheckRequest> requests) {
    List<String> skuCodes = requests.stream()
            .map(InventoryCheckRequest::getSkuCode)
            .toList();

    Map<String, Inventory> inventoryMap = inventoryRepository.findBySkuCodeIn(skuCodes)
            .stream()
            .collect(Collectors.toMap(Inventory::getSkuCode, inv -> inv));

    return requests.stream()
            .map(request -> {
                Inventory inv = inventoryMap.get(request.getSkuCode());
                boolean sufficient = inv != null
                        && inv.getQuantity() >= request.getRequiredQuantity();// ✅ actual check
                return InventoryResponse.builder()
                        .skuCode(request.getSkuCode())
                        .isInStock(sufficient)
                        .quantity(inv != null ? inv.getQuantity() : 0)
                        .build();
            })
            .toList();
}

    // Add stock
    @Transactional
    public void addInventory(InventoryRequest request) {
        Inventory inventory = inventoryRepository.findBySkuCode(request.getSkuCode())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + request.getQuantity());
                    return existing;
                })
                .orElseGet(() -> Inventory.builder()
                        .skuCode(request.getSkuCode())
                        .quantity(request.getQuantity())
                        .warehouseLocation(request.getWarehouseLocation())
                        .build());

        inventoryRepository.save(inventory);
    }

    //Reduce Stock (used when order is placed)
    @Transactional  // ensures find + modify + save are atomic
    public void reduceStock(String skuCode, Integer orderedQty) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new SkuNotFoundException("SKU not found: " + skuCode));

        if (inventory.getQuantity() < orderedQty) {
            throw new InsufficientStockException("Not enough stock for SKU: " + skuCode);
        }

        inventory.setQuantity(inventory.getQuantity() - orderedQty);
        inventoryRepository.save(inventory);
    }

    // Low stock products
    public List<InventoryResponse> getLowStockProducts() {

        return inventoryRepository.findByQuantityLessThan(lowStockThreshold)
                .stream()
                .map(inv -> InventoryResponse.builder()
                        .skuCode(inv.getSkuCode())
                        .quantity(inv.getQuantity())
                        .isInStock(inv.getQuantity()>0)
                        .build())
                .toList();
    }




}
