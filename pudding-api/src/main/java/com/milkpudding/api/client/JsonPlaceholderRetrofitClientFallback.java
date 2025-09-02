package com.milkpudding.api.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * JsonPlaceholder Retrofit 客户端的熔断降级处理器
 */
@Slf4j
@Component
public class JsonPlaceholderRetrofitClientFallback {

    public Map<String, Object> getPostFallback(Long id, Exception ex) {
        log.warn("Fallback triggered for getPost with id: {}, exception: {}", id, ex.getMessage());
        return Map.of(
            "id", id,
            "title", "Fallback Post",
            "body", "This is a fallback response due to service unavailability.",
            "userId", 1,
            "fallback", true
        );
    }

    public List<Map<String, Object>> getAllPostsFallback(Exception ex) {
        log.warn("Fallback triggered for getAllPosts, exception: {}", ex.getMessage());
        return List.of(Map.of(
            "id", 1,
            "title", "Fallback Post",
            "body", "This is a fallback response due to service unavailability.",
            "userId", 1,
            "fallback", true
        ));
    }

    public Map<String, Object> createPostFallback(Map<String, Object> post, Exception ex) {
        log.warn("Fallback triggered for createPost, exception: {}", ex.getMessage());
        Map<String, Object> result = Map.of(
            "id", 101,
            "title", post.getOrDefault("title", "Fallback Title"),
            "body", post.getOrDefault("body", "Fallback Body"),
            "userId", post.getOrDefault("userId", 1),
            "fallback", true
        );
        return result;
    }

    public Map<String, Object> updatePostFallback(Long id, Map<String, Object> post, Exception ex) {
        log.warn("Fallback triggered for updatePost with id: {}, exception: {}", id, ex.getMessage());
        Map<String, Object> result = Map.of(
            "id", id,
            "title", post.getOrDefault("title", "Fallback Updated Title"),
            "body", post.getOrDefault("body", "Fallback Updated Body"),
            "userId", post.getOrDefault("userId", 1),
            "fallback", true
        );
        return result;
    }

    public Void deletePostFallback(Long id, Exception ex) {
        log.warn("Fallback triggered for deletePost with id: {}, exception: {}", id, ex.getMessage());
        return null;
    }
}
