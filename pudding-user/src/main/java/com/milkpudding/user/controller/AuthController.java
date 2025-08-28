package com.milkpudding.user.controller;

import cn.hutool.core.util.IdUtil;
import com.milkpudding.user.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 提供登录、注册等认证相关功能
 */
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
        log.info("User login attempt: {}", request.getUsername());
        
        // 简单的模拟登录验证（实际项目中应该查询数据库并验证密码）
        if ("admin".equals(request.getUsername()) && "123456".equals(request.getPassword())) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Login successful");
            result.put("user", User.builder()
                .id("admin-001")
                .name("Administrator")
                .email("admin@pudding.dev")
                .build());
            result.put("tokenInfo", Map.of(
                "message", "Please use /gateway/token/generate to get JWT token",
                "userId", "admin-001",
                "username", "Administrator"
            ));
            
            log.info("User login successful: {}", request.getUsername());
            return result;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Invalid username or password");
            
            log.warn("User login failed: {}", request.getUsername());
            return result;
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody RegisterRequest request) {
        log.info("User register attempt: {}", request.getUsername());
        
        String userId = IdUtil.fastSimpleUUID();
        User user = User.builder()
            .id(userId)
            .name(request.getUsername())
            .email(request.getEmail())
            .build();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Registration successful");
        result.put("user", user);
        
        log.info("User registration successful: {}", request.getUsername());
        return result;
    }
    
    /**
     * 获取当前用户信息（需要token）
     */
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @RequestHeader(value = "X-Username", required = false) String username) {
        
        if (userId == null || username == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "User not authenticated");
            return result;
        }
        
        User user = User.builder()
            .id(userId)
            .name(username)
            .email(username + "@pudding.dev")
            .build();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("user", user);
        
        log.info("Current user info requested: {} ({})", username, userId);
        return result;
    }
    
    @Data
    public static class LoginRequest {
        @NotBlank(message = "Username cannot be empty")
        private String username;
        
        @NotBlank(message = "Password cannot be empty")
        private String password;
    }
    
    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Username cannot be empty")
        private String username;
        
        @NotBlank(message = "Password cannot be empty")
        private String password;
        
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email cannot be empty")
        private String email;
    }
}