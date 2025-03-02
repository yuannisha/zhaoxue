package com.example.farmproducts.controller;

import com.example.farmproducts.dto.ProductRequest;
import com.example.farmproducts.dto.ProductResponse;
import com.example.farmproducts.entity.ProductType;
import com.example.farmproducts.security.CustomUserDetails;
import com.example.farmproducts.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 从UserDetails中获取用户ID
        Long sellerId = getUserId(userDetails);
        return ResponseEntity.ok(productService.createProduct(request, sellerId));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long sellerId = getUserId(userDetails);
        return ResponseEntity.ok(productService.updateProduct(id, request, sellerId));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long sellerId = getUserId(userDetails);
        productService.deleteProduct(id, sellerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping("/get/all")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) ProductType type,
            Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(
                keyword, minPrice, maxPrice, type, pageable));
    }

    @GetMapping("/seller/get/all")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<ProductResponse>> searchSellerProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) ProductType type,
            Pageable pageable) {
        Long sellerId = getUserId(userDetails);
        return ResponseEntity.ok(productService.searchSellerProducts(
                sellerId, keyword, minPrice, maxPrice, type, pageable));
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getId();
        }
        throw new RuntimeException("Invalid user details type");
    }
} 