package com.milkpudding.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 日志过滤器
 * 记录请求和响应信息
 */
@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
    
    public LoggingFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            
            long startTime = System.currentTimeMillis();
            String requestId = generateRequestId();
            
            // 记录请求信息
            log.info("Request [{}] - {} {} from {}", 
                requestId,
                request.getMethod(), 
                request.getURI(), 
                getClientIp(request));
            
            // 记录请求头（如果启用详细日志）
            if (config.isDetailedLogging()) {
                request.getHeaders().forEach((name, values) ->
                    log.debug("Request [{}] Header - {}: {}", requestId, name, values));
            }
            
            return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    long duration = System.currentTimeMillis() - startTime;
                    
                    // 记录响应信息
                    log.info("Response [{}] - {} {} completed in {}ms with status {}", 
                        requestId,
                        request.getMethod(), 
                        request.getURI(), 
                        duration,
                        response.getStatusCode());
                    
                    // 记录响应头（如果启用详细日志）
                    if (config.isDetailedLogging()) {
                        response.getHeaders().forEach((name, values) ->
                            log.debug("Response [{}] Header - {}: {}", requestId, name, values));
                    }
                })
            );
        };
    }
    
    /**
     * 生成请求ID
     */
    private String generateRequestId() {
        return String.valueOf(System.currentTimeMillis() % 1000000);
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddress() != null ? 
            request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }
    
    public static class Config {
        private boolean enabled = true;
        private boolean detailedLogging = false;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public boolean isDetailedLogging() {
            return detailedLogging;
        }
        
        public void setDetailedLogging(boolean detailedLogging) {
            this.detailedLogging = detailedLogging;
        }
    }
}