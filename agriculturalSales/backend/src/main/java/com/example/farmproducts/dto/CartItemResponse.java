package com.example.farmproducts.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private String productDescription;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
} 