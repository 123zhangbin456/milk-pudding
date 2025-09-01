#!/bin/bash

# Milk Pudding 前端登录页面启动脚本

echo "🍮 Milk Pudding - 启动前端登录页面"
echo "======================================"

# 检查当前目录
if [ ! -f "login.html" ]; then
    echo "❌ 错误: 请在包含 login.html 的目录中运行此脚本"
    exit 1
fi

# 检查是否安装了 Python
if command -v python3 &> /dev/null; then
    echo "✅ 使用 Python3 启动 HTTP 服务器"
    echo "📂 服务根目录: $(pwd)"
    echo "🌐 访问地址: http://localhost:8000/login.html"
    echo "🔗 或直接访问: http://localhost:8000"
    echo ""
    echo "按 Ctrl+C 停止服务器"
    echo "======================================"
    python3 -m http.server 8000
elif command -v python &> /dev/null; then
    echo "✅ 使用 Python2 启动 HTTP 服务器"
    echo "📂 服务根目录: $(pwd)"
    echo "🌐 访问地址: http://localhost:8000/login.html"
    echo "🔗 或直接访问: http://localhost:8000"
    echo ""
    echo "按 Ctrl+C 停止服务器"
    echo "======================================"
    python -m SimpleHTTPServer 8000
elif command -v npx &> /dev/null; then
    echo "✅ 使用 Node.js http-server 启动服务器"
    echo "📂 服务根目录: $(pwd)"
    echo "🌐 访问地址: http://localhost:8000/login.html"
    echo "🔗 或直接访问: http://localhost:8000"
    echo ""
    echo "按 Ctrl+C 停止服务器"
    echo "======================================"
    npx http-server -p 8000
else
    echo "❌ 错误: 未找到 Python 或 Node.js"
    echo "请安装以下任一工具："
    echo "  - Python 3: brew install python3"
    echo "  - Python 2: brew install python"
    echo "  - Node.js: brew install node"
    echo ""
    echo "或者直接用浏览器打开 login.html 文件"
    echo "文件路径: $(pwd)/login.html"
fi
