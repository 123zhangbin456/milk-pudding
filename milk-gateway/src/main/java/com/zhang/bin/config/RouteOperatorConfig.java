package com.zhang.bin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhang.bin.service.RouteOperator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ProjectName: milk-pudding
 * @ProjectPackage: com.zhang.bin.config
 * @Author: Mr.Pudding
 * @CreateTime: 2024-01-30  14:54
 * @Description: 路由配置类
 * @Version: 1.0
 */
@Configuration
public class RouteOperatorConfig {
    @Bean
    public RouteOperator routeOperator(ObjectMapper objectMapper,
                                       RouteDefinitionWriter routeDefinitionWriter,
                                       ApplicationEventPublisher applicationEventPublisher) {

        return new RouteOperator(objectMapper,
                routeDefinitionWriter,
                applicationEventPublisher);
    }
}
