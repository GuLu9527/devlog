<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { loginApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const result = await loginApi(loginForm)
    if (result.code === 200) {
      // 保存token到localStorage
      localStorage.setItem('token', result.data.token)
      
      // 设置用户信息到store（包含权限数据）
      authStore.setUser(result.data)
      
      ElMessage.success('登录成功')
      router.push('/index')
    } else {
      ElMessage.error(result.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
}
</script>

<template>
  <div class="login-container">
    <div class="login-content">
      <!-- 左侧品牌区域 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="brand-icon">
            <div class="icon-wrapper">
              <span class="brand-text">DevLog</span>
            </div>
          </div>
          <h1 class="brand-title">DevLog</h1>
          <p class="brand-subtitle">项目开发日志管理系统</p>
          <div class="feature-list">
            <div class="feature-item">
              <div class="feature-icon">✨</div>
              <span>智能项目管理</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon">📊</div>
              <span>数据可视化分析</span>
            </div>
            <div class="feature-item">
              <div class="feature-icon">🔒</div>
              <span>安全权限控制</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录区域 -->
      <div class="login-section">
        <div class="login-box">
          <div class="login-header">
            <h2 class="login-title">欢迎回来</h2>
            <p class="login-desc">请登录您的账户以继续使用</p>
          </div>
          
          <el-form
            ref="formRef"
            :model="loginForm"
            :rules="rules"
            class="login-form"
            @keyup.enter="handleLogin"
          >
            <el-form-item prop="username">
              <div class="input-wrapper">
                <label class="input-label">用户名</label>
                <el-input
                  v-model="loginForm.username"
                  placeholder="请输入您的用户名"
                  :prefix-icon="User"
                  clearable
                  class="custom-input"
                  autocomplete="username"
                />
              </div>
            </el-form-item>
            
            <el-form-item prop="password">
              <div class="input-wrapper">
                <label class="input-label">密码</label>
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入您的密码"
                  :prefix-icon="Lock"
                  show-password
                  clearable
                  class="custom-input"
                  autocomplete="current-password"
                />
              </div>
            </el-form-item>
            
            <div class="form-options">
              <el-checkbox class="remember-me">记住我</el-checkbox>
              <el-link type="primary" class="forgot-link">忘记密码？</el-link>
            </div>
            
            <div class="form-actions">
              <el-button
                type="primary"
                class="login-button"
                :loading="loading"
                @click="handleLogin"
              >
                <span v-if="!loading">登录</span>
                <span v-else>登录中...</span>
              </el-button>
              <el-button
                class="reset-button"
                @click="handleReset"
              >
                重置
              </el-button>
            </div>
            
            <div class="register-link">
              <span>还没有账号？</span>
              <el-link type="primary" @click="$router.push('/register')" class="register-btn">立即注册</el-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
// 导入按钮样式规范
@import '@/assets/button-style-guide.scss';

.login-container {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 30%, #f5f5f7 70%, #e5e5ea 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(circle at 25% 25%, rgba(0, 0, 0, 0.03) 0%, transparent 50%),
                radial-gradient(circle at 75% 75%, rgba(0, 0, 0, 0.02) 0%, transparent 50%),
                linear-gradient(45deg, transparent 30%, rgba(0, 0, 0, 0.01) 50%, transparent 70%);
    pointer-events: none;
  }
}

.login-content {
  width: 100%;
  max-width: 900px;
  height: auto;
  max-height: 90vh;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08), 0 1px 2px rgba(0, 0, 0, 0.1);
  display: flex;
  overflow: hidden;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-sizing: border-box;
}

// 左侧品牌区域
.brand-section {
  flex: 1;
  background: linear-gradient(135deg, #1d1d1f 0%, #2c2c2e 25%, #48484a 75%, #636366 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 32px;
  position: relative;
  min-height: 0;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
      radial-gradient(circle at 20% 30%, rgba(255, 255, 255, 0.08) 0%, transparent 40%),
      radial-gradient(circle at 80% 70%, rgba(255, 255, 255, 0.05) 0%, transparent 40%),
      linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.02) 50%, transparent 70%);
    animation: brandFloat 6s ease-in-out infinite;
  }
  
  &::after {
    content: '';
    position: absolute;
    top: 8%;
    left: 8%;
    right: 8%;
    bottom: 8%;
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 16px;
    animation: borderGlow 8s ease-in-out infinite;
  }
}

.brand-content {
  text-align: center;
  color: white;
  position: relative;
  z-index: 1;
}

.brand-icon {
  margin-bottom: 32px;
  
  .icon-wrapper {
    width: 88px;
    height: 88px;
    background: rgba(255, 255, 255, 0.15);
    border-radius: 22px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto;
    backdrop-filter: blur(20px);
    border: 2px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    animation: iconFloat 3s ease-in-out infinite;
    
    &:hover {
      transform: scale(1.05);
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
    }
    
    .brand-text {
      font-size: 26px;
      font-weight: 800;
      background: linear-gradient(135deg, #ffffff 0%, #f0f8ff 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
  }
}

.brand-title {
  font-size: 40px;
  font-weight: 700;
  margin: 0 0 12px 0;
  letter-spacing: -1px;
}

.brand-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 32px 0;
  font-weight: 400;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
  padding: 8px;
  border-radius: 12px;
  
  &:hover {
    background: rgba(255, 255, 255, 0.1);
    transform: translateX(8px);
  }
  
  .feature-icon {
    width: 52px;
    height: 52px;
    background: rgba(255, 255, 255, 0.15);
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
    backdrop-filter: blur(15px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
  }
  
  span {
    font-size: 16px;
    font-weight: 500;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  }
}

// 右侧登录区域
.login-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 32px;
  background: linear-gradient(135deg, #ffffff 0%, #f9f9f9 100%);
  min-height: 0;
}

.login-box {
  width: 100%;
  max-width: 360px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
  
  .login-title {
    font-size: 28px;
    font-weight: 700;
    color: #1d1d1f;
    margin: 0 0 8px 0;
    letter-spacing: -0.5px;
  }
  
  .login-desc {
    font-size: 15px;
    color: #6e6e73;
    margin: 0;
    font-weight: 400;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 24px;
  }
  
  .input-wrapper {
    .input-label {
      display: block;
      font-size: 15px;
      font-weight: 600;
      color: #1d1d1f;
      margin-bottom: 10px;
      letter-spacing: -0.2px;
    }
  }
  
  .custom-input {
    :deep(.el-input__wrapper) {
      height: 52px;
      border-radius: 14px;
      border: 1.5px solid #e5e5ea;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
      background: rgba(255, 255, 255, 0.8);
      backdrop-filter: blur(10px);
      padding: 0 16px;
      transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
      
      &:hover {
        border-color: #48484a;
        transform: translateY(-1px);
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
        background: rgba(255, 255, 255, 0.95);
      }
      
      &.is-focus {
        border-color: #1d1d1f;
        box-shadow: 0 0 0 4px rgba(0, 0, 0, 0.06), 0 6px 20px rgba(0, 0, 0, 0.12);
        transform: translateY(-1px);
        background: rgba(255, 255, 255, 1);
      }
    }
    
    :deep(.el-input__inner) {
      font-size: 16px;
      color: #1d1d1f;
      font-weight: 400;
      line-height: 1.4;
      padding: 0;
      height: 100%;
      
      &::placeholder {
        color: #8e8e93;
        font-weight: 400;
        opacity: 0.8;
      }
    }
    
    :deep(.el-input__prefix-inner) {
      color: #8e8e93;
      font-size: 18px;
      margin-right: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 20px;
      transition: color 0.3s ease;
    }
    
    &:hover :deep(.el-input__prefix-inner),
    &.is-focused :deep(.el-input__prefix-inner) {
      color: #48484a;
    }
    
    // 清除按钮样式优化
    :deep(.el-input__suffix-inner) {
      display: flex;
      align-items: center;
      gap: 6px;
      
      .el-input__clear {
        color: #8e8e93;
        font-size: 16px;
        transition: all 0.3s ease;
        
        &:hover {
          color: #48484a;
          transform: scale(1.1);
        }
      }
      
      .el-input__password {
        color: #8e8e93;
        font-size: 16px;
        transition: all 0.3s ease;
        
        &:hover {
          color: #48484a;
          transform: scale(1.05);
        }
      }
    }
  }
  
  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 28px 0 36px 0;
    
    .remember-me {
      :deep(.el-checkbox__label) {
        color: #6e6e73;
        font-size: 14px;
        font-weight: 500;
      }
      
      :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
        background: #1d1d1f;
        border-color: #1d1d1f;
      }
    }
    
    .forgot-link {
      font-size: 14px;
      font-weight: 500;
      text-decoration: none;
      color: #48484a;
      
      &:hover {
        color: #1d1d1f;
        text-decoration: underline;
      }
    }
  }
  
  .form-actions {
    display: flex;
    gap: 12px;
    margin-bottom: 24px;
    
    .login-button {
      @include primary-button;
      flex: 2;
      height: 48px;
      font-size: 16px;
      font-weight: 600;
      background: linear-gradient(135deg, #1d1d1f 0%, #48484a 100%);
      border: none;
      position: relative;
      overflow: hidden;
      color: #ffffff;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
        transition: all 0.6s ease;
      }
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
        background: linear-gradient(135deg, #2c2c2e 0%, #48484a 100%);
        
        &::before {
          left: 100%;
        }
      }
      
      &.is-loading {
        transform: none;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
      }
    }
    
    .reset-button {
      @include secondary-button;
      flex: 1;
      height: 48px;
      font-size: 16px;
      font-weight: 500;
      background: linear-gradient(135deg, #f2f2f7 0%, #e5e5ea 100%);
      color: #1d1d1f;
      border: 1px solid rgba(0, 0, 0, 0.06);
      
      &:hover {
        background: linear-gradient(135deg, #e5e5ea 0%, #d1d1d6 100%);
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
    }
  }
  
  .register-link {
    text-align: center;
    font-size: 14px;
    color: #6e6e73;
    
    .register-btn {
      margin-left: 8px;
      font-weight: 600;
      text-decoration: none;
      color: #48484a;
      
      &:hover {
        color: #1d1d1f;
        text-decoration: underline;
      }
    }
  }
}

// 响应式设计
@media screen and (max-width: 1024px) {
  .login-container {
    padding: 12px;
  }
  
  .login-content {
    flex-direction: column;
    max-height: calc(100vh - 24px);
    height: auto;
    max-width: 600px;
  }
  
  .brand-section {
    flex: none;
    min-height: 240px;
    padding: 32px 24px;
  }
  
  .brand-title {
    font-size: 32px;
  }
  
  .feature-list {
    flex-direction: row;
    justify-content: center;
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .feature-item {
    flex-direction: column;
    text-align: center;
    padding: 4px;
    
    span {
      font-size: 13px;
    }
    
    .feature-icon {
      width: 44px;
      height: 44px;
    }
  }
  
  .login-section {
    padding: 32px 24px;
  }
}

@media screen and (max-width: 768px) {
  .login-container {
    padding: 8px;
  }
  
  .login-content {
    border-radius: 16px;
    max-height: calc(100vh - 16px);
  }
  
  .brand-section {
    min-height: 200px;
    padding: 24px 20px;
  }
  
  .brand-title {
    font-size: 28px;
  }
  
  .brand-subtitle {
    font-size: 14px;
    margin-bottom: 24px;
  }
  
  .login-header {
    margin-bottom: 24px;
    
    .login-title {
      font-size: 24px;
    }
    
    .login-desc {
      font-size: 14px;
    }
  }
  
  .login-form {
    .el-form-item {
      margin-bottom: 18px;
    }
    
    .custom-input {
      :deep(.el-input__wrapper) {
        height: 50px;
        border-radius: 12px;
        padding: 0 14px;
      }
      
      :deep(.el-input__inner) {
        font-size: 16px;
      }
      
      :deep(.el-input__prefix-inner) {
        margin-right: 10px;
        width: 18px;
      }
    }
    
    .input-wrapper {
      .input-label {
        font-size: 14px;
        margin-bottom: 8px;
      }
    }
    
    .form-options {
      margin: 24px 0 28px 0;
    }
  }
  
  .form-actions {
    flex-direction: column;
    gap: 12px;
    margin-bottom: 20px;
    
    .login-button,
    .reset-button {
      flex: none;
      width: 100%;
      height: 44px;
    }
  }
}

@media screen and (max-width: 480px) {
  .login-container {
    padding: 4px;
  }
  
  .login-content {
    max-height: calc(100vh - 8px);
    border-radius: 12px;
  }
  
  .brand-section {
    min-height: 160px;
    padding: 20px 16px;
  }
  
  .brand-title {
    font-size: 24px;
  }
  
  .brand-subtitle {
    font-size: 13px;
    margin-bottom: 20px;
  }
  
  .feature-list {
    gap: 8px;
    
    .feature-item {
      .feature-icon {
        width: 36px;
        height: 36px;
        font-size: 16px;
      }
      
      span {
        font-size: 12px;
      }
    }
  }
  
  .login-section {
    padding: 24px 16px;
  }
  
  .login-box {
    max-width: 100%;
  }
  
  .login-header {
    margin-bottom: 20px;
    
    .login-title {
      font-size: 22px;
    }
  }
  
  .login-form {
    .custom-input {
      :deep(.el-input__wrapper) {
        height: 48px;
        border-radius: 10px;
        padding: 0 12px;
      }
      
      :deep(.el-input__inner) {
        font-size: 15px;
      }
      
      :deep(.el-input__prefix-inner) {
        margin-right: 8px;
        width: 16px;
        font-size: 16px;
      }
    }
    
    .input-wrapper {
      .input-label {
        font-size: 13px;
        margin-bottom: 6px;
      }
    }
    
    .form-options {
      margin: 20px 0 24px 0;
    }
  }
}

// 动画效果
@keyframes brandFloat {
  0%, 100% {
    opacity: 0.8;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.02);
  }
}

@keyframes borderGlow {
  0%, 100% {
    opacity: 0.3;
    box-shadow: inset 0 0 20px rgba(255, 255, 255, 0.1);
  }
  50% {
    opacity: 0.6;
    box-shadow: inset 0 0 30px rgba(255, 255, 255, 0.2);
  }
}

@keyframes iconFloat {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-5px);
  }
}

// 额外的视觉增强
.login-content {
  position: relative;
  z-index: 1;
  
  &::before {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: linear-gradient(45deg, 
      rgba(0, 122, 255, 0.1), 
      rgba(52, 199, 89, 0.1), 
      rgba(255, 149, 0, 0.1),
      rgba(0, 122, 255, 0.1)
    );
    border-radius: 26px;
    z-index: -1;
    opacity: 0;
    animation: borderShine 4s ease-in-out infinite;
  }
}

@keyframes borderShine {
  0%, 100% {
    opacity: 0;
  }
  50% {
    opacity: 0.3;
  }
}

// 增强表单交互
.custom-input {
  :deep(.el-input__wrapper) {
    position: relative;
    overflow: hidden;
    
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      width: 0;
      height: 2px;
      background: linear-gradient(90deg, #1d1d1f 0%, #48484a 100%);
      transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
      transform: translateX(-50%);
      border-radius: 1px;
    }
    
    &.is-focus::after {
      width: calc(100% - 32px);
    }
  }
  
  // 增强焦点状态的视觉反馈
  &:has(.el-input__wrapper.is-focus) {
    .input-label {
      color: #1d1d1f;
      transform: scale(0.95);
      transition: all 0.3s ease;
    }
  }
}

// 增强按钮效果
.reset-button {
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    background: radial-gradient(circle, rgba(0, 0, 0, 0.05) 0%, transparent 70%);
    transition: all 0.4s ease;
    transform: translate(-50%, -50%);
    border-radius: 50%;
  }
  
  &:hover::before {
    width: 300px;
    height: 300px;
  }
}
</style>