package com.milkpudding.api.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Retrofit 相关配置
 */
@Configuration
public class RetrofitConfig {

    /**
     * 配置 JsonPlaceholder API 的熔断器
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50.0f) // 失败率阈值 50%
                .waitDurationInOpenState(Duration.ofSeconds(30)) // 熔断器打开状态等待时间 30 秒
                .slidingWindowSize(10) // 滑动窗口大小 10
                .minimumNumberOfCalls(5) // 最小调用次数 5
                .slowCallRateThreshold(50.0f) // 慢调用率阈值 50%
                .slowCallDurationThreshold(Duration.ofSeconds(2)) // 慢调用时间阈值 2 秒
                .permittedNumberOfCallsInHalfOpenState(3) // 半开状态允许的调用次数 3
                .automaticTransitionFromOpenToHalfOpenEnabled(true) // 自动从打开状态转换到半开状态
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        
        // 为不同的操作创建不同的熔断器实例
        registry.circuitBreaker("jsonplaceholder-get-post");
        registry.circuitBreaker("jsonplaceholder-get-all-posts");
        registry.circuitBreaker("jsonplaceholder-create-post");
        registry.circuitBreaker("jsonplaceholder-update-post");
        registry.circuitBreaker("jsonplaceholder-delete-post");
        
        return registry;
    }
}
