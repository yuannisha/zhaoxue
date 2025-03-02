package com.example.farmproducts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

import com.example.farmproducts.entity.ProductType;

@Data
public class ProductRequest {
    @NotBlank(message = "商品名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "商品价格不能为空")
    @Positive(message = "商品价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "商品类型不能为空")
    private ProductType type;

    @NotBlank(message = "商品图片不能为空")
    private String image;

    @NotNull(message = "商品库存不能为空")
    @PositiveOrZero(message = "商品库存不能小于0")
    private Integer stock;
} 