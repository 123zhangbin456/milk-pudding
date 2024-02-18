package com.zhang.bin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: milk-pudding
 * @ProjectPackage: com.zhang.bin.service
 * @Author: Mr.Pudding
 * @CreateTime: 2024-01-30  14:35
 * @Description: 动态路由更新服务主要提供网关进程内删除、添加等操作。
 * 该类主要有路由清除clear、路由添加add、路由发布到进程publish和更新全部refreshAll方法。
 * 其中clear、add、publish为private方法，对外提供的为refreshAll方法。
 * 实现思路：先清空路由->添加全部路由->发布路由更新事件->完成。
 * @Version: 1.0
 */
@Slf4j
public class RouteOperator {
    private ObjectMapper objectMapper;

    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher applicationEventPublisher;

    private static final List<String> routeList = new ArrayList<>();

    public RouteOperator(ObjectMapper objectMapper, RouteDefinitionWriter routeDefinitionWriter, ApplicationEventPublisher applicationEventPublisher) {
        this.objectMapper = objectMapper;
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 清理集合中的所有路由，并清空集合
     */
    private void clear() {
        // 全部调用API清理掉
        try {
            routeList.forEach(id -> routeDefinitionWriter.delete(Mono.just(id)).subscribe());
        } catch (Exception e) {
            log.error("clear Route is error !");
        }
        // 清空集合
        routeList.clear();
    }

    /**
     * 新增路由
     *
     * @param routeDefinitions
     */
    private void add(List<RouteDefinition> routeDefinitions) {

        try {
            routeDefinitions.forEach(routeDefinition -> {
                routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
                routeList.add(routeDefinition.getId());
            });
        } catch (Exception exception) {
            log.error("add route is error", exception);
        }
    }

    /**
     * 发布进程内通知，更新路由
     */
    private void publish() {
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(routeDefinitionWriter));
    }

    /**
     * 更新所有路由信息
     *
     * @param configStr
     */
    public void refreshAll(String configStr) {
        log.info("start refreshAll : {}", configStr);
        // 无效字符串不处理
        if (!StringUtils.hasText(configStr)) {
            log.error("invalid string for route config");
            return;
        }
        // 用Jackson反序列化
        List<RouteDefinition> routeDefinitions = null;
        try {
            routeDefinitions = objectMapper.readValue(configStr, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("get route definition from nacos string error", e);
        }
        // 如果等于null，表示反序列化失败，立即返回
        if (null == routeDefinitions) {
            return;
        }
        // 清理掉当前所有路由
        clear();
        // 添加最新路由
        add(routeDefinitions);

        // 通过应用内消息的方式发布
        publish();

        log.info("finish refreshAll");
    }
}
