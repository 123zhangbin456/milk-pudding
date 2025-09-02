package com.milkpudding.api.service;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 性能对比结果
 */
@Data
@Builder
public class ComparisonResult {
    private long feignResponseTime;
    private long retrofitResponseTime;
    private Map<String, Object> feignResult;
    private Map<String, Object> retrofitResult;
    private boolean feignSuccess;
    private boolean retrofitSuccess;
    private String feignError;
    private String retrofitError;
}
