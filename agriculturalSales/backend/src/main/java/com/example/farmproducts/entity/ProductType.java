package com.example.farmproducts.entity;

public enum ProductType {
    VEGETABLE("蔬菜"),
    FRUIT("水果"),
    GRAIN("粮食"),
    MEAT("肉类"),
    SEAFOOD("海鲜"),
    OTHER("其他");

    private final String description;

    ProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 