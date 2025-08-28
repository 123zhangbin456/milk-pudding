# Gateway快速测试命令集合
# 复制粘贴即可使用

export GATEWAY_URL="http://localhost:8080"

echo "=== Gateway快速测试命令 ==="

# 1. 基础连通性测试
echo "1. 测试根路径："
curl -s "$GATEWAY_URL/" && echo

echo -e "\n2. 健康检查："
curl -s "$GATEWAY_URL/actuator/health" | jq .

echo -e "\n3. 网关状态："
curl -s "$GATEWAY_URL/gateway/status" | jq .

echo -e "\n4. 路由信息："
curl -s "$GATEWAY_URL/gateway/routes" | jq .

echo -e "\n5. 生成测试Token："
curl -s -X POST "$GATEWAY_URL/gateway/token/generate" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "test-001",
    "username": "testuser",
    "email": "test@example.com",
    "role": "admin"
  }' | jq .

echo -e "\n6. 验证Token（需要先生成Token）："
echo "# 先生成token并保存"
TOKEN=$(curl -s -X POST "$GATEWAY_URL/gateway/token/generate" \
  -H "Content-Type: application/json" \
  -d '{"userId":"test","username":"test","email":"test@example.com"}' \
  | jq -r '.data.token')

echo "Generated Token: $TOKEN"

echo -e "\n# 验证token"
curl -s -X POST "$GATEWAY_URL/gateway/token/validate" \
  -H "Content-Type: application/json" \
  -d "{\"token\": \"$TOKEN\"}" | jq .

echo -e "\n7. 测试无效路径（404）："
curl -s "$GATEWAY_URL/nonexistent" -w "\nHTTP Code: %{http_code}\n"

echo -e "\n8. 测试带认证头的请求："
curl -s "$GATEWAY_URL/gateway/status" \
  -H "Authorization: Bearer $TOKEN" | jq .

echo -e "\n9. 性能测试（10次请求的平均响应时间）："
for i in {1..10}; do
  curl -s "$GATEWAY_URL/gateway/status" -w "%{time_total}\n" -o /dev/null
done | awk '{sum+=$1} END {print "Average response time: " sum/NR "s"}'

echo -e "\n=== 测试完成 ==="
