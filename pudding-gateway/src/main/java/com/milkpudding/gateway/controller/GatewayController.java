package com.milkpudding.gateway.controller;

import com.milkpudding.gateway.common.Result;
import com.milkpudding.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 网关管理控制器
 * 提供网关状态查询、路由信息等管理功能
 */
@Slf4j
@RestController
@RequestMapping("/gateway")
public class GatewayController {
    
    @Autowired
    private RouteLocator routeLocator;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取网关状态信息
     */
    @GetMapping("/status")
    public Mono<Result<Map<String, Object>>> getStatus() {
        return Mono.fromCallable(() -> {
            Map<String, Object> status = new HashMap<>();
            status.put("service", "pudding-gateway");
            status.put("status", "running");
            status.put("timestamp", System.currentTimeMillis());
            status.put("version", "1.0.0");
            
            log.debug("Gateway status requested");
            return Result.success(status);
        });
    }
    
    /**
     * 获取路由信息
     */
    @GetMapping("/routes")
    public Mono<Result<List<Map<String, Object>>>> getRoutes() {
        return routeLocator.getRoutes()
            .map(this::routeToMap)
            .collectList()
            .map(routes -> {
                log.debug("Routes information requested, found {} routes", routes.size());
                return Result.success(routes);
            });
    }
    
    /**
     * 生成测试JWT Token（仅用于开发测试）
     */
    @PostMapping("/token/generate")
    public Mono<Result<Map<String, String>>> generateTestToken(@RequestBody GenerateTokenRequest request) {
        return Mono.fromCallable(() -> {
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", request.getRole() != null ? request.getRole() : "user");
            claims.put("email", request.getEmail());
            
            String token = jwtUtil.generateToken(request.getUserId(), request.getUsername(), claims);
            
            Map<String, String> result = new HashMap<>();
            result.put("token", token);
            result.put("type", "Bearer");
            result.put("expiresIn", "86400"); // 24小时
            
            log.info("Test token generated for user: {}", request.getUsername());
            return Result.success(result);
        });
    }
    
    /**
     * 验证JWT Token
     */
    @PostMapping("/token/validate")
    public Mono<Result<Map<String, Object>>> validateToken(@RequestBody ValidateTokenRequest request) {
        return Mono.fromCallable(() -> {
            Map<String, Object> payload = jwtUtil.parseToken(request.getToken());
            
            if (payload != null) {
                log.debug("Token validation successful for token");
                return Result.success(payload);
            } else {
                log.warn("Token validation failed");
                return Result.error(401, "Invalid token");
            }
        });
    }
    
    /**
     * 将Route转换为Map
     */
    private Map<String, Object> routeToMap(Route route) {
        Map<String, Object> routeMap = new HashMap<>();
        routeMap.put("id", route.getId());
        routeMap.put("uri", route.getUri().toString());
        routeMap.put("predicate", route.getPredicate().toString());
        routeMap.put("filters", route.getFilters().stream()
            .map(filter -> filter.toString())
            .collect(Collectors.toList()));
        routeMap.put("order", route.getOrder());
        return routeMap;
    }
    
    /**
     * 生成Token请求
     */
    public static class GenerateTokenRequest {
        private String userId;
        private String username;
        private String email;
        private String role;
        
        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
    
    /**
     * 验证Token请求
     */
    public static class ValidateTokenRequest {
        private String token;
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}