package com.milkpudding.gateway.util;

import cn.hutool.json.JSONUtil;
import com.milkpudding.gateway.common.Result;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 响应工具类
 * 用于生成统一的响应格式
 */
public class ResponseUtil {
    
    /**
     * 返回JSON响应
     */
    public static Mono<Void> writeJsonResponse(ServerHttpResponse response, HttpStatus status, Result<?> result) {
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        response.getHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
        
        String jsonResult = JSONUtil.toJsonStr(result);
        DataBuffer buffer = response.bufferFactory().wrap(jsonResult.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
    
    /**
     * 返回成功响应
     */
    public static Mono<Void> writeSuccessResponse(ServerHttpResponse response, Object data) {
        return writeJsonResponse(response, HttpStatus.OK, Result.success(data));
    }
    
    /**
     * 返回错误响应
     */
    public static Mono<Void> writeErrorResponse(ServerHttpResponse response, HttpStatus status, String message) {
        return writeJsonResponse(response, status, Result.error(status.value(), message));
    }
    
    /**
     * 返回未授权响应
     */
    public static Mono<Void> writeUnauthorizedResponse(ServerHttpResponse response) {
        return writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthorized access");
    }
    
    /**
     * 返回禁止访问响应
     */
    public static Mono<Void> writeForbiddenResponse(ServerHttpResponse response) {
        return writeErrorResponse(response, HttpStatus.FORBIDDEN, "Access forbidden");
    }
    
    /**
     * 返回限流响应
     */
    public static Mono<Void> writeRateLimitResponse(ServerHttpResponse response) {
        return writeErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
    }
}