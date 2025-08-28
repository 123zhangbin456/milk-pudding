package com.milkpudding.gateway.config;

import com.milkpudding.gateway.filter.AuthFilter;
import com.milkpudding.gateway.filter.LoggingFilter;
import com.milkpudding.gateway.filter.RateLimitFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 网关配置类
 * 配置路由规则、过滤器和CORS
 */
@Configuration
public class GatewayConfig {
    
    @Autowired
    private AuthFilter authFilter;
    
    @Autowired
    private LoggingFilter loggingFilter;
    
    @Autowired
    private RateLimitFilter rateLimitFilter;
    
    /**
     * 配置路由规则
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // 用户服务路由
            .route("user-service", r -> r
                .path("/api/v1/users/**")
                .filters(f -> f
                    .filter(loggingFilter.apply(new LoggingFilter.Config()))
                    .filter(rateLimitFilter.apply(createRateLimitConfig()))
                    .filter(authFilter.apply(new AuthFilter.Config()))
                    .stripPrefix(0)  // 不去除路径前缀
                )
                .uri("lb://pudding-user")
            )
            // 认证服务路由（不需要鉴权）
            .route("auth-service", r -> r
                .path("/api/v1/auth/**")
                .filters(f -> f
                    .filter(loggingFilter.apply(new LoggingFilter.Config()))
                    .filter(rateLimitFilter.apply(createAuthRateLimitConfig()))
                    .stripPrefix(0)
                )
                .uri("lb://pudding-user")
            )
            // 健康检查路由（直接转发给用户服务）
            .route("health-check", r -> r
                .path("/actuator/**")
                .filters(f -> f
                    .filter(loggingFilter.apply(new LoggingFilter.Config()))
                )
                .uri("lb://pudding-user")
            )
            // 其他API路由（默认需要鉴权）
            .route("api-service", r -> r
                .path("/api/**")
                .filters(f -> f
                    .filter(loggingFilter.apply(new LoggingFilter.Config()))
                    .filter(rateLimitFilter.apply(createRateLimitConfig()))
                    .filter(authFilter.apply(new AuthFilter.Config()))
                    .stripPrefix(0)
                )
                .uri("lb://pudding-user")
            )
            .build();
    }
    
    /**
     * 创建普通限流配置
     */
    private RateLimitFilter.Config createRateLimitConfig() {
        RateLimitFilter.Config config = new RateLimitFilter.Config();
        config.setCapacity(100);      // 桶容量100
        config.setRefillRate(10);     // 每秒补充10个令牌
        return config;
    }
    
    /**
     * 创建认证接口限流配置（更严格）
     */
    private RateLimitFilter.Config createAuthRateLimitConfig() {
        RateLimitFilter.Config config = new RateLimitFilter.Config();
        config.setCapacity(20);       // 桶容量20
        config.setRefillRate(2);      // 每秒补充2个令牌
        return config;
    }
    
    /**
     * CORS配置
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedOriginPattern("*");
        corsConfig.addAllowedMethod(HttpMethod.GET);
        corsConfig.addAllowedMethod(HttpMethod.POST);
        corsConfig.addAllowedMethod(HttpMethod.PUT);
        corsConfig.addAllowedMethod(HttpMethod.DELETE);
        corsConfig.addAllowedMethod(HttpMethod.OPTIONS);
        corsConfig.addAllowedMethod(HttpMethod.PATCH);
        corsConfig.addAllowedHeader("*");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}