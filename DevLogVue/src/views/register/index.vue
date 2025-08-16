<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Phone, UserFilled } from '@element-plus/icons-vue'
import { registerApi } from '@/api/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  email: '',
  phone: '',
  departmentId: null
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度必须在3-20个字符之间', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_-]+$/, message: '用户名只能包含字母、数字、下划线和中划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度必须在6-20个字符之间', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{6,}$/, message: '密码必须包含大小写字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [
    { max: 50, message: '真实姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const result = await registerApi(registerForm)
    if (result.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(result.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
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

const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h1 class="title">用户注册</h1>
        <p class="subtitle">DevLog 项目开发日志管理系统</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="registerForm"
        :rules="rules"
        class="register-form"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input
            v-model="registerForm.realName"
            placeholder="请输入真实姓名"
            :prefix-icon="UserFilled"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            :prefix-icon="Message"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            :prefix-icon="Phone"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="所属部门" prop="departmentId">
          <el-select
            v-model="registerForm.departmentId"
            placeholder="请选择所属部门"
            style="width: 100%"
          >
            <!-- TODO: 动态加载部门列表 -->
            <el-option label="技术部" :value="1" />
            <el-option label="产品部" :value="2" />
            <el-option label="运营部" :value="3" />
          </el-select>
        </el-form-item>
        
        <div class="form-actions">
          <el-button
            type="primary"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
          <el-button
            class="reset-button"
            @click="handleReset"
          >
            重置
          </el-button>
          <el-button
            type="info"
            class="login-button"
            @click="goToLogin"
          >
            返回登录
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.register-container {
  width: 100%;
  min-height: 100vh;
  background: url('@/assets/login.png') no-repeat center center;
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;

  .register-box {
    width: 500px;
    max-width: 90%;
    padding: 40px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .register-header {
      text-align: center;
      margin-bottom: 30px;
      
      .title {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
        margin: 0;
        background: linear-gradient(45deg, #667eea, #764ba2);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
      }
      
      .subtitle {
        font-size: 14px;
        color: #606266;
        margin: 8px 0 0;
      }
    }
    
    .register-form {
      .el-form-item {
        margin-bottom: 20px;
        
        :deep(.el-form-item__label) {
          font-weight: 500;
          color: #606266;
        }
      }
      
      .el-input, .el-select {
        --el-input-height: 40px;
        
        :deep(.el-input__wrapper) {
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
          border-radius: 6px;
          
          &.is-focus {
            box-shadow: 0 0 0 1px #667eea;
          }
        }
        
        :deep(.el-input__prefix-inner) {
          font-size: 16px;
          color: #909399;
        }
      }
      
      .form-actions {
        display: flex;
        gap: 12px;
        margin-top: 30px;
        
        .el-button {
          flex: 1;
          height: 40px;
          border-radius: 6px;
          font-size: 14px;
          
          &.register-button {
            background: linear-gradient(45deg, #667eea, #764ba2);
            border: none;
            
            &:hover {
              opacity: 0.9;
            }
          }
          
          &.reset-button {
            background: #f5f7fa;
            border: 1px solid #dcdfe6;
            color: #606266;
            
            &:hover {
              background: #e4e7ed;
            }
          }
          
          &.login-button {
            background: #909399;
            border: none;
            color: white;
            
            &:hover {
              background: #79808a;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .register-container {
    padding: 10px;
    
    .register-box {
      padding: 30px 20px;
    }
  }
}
</style>