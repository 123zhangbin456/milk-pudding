package com.milkpudding.gateway.filter;

import com.milkpudding.gateway.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流过滤器
 * 基于令牌桶算法实现简单限流
 */
@Slf4j
@Component
public class RateLimitFilter extends AbstractGatewayFilterFactory<RateLimitFilter.Config> {
    
    // 存储每个IP的限流信息
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    
    public RateLimitFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String clientIp = getClientIp(request);
            
            // 获取或创建令牌桶
            TokenBucket bucket = buckets.get(clientIp);
            if (bucket == null) {
                bucket = new TokenBucket(config.getCapacity(), config.getRefillRate());
                buckets.put(clientIp, bucket);
            }
            
            // 检查是否允许请求
            if (!bucket.tryConsume()) {
                log.warn("Rate limit exceeded for IP: {} on path: {}", clientIp, request.getURI().getPath());
                return ResponseUtil.writeRateLimitResponse(exchange.getResponse());
            }
            
            log.debug("Request allowed for IP: {}, remaining tokens: {}", clientIp, bucket.getTokens());
            return chain.filter(exchange);
        };
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
    
    /**
     * 令牌桶实现
     */
    private static class TokenBucket {
        private final long capacity;        // 桶容量
        private final long refillRate;      // 每秒补充令牌数
        private long tokens;                // 当前令牌数
        private long lastRefillTime;        // 上次补充时间
        
        public TokenBucket(long capacity, long refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = capacity;
            this.lastRefillTime = Instant.now().getEpochSecond();
        }
        
        public synchronized boolean tryConsume() {
            refill();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }
        
        public synchronized long getTokens() {
            refill();
            return tokens;
        }
        
        private void refill() {
            long now = Instant.now().getEpochSecond();
            long timeElapsed = now - lastRefillTime;
            
            if (timeElapsed > 0) {
                long tokensToAdd = timeElapsed * refillRate;
                tokens = Math.min(capacity, tokens + tokensToAdd);
                lastRefillTime = now;
            }
        }
    }
    
    public static class Config {
        private long capacity = 100;      // 桶容量，默认100
        private long refillRate = 10;     // 每秒补充令牌数，默认10
        
        public long getCapacity() {
            return capacity;
        }
        
        public void setCapacity(long capacity) {
            this.capacity = capacity;
        }
        
        public long getRefillRate() {
            return refillRate;
        }
        
        public void setRefillRate(long refillRate) {
            this.refillRate = refillRate;
        }
    }
}