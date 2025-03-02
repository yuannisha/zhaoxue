package com.example.farmproducts.service.impl;

import com.example.farmproducts.dto.ProductRequest;
import com.example.farmproducts.dto.ProductResponse;
import com.example.farmproducts.entity.Product;
import com.example.farmproducts.entity.ProductType;
import com.example.farmproducts.entity.User;
import com.example.farmproducts.repository.ProductRepository;
import com.example.farmproducts.repository.UserRepository;
import com.example.farmproducts.service.ProductService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("卖家不存在"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setType(request.getType());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSeller(seller);
        product.setImage(request.getImage());

        return convertToResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, Long sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("无权修改此商品");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setType(request.getType());
        product.setImage(request.getImage());

        return convertToResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id, Long sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("无权删除此商品");
        }

        productRepository.delete(product);
    }

    @Override
    public ProductResponse getProduct(Long id) {
        return productRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
    }

    private Product convertToEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setType(request.getType());
        product.setImage(request.getImage());
        return product;
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setType(product.getType());
        response.setImage(product.getImage());
        response.setSellerId(product.getSeller().getId());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, BigDecimal minPrice, BigDecimal maxPrice,
            ProductType type, Pageable pageable) {
        Page<Product> products = productRepository.searchProducts(keyword, minPrice, maxPrice,type, pageable);
        return products.map(this::convertToResponse);
    }

    @Override
    public Page<ProductResponse> searchSellerProducts(Long sellerId, String keyword, BigDecimal minPrice,
            BigDecimal maxPrice, ProductType type, Pageable pageable) {
        return productRepository.searchSellerProducts(sellerId, keyword, minPrice, maxPrice, type, pageable)
                .map(this::convertToResponse);
    }
} 