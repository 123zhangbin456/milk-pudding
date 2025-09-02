package com.milkpudding.api.controller;

import com.milkpudding.api.service.ComparisonResult;
import com.milkpudding.api.service.RetrofitComparisonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Retrofit 和 Feign 性能对比控制器
 */
@RestController
@RequestMapping("/api/comparison")
@RequiredArgsConstructor
@Slf4j
public class ComparisonController {
    
    private final RetrofitComparisonService comparisonService;
    
    /**
     * 对比获取单个Post的性能
     */
    @GetMapping("/post/{id}")
    public ComparisonResult compareGetPost(@PathVariable Long id) {
        log.info("Comparing getPost performance for id: {}", id);
        return comparisonService.compareGetPost(id);
    }
    
    /**
     * 对比获取所有Posts的性能
     */
    @GetMapping("/posts")
    public ComparisonResult compareGetAllPosts() {
        log.info("Comparing getAllPosts performance");
        return comparisonService.compareGetAllPosts();
    }
    
    /**
     * 对比创建Post的性能
     */
    @PostMapping("/post")
    public ComparisonResult compareCreatePost(@RequestBody Map<String, Object> postData) {
        log.info("Comparing createPost performance");
        return comparisonService.compareCreatePost(postData);
    }
    
    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "service", "Retrofit Comparison API",
            "timestamp", String.valueOf(System.currentTimeMillis()),
            "features", Map.of(
                "retrofit", "enabled",
                "feign", "enabled",
                "circuitBreaker", "enabled",
                "retry", "enabled",
                "logging", "enabled"
            ),
            "endpoints", Map.of(
                "compareGetPost", "/api/comparison/post/{id}",
                "compareGetAllPosts", "/api/comparison/posts",
                "compareCreatePost", "/api/comparison/post"
            )
        );
    }
}
