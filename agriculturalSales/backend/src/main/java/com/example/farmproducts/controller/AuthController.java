package com.example.farmproducts.controller;

import com.example.farmproducts.config.JwtTokenUtil;
import com.example.farmproducts.dto.AuthRequest;
import com.example.farmproducts.dto.AuthResponse;
import com.example.farmproducts.dto.RegisterRequest;
import com.example.farmproducts.dto.UserProfileResponse;
import com.example.farmproducts.entity.User;
import com.example.farmproducts.repository.UserRepository;
import com.example.farmproducts.security.CustomUserDetails;
import com.example.farmproducts.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationProvider authenticationProvider;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {

        final UserDetails user = userService.loadUserByPhone(request.getPhone());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        authenticationProvider.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );
        
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());

        final String jwt = jwtTokenUtil.generateToken(user);
        
        return ResponseEntity.ok(new AuthResponse(user.getUsername(), jwt, userOptional.get().getRole().name()));
    }
} 