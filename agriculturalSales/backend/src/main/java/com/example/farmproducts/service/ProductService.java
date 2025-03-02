package com.example.farmproducts.service;

import com.example.farmproducts.dto.ProductRequest;
import com.example.farmproducts.dto.ProductResponse;
import com.example.farmproducts.entity.ProductType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request, Long sellerId);
    ProductResponse updateProduct(Long id, ProductRequest request, Long sellerId);
    void deleteProduct(Long id, Long sellerId);
    ProductResponse getProduct(Long id);
    Page<ProductResponse> searchProducts(String keyword, BigDecimal minPrice, 
            BigDecimal maxPrice, ProductType type, Pageable pageable);
    Page<ProductResponse> searchSellerProducts(Long sellerId, String keyword, 
            BigDecimal minPrice, BigDecimal maxPrice, ProductType type, Pageable pageable);
} 