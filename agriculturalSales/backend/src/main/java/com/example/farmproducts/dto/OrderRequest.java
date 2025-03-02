package com.example.farmproducts.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private List<Long> cartItemIds;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
} 