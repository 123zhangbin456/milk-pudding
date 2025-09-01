package com.milkpudding.api.service;

import com.milkpudding.api.client.JsonPlaceholderClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final JsonPlaceholderClient jsonPlaceholderClient;
    private final WebClient webClient;

    /**
     * 使用Feign客户端调用外部API
     */
    @CircuitBreaker(name = "jsonplaceholder", fallbackMethod = "fallbackGetPost")
    @Retry(name = "jsonplaceholder")
    @RateLimiter(name = "jsonplaceholder")
    public Map<String, Object> getPostWithFeign(Long id) {
        log.info("Calling external API with Feign for post id: {}", id);
        return jsonPlaceholderClient.getPost(id);
    }

    /**
     * 使用WebClient调用外部API (响应式)
     */
    @CircuitBreaker(name = "jsonplaceholder", fallbackMethod = "fallbackGetPostReactive")
    @Retry(name = "jsonplaceholder")
    @RateLimiter(name = "jsonplaceholder")
    public Mono<Map> getPostWithWebClient(Long id) {
        log.info("Calling external API with WebClient for post id: {}", id);
        return webClient
                .get()
                .uri("https://jsonplaceholder.typicode.com/posts/{id}", id)
                .retrieve()
                .bodyToMono(Map.class)
                .doOnSuccess(result -> log.info("Successfully retrieved post: {}", result))
                .doOnError(error -> log.error("Error retrieving post: {}", error.getMessage()));
    }

    /**
     * Feign调用失败时的降级方法
     */
    public Map<String, Object> fallbackGetPost(Long id, Exception ex) {
        log.warn("Fallback method called for post id: {} due to: {}", id, ex.getMessage());
        return Map.of(
                "id", id,
                "title", "Service Unavailable",
                "body", "The external service is currently unavailable. Please try again later.",
                "userId", 0,
                "fallback", true
        );
    }

    /**
     * WebClient调用失败时的降级方法
     */
    public Mono<Map> fallbackGetPostReactive(Long id, Exception ex) {
        log.warn("Reactive fallback method called for post id: {} due to: {}", id, ex.getMessage());
        return Mono.just(Map.of(
                "id", id,
                "title", "Service Unavailable",
                "body", "The external service is currently unavailable. Please try again later.",
                "userId", 0,
                "fallback", true
        ));
    }

    /**
     * 创建新的Post
     */
    @CircuitBreaker(name = "jsonplaceholder", fallbackMethod = "fallbackCreatePost")
    @Retry(name = "jsonplaceholder")
    @RateLimiter(name = "jsonplaceholder")
    public Map<String, Object> createPost(Map<String, Object> post) {
        log.info("Creating new post: {}", post);
        return jsonPlaceholderClient.createPost(post);
    }

    public Map<String, Object> fallbackCreatePost(Map<String, Object> post, Exception ex) {
        log.warn("Fallback method called for creating post due to: {}", ex.getMessage());
        return Map.of(
                "id", 0,
                "title", post.getOrDefault("title", "Unknown"),
                "body", post.getOrDefault("body", "Unknown"),
                "userId", post.getOrDefault("userId", 0),
                "error", "Failed to create post",
                "fallback", true
        );
    }
}
