package com.milkpudding.gateway.filter;

import com.milkpudding.gateway.util.JwtUtil;
import com.milkpudding.gateway.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * JWT认证过滤器
 * 验证请求中的JWT token
 */
@Slf4j
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // 不需要鉴权的路径
    private static final List<String> SKIP_AUTH_PATHS = Arrays.asList(
        "/",
        "/actuator/health",
        "/api/v1/auth/login",
        "/api/v1/auth/register"
    );
    
    public AuthFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            
            // 跳过不需要鉴权的路径
            if (shouldSkipAuth(path)) {
                return chain.filter(exchange);
            }
            
            // 获取Authorization头
            String authHeader = request.getHeaders().getFirst("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or invalid Authorization header for path: {}", path);
                return ResponseUtil.writeUnauthorizedResponse(exchange.getResponse());
            }
            
            // 提取token
            String token = authHeader.substring(7);
            
            // 验证token
            Map<String, Object> payload = jwtUtil.parseToken(token);
            if (payload == null) {
                log.warn("Invalid JWT token for path: {}", path);
                return ResponseUtil.writeUnauthorizedResponse(exchange.getResponse());
            }
            
            // 将用户信息添加到请求头中，传递给下游服务
            String userId = (String) payload.get("sub");
            String username = (String) payload.get("username");
            
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-Username", username)
                    .build();
            
            log.info("Authentication successful for user: {} ({}), path: {}", username, userId, path);
            
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }
    
    /**
     * 判断是否需要跳过鉴权
     */
    private boolean shouldSkipAuth(String path) {
        return SKIP_AUTH_PATHS.stream().anyMatch(skipPath -> 
            path.equals(skipPath) || path.startsWith(skipPath + "/"));
    }
    
    public static class Config {
        // 配置参数，如需要可以添加
        private boolean enabled = true;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}