package com.example.farmproducts.service;

import com.example.farmproducts.dto.CartItemRequest;
import com.example.farmproducts.dto.CartItemResponse;
import java.util.List;

public interface CartService {
    CartItemResponse addToCart(CartItemRequest request, Long userId);
    CartItemResponse updateCartItem(Long cartItemId, CartItemRequest request, Long userId);
    void removeFromCart(Long cartItemId, Long userId);
    List<CartItemResponse> getUserCart(Long userId);
    void clearCart(Long userId);
    void checkoutItems(List<Long> itemIds, Long userId);
} 