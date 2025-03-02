package com.example.farmproducts.service;

import com.example.farmproducts.dto.RegisterRequest;
import com.example.farmproducts.dto.UserProfileRequest;
import com.example.farmproducts.dto.UserProfileResponse;
import com.example.farmproducts.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.farmproducts.exception.PhoneNotFoundException;

public interface UserService {
    User register(RegisterRequest request);
    UserDetails loadUserByPhone(String phone) throws PhoneNotFoundException;
    UserProfileResponse getUserProfile(Long userId);
    UserProfileResponse updateUserProfile(Long userId, UserProfileRequest request);
}