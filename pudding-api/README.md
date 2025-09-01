# Pudding API Service

这是一个基于 Spring Boot 3 和 JDK 22 的外部API集成服务。

## 技术栈

- **Spring Boot 3** - 核心框架
- **Spring WebFlux** - 响应式编程支持
- **Spring Cloud OpenFeign** - 声明式HTTP客户端
- **WebClient** - 响应式HTTP客户端
- **Resilience4j** - 容错处理（断路器、重试、限流）
- **Micrometer** - 指标收集
- **WireMock** - 测试Mock服务

## 特性

- ✅ 支持同步和异步HTTP调用
- ✅ 内置断路器模式，防止雪崩效应
- ✅ 自动重试机制
- ✅ 请求限流保护
- ✅ 连接池优化
- ✅ 健康检查和指标监控
- ✅ 完整的单元测试

## 快速开始

### 启动服务
```bash
mvn spring-boot:run
```

服务将在端口 8083 启动。

### API 端点

#### 获取文章（同步）
```bash
curl http://localhost:8083/api/external/posts/1
```

#### 获取文章（异步响应式）
```bash
curl http://localhost:8083/api/external/posts/1/reactive
```

#### 创建文章
```bash
curl -X POST http://localhost:8083/api/external/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New Post",
    "body": "This is a new post",
    "userId": 1
  }'
```

#### 健康检查
```bash
curl http://localhost:8083/api/external/health
```

### 监控端点

- 健康检查: http://localhost:8083/actuator/health
- 断路器状态: http://localhost:8083/actuator/circuitbreakers
- 限流器状态: http://localhost:8083/actuator/ratelimiters
- 应用指标: http://localhost:8083/actuator/metrics

## 配置说明

### 断路器配置
- 滑动窗口大小: 10
- 失败率阈值: 50%
- 半开状态等待时间: 5秒

### 重试配置
- 最大重试次数: 3
- 重试间隔: 1秒（指数退避）

### 限流配置
- 每秒限制: 10个请求
- 超时时间: 3秒

## 扩展指南

### 添加新的外部API客户端

1. 在 `client` 包下创建新的 Feign 客户端接口
2. 在 `service` 包下创建对应的服务类
3. 添加相应的容错配置
4. 创建对应的控制器端点

### 自定义容错策略

在 `application.yml` 中调整 `resilience4j` 配置段的参数。

## 测试

运行所有测试：
```bash
mvn test
```

运行特定测试：
```bash
mvn test -Dtest=ExternalApiServiceTest
```
