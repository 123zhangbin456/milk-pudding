# Milk Pudding - 前端登录页面

这是一个为 Milk Pudding 项目创建的简单前端登录页面，包含完整的用户界面和交互功能。

## 功能特性

### 🔐 登录功能
- 用户名和密码登录
- 记住登录状态
- JWT Token 管理
- 登录状态检查

### 🎨 界面设计
- 现代化响应式设计
- 渐变背景和动画效果
- 移动端友好
- 直观的用户体验

### 🔧 技术特性
- 原生 JavaScript（无框架依赖）
- CSS3 动画和过渡效果
- 本地存储支持
- 网络状态检测
- 开发模式支持（离线测试）

## 文件结构

```
frontend/
├── login.html          # 登录页面
├── register.html       # 注册页面（UI 示例）
├── styles.css          # 样式文件
├── script.js          # JavaScript 逻辑
└── README.md          # 说明文档
```

## 快速开始

### 1. 直接访问
用浏览器打开 `login.html` 文件即可使用。

### 2. 本地服务器（推荐）
```bash
# 使用 Python 启动简单服务器
cd frontend
python -m http.server 8000

# 或使用 Node.js
npx http-server -p 8000
```

然后访问 `http://localhost:8000/login.html`

## 配置说明

在 `script.js` 文件中修改 `CONFIG` 对象来适应你的后端服务：

```javascript
const CONFIG = {
    // 网关地址
    GATEWAY_URL: 'http://localhost:8080',
    // API 路径
    LOGIN_API: '/api/v1/auth/login',
    REGISTER_API: '/api/v1/auth/register',
    USER_API: '/api/v1/users/profile',
    TEST_API: '/api/v1/users/me'
};
```

## API 接口说明

### 登录接口
```
POST /api/v1/auth/login
Content-Type: application/json

请求体:
{
    "username": "用户名",
    "password": "密码"
}

响应:
{
    "success": true,
    "data": {
        "token": "JWT_TOKEN",
        "userId": "用户ID",
        "expiresIn": 86400
    }
}
```

### 用户信息接口
```
GET /api/v1/users/me
Authorization: Bearer JWT_TOKEN

响应:
{
    "success": true,
    "data": {
        "userId": "用户ID",
        "username": "用户名",
        "email": "邮箱",
        "role": "角色"
    }
}
```

## 使用说明

### 登录流程
1. 打开登录页面
2. 输入用户名和密码
3. 选择是否记住登录状态
4. 点击登录按钮
5. 登录成功后显示用户信息弹窗

### 功能按钮
- **测试 API 调用**: 使用当前 Token 调用后端 API 测试连通性
- **退出登录**: 清除本地存储的登录信息
- **Token 复制**: 点击 Token 文本框可复制到剪贴板

### 开发模式
当在本地环境（localhost、127.0.0.1 或 file:// 协议）下运行时，如果后端服务不可用，页面会自动切换到开发模式：
- 使用模拟登录数据
- 生成虚拟 JWT Token
- 提供模拟 API 响应

## 浏览器支持

- Chrome 60+
- Firefox 60+
- Safari 12+
- Edge 79+

## 安全注意事项

1. **生产环境配置**
   - 更改默认的网关地址
   - 启用 HTTPS
   - 配置适当的 CORS 策略

2. **Token 安全**
   - Token 存储在 localStorage 或 sessionStorage
   - 建议设置合理的过期时间
   - 避免在控制台输出敏感信息

3. **网络安全**
   - 所有 API 调用都应通过 HTTPS
   - 验证服务器证书
   - 实施适当的 CSP 策略

## 自定义样式

你可以通过修改 `styles.css` 文件来自定义页面样式：

```css
/* 修改主题色 */
:root {
    --primary-color: #667eea;
    --secondary-color: #764ba2;
}

/* 修改背景渐变 */
body {
    background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
}
```

## 故障排除

### 1. 无法连接到后端服务
- 检查 `CONFIG.GATEWAY_URL` 配置
- 确认后端服务正在运行
- 检查网络连接和防火墙设置

### 2. CORS 错误
- 确保后端服务配置了正确的 CORS 策略
- 检查请求头和响应头

### 3. Token 验证失败
- 检查 Token 格式是否正确
- 确认 Token 未过期
- 验证后端 JWT 验证逻辑

## 开发计划

- [ ] 添加多语言支持
- [ ] 集成社交登录（GitHub、Google）
- [ ] 添加双因素认证支持
- [ ] 实现密码强度检查器
- [ ] 添加登录历史记录

## 贡献

欢迎提交 Issue 和 Pull Request 来改进这个登录页面！
