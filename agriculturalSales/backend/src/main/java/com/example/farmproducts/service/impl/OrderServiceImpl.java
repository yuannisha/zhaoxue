package com.example.farmproducts.service.impl;

import com.example.farmproducts.dto.OrderRequest;
import com.example.farmproducts.dto.OrderResponse;
import com.example.farmproducts.dto.ProductRequest;
import com.example.farmproducts.dto.ProductResponse;
import com.example.farmproducts.dto.OrderItemResponse;
import com.example.farmproducts.entity.*;
import com.example.farmproducts.enums.OrderStatus;
import com.example.farmproducts.repository.*;
import com.example.farmproducts.service.OrderService;
import com.example.farmproducts.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private boolean isStockEnough = true;
    private String productNames = "";
    private String stock = "";

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        List<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemIds());
      
        // 检查库存 
        cartItems.forEach(item->{
            ProductResponse productResponse = productService.getProduct(item.getProduct().getId());
            if(productResponse.getStock() < item.getQuantity()){
                productNames += productResponse.getName() + "、" ;
                stock += productResponse.getStock() + "、";
                isStockEnough = false;
            }
        });

        if(!isStockEnough){
            throw new RuntimeException(productNames + "的库存不足，库存分别为" + stock);
        }

        // 按卖家分组
        Map<Long, List<CartItem>> sellerGroups = cartItems.stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getSeller().getId()));
        
        List<Order> orders = new ArrayList<>();
        
        // 为每个卖家创建订单
        sellerGroups.forEach((sellerId, items) -> {
            Order order = new Order();
            order.setOrderNumber(generateOrderNumber());
            order.setUser(user);
            order.setSeller(userRepository.findById(sellerId).orElseThrow());
            order.setReceiverName(request.getReceiverName());
            order.setReceiverPhone(request.getReceiverPhone());
            order.setReceiverAddress(request.getReceiverAddress());
            order.setStatus(OrderStatus.PENDING_PAYMENT);
            
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();
            
            for (CartItem cartItem : items) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getProduct().getPrice());
                orderItem.setTotalPrice(cartItem.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                
                totalAmount = totalAmount.add(orderItem.getTotalPrice());
                orderItems.add(orderItem);
            }
            
            order.setTotalAmount(totalAmount);
            order.setItems(orderItems);
            orders.add(order);
        });
        
        // 保存所有订单
        orderRepository.saveAll(orders);

        // 减库存，如果商品库存为
        cartItems.forEach(item->{
            ProductResponse productResponse = productService.getProduct(item.getProduct().getId());
            productResponse.setStock(productResponse.getStock() - item.getQuantity());
            ProductRequest requestforUpdate = convertToRequest(productResponse);
            productService.updateProduct(productResponse.getId(), requestforUpdate, productResponse.getSellerId());

        });
        
        // 删除购物车项
        cartItemRepository.deleteAll(cartItems);
        
        // 返回第一个订单的响应（如果需要返回所有订单，可以修改返回类型为List<OrderResponse>）
        return convertToResponse(orders.get(0));
    }

    @Override
    public List<OrderResponse> getUserOrders(Long userId) {
        //通过创建时间排序，最新的订单排在最前面
        return orderRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(Order::getCreateTime).reversed())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getSellerOrders(Long sellerId) {
        return orderRepository.findBySellerId(sellerId).stream()
                .sorted(Comparator.comparing(Order::getCreateTime).reversed())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (!order.getUser().getId().equals(userId) && !order.getSeller().getId().equals(userId)) {
            throw new RuntimeException("无权访问此订单");
        }
        
        return convertToResponse(order);
    }

    @Override
    @Transactional
    public void payOrder(Long orderId, Long userId) {
        Order order = getOrderForUpdate(orderId, userId);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(OrderStatus.PENDING_SHIPMENT);
        order.setPayTime(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void shipOrder(Long orderId, Long sellerId, String trackingNumber, String shippingCompany) {
        Order order = getOrderForUpdate(orderId, sellerId);
        if (order.getStatus() != OrderStatus.PENDING_SHIPMENT) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(OrderStatus.SHIPPED);
        order.setTrackingNumber(trackingNumber);
        order.setShippingCompany(shippingCompany);
        order.setShipTime(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void confirmReceipt(Long orderId, Long userId) {
        Order order = getOrderForUpdate(orderId, userId);
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompleteTime(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = getOrderForUpdate(orderId, userId);
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("只能取消待付款订单");
        }
        
        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        //取消订单后，恢复商品的库存
        order.getItems().forEach(item->{
            ProductResponse productResponse = productService.getProduct(item.getProduct().getId());
            productResponse.setStock(productResponse.getStock() + item.getQuantity());
            ProductRequest requestforUpdate = convertToRequest(productResponse);
            productService.updateProduct(productResponse.getId(), requestforUpdate, productResponse.getSellerId());
        });

    }

    private Order getOrderForUpdate(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (!order.getUser().getId().equals(userId) && !order.getSeller().getId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        return order;
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setReceiverName(order.getReceiverName());
        response.setReceiverPhone(order.getReceiverPhone());
        response.setReceiverAddress(order.getReceiverAddress());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setStatusDescription(order.getStatus().getDescription());
        response.setTrackingNumber(order.getTrackingNumber());
        response.setShippingCompany(order.getShippingCompany());
        response.setCreateTime(order.getCreateTime());
        response.setPayTime(order.getPayTime());
        response.setShipTime(order.getShipTime());
        response.setCompleteTime(order.getCompleteTime());
        
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::convertToItemResponse)
                .collect(Collectors.toList());
        response.setItems(items);
        
        return response;
    }

    private OrderItemResponse convertToItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setProductImage(item.getProduct().getImage());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        response.setTotalPrice(item.getTotalPrice());
        return response;
    }

    //将ProductResponse转换为ProductRequest
    private ProductRequest convertToRequest(ProductResponse productResponse) {
        ProductRequest request = new ProductRequest();
        request.setName(productResponse.getName());
        request.setDescription(productResponse.getDescription());
        request.setPrice(productResponse.getPrice());
        request.setStock(productResponse.getStock());
        request.setType(productResponse.getType());
        request.setImage(productResponse.getImage());
        return request;
    }

    private String generateOrderNumber() {
        return String.format("%s%06d",
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                new Random().nextInt(1000000));
    }
} 