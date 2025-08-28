#!/bin/bash

# API测试用例集合
# 包含各种场景的具体测试用例

GATEWAY_URL="http://localhost:8080"

# 测试用例数据
declare -A test_cases

# JWT Token测试用例
test_cases["jwt_generate_admin"]='POST /gateway/token/generate {
    "userId": "admin-001", 
    "username": "admin", 
    "email": "admin@milkpudding.com", 
    "role": "admin"
}'

test_cases["jwt_generate_user"]='POST /gateway/token/generate {
    "userId": "user-001", 
    "username": "testuser", 
    "email": "user@milkpudding.com", 
    "role": "user"
}'

test_cases["jwt_generate_missing_fields"]='POST /gateway/token/generate {
    "username": "incomplete"
}'

# Gateway管理测试用例
test_cases["gateway_status"]='GET /gateway/status'
test_cases["gateway_routes"]='GET /gateway/routes'

# 健康检查测试用例
test_cases["health_check"]='GET /actuator/health'
test_cases["actuator_info"]='GET /actuator/info'

# 执行单个测试用例
run_test_case() {
    local name="$1"
    local test_data="${test_cases[$name]}"
    
    if [[ -z "$test_data" ]]; then
        echo "❌ 测试用例 '$name' 不存在"
        return 1
    fi
    
    echo "🧪 执行测试: $name"
    echo "   $test_data"
    
    # 解析测试数据
    local method=$(echo "$test_data" | cut -d' ' -f1)
    local path=$(echo "$test_data" | cut -d' ' -f2)
    local json_part=$(echo "$test_data" | cut -d' ' -f3-)
    
    local url="$GATEWAY_URL$path"
    local response
    local http_code
    
    if [[ "$method" == "GET" ]]; then
        response=$(curl -s -w "\n%{http_code}" "$url")
    elif [[ "$method" == "POST" ]]; then
        response=$(curl -s -w "\n%{http_code}" \
            -X POST "$url" \
            -H "Content-Type: application/json" \
            -d "$json_part")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | head -n -1)
    
    echo "   📊 HTTP状态码: $http_code"
    echo "   📝 响应内容:"
    echo "$response_body" | jq . 2>/dev/null || echo "$response_body" | sed 's/^/      /'
    echo ""
    
    # 根据测试用例名称判断期望结果
    case "$name" in
        *"missing_fields")
            if [[ "$http_code" -ge "400" ]]; then
                echo "   ✅ 测试通过: 正确处理了缺失字段"
            else
                echo "   ❌ 测试失败: 应该返回错误状态码"
            fi
            ;;
        *)
            if [[ "$http_code" -eq "200" ]]; then
                echo "   ✅ 测试通过"
            else
                echo "   ❌ 测试失败: 期望200，实际$http_code"
            fi
            ;;
    esac
    
    echo "----------------------------------------"
}

# 执行所有测试用例
run_all_tests() {
    echo "🚀 开始执行所有API测试用例"
    echo "========================================"
    
    for test_name in "${!test_cases[@]}"; do
        run_test_case "$test_name"
        sleep 1  # 避免请求过于频繁
    done
    
    echo "🎉 所有测试用例执行完成"
}

# 主函数
if [[ $# -eq 0 ]]; then
    run_all_tests
else
    run_test_case "$1"
fi
