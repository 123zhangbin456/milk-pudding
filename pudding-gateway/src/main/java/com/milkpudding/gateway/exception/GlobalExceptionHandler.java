package com.milkpudding.gateway.exception;

import com.milkpudding.gateway.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

/**
 * 全局异常处理器
 * 处理网关层面的异常并返回统一的错误响应
 */
@Slf4j
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        String path = exchange.getRequest().getURI().getPath();
        
        // 记录异常信息
        log.error("Gateway error occurred on path: {} - {}", path, ex.getMessage(), ex);
        
        // 根据异常类型返回不同的错误响应
        if (ex instanceof NotFoundException) {
            // 服务未找到
            return ResponseUtil.writeErrorResponse(response, HttpStatus.SERVICE_UNAVAILABLE, 
                "Service temporarily unavailable");
        } else if (ex instanceof ConnectException) {
            // 连接异常
            return ResponseUtil.writeErrorResponse(response, HttpStatus.BAD_GATEWAY, 
                "Unable to connect to downstream service");
        } else if (ex instanceof TimeoutException) {
            // 超时异常
            return ResponseUtil.writeErrorResponse(response, HttpStatus.GATEWAY_TIMEOUT, 
                "Gateway timeout");
        } else if (ex instanceof ResponseStatusException) {
            // 响应状态异常
            ResponseStatusException rse = (ResponseStatusException) ex;
            HttpStatus status = HttpStatus.valueOf(rse.getStatusCode().value());
            return ResponseUtil.writeErrorResponse(response, status, 
                rse.getReason() != null ? rse.getReason() : "Request failed");
        } else if (ex instanceof IllegalArgumentException) {
            // 参数异常
            return ResponseUtil.writeErrorResponse(response, HttpStatus.BAD_REQUEST, 
                "Invalid request parameters");
        } else {
            // 其他未知异常
            return ResponseUtil.writeErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, 
                "Internal server error");
        }
    }
}