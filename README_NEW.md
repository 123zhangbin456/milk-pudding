# milk-pudding (Maven, JDK 22, Spring Boot 3.3.x + Spring Cloud 2023.0.x + Consul)

## 项目介绍

Milk-Pudding 是一个基于 Spring Cloud 微服务架构的分布式系统，具备完整的API网关、服务发现、认证鉴权和限流功能。

### 核心特性

- ✅ **API网关**: 基于Spring Cloud Gateway实现统一入口
- ✅ **服务发现**: 使用Consul实现自动服务注册与发现
- ✅ **认证鉴权**: JWT Token认证，支持用户身份验证
- ✅ **限流保护**: 基于令牌桶算法的API限流
- ✅ **日志监控**: 请求响应日志记录和链路追踪
- ✅ **全局异常处理**: 统一的错误响应格式
- ✅ **CORS支持**: 跨域请求处理
- ✅ **容器化部署**: Docker和Docker Compose支持

## 快速开始

### 🚀 一键启动（推荐）

```bash
# 使用启动脚本
./start-services.sh

# 选择Docker Compose启动方式
# 所有服务将自动启动并可用
```

### 1) 环境检查
```bash
java -version   # 确认是 22
mvn -version
docker --version
```

### 2) 快速测试
```bash
# 运行完整功能测试
./test-gateway.sh
```

## 服务架构

```
外部请求 → Nginx → API Gateway (8080) → User Service (8081)
                      ↓
                 Consul (8500)
```

### 服务端口

| 服务 | 端口 | 描述 |
|-----|------|------|
| pudding-gateway | 8080 | API网关服务 |
| pudding-user | 8081 | 用户服务 |
| consul | 8500 | 服务注册中心 |

## API接口

### 🔐 认证接口

```bash
# 用户登录
POST /api/v1/auth/login
{
  "username": "admin",
  "password": "123456"
}

# 用户注册  
POST /api/v1/auth/register
{
  "username": "testuser",
  "password": "123456",
  "email": "test@example.com"
}

# 获取当前用户信息（需要Token）
GET /api/v1/auth/me
Header: Authorization: Bearer <token>
```

### 👤 用户接口

```bash
# 获取用户信息（需要Token）
GET /api/v1/users/{id}
Header: Authorization: Bearer <token>

# 创建用户（需要Token）
POST /api/v1/users
Header: Authorization: Bearer <token>
{
  "name": "Test User",
  "email": "test@example.com"
}
```

### 🔧 网关管理接口

```bash
# 网关状态
GET /gateway/status

# 路由信息
GET /gateway/routes

# 生成测试Token
POST /gateway/token/generate
{
  "userId": "admin-001",
  "username": "Administrator", 
  "email": "admin@pudding.dev",
  "role": "admin"
}

# 验证Token
POST /gateway/token/validate
{
  "token": "your-jwt-token"
}
```

## 功能特性详解

### 🛡️ 认证鉴权系统

- **JWT Token认证**: 基于JWT实现无状态认证
- **路径白名单**: 配置无需认证的接口路径
- **用户信息传递**: 自动将用户信息注入到请求头传递给下游服务
- **Token验证**: 支持Token有效期验证和签名校验

### 🚦 限流保护

- **令牌桶算法**: 基于令牌桶的分布式限流
- **IP级别限流**: 针对客户端IP进行限流控制
- **差异化配置**: 不同接口可配置不同的限流策略
- **实时监控**: 限流状态实时监控和告警

### 📊 日志监控

- **请求链路追踪**: 完整记录请求响应链路
- **性能监控**: 记录接口响应时间和状态
- **错误日志**: 详细的错误信息和堆栈跟踪
- **访问统计**: 接口访问频次和模式分析

### 🔧 配置管理

- **环境变量支持**: 支持环境变量配置覆盖
- **配置热更新**: Consul配置中心支持配置热加载
- **多环境配置**: 支持dev/test/prod多环境配置

## 部署方案

### Docker Compose（推荐）

```bash
# 一键启动所有服务
docker-compose up --build -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### Maven 本地启动

```bash
# 1. 启动 Consul
docker run -d --name consul -p 8500:8500 consul:1.15.3

# 2. 启动用户服务
CONSUL_HOST=127.0.0.1 mvn -pl pudding-user spring-boot:run

# 3. 启动网关（新终端）
CONSUL_HOST=127.0.0.1 mvn -pl pudding-gateway spring-boot:run
```

### Docker 单独构建

```bash
# 构建镜像
docker build -t milk/pudding-user:dev -f pudding-user/Dockerfile .
docker build -t milk/pudding-gateway:dev -f pudding-gateway/Dockerfile .

# 运行容器
docker run -d --name pudding-user -p 8081:8081 \
  -e CONSUL_HOST=host.docker.internal milk/pudding-user:dev

docker run -d --name pudding-gateway -p 8080:8080 \
  -e CONSUL_HOST=host.docker.internal milk/pudding-gateway:dev
```

## 网关配置说明

### 路由配置

网关自动配置以下路由规则：

- `/api/v1/users/**` → pudding-user服务（需要认证）
- `/api/v1/auth/**` → pudding-user服务（无需认证）
- `/actuator/**` → 健康检查路由
- `/api/**` → 默认API路由（需要认证）

### 过滤器配置

- **认证过滤器**: 验证JWT Token，注入用户信息
- **日志过滤器**: 记录请求响应信息
- **限流过滤器**: API访问频率限制
- **CORS过滤器**: 跨域请求处理

### Consul配置

```yaml
spring:
  cloud:
    consul:
      host: ${CONSUL_HOST:localhost}
      port: ${CONSUL_PORT:8500}
      discovery:
        service-name: ${spring.application.name}
        health-check-path: /actuator/health
        health-check-interval: 10s
        prefer-ip-address: true
```

## 开发指南

### 添加新的微服务

1. 创建新的Maven模块
2. 添加Consul客户端依赖
3. 配置服务注册信息
4. 在网关中添加路由配置

### 自定义过滤器

1. 继承`AbstractGatewayFilterFactory`
2. 实现`apply`方法定义过滤逻辑
3. 在`GatewayConfig`中注册过滤器

### 扩展认证策略

1. 修改`AuthFilter`添加新的认证逻辑
2. 更新`JwtUtil`支持新的Token格式
3. 调整白名单路径配置

## 监控与运维

### 健康检查

- Gateway: http://localhost:8080/actuator/health
- User Service: http://localhost:8081/actuator/health
- Consul UI: http://localhost:8500

### 日志查看

```bash
# Docker Compose日志
docker-compose logs -f pudding-gateway
docker-compose logs -f pudding-user

# 应用日志级别
logging.level.com.milkpudding=DEBUG
```

### 性能监控

- 服务发现状态监控
- API响应时间统计
- 限流触发情况监控
- 错误率和成功率统计

## 测试用例

### 功能测试

```bash
# 自动化测试脚本
./test-gateway.sh

# 手动测试命令示例
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 性能测试

```bash
# 使用ab进行压力测试
ab -n 1000 -c 10 http://localhost:8080/api/v1/auth/login

# 限流测试
for i in {1..20}; do curl http://localhost:8080/api/v1/auth/login; done
```

## 故障排除

### 常见问题

1. **服务无法注册到Consul**
   - 检查Consul是否启动
   - 验证网络连接配置

2. **JWT Token验证失败**
   - 检查Token格式是否正确
   - 验证密钥配置是否一致

3. **限流触发过于频繁**
   - 调整令牌桶容量参数
   - 检查客户端请求频率

### 日志分析

- 查看Gateway日志定位路由问题
- 检查User Service日志确认业务逻辑
- 监控Consul日志排查服务发现问题

## 约定规范

- Java 基础包：`com.milkpudding.*`
- 依赖：Spring Boot、Spring Cloud Gateway、Consul、Lombok、Hutool
- API版本：统一使用`/api/v1/`前缀
- 响应格式：统一的Result<T>响应结构
