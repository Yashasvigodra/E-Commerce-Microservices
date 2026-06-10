package com.mini.order_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOrderStateException extends RuntimeException {

    private List<String> notFound;
    private List<String> outOfStock;

    // ✅ Constructor for simple cases (like cancelOrder)
    public InvalidOrderStateException(String message) {
        super(message);
        this.notFound = List.of();
        this.outOfStock = List.of();
    }

    public InvalidOrderStateException(String message,
                                      List<String> notFound,
                                      List<String> outOfStock) {
        super(message);
        this.notFound = notFound;
        this.outOfStock = outOfStock;
    }

    public List<String> getNotFound() { return notFound; }
    public List<String> getOutOfStock() { return outOfStock; }
}