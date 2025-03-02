package com.example.farmproducts.repository;

import com.example.farmproducts.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    List<Order> findByUserId(Long userId);
    List<Order> findBySellerId(Long sellerId);
} 