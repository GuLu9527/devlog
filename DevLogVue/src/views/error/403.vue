<template>
  <div class="error-container">
    <div class="error-content">
      <div class="error-header">
        <div class="error-icon">
          <el-icon size="64" color="#FF3B30">
            <Lock />
          </el-icon>
        </div>
        <h1 class="error-title">403</h1>
        <h2 class="error-subtitle">权限不足</h2>
        <p class="error-description">
          很抱歉，您当前没有访问该页面的权限<br>
          请联系系统管理员为您分配相应的权限
        </p>
      </div>

      <div class="error-actions">
        <el-button class="action-button back-button" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回上一页
        </el-button>
        <el-button class="action-button home-button" @click="goHome">
          <el-icon><House /></el-icon>
          回到首页
        </el-button>
      </div>

      <div class="user-info-section">
        <div class="info-card">
          <div class="card-header">
            <el-icon color="#007AFF"><User /></el-icon>
            <span>当前用户信息</span>
          </div>
          <div class="card-content">
            <div class="info-item">
              <span class="label">用户名：</span>
              <span class="value">{{ userInfo?.username || '未知用户' }}</span>
            </div>
            <div class="info-item">
              <span class="label">角色：</span>
              <span class="value">{{ userInfo?.roleName || '未分配角色' }}</span>
            </div>
            <div class="info-item">
              <span class="label">权限列表：</span>
              <div class="permission-tags">
                <span 
                  v-for="permission in userPermissions.slice(0, 6)" 
                  :key="permission"
                  class="permission-tag"
                >
                  {{ permission }}
                </span>
                <span v-if="userPermissions.length > 6" class="more-permissions">
                  +{{ userPermissions.length - 6 }} 更多
                </span>
                <span v-if="userPermissions.length === 0" class="no-permissions">
                  暂无权限
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Lock, ArrowLeft, House, User } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const userInfo = ref(null)
const userPermissions = ref([])

onMounted(() => {
  userInfo.value = authStore.user
  userPermissions.value = authStore.userPermissions
})

const goBack = () => {
  router.go(-1)
}

const goHome = () => {
  router.push('/index')
}
</script>

<style scoped>
.error-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.error-content {
  background: white;
  border-radius: 16px;
  padding: 48px 32px 32px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  max-width: 520px;
  width: 100%;
  position: relative;
}

.error-header {
  margin-bottom: 40px;
}

.error-icon {
  margin-bottom: 24px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.error-title {
  font-size: 64px;
  font-weight: 700;
  color: #FF3B30;
  margin: 0 0 16px 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
  letter-spacing: -2px;
}

.error-subtitle {
  font-size: 24px;
  color: #1D1D1F;
  margin: 0 0 16px 0;
  font-weight: 600;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
}

.error-description {
  font-size: 16px;
  color: #6E6E73;
  line-height: 1.5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
}

.error-actions {
  margin-bottom: 32px;
  display: flex;
  gap: 12px;
  justify-content: center;
}

.action-button {
  height: 44px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  padding: 0 24px;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  }
  
  &:active {
    transform: translateY(0);
  }
}

.back-button {
  background: #F2F2F7;
  border: 1px solid #E5E5EA;
  color: #1D1D1F;
  
  &:hover {
    background: #E5E5EA;
    border-color: #D1D1D6;
  }
}

.home-button {
  background: linear-gradient(135deg, #007AFF, #5856D6);
  border: none;
  color: white;
  
  &:hover {
    opacity: 0.9;
  }
}

.user-info-section {
  text-align: left;
}

.info-card {
  background: #F8F9FA;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #E5E5EA;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-weight: 600;
  color: #1D1D1F;
  font-size: 14px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.label {
  font-size: 12px;
  color: #6E6E73;
  font-weight: 500;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
}

.value {
  font-size: 14px;
  color: #1D1D1F;
  font-weight: 500;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
}

.permission-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 4px;
}

.permission-tag {
  background: #007AFF;
  color: white;
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 500;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
}

.more-permissions {
  background: #8E8E93;
  color: white;
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 500;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
}

.no-permissions {
  background: #FF9500;
  color: white;
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 500;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
}

@media (max-width: 768px) {
  .error-content {
    padding: 32px 20px 24px;
    margin: 0 16px;
  }
  
  .error-title {
    font-size: 48px;
  }
  
  .error-subtitle {
    font-size: 20px;
  }
  
  .error-actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .action-button {
    width: 100%;
  }
}
</style>