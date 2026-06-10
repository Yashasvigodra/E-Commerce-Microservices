package com.mini.s.product.service.controller;

import com.mini.s.product.service.dto.ProductRequest;
import com.mini.s.product.service.dto.ProductResponse;
import com.mini.s.product.service.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/api/product")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // CREATE
    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        productService.createProduct(productRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Product created successfully");
    }

    // GET ALL — paginated by default, no more full collection loads
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.getAllProducts(page, size));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // UPDATE — full update
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(
            @PathVariable String id,
            @RequestBody @Valid ProductRequest productRequest) {
        productService.updateProduct(id, productRequest);
        return ResponseEntity.ok("Product updated successfully");
    }

    // PARTIAL UPDATE — only fields provided are changed
    @PatchMapping("/{id}")
    public ResponseEntity<String> patchProduct(
            @PathVariable String id,
            @RequestBody ProductRequest productRequest) {  // no @Valid — fields are optional for patch
        productService.patchProduct(id, productRequest);
        return ResponseEntity.ok("Product patched successfully");
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("Product deleted successfully");
    }

    // SEARCH BY NAME
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProductsByName(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    // SEARCH BY PRICE RANGE
    @GetMapping("/search/price")
    public ResponseEntity<List<ProductResponse>> searchProductsByPriceRange(
            @RequestParam @Positive BigDecimal min,
            @RequestParam @Positive BigDecimal max) {
        return ResponseEntity.ok(productService.filterByPrice(min, max));
    }
}