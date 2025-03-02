package com.example.farmproducts.dto;

import com.example.farmproducts.entity.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileResponse {
    private Long id;
    private String username;
    private UserRole role;
    private String realName;
    private String phone;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 