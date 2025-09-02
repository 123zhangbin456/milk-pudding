package com.milkpudding.api.service;

import com.milkpudding.api.client.JsonPlaceholderClient;
import com.milkpudding.api.client.JsonPlaceholderRetrofitClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Retrofit 和 Feign 客户端性能对比服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RetrofitComparisonService {
    
    private final JsonPlaceholderClient feignClient;
    private final JsonPlaceholderRetrofitClient retrofitClient;
    
    /**
     * 对比获取单个Post的性能
     */
    public ComparisonResult compareGetPost(Long postId) {
        log.info("Starting performance comparison for getPost with id: {}", postId);
        
        ComparisonResult.ComparisonResultBuilder builder = ComparisonResult.builder();
        
        // 测试 Feign 客户端
        long start1 = System.currentTimeMillis();
        Map<String, Object> feignResult = null;
        boolean feignSuccess = false;
        String feignError = null;
        
        try {
            feignResult = feignClient.getPost(postId);
            feignSuccess = true;
            log.info("Feign client succeeded");
        } catch (Exception e) {
            feignError = e.getMessage();
            log.warn("Feign client failed: {}", e.getMessage());
        }
        long feignTime = System.currentTimeMillis() - start1;
        
        // 测试 Retrofit 客户端
        long start2 = System.currentTimeMillis();
        Map<String, Object> retrofitResult = null;
        boolean retrofitSuccess = false;
        String retrofitError = null;
        
        try {
            retrofitResult = retrofitClient.getPost(postId);
            retrofitSuccess = true;
            log.info("Retrofit client succeeded");
        } catch (Exception e) {
            retrofitError = e.getMessage();
            log.warn("Retrofit client failed: {}", e.getMessage());
        }
        long retrofitTime = System.currentTimeMillis() - start2;
        
        ComparisonResult result = builder
            .feignResponseTime(feignTime)
            .retrofitResponseTime(retrofitTime)
            .feignResult(feignResult)
            .retrofitResult(retrofitResult)
            .feignSuccess(feignSuccess)
            .retrofitSuccess(retrofitSuccess)
            .feignError(feignError)
            .retrofitError(retrofitError)
            .build();
            
        log.info("Performance comparison completed - Feign: {}ms, Retrofit: {}ms", 
                feignTime, retrofitTime);
        
        return result;
    }
    
    /**
     * 对比获取所有Posts的性能
     */
    public ComparisonResult compareGetAllPosts() {
        log.info("Starting performance comparison for getAllPosts");
        
        ComparisonResult.ComparisonResultBuilder builder = ComparisonResult.builder();
        
        // 测试 Feign 客户端
        long start1 = System.currentTimeMillis();
        Map<String, Object>[] feignResult = null;
        boolean feignSuccess = false;
        String feignError = null;
        
        try {
            feignResult = feignClient.getAllPosts();
            feignSuccess = true;
            log.info("Feign client succeeded, got {} posts", feignResult.length);
        } catch (Exception e) {
            feignError = e.getMessage();
            log.warn("Feign client failed: {}", e.getMessage());
        }
        long feignTime = System.currentTimeMillis() - start1;
        
        // 测试 Retrofit 客户端
        long start2 = System.currentTimeMillis();
        List<Map<String, Object>> retrofitResult = null;
        boolean retrofitSuccess = false;
        String retrofitError = null;
        
        try {
            retrofitResult = retrofitClient.getAllPosts();
            retrofitSuccess = true;
            log.info("Retrofit client succeeded, got {} posts", retrofitResult.size());
        } catch (Exception e) {
            retrofitError = e.getMessage();
            log.warn("Retrofit client failed: {}", e.getMessage());
        }
        long retrofitTime = System.currentTimeMillis() - start2;
        
        ComparisonResult result = builder
            .feignResponseTime(feignTime)
            .retrofitResponseTime(retrofitTime)
            .feignResult(Map.of("posts", feignResult, "count", feignResult != null ? feignResult.length : 0))
            .retrofitResult(Map.of("posts", retrofitResult, "count", retrofitResult != null ? retrofitResult.size() : 0))
            .feignSuccess(feignSuccess)
            .retrofitSuccess(retrofitSuccess)
            .feignError(feignError)
            .retrofitError(retrofitError)
            .build();
            
        log.info("Performance comparison completed - Feign: {}ms, Retrofit: {}ms", 
                feignTime, retrofitTime);
        
        return result;
    }
    
    /**
     * 对比创建Post的性能
     */
    public ComparisonResult compareCreatePost(Map<String, Object> postData) {
        log.info("Starting performance comparison for createPost");
        
        ComparisonResult.ComparisonResultBuilder builder = ComparisonResult.builder();
        
        // 测试 Feign 客户端
        long start1 = System.currentTimeMillis();
        Map<String, Object> feignResult = null;
        boolean feignSuccess = false;
        String feignError = null;
        
        try {
            feignResult = feignClient.createPost(postData);
            feignSuccess = true;
            log.info("Feign client succeeded");
        } catch (Exception e) {
            feignError = e.getMessage();
            log.warn("Feign client failed: {}", e.getMessage());
        }
        long feignTime = System.currentTimeMillis() - start1;
        
        // 测试 Retrofit 客户端
        long start2 = System.currentTimeMillis();
        Map<String, Object> retrofitResult = null;
        boolean retrofitSuccess = false;
        String retrofitError = null;
        
        try {
            retrofitResult = retrofitClient.createPost(postData);
            retrofitSuccess = true;
            log.info("Retrofit client succeeded");
        } catch (Exception e) {
            retrofitError = e.getMessage();
            log.warn("Retrofit client failed: {}", e.getMessage());
        }
        long retrofitTime = System.currentTimeMillis() - start2;
        
        ComparisonResult result = builder
            .feignResponseTime(feignTime)
            .retrofitResponseTime(retrofitTime)
            .feignResult(feignResult)
            .retrofitResult(retrofitResult)
            .feignSuccess(feignSuccess)
            .retrofitSuccess(retrofitSuccess)
            .feignError(feignError)
            .retrofitError(retrofitError)
            .build();
            
        log.info("Performance comparison completed - Feign: {}ms, Retrofit: {}ms", 
                feignTime, retrofitTime);
        
        return result;
    }
}
