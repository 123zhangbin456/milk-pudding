# milk-pudding (Maven, JDK 22, Spring Boot 3.3.x + Spring Cloud 2023.0.x + Spring Cloud Alibaba/Nacos)

## 快速开始

### 1) 环境检查
```bash
java -version   # 确认是 22
mvn -version
docker --version
```

### 2) 编译打包
```bash
mvn clean package -DskipTests
```

### 3) 启动 Nacos（本地）
- Docker 快速启动：
```bash
docker run -d --name nacos -p 8848:8848 -e MODE=standalone nacos/nacos-server:v2.3.2
```
- 或按需修改 `NACOS_ADDR` 环境变量。

### 4) 运行（本地 Maven）
```bash
# 启动用户服务
NACOS_ADDR=127.0.0.1:8848 mvn -pl pudding-user spring-boot:run

# 启动网关（通过 lb://pudding-user 转发）
NACOS_ADDR=127.0.0.1:8848 mvn -pl pudding-gateway spring-boot:run
```
验证：
- 用户服务：GET http://localhost:8081/api/v1/users/123
- 网关：GET http://localhost:8080/api/v1/users/123

### 5) Docker 构建与运行
> 在项目根目录执行（注意 `-f` 指向子模块的 Dockerfile）

```bash
# 构建镜像
docker build -t milk/pudding-user:dev -f pudding-user/Dockerfile .
docker build -t milk/pudding-gateway:dev -f pudding-gateway/Dockerfile .

# 运行（依赖 NACOS_ADDR，可根据实际替换）
docker run -d --name pudding-user -p 8081:8081 \
  -e NACOS_ADDR=host.docker.internal:8848 milk/pudding-user:dev

docker run -d --name pudding-gateway -p 8080:8080 \
  -e NACOS_ADDR=host.docker.internal:8848 milk/pudding-gateway:dev
```

### 6) Docker Compose 启动 (包括 Nacos、pudding-user 和 pudding-gateway)
```bash
docker-compose up --build
```

这会拉起 `nacos`、`pudding-user`、`pudding-gateway` 三个容器，并确保 `pudding-gateway` 通过 `lb://pudding-user` 路由到用户服务。

### 7) 配置 Nacos 中的 DataId 示例（`pudding-gateway.yml`）
在 Nacos 配置中心中，添加以下 `DataId` 来配置 `pudding-gateway` 服务的参数：

- **DataId**：`pudding-gateway.yml`
- **内容**：
```yaml
server:
  port: 8080
spring:
  cloud:
    gateway:
      routes:
        - id: user_route
          uri: lb://pudding-user
          predicates:
            - Path=/api/v1/users/**
      default-filters:
        - DedupeResponseHeader=Access-Control-Allow-Credentials Access-Control-Allow-Origin
      httpclient:
        pool:
          # 略小于 Nginx 的 keepalive_timeout 60s
          max-idle-time: 55s
          # 可选：让连接定期轮换，避免长寿命连接堆积
          max-life-time: 10m
        connect-timeout: 5000
      # 建议 ≤ Nginx 的 proxy_read_timeout
      response-timeout: 30s

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

**说明**：上面的配置将被 `pudding-gateway` 服务在启动时自动加载，若使用 Nacos 作为配置中心。你可以在 Nacos 控制台添加此 DataId，或者直接通过配置文件 `application.yml` 覆盖。

### 8) 配置 Nginx 示例
```nginx configuration
upstream user_svc {
    server 127.0.0.1:8081;
    keepalive 100;              # 允许缓存的空闲上游连接数
    keepalive_timeout 60s;      # 上游连接的空闲超时
    keepalive_requests 1000;    # 每条连接最多复用次数（可选）
}

server {
    location /api/ {
        proxy_pass http://user_svc;
        proxy_http_version 1.1;
        proxy_set_header Connection "";  # 允许 keep-alive
        proxy_read_timeout 30s;          # 响应读超时，和网关的 response-timeout 对齐
        proxy_send_timeout 30s;
    }
}
```
**说明**：
- `response-timeout`：如果网关直接连 Nginx，建议设为 不大于 Nginx 的 `proxy_read_timeout`，避免 Nginx先超时、网关还在等。
- `keepalive_requests`：Nginx 可能会在复用一定次数后主动关连接；可用 `max-life-time` 做个“时间上限”让网关周期性换血，降低命中关闭的概率。
- 过小 / 过大：
  - 太小 → 频繁建连，RTT 抖；
  - 太大 → 更容易撞上对端先关导致 `connection reset`。
  一般“略小于对端”就是稳妥值。

## 配置说明：
- Nacos 配置中心会通过 `spring.cloud.nacos.config.server-addr` 连接到指定的地址，支持从 Nacos 加载所有服务的配置文件。
- 若本地环境没有启用 Nacos，可以通过环境变量 `NACOS_ADDR` 设置 Nacos 地址，默认值是 `127.0.0.1:8848`。
- 在 Spring Cloud Alibaba 2023.x 的 “spring.config.import” 语法里，nacos: 后面必须跟上要拉取的 DataId（还可以带 group/namespace 等查询参数）。只写 nacos: 是不合法的。

## 约定
- Java 基础包：`com.milkpudding.*`
- 依赖：Spring Boot、Spring Cloud Gateway、Spring Cloud Alibaba（Nacos）、Lombok、Hutool。
