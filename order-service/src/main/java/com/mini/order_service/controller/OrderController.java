package com.mini.order_service.controller;


import com.mini.order_service.dto.OrderRequest;
import com.mini.order_service.dto.OrderResponse;
import com.mini.order_service.model.OrderStatus;
import com.mini.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "inventoryFallback")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody @Valid OrderRequest orderRequest) {
       return CompletableFuture.supplyAsync(()->orderService.placeOrder(orderRequest));

    }

    public CompletableFuture<String> inventoryFallback(OrderRequest orderRequest, Throwable throwable) {
        log.error("Fallback triggered. Cause: {}", throwable.getMessage(), throwable); // ← see real error
        return CompletableFuture.supplyAsync(() -> "Failed to place order...");
    }

    @GetMapping
    public List<OrderResponse> getAllOrders(){
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderNumber}")
    public OrderResponse getOrder(@PathVariable String orderNumber){
        return orderService.getOrder(orderNumber);
    }

    @PutMapping("/{orderNumber}/cancel")
    public void cancelOrder(@PathVariable String orderNumber){
        orderService.cancelOrder(orderNumber);
    }

    @PutMapping("/{orderNumber}/status")
    public void updateOrderStatus(@PathVariable String orderNumber,
                                  @RequestParam OrderStatus status){

        orderService.updateStatus(orderNumber, status);
    }





}
