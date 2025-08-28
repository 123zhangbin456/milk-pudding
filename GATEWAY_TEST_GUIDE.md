# Gateway 测试指南

本文档提供了多种方式来测试 Pudding Gateway 的功能。

## 测试文件说明

### 1. `test-gateway-functions.sh` - 完整功能测试套件
这是最全面的测试脚本，包含：
- 基础健康检查
- 网关管理功能测试
- JWT Token 功能测试
- 过滤器功能测试
- 错误处理测试
- 简单性能测试

**使用方法：**
```bash
# 执行所有测试
./test-gateway-functions.sh

# 执行特定测试
./test-gateway-functions.sh health      # 只测试健康检查
./test-gateway-functions.sh jwt         # 只测试JWT功能
./test-gateway-functions.sh performance # 只测试性能
```

### 2. `test-api-cases.sh` - API测试用例集合
包含具体的API测试用例，适合单个接口测试：

```bash
# 执行所有API测试用例
./test-api-cases.sh

# 执行特定测试用例
./test-api-cases.sh jwt_generate_admin
./test-api-cases.sh gateway_status
```

### 3. `quick-test-commands.sh` - 快速测试命令
包含可直接复制粘贴的curl命令，适合快速验证：

```bash
# 直接执行（会运行所有命令）
source quick-test-commands.sh
```

### 4. `postman-collection.json` - Postman测试集合
可导入Postman的测试集合，包含：
- 自动化测试脚本
- 参数化配置
- 响应验证

**导入步骤：**
1. 打开Postman
2. 点击 Import
3. 选择 `postman-collection.json` 文件
4. 设置环境变量 `gateway_url` 为 `http://localhost:8080`

## 前提条件

### 1. 启动Gateway服务
```bash
# 方式1：使用Maven
cd pudding-gateway
mvn spring-boot:run

# 方式2：使用Docker Compose
docker-compose up pudding-gateway

# 方式3：使用启动脚本（如果有）
./start-services.sh
```

### 2. 确保依赖服务运行
- Consul (默认 localhost:8500)
- 如果有下游服务，确保它们也在运行

### 3. 安装测试工具（可选）
```bash
# 安装jq用于JSON格式化（推荐）
brew install jq

# 或者使用其他包管理器
sudo apt-get install jq  # Ubuntu/Debian
```

## 测试场景说明

### 基础功能测试
- ✅ 服务健康检查 (`/` 和 `/actuator/health`)
- ✅ 网关状态查询 (`/gateway/status`)
- ✅ 路由信息获取 (`/gateway/routes`)

### JWT Token 功能测试
- ✅ 生成管理员Token
- ✅ 生成普通用户Token
- ✅ Token有效性验证
- ✅ 无效Token处理
- ✅ 缺失字段处理

### 过滤器功能测试
- ✅ 请求日志过滤器
- ✅ CORS处理
- ✅ 自定义请求头处理

### 错误处理测试
- ✅ 404错误处理
- ✅ 无效JSON请求处理
- ✅ 异常情况处理

### 性能测试
- ✅ 响应时间测试
- ✅ 并发请求测试

## 常见问题

### Q: 测试失败，显示连接被拒绝
A: 确保Gateway服务已经启动，并且运行在8080端口

### Q: JWT Token测试失败
A: 检查JWT配置是否正确，确保密钥配置正确

### Q: 路由测试返回空数组
A: 这是正常的，因为可能还没有配置具体的下游服务路由

### Q: Consul相关错误
A: 确保Consul服务运行在localhost:8500，或者修改配置中的Consul地址

## 自定义测试

### 添加新的测试用例
在 `test-api-cases.sh` 中添加：
```bash
test_cases["your_test_name"]='GET /your/endpoint'
```

### 修改测试配置
修改脚本顶部的配置：
```bash
GATEWAY_HOST="your-gateway-host"
GATEWAY_PORT="your-port"
```

## 测试报告

测试完成后，检查以下内容：
1. 所有HTTP状态码是否符合预期
2. 响应JSON格式是否正确
3. JWT Token是否正确生成和验证
4. 错误处理是否合适
5. 响应时间是否在可接受范围内

## 下一步

1. 添加下游服务后，测试路由转发功能
2. 配置更多的网关过滤器并测试
3. 进行更全面的负载测试
4. 添加监控和日志分析
