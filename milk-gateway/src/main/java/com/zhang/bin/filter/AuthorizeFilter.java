package com.zhang.bin.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @ProjectName: milk-pudding
 * @ProjectPackage: com.zhang.bin.filter
 * @Author: Mr.Pudding
 * @CreateTime: 2024-01-24  18:06
 * @Description: TODO
 * @Version: 1.0
 */
@Component
@Slf4j
public class AuthorizeFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        List<String> sign = headers.get("sign");
        log.info("signList:{}",sign);
        if(sign.size() < 1) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            //被拦截，请求不到对应模块了
            return exchange.getResponse().setComplete();
        }

        // 在Spring WebFlux中，标准的HttpHeaders类是不可变的，这意味着一旦创建后就不能添加、删除或修改其内容。
        // 而HttpHeaders.writableHttpHeaders()方法返回的是一个可以修改的HttpHeaders实现。
        headers = HttpHeaders.writableHttpHeaders(headers);
        headers.set("sign1", "bigAAA");
        //放行
        return chain.filter(exchange);
    }
}
