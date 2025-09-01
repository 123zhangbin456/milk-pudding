// 配置信息
const CONFIG = {
    // 网关地址，根据你的实际部署情况修改
    GATEWAY_URL: 'http://localhost:8080',
    // API 路径
    LOGIN_API: '/api/v1/auth/login',
    REGISTER_API: '/api/v1/auth/register',
    USER_API: '/api/v1/users/profile',
    TEST_API: '/api/v1/users/me'
};

// 全局变量
let currentToken = null;
let currentUser = null;

// DOM 元素
const loginForm = document.getElementById('loginForm');
const loginBtn = document.getElementById('loginBtn');
const messageDiv = document.getElementById('message');
const userModal = document.getElementById('userModal');
const closeModal = document.querySelector('.close');

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    // 检查是否已经登录
    checkExistingLogin();
    
    // 绑定表单提交事件
    loginForm.addEventListener('submit', handleLogin);
    
    // 绑定模态框关闭事件
    closeModal.addEventListener('click', closeUserModal);
    window.addEventListener('click', function(event) {
        if (event.target === userModal) {
            closeUserModal();
        }
    });
    
    // 绑定回车键登录
    document.addEventListener('keypress', function(event) {
        if (event.key === 'Enter' && !userModal.style.display) {
            handleLogin(event);
        }
    });
});

// 检查本地存储中是否有登录信息
function checkExistingLogin() {
    const savedToken = localStorage.getItem('milk_pudding_token');
    const savedUser = localStorage.getItem('milk_pudding_user');
    
    if (savedToken && savedUser) {
        try {
            currentToken = savedToken;
            currentUser = JSON.parse(savedUser);
            showMessage('检测到已保存的登录信息', 'info');
        } catch (error) {
            console.error('解析用户信息失败:', error);
            clearStorage();
        }
    }
}

// 处理登录
async function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const rememberMe = document.getElementById('rememberMe').checked;
    
    if (!username || !password) {
        showMessage('请填写用户名和密码', 'error');
        return;
    }
    
    // 显示加载状态
    setLoadingState(true);
    
    try {
        const response = await fetch(CONFIG.GATEWAY_URL + CONFIG.LOGIN_API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });
        
        const data = await response.json();
        
        if (response.ok && data.success) {
            // 登录成功
            const tokenData = data.data;
            currentToken = tokenData.token;
            currentUser = {
                userId: tokenData.userId || username,
                username: username,
                loginTime: new Date().toISOString()
            };
            
            // 保存登录信息（如果选择了记住我）
            if (rememberMe) {
                localStorage.setItem('milk_pudding_token', currentToken);
                localStorage.setItem('milk_pudding_user', JSON.stringify(currentUser));
            } else {
                // 使用 sessionStorage 仅在当前会话中保存
                sessionStorage.setItem('milk_pudding_token', currentToken);
                sessionStorage.setItem('milk_pudding_user', JSON.stringify(currentUser));
            }
            
            showMessage('登录成功！', 'success');
            
            // 延迟显示用户信息弹窗
            setTimeout(() => {
                showUserModal();
            }, 1000);
            
        } else {
            // 登录失败
            const errorMsg = data.message || '登录失败，请检查用户名和密码';
            showMessage(errorMsg, 'error');
        }
        
    } catch (error) {
        console.error('登录请求失败:', error);
        
        // 网络错误或服务器错误的处理
        if (error.name === 'TypeError' && error.message.includes('fetch')) {
            showMessage('无法连接到服务器，请检查网络连接', 'error');
        } else {
            showMessage('登录过程中发生错误，请稍后重试', 'error');
        }
        
        // 开发环境下可以使用模拟数据测试界面
        if (isDevelopmentMode()) {
            console.log('开发模式：使用模拟登录');
            simulateLogin(username);
        }
    } finally {
        setLoadingState(false);
    }
}

// 模拟登录（用于开发测试）
function simulateLogin(username) {
    currentToken = generateMockToken();
    currentUser = {
        userId: 'user_' + Date.now(),
        username: username,
        loginTime: new Date().toISOString(),
        mock: true
    };
    
    showMessage('登录成功！（模拟模式）', 'success');
    
    setTimeout(() => {
        showUserModal();
    }, 1000);
}

// 生成模拟 Token
function generateMockToken() {
    const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
    const payload = btoa(JSON.stringify({
        sub: 'user_' + Date.now(),
        username: document.getElementById('username').value,
        iat: Math.floor(Date.now() / 1000),
        exp: Math.floor(Date.now() / 1000) + 3600,
        mock: true
    }));
    const signature = 'mock_signature_' + Math.random().toString(36);
    
    return `${header}.${payload}.${signature}`;
}

// 判断是否为开发模式
function isDevelopmentMode() {
    return window.location.hostname === 'localhost' || 
           window.location.hostname === '127.0.0.1' ||
           window.location.protocol === 'file:';
}

// 显示用户信息模态框
function showUserModal() {
    document.getElementById('userId').textContent = currentUser.userId;
    document.getElementById('displayUsername').textContent = currentUser.username;
    document.getElementById('tokenDisplay').value = currentToken;
    
    userModal.style.display = 'block';
}

// 关闭用户信息模态框
function closeUserModal() {
    userModal.style.display = 'none';
}

// 测试 API 调用
async function testApi() {
    if (!currentToken) {
        showMessage('请先登录', 'error');
        return;
    }
    
    try {
        showMessage('正在测试 API 调用...', 'info');
        
        const response = await fetch(CONFIG.GATEWAY_URL + CONFIG.TEST_API, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${currentToken}`,
                'Content-Type': 'application/json'
            }
        });
        
        const data = await response.json();
        
        if (response.ok) {
            showMessage('API 调用成功！检查控制台查看详细信息', 'success');
            console.log('API 响应数据:', data);
        } else {
            showMessage(`API 调用失败: ${data.message || response.statusText}`, 'error');
            console.error('API 错误:', data);
        }
        
    } catch (error) {
        console.error('API 调用错误:', error);
        showMessage('API 调用时发生错误', 'error');
        
        // 开发模式下的模拟响应
        if (isDevelopmentMode() && currentUser.mock) {
            console.log('开发模式：模拟 API 响应');
            showMessage('API 调用成功！（模拟响应）', 'success');
            console.log('模拟 API 响应:', {
                success: true,
                data: {
                    userId: currentUser.userId,
                    username: currentUser.username,
                    email: currentUser.username + '@example.com',
                    role: 'user',
                    loginTime: currentUser.loginTime
                }
            });
        }
    }
}

// 退出登录
function logout() {
    currentToken = null;
    currentUser = null;
    
    // 清除存储
    clearStorage();
    
    // 重置表单
    loginForm.reset();
    
    // 关闭模态框
    closeUserModal();
    
    showMessage('已退出登录', 'info');
}

// 清除本地存储
function clearStorage() {
    localStorage.removeItem('milk_pudding_token');
    localStorage.removeItem('milk_pudding_user');
    sessionStorage.removeItem('milk_pudding_token');
    sessionStorage.removeItem('milk_pudding_user');
}

// 设置加载状态
function setLoadingState(loading) {
    const btnText = document.querySelector('.btn-text');
    const btnLoading = document.querySelector('.btn-loading');
    
    if (loading) {
        loginBtn.disabled = true;
        btnText.style.display = 'none';
        btnLoading.style.display = 'inline';
    } else {
        loginBtn.disabled = false;
        btnText.style.display = 'inline';
        btnLoading.style.display = 'none';
    }
}

// 显示消息
function showMessage(text, type = 'info') {
    messageDiv.textContent = text;
    messageDiv.className = `message ${type}`;
    messageDiv.style.display = 'block';
    
    // 3秒后自动隐藏（除非是错误消息）
    if (type !== 'error') {
        setTimeout(() => {
            messageDiv.style.display = 'none';
        }, 3000);
    }
}

// 工具函数：复制 Token 到剪贴板
function copyToken() {
    const tokenDisplay = document.getElementById('tokenDisplay');
    tokenDisplay.select();
    tokenDisplay.setSelectionRange(0, 99999); // 兼容移动设备
    
    try {
        document.execCommand('copy');
        showMessage('Token 已复制到剪贴板', 'success');
    } catch (error) {
        console.error('复制失败:', error);
        showMessage('复制失败，请手动选择复制', 'error');
    }
}

// 添加复制功能到页面
document.addEventListener('DOMContentLoaded', function() {
    const tokenDisplay = document.getElementById('tokenDisplay');
    if (tokenDisplay) {
        tokenDisplay.addEventListener('click', copyToken);
        tokenDisplay.style.cursor = 'pointer';
        tokenDisplay.title = '点击复制 Token';
    }
});

// 网络状态检查
function checkNetworkStatus() {
    if (!navigator.onLine) {
        showMessage('网络连接已断开', 'error');
        return false;
    }
    return true;
}

// 监听网络状态变化
window.addEventListener('online', () => {
    showMessage('网络连接已恢复', 'success');
});

window.addEventListener('offline', () => {
    showMessage('网络连接已断开', 'error');
});
