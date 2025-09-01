#!/bin/bash

# Milk Pudding 项目启动脚本 - 支持 JDK 8-22

echo "🍮 Milk Pudding - 项目启动脚本"
echo "======================================"

# 检查并设置JAVA_HOME
check_and_set_java() {
    echo "🔍 检查可用的Java版本..."
    
    # 优先级: Java 17 > Java 21 > Java 22 > Java 11 > Java 8
    PREFERRED_VERSIONS=("17" "21" "22" "11" "8")
    
    for version in "${PREFERRED_VERSIONS[@]}"; do
        JAVA_PATH=$(/usr/libexec/java_home -v "$version" 2>/dev/null)
        if [ $? -eq 0 ]; then
            export JAVA_HOME="$JAVA_PATH"
            echo "✅ 使用 Java $version: $JAVA_HOME"
            java -version
            return 0
        fi
    done
    
    echo "❌ 未找到合适的Java版本 (需要 Java 8+)"
    echo "请安装以下任一版本："
    echo "  - OpenJDK 17 (推荐): brew install openjdk@17"
    echo "  - OpenJDK 11: brew install openjdk@11"
    echo "  - Oracle JDK: 从官网下载"
    exit 1
}

# 构建项目
build_project() {
    echo ""
    echo "🔨 构建项目..."
    mvn clean compile -DskipTests
    if [ $? -ne 0 ]; then
        echo "❌ 项目构建失败"
        exit 1
    fi
    echo "✅ 项目构建成功"
}

# 启动 Consul (如果需要)
start_consul() {
    if ! pgrep -f "consul" > /dev/null; then
        echo ""
        echo "🚀 启动 Consul..."
        if command -v consul &> /dev/null; then
            consul agent -dev -ui &
            echo "✅ Consul 已启动 (http://localhost:8500)"
        else
            echo "⚠️  Consul 未安装，某些服务发现功能可能不可用"
            echo "安装: brew install consul"
        fi
    else
        echo "✅ Consul 已在运行"
    fi
}

# 启动用户服务
start_user_service() {
    echo ""
    echo "🚀 启动用户服务..."
    cd pudding-user
    mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081" &
    USER_SERVICE_PID=$!
    echo "✅ 用户服务启动中 (PID: $USER_SERVICE_PID, Port: 8081)"
    cd ..
}

# 启动网关服务
start_gateway_service() {
    echo ""
    echo "🚀 启动网关服务..."
    cd pudding-gateway
    mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8080" &
    GATEWAY_SERVICE_PID=$!
    echo "✅ 网关服务启动中 (PID: $GATEWAY_SERVICE_PID, Port: 8080)"
    cd ..
}

# 显示服务信息
show_services() {
    echo ""
    echo "🎉 服务启动完成！"
    echo "======================================"
    echo "📋 服务信息:"
    echo "  🌐 网关服务:   http://localhost:8080"
    echo "  👤 用户服务:   http://localhost:8081"
    echo "  🏠 前端页面:   http://localhost:8080/login.html"
    echo "  📊 Consul UI:  http://localhost:8500 (如果已安装)"
    echo ""
    echo "🔧 管理命令:"
    echo "  查看日志:     tail -f pudding-*/target/logs/*.log"
    echo "  停止服务:     pkill -f 'spring-boot:run' 或 Ctrl+C"
    echo "  重启服务:     ./start-services.sh"
    echo ""
    echo "📖 API文档:"
    echo "  登录接口:     POST http://localhost:8080/api/v1/auth/login"
    echo "  用户信息:     GET  http://localhost:8080/api/v1/users/me"
    echo "  网关状态:     GET  http://localhost:8080/gateway/status"
    echo "======================================"
}

# 等待服务启动
wait_for_services() {
    echo ""
    echo "⏳ 等待服务启动..."
    
    # 等待用户服务
    echo "🔄 检查用户服务状态..."
    for i in {1..30}; do
        if curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
            echo "✅ 用户服务已就绪"
            break
        fi
        if [ $i -eq 30 ]; then
            echo "⚠️  用户服务启动超时，但将继续启动网关"
        fi
        sleep 2
    done
    
    # 等待网关服务
    echo "🔄 检查网关服务状态..."
    for i in {1..30}; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            echo "✅ 网关服务已就绪"
            break
        fi
        if [ $i -eq 30 ]; then
            echo "⚠️  网关服务启动超时，请检查日志"
        fi
        sleep 2
    done
}

# 清理函数
cleanup() {
    echo ""
    echo "🛑 正在停止服务..."
    pkill -f "spring-boot:run"
    pkill -f "consul"
    echo "✅ 服务已停止"
    exit 0
}

# 捕获中断信号
trap cleanup SIGINT SIGTERM

# 主流程
main() {
    check_and_set_java
    build_project
    start_consul
    start_user_service
    sleep 5
    start_gateway_service
    wait_for_services
    show_services
    
    # 保持脚本运行
    echo "按 Ctrl+C 停止所有服务"
    while true; do
        sleep 1
    done
}

# 检查是否在正确的目录中
if [ ! -f "pom.xml" ]; then
    echo "❌ 请在项目根目录中运行此脚本"
    echo "当前目录: $(pwd)"
    echo "应该在: /path/to/milk-pudding"
    exit 1
fi

# 运行主程序
main
