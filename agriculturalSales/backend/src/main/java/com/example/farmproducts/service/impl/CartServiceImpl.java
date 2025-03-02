package com.example.farmproducts.service.impl;

import com.example.farmproducts.dto.CartItemRequest;
import com.example.farmproducts.dto.CartItemResponse;
import com.example.farmproducts.entity.CartItem;
import com.example.farmproducts.entity.Product;
import com.example.farmproducts.entity.User;
import com.example.farmproducts.repository.CartItemRepository;
import com.example.farmproducts.repository.ProductRepository;
import com.example.farmproducts.repository.UserRepository;
import com.example.farmproducts.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartItemResponse addToCart(CartItemRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("商品库存不足");
        }

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId())
                .orElse(new CartItem());

            
        if (cartItem.getId() != null) {
            if(product.getStock() < cartItem.getQuantity() + request.getQuantity()  ) 
            {
                throw new RuntimeException(String.format("商品%s库存不足,当前库存为%d", product.getName(), product.getStock()));
            }else{
                cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            }
        }
        else{
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setImage(product.getImage());
        }

        return convertToResponse(cartItemRepository.save(cartItem));
    }

    @Override
    @Transactional
    public CartItemResponse updateCartItem(Long cartItemId, CartItemRequest request, Long userId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权修改此购物车项");
        }

        Product product = cartItem.getProduct();
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("商品库存不足");
        }

        cartItem.setQuantity(request.getQuantity());
        return convertToResponse(cartItemRepository.save(cartItem));
    }

    @Override
    @Transactional
    public void removeFromCart(Long cartItemId, Long userId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权删除此购物车项");
        }

        cartItemRepository.delete(cartItem);
    }

    @Override
    public List<CartItemResponse> getUserCart(Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        cartItemRepository.deleteAll(cartItems);
    }

    @Override
    @Transactional
    public void checkoutItems(List<Long> itemIds, Long userId) {
        List<CartItem> items = cartItemRepository.findAllById(itemIds);
        
        // 验证所有商品都属于当前用户
        items.forEach(item -> {
            if (!item.getUser().getId().equals(userId)) {
                throw new RuntimeException("无权操作此购物车项");
            }
        });
        
        // 检查库存
        items.forEach(item -> {
            Product product = item.getProduct();
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException(
                    String.format("商品%s库存不足", product.getName())
                );
            }
        });
        
        // 更新库存
        items.forEach(item -> {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        });
        
        // 删除购物车项
        cartItemRepository.deleteAll(items);
    }

    private CartItemResponse convertToResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.getId());
        response.setProductId(cartItem.getProduct().getId());
        response.setProductName(cartItem.getProduct().getName());
        response.setProductDescription(cartItem.getProduct().getDescription());
        response.setProductImage(cartItem.getImage());
        response.setProductPrice(cartItem.getProduct().getPrice());
        response.setQuantity(cartItem.getQuantity());
        response.setTotalPrice(cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        return response;
    }
} 