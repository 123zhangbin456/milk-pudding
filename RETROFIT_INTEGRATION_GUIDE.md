# Retrofit 集成配置示例

## 1. 添加 Maven 依赖

在 `pudding-api/pom.xml` 中添加：

```xml
<!-- Retrofit Spring Boot Starter -->
<dependency>
    <groupId>com.github.lianjiatech</groupId>
    <artifactId>retrofit-spring-boot-starter</artifactId>
    <version>3.1.8</version>
</dependency>
```

## 2. 在 consul-configs/pudding-api.yml 中添加配置

```yaml
# Retrofit 配置
retrofit:
  # 全局日志打印配置
  global-log:
    enable: true
    log-level: info
    log-strategy: basic
    aggregate: true
  
  # 全局重试配置
  global-retry:
    enable: false  # 使用声明式重试
    interval-ms: 100
    max-retries: 2
    retry-rules:
      - response_status_not_2xx
      - occur_io_exception
  
  # 全局超时时间配置
  global-timeout:
    read-timeout-ms: 10000
    write-timeout-ms: 10000
    connect-timeout-ms: 5000
    call-timeout-ms: 0
  
  # 熔断降级配置
  degrade:
    degrade-type: resilience4j
    global-resilience4j-degrade:
      enable: false  # 使用现有的 Resilience4j 配置
```

## 3. 创建对比测试服务

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class RetrofitComparisonService {
    
    private final JsonPlaceholderClient feignClient;
    private final JsonPlaceholderRetrofitClient retrofitClient;
    
    public ComparisonResult comparePerformance(Long postId) {
        long start1 = System.currentTimeMillis();
        Map<String, Object> feignResult = feignClient.getPost(postId);
        long feignTime = System.currentTimeMillis() - start1;
        
        long start2 = System.currentTimeMillis();
        Map<String, Object> retrofitResult = retrofitClient.getPost(postId);
        long retrofitTime = System.currentTimeMillis() - start2;
        
        return ComparisonResult.builder()
            .feignResponseTime(feignTime)
            .retrofitResponseTime(retrofitTime)
            .feignResult(feignResult)
            .retrofitResult(retrofitResult)
            .build();
    }
}
```

## 4. 迁移策略

### Phase 1: 并存测试 (1-2周)
- 保留现有 Feign 客户端
- 添加 Retrofit 客户端
- 创建 A/B 测试端点

### Phase 2: 功能验证 (2-3周)
- 验证所有 API 调用功能
- 测试错误处理和重试机制
- 性能基准测试

### Phase 3: 逐步替换 (3-4周)
- 替换非关键业务 API
- 监控错误率和性能
- 完全替换并移除 Feign

## 5. 回滚计划

如果出现问题，可以：
1. 通过配置开关快速切换回 Feign
2. 移除 retrofit 依赖
3. 恢复原有配置
