package com.milkpudding.gateway.util;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于JWT token的生成、解析和验证
 */
@Slf4j
@Component
public class JwtUtil {
    
    private static final String SECRET_KEY = "milk-pudding-gateway-secret-key-2024";
    private static final String ALGORITHM = "HmacSHA256";
    private static final long EXPIRATION_HOURS = 24; // token过期时间24小时
    
    /**
     * 生成JWT token
     */
    public String generateToken(String userId, String username, Map<String, Object> claims) {
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("sub", userId);
            payload.put("username", username);
            payload.put("iat", Instant.now().getEpochSecond());
            payload.put("exp", Instant.now().plus(EXPIRATION_HOURS, ChronoUnit.HOURS).getEpochSecond());
            
            if (claims != null) {
                payload.putAll(claims);
            }
            
            String headerJson = JSONUtil.toJsonStr(header);
            String payloadJson = JSONUtil.toJsonStr(payload);
            
            String headerBase64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadBase64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            
            String signature = generateSignature(headerBase64 + "." + payloadBase64);
            
            return headerBase64 + "." + payloadBase64 + "." + signature;
        } catch (Exception e) {
            log.error("Generate JWT token failed", e);
            return null;
        }
    }
    
    /**
     * 解析JWT token
     */
    public Map<String, Object> parseToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return null;
            }
            
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            
            String headerBase64 = parts[0];
            String payloadBase64 = parts[1];
            String signature = parts[2];
            
            // 验证签名
            String expectedSignature = generateSignature(headerBase64 + "." + payloadBase64);
            if (!signature.equals(expectedSignature)) {
                log.warn("JWT token signature verification failed");
                return null;
            }
            
            // 解析payload
            String payloadJson = new String(Base64.getUrlDecoder().decode(payloadBase64), StandardCharsets.UTF_8);
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = JSONUtil.toBean(payloadJson, Map.class);
            
            // 检查过期时间
            Long exp = ((Number) payload.get("exp")).longValue();
            if (Instant.now().getEpochSecond() > exp) {
                log.warn("JWT token has expired");
                return null;
            }
            
            return payload;
        } catch (Exception e) {
            log.error("Parse JWT token failed", e);
            return null;
        }
    }
    
    /**
     * 验证JWT token是否有效
     */
    public boolean validateToken(String token) {
        return parseToken(token) != null;
    }
    
    /**
     * 从token中获取用户ID
     */
    public String getUserId(String token) {
        Map<String, Object> payload = parseToken(token);
        return payload != null ? (String) payload.get("sub") : null;
    }
    
    /**
     * 从token中获取用户名
     */
    public String getUsername(String token) {
        Map<String, Object> payload = parseToken(token);
        return payload != null ? (String) payload.get("username") : null;
    }
    
    /**
     * 生成签名
     */
    private String generateSignature(String data) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        mac.init(secretKeySpec);
        byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
    }
}