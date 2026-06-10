package com.mini.s.product.service.service;

import com.mini.s.product.service.Exceptions.ProductNotFoundException;
import com.mini.s.product.service.dto.ProductRequest;
import com.mini.s.product.service.dto.ProductResponse;
import com.mini.s.product.service.model.Product;
import com.mini.s.product.service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    // ──────────────────────────────────────────────
    // CREATE
    // ──────────────────────────────────────────────

    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    // ──────────────────────────────────────────────
    // READ
    // ──────────────────────────────────────────────

    // Paginated by default — never loads full collection into memory
    public Page<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.findAll(pageable)
                .map(this::mapToProductResponse);   // Page has .map() built in — no manual stream needed
    }

    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));
        return mapToProductResponse(product);
    }

    public List<ProductResponse> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    public List<ProductResponse> filterByPrice(BigDecimal min, BigDecimal max) {
        // Validate range before hitting the DB
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException(
                    "Min price " + min + " cannot be greater than max price " + max
            );
        }
        return productRepository.findByPriceBetween(min, max)
                .stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    // ──────────────────────────────────────────────
    // UPDATE — full replace, all fields required
    // ──────────────────────────────────────────────

    public void updateProduct(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        productRepository.save(product);
        log.info("Product {} is updated", product.getId());
    }

    // ──────────────────────────────────────────────
    // PATCH — partial update, only provided fields changed
    // ──────────────────────────────────────────────

    public void patchProduct(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + id));

        // Only update fields that are actually provided — nulls are ignored
        if (StringUtils.hasText(request.getName())) {
            product.setName(request.getName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        productRepository.save(product);
        log.info("Product {} is patched", product.getId());
    }

    // ──────────────────────────────────────────────
    // DELETE
    // ──────────────────────────────────────────────

    public void deleteProduct(String id) {
        // existsById avoids loading the full object just to delete it
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id " + id);
        }
        productRepository.deleteById(id);
        log.info("Product {} is deleted", id);
    }

    // ──────────────────────────────────────────────
    // PRIVATE HELPERS
    // ──────────────────────────────────────────────

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}