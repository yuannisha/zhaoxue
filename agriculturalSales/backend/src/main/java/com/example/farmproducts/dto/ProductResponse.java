package com.example.farmproducts.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.farmproducts.entity.ProductType;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private ProductType type;
    private Integer stock;
    private Long sellerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 