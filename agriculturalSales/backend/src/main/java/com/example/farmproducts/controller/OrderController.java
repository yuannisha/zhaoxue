package com.example.farmproducts.controller;

import com.example.farmproducts.dto.OrderRequest;
import com.example.farmproducts.dto.OrderResponse;
import com.example.farmproducts.entity.OrderStatus;
import com.example.farmproducts.entity.User;
import com.example.farmproducts.repository.UserRepository;
import com.example.farmproducts.security.CustomUserDetails;
import com.example.farmproducts.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping("/customer/create")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(orderService.createOrder(request, userOptional.get().getId()));
    }

    @GetMapping("/customer/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(orderService.getUserOrders(userOptional.get().getId()));
    }

    @GetMapping("/seller/orders")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<OrderResponse>> getSellerOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(orderService.getSellerOrders(userOptional.get().getId()));
    }

    @GetMapping("/getOrder/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER')")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(orderService.getOrderById(orderId, userOptional.get().getId()));
    }

    @PostMapping("/customer/{orderId}/pay")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> payOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        orderService.payOrder(orderId, userOptional.get().getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seller/{orderId}/ship")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> shipOrder(
            @PathVariable Long orderId,
            @RequestParam String trackingNumber,
            @RequestParam String shippingCompany,
            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        orderService.shipOrder(orderId, userOptional.get().getId(), trackingNumber, shippingCompany);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/customer/{orderId}/confirm")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> confirmReceipt(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        orderService.confirmReceipt(orderId, userOptional.get().getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/customer/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByUsername(userDetails.getUsername());
        orderService.cancelOrder(orderId, userOptional.get().getId());
        return ResponseEntity.ok().build();
    }
} 