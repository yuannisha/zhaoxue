package com.example.farmproducts.enums;

public enum OrderStatus {
    PENDING_PAYMENT("待付款"),
    PENDING_SHIPMENT("待发货"),
    SHIPPED("已发货"),
    PENDING_RECEIPT("待收货"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 