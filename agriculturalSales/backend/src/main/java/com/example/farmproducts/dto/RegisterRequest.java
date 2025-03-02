package com.example.farmproducts.dto;

import com.example.farmproducts.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotNull(message = "用户角色不能为空")
    private UserRole role;
} 