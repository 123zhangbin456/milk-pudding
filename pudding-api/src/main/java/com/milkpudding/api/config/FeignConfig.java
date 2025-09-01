package com.milkpudding.api.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                // 添加通用请求头
                requestTemplate.header("User-Agent", "PuddingAPI/1.0");
                requestTemplate.header("Accept", "application/json");
                requestTemplate.header("Content-Type", "application/json");
            }
        };
    }
}
