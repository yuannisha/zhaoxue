package com.example.farmproducts.service;

import com.example.farmproducts.dto.OrderRequest;
import com.example.farmproducts.dto.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request, Long userId);
    List<OrderResponse> getUserOrders(Long userId);
    List<OrderResponse> getSellerOrders(Long sellerId);
    OrderResponse getOrderById(Long orderId, Long userId);
    void payOrder(Long orderId, Long userId);
    void shipOrder(Long orderId, Long sellerId, String trackingNumber, String shippingCompany);
    void confirmReceipt(Long orderId, Long userId);
    void cancelOrder(Long orderId, Long userId);
} 