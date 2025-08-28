#!/bin/bash

# Milk-Pudding 快速启动脚本

echo "=================================="
echo "  Milk-Pudding Gateway 启动脚本"
echo "=================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 检查Java版本
echo -e "${BLUE}1. 检查Java环境...${NC}"
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
echo "Java版本: $java_version"

if [[ "$java_version" < "22" ]]; then
    echo -e "${RED}警告: 需要Java 22或更高版本${NC}"
fi
echo ""

# 检查Maven
echo -e "${BLUE}2. 检查Maven环境...${NC}"
if command -v mvn &> /dev/null; then
    mvn_version=$(mvn -version 2>&1 | head -n 1)
    echo "Maven版本: $mvn_version"
else
    echo -e "${RED}错误: 未找到Maven，请先安装Maven${NC}"
    exit 1
fi
echo ""

# 选择启动方式
echo -e "${BLUE}3. 选择启动方式:${NC}"
echo "1) Docker Compose (推荐)"
echo "2) Maven本地启动"
echo "3) 仅启动Consul"
echo ""
read -p "请选择 (1-3): " choice

case $choice in
    1)
        echo -e "${BLUE}使用Docker Compose启动所有服务...${NC}"
        echo ""
        
        # 检查Docker
        if ! command -v docker &> /dev/null; then
            echo -e "${RED}错误: 未找到Docker，请先安装Docker${NC}"
            exit 1
        fi
        
        # 构建并启动
        echo -e "${YELLOW}正在构建和启动服务...${NC}"
        docker-compose down
        docker-compose up --build -d
        
        echo ""
        echo -e "${GREEN}所有服务已启动！${NC}"
        echo "- Consul: http://localhost:8500"
        echo "- Gateway: http://localhost:8080"
        echo "- User Service: http://localhost:8081"
        echo ""
        echo "等待服务完全启动（约30秒）..."
        sleep 30
        
        # 测试服务
        echo -e "${BLUE}测试服务状态...${NC}"
        curl -s http://localhost:8080/ > /dev/null && echo -e "${GREEN}✓ Gateway可访问${NC}" || echo -e "${RED}✗ Gateway无法访问${NC}"
        curl -s http://localhost:8081/actuator/health > /dev/null && echo -e "${GREEN}✓ User Service可访问${NC}" || echo -e "${RED}✗ User Service无法访问${NC}"
        ;;
        
    2)
        echo -e "${BLUE}使用Maven本地启动...${NC}"
        echo ""
        
        # 启动Consul
        echo -e "${YELLOW}1. 启动Consul...${NC}"
        if docker ps | grep -q consul; then
            echo "Consul已经在运行"
        else
            docker run -d --name consul -p 8500:8500 consul:1.15.3
            echo "Consul已启动: http://localhost:8500"
        fi
        
        echo ""
        echo -e "${YELLOW}2. 编译项目...${NC}"
        mvn clean compile -DskipTests
        
        echo ""
        echo -e "${YELLOW}3. 启动用户服务...${NC}"
        echo "命令: CONSUL_HOST=127.0.0.1 mvn -pl pudding-user spring-boot:run"
        echo ""
        echo -e "${YELLOW}4. 启动网关服务...${NC}"
        echo "命令: CONSUL_HOST=127.0.0.1 mvn -pl pudding-gateway spring-boot:run"
        echo ""
        echo -e "${BLUE}请在两个不同的终端窗口中运行上述命令${NC}"
        ;;
        
    3)
        echo -e "${BLUE}仅启动Consul...${NC}"
        
        if docker ps | grep -q consul; then
            echo "Consul已经在运行"
        else
            docker run -d --name consul -p 8500:8500 consul:1.15.3
            echo -e "${GREEN}Consul已启动: http://localhost:8500${NC}"
        fi
        ;;
        
    *)
        echo -e "${RED}无效选择${NC}"
        exit 1
        ;;
esac

echo ""
echo "=================================="
echo -e "${GREEN}启动完成！${NC}"
echo ""
echo "🔗 服务地址:"
echo "  Consul UI:    http://localhost:8500"
echo "  Gateway:      http://localhost:8080"  
echo "  User Service: http://localhost:8081"
echo ""
echo "📖 测试命令:"
echo "  ./test-gateway.sh  # 运行完整测试"
echo ""
echo "📚 API文档:"
echo "  登录: POST /api/v1/auth/login"
echo "  用户: GET /api/v1/users/{id}"
echo "  网关状态: GET /gateway/status"
echo "  生成Token: POST /gateway/token/generate"
echo "=================================="
