#!/bin/bash

# Gateway功能测试脚本
# 用于测试pudding-gateway的各项功能

# 配置
GATEWAY_HOST="localhost"
GATEWAY_PORT="8080"
BASE_URL="http://${GATEWAY_HOST}:${GATEWAY_PORT}"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# 检查HTTP响应状态
check_response() {
    local response="$1"
    local expected_status="$2"
    local test_name="$3"
    
    local status_code=$(echo "$response" | grep -o '"status":[0-9]*' | grep -o '[0-9]*' | head -1)
    local http_code=$(echo "$response" | tail -n1)
    
    if [[ "$http_code" -eq "$expected_status" ]]; then
        log_success "$test_name - HTTP $http_code"
        echo "$response" | head -n -1 | jq . 2>/dev/null || echo "$response" | head -n -1
        return 0
    else
        log_error "$test_name - Expected HTTP $expected_status, got $http_code"
        echo "$response" | head -n -1
        return 1
    fi
}

# 等待服务启动
wait_for_service() {
    log_info "等待网关服务启动..."
    local max_attempts=30
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -s "$BASE_URL/" > /dev/null 2>&1; then
            log_success "网关服务已启动"
            return 0
        fi
        attempt=$((attempt + 1))
        echo -n "."
        sleep 1
    done
    
    log_error "网关服务启动超时"
    return 1
}

# 测试1: 基础健康检查
test_health_check() {
    echo "=================================="
    log_info "测试1: 基础健康检查"
    echo "=================================="
    
    # 测试根路径
    log_info "测试根路径 /"
    local response=$(curl -s -w "\n%{http_code}" "$BASE_URL/")
    check_response "$response" "200" "根路径访问"
    
    echo ""
    
    # 测试Actuator健康检查
    log_info "测试健康检查 /actuator/health"
    local response=$(curl -s -w "\n%{http_code}" "$BASE_URL/actuator/health")
    check_response "$response" "200" "健康检查"
    
    echo ""
}

# 测试2: 网关状态和路由信息
test_gateway_management() {
    echo "=================================="
    log_info "测试2: 网关管理功能"
    echo "=================================="
    
    # 测试网关状态
    log_info "测试网关状态 /gateway/status"
    local response=$(curl -s -w "\n%{http_code}" "$BASE_URL/gateway/status")
    check_response "$response" "200" "网关状态查询"
    
    echo ""
    
    # 测试路由信息
    log_info "测试路由信息 /gateway/routes"
    local response=$(curl -s -w "\n%{http_code}" "$BASE_URL/gateway/routes")
    check_response "$response" "200" "路由信息查询"
    
    echo ""
}

# 测试3: JWT Token功能
test_jwt_functionality() {
    echo "=================================="
    log_info "测试3: JWT Token功能"
    echo "=================================="
    
    # 生成测试Token
    log_info "生成测试Token"
    local token_response=$(curl -s -w "\n%{http_code}" \
        -X POST "$BASE_URL/gateway/token/generate" \
        -H "Content-Type: application/json" \
        -d '{
            "userId": "test-user-001",
            "username": "testuser",
            "email": "test@example.com",
            "role": "admin"
        }')
    
    if check_response "$token_response" "200" "Token生成"; then
        # 提取token
        local token=$(echo "$token_response" | head -n -1 | jq -r '.data.token' 2>/dev/null)
        if [[ "$token" != "null" && "$token" != "" ]]; then
            log_success "Token提取成功: ${token:0:50}..."
            
            echo ""
            
            # 验证Token
            log_info "验证生成的Token"
            local validate_response=$(curl -s -w "\n%{http_code}" \
                -X POST "$BASE_URL/gateway/token/validate" \
                -H "Content-Type: application/json" \
                -d "{\"token\": \"$token\"}")
            
            check_response "$validate_response" "200" "Token验证"
            
            echo ""
            
            # 测试无效Token验证
            log_info "测试无效Token验证"
            local invalid_response=$(curl -s -w "\n%{http_code}" \
                -X POST "$BASE_URL/gateway/token/validate" \
                -H "Content-Type: application/json" \
                -d '{"token": "invalid.token.here"}')
            
            # 无效token可能返回400或401
            local http_code=$(echo "$invalid_response" | tail -n1)
            if [[ "$http_code" -eq "400" || "$http_code" -eq "401" ]]; then
                log_success "无效Token验证 - HTTP $http_code (符合预期)"
                echo "$invalid_response" | head -n -1 | jq . 2>/dev/null || echo "$invalid_response" | head -n -1
            else
                log_warning "无效Token验证 - HTTP $http_code (可能需要检查)"
                echo "$invalid_response" | head -n -1
            fi
            
        else
            log_error "无法提取Token"
        fi
    fi
    
    echo ""
}

# 测试4: 网关过滤器功能
test_gateway_filters() {
    echo "=================================="
    log_info "测试4: 网关过滤器功能"
    echo "=================================="
    
    # 测试日志过滤器（通过请求头观察）
    log_info "测试请求日志过滤器"
    local response=$(curl -s -w "\n%{http_code}" \
        -H "X-Test-Header: filter-test" \
        -H "User-Agent: Gateway-Test-Script" \
        "$BASE_URL/gateway/status")
    
    check_response "$response" "200" "带自定义头的请求"
    
    echo ""
    
    # 测试CORS（如果配置了）
    log_info "测试CORS预检请求"
    local cors_response=$(curl -s -w "\n%{http_code}" \
        -X OPTIONS \
        -H "Origin: http://localhost:3000" \
        -H "Access-Control-Request-Method: POST" \
        -H "Access-Control-Request-Headers: Content-Type,Authorization" \
        "$BASE_URL/gateway/status")
    
    local cors_code=$(echo "$cors_response" | tail -n1)
    if [[ "$cors_code" -eq "200" || "$cors_code" -eq "204" ]]; then
        log_success "CORS预检请求 - HTTP $cors_code"
        echo "$cors_response" | head -n -1
    else
        log_warning "CORS预检请求 - HTTP $cors_code (可能未配置CORS)"
        echo "$cors_response" | head -n -1
    fi
    
    echo ""
}

# 测试5: 错误处理
test_error_handling() {
    echo "=================================="
    log_info "测试5: 错误处理"
    echo "=================================="
    
    # 测试404错误
    log_info "测试404错误处理"
    local response=$(curl -s -w "\n%{http_code}" "$BASE_URL/nonexistent/path")
    local http_code=$(echo "$response" | tail -n1)
    if [[ "$http_code" -eq "404" ]]; then
        log_success "404错误处理 - HTTP $http_code"
        echo "$response" | head -n -1
    else
        log_warning "404错误处理 - HTTP $http_code (可能有自定义处理)"
        echo "$response" | head -n -1
    fi
    
    echo ""
    
    # 测试无效JSON请求
    log_info "测试无效JSON请求"
    local response=$(curl -s -w "\n%{http_code}" \
        -X POST "$BASE_URL/gateway/token/generate" \
        -H "Content-Type: application/json" \
        -d '{"invalid": json}')
    
    local http_code=$(echo "$response" | tail -n1)
    if [[ "$http_code" -eq "400" ]]; then
        log_success "无效JSON处理 - HTTP $http_code"
        echo "$response" | head -n -1
    else
        log_warning "无效JSON处理 - HTTP $http_code"
        echo "$response" | head -n -1
    fi
    
    echo ""
}

# 测试6: 性能测试
test_performance() {
    echo "=================================="
    log_info "测试6: 简单性能测试"
    echo "=================================="
    
    log_info "执行10次并发请求测试响应时间"
    
    # 使用curl进行简单的并发测试
    for i in {1..10}; do
        (
            local start_time=$(date +%s%3N)
            local response=$(curl -s -w "%{http_code}" "$BASE_URL/gateway/status" -o /dev/null)
            local end_time=$(date +%s%3N)
            local duration=$((end_time - start_time))
            
            if [[ "$response" -eq "200" ]]; then
                echo "请求 $i: ${duration}ms ✓"
            else
                echo "请求 $i: 失败 (HTTP $response) ✗"
            fi
        ) &
    done
    
    wait
    echo ""
}

# 主函数
main() {
    echo "========================================"
    echo "     Pudding Gateway 功能测试套件"
    echo "========================================"
    echo ""
    
    log_info "开始测试 Gateway: $BASE_URL"
    echo ""
    
    # 检查jq是否安装（用于JSON格式化）
    if ! command -v jq &> /dev/null; then
        log_warning "jq未安装，JSON输出可能不够美观"
    fi
    
    # 等待服务启动
    if ! wait_for_service; then
        log_error "无法连接到网关服务，请确保服务已启动"
        exit 1
    fi
    
    echo ""
    
    # 执行所有测试
    test_health_check
    test_gateway_management
    test_jwt_functionality
    test_gateway_filters
    test_error_handling
    test_performance
    
    echo "========================================"
    log_info "测试完成！"
    echo "========================================"
    
    # 生成简单的测试报告
    echo ""
    log_info "测试建议："
    echo "1. 检查gateway服务日志以确认过滤器正常工作"
    echo "2. 验证Consul服务发现是否正常注册"
    echo "3. 如果有下游服务，测试路由转发功能"
    echo "4. 在生产环境前进行更全面的负载测试"
}

# 如果有参数，执行特定测试
if [[ $# -gt 0 ]]; then
    case "$1" in
        "health")
            wait_for_service && test_health_check
            ;;
        "management")
            wait_for_service && test_gateway_management
            ;;
        "jwt")
            wait_for_service && test_jwt_functionality
            ;;
        "filters")
            wait_for_service && test_gateway_filters
            ;;
        "errors")
            wait_for_service && test_error_handling
            ;;
        "performance")
            wait_for_service && test_performance
            ;;
        *)
            echo "使用方法: $0 [health|management|jwt|filters|errors|performance]"
            echo "不带参数将执行所有测试"
            ;;
    esac
else
    main
fi
