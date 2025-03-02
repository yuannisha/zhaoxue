package com.example.farmproducts.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CartItemRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "商品数量不能为空")
    @Positive(message = "商品数量必须大于0")
    private Integer quantity;
} 