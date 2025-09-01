package com.milkpudding.api.controller;

import com.milkpudding.api.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    /**
     * 使用Feign客户端获取外部数据 (同步)
     */
    @GetMapping("/posts/{id}")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable Long id) {
        Map<String, Object> result = externalApiService.getPostWithFeign(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 使用WebClient获取外部数据 (异步)
     */
    @GetMapping("/posts/{id}/reactive")
    public Mono<ResponseEntity<Map>> getPostReactive(@PathVariable Long id) {
        return externalApiService.getPostWithWebClient(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 创建新的Post
     */
    @PostMapping("/posts")
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Map<String, Object> post) {
        Map<String, Object> result = externalApiService.createPost(post);
        return ResponseEntity.ok(result);
    }

    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "pudding-api",
                "timestamp", java.time.Instant.now().toString()
        ));
    }
}
