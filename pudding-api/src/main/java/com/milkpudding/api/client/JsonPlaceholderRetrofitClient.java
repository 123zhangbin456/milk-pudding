package com.milkpudding.api.client;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import com.github.lianjiatech.retrofit.spring.boot.log.Logging;
import com.github.lianjiatech.retrofit.spring.boot.retry.Retry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * Retrofit 版本的 JsonPlaceholder 客户端
 * 用于与现有 Feign 客户端进行对比测试
 */
@RetrofitClient(
    baseUrl = "${external.api.jsonplaceholder.url:https://jsonplaceholder.typicode.com}",
    connectTimeoutMs = 5000,
    readTimeoutMs = 10000,
    writeTimeoutMs = 10000
)
@Logging(enable = true)
@Retry(enable = true, maxRetries = 3, intervalMs = 1000)
public interface JsonPlaceholderRetrofitClient {

    /**
     * 获取单个 Post
     */
    @GET("posts/{id}")
    @CircuitBreaker(name = "jsonplaceholder-get-post", fallbackMethod = "getPostFallback")
    Map<String, Object> getPost(@Path("id") Long id);

    /**
     * 获取所有 Posts
     */
    @GET("posts")
    @CircuitBreaker(name = "jsonplaceholder-get-all-posts", fallbackMethod = "getAllPostsFallback")
    List<Map<String, Object>> getAllPosts();

    /**
     * 创建新的 Post
     */
    @POST("posts")
    @CircuitBreaker(name = "jsonplaceholder-create-post", fallbackMethod = "createPostFallback")
    Map<String, Object> createPost(@Body Map<String, Object> post);

    /**
     * 更新 Post
     */
    @PUT("posts/{id}")
    @CircuitBreaker(name = "jsonplaceholder-update-post", fallbackMethod = "updatePostFallback")
    Map<String, Object> updatePost(@Path("id") Long id, @Body Map<String, Object> post);

    /**
     * 删除 Post
     */
    @DELETE("posts/{id}")
    @CircuitBreaker(name = "jsonplaceholder-delete-post", fallbackMethod = "deletePostFallback")
    Void deletePost(@Path("id") Long id);
}
