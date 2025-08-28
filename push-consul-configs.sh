#!/bin/bash

# Consul配置推送脚本
# 使用方法: ./push-consul-configs.sh [consul-host] [consul-port]

CONSUL_HOST=${1:-localhost}
CONSUL_PORT=${2:-8500}
CONSUL_URL="http://${CONSUL_HOST}:${CONSUL_PORT}"

echo "正在推送配置到Consul: ${CONSUL_URL}"

# 检查Consul是否可访问
if ! curl -s "${CONSUL_URL}/v1/status/leader" > /dev/null; then
    echo "错误: 无法连接到Consul服务器 ${CONSUL_URL}"
    exit 1
fi

# 推送pudding-gateway配置
echo "推送 pudding-gateway 配置..."
curl -X PUT \
    --data-binary @consul-configs/pudding-gateway.yml \
    "${CONSUL_URL}/v1/kv/config/pudding-gateway/data"

if [ $? -eq 0 ]; then
    echo "✅ pudding-gateway 配置推送成功"
else
    echo "❌ pudding-gateway 配置推送失败"
fi

# 推送pudding-user配置
echo "推送 pudding-user 配置..."
curl -X PUT \
    --data-binary @consul-configs/pudding-user.yml \
    "${CONSUL_URL}/v1/kv/config/pudding-user/data"

if [ $? -eq 0 ]; then
    echo "✅ pudding-user 配置推送成功"
else
    echo "❌ pudding-user 配置推送失败"
fi

# 推送通用配置（可选）
echo "推送通用应用配置..."
cat > consul-configs/application.yml << EOF
# 通用配置
management:
  server:
    port: -1  # 使用随机端口或禁用管理端口
  security:
    enabled: false

# 全局日志配置
logging:
  pattern:
    dateformat: "yyyy-MM-dd HH:mm:ss"
EOF

curl -X PUT \
    --data-binary @consul-configs/application.yml \
    "${CONSUL_URL}/v1/kv/config/application/data"

if [ $? -eq 0 ]; then
    echo "✅ 通用配置推送成功"
else
    echo "❌ 通用配置推送失败"
fi

echo ""
echo "配置推送完成！"
echo "你可以通过以下URL查看Consul配置:"
echo "- Consul UI: ${CONSUL_URL}/ui"
echo "- 配置列表: ${CONSUL_URL}/v1/kv/config?recurse"
