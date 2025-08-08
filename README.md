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

  ## 配置说明
  - 已引入 `spring-cloud-alibaba-dependencies:${spring-cloud-alibaba.version}`，无需在子模块写 nacos 依赖版本；
  - Nacos 注册中心 & 配置中心默认从 `NACOS_ADDR` 读取地址，默认为 `127.0.0.1:8848`；
  - Gateway 路由使用 `lb://pudding-user`，通过注册中心做服务发现与负载均衡。

  ## 约定
  - Java 基础包：`com.milkpudding.*`
  - 依赖：Spring Boot、Spring Cloud Gateway、Spring Cloud Alibaba（Nacos）、Lombok、Hutool。
