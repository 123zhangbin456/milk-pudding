package com.milkpudding.api.client;

import com.milkpudding.api.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 示例外部API客户端
 * 这里以JSONPlaceholder API为例
 */
@FeignClient(
    name = "jsonplaceholder-api",
    url = "${external.api.jsonplaceholder.url:https://jsonplaceholder.typicode.com}",
    configuration = FeignConfig.class
)
public interface JsonPlaceholderClient {

    @GetMapping("/posts/{id}")
    Map<String, Object> getPost(@PathVariable("id") Long id);

    @GetMapping("/posts")
    Map<String, Object>[] getAllPosts();

    @PostMapping("/posts")
    Map<String, Object> createPost(@RequestBody Map<String, Object> post);

    @PutMapping("/posts/{id}")
    Map<String, Object> updatePost(@PathVariable("id") Long id, @RequestBody Map<String, Object> post);

    @DeleteMapping("/posts/{id}")
    void deletePost(@PathVariable("id") Long id);
}
