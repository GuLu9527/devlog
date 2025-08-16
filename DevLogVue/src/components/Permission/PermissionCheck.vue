<!--
  页面级权限检查组件
  用于包装需要权限验证的页面内容
-->

<template>
  <div v-if="hasAccess" class="permission-wrapper">
    <slot />
  </div>
  
  <!-- 无权限时显示的内容 -->
  <div v-else class="no-permission">
    <slot name="no-permission">
      <div class="no-permission-content">
        <div class="no-permission-icon">
          <el-icon size="64" color="#FF3B30">
            <Lock />
          </el-icon>
        </div>
        <h3 class="no-permission-title">访问受限</h3>
        <p class="no-permission-desc">{{ noPermissionMessage }}</p>
        <div class="no-permission-actions" v-if="showActions">
          <el-button @click="goBack" class="back-btn">
            <el-icon><ArrowLeft /></el-icon>
            返回上一页
          </el-button>
          <el-button type="primary" @click="goHome" class="home-btn">
            <el-icon><House /></el-icon>
            回到首页
          </el-button>
        </div>
      </div>
    </slot>
  </div>
</template>

<script setup>
import { computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Lock, ArrowLeft, House } from '@element-plus/icons-vue'

const props = defineProps({
  // 需要的权限（字符串或数组）
  permission: {
    type: [String, Array],
    default: null
  },
  // 需要的角色
  role: {
    type: [String, Array],
    default: null
  },
  // 是否需要全部权限（仅当permission为数组时有效）
  requireAll: {
    type: Boolean,
    default: false
  },
  // 无权限时的提示信息
  noPermissionMessage: {
    type: String,
    default: '您当前没有访问此页面的权限，请联系管理员'
  },
  // 是否显示操作按钮
  showActions: {
    type: Boolean,
    default: true
  },
  // 是否在无权限时重定向到403页面
  redirectTo403: {
    type: Boolean,
    default: false
  }
})

const router = useRouter()
const authStore = useAuthStore()

// 检查是否有访问权限
const hasAccess = computed(() => {
  // 如果权限系统未初始化，拒绝访问
  if (!authStore.hasInitialized) {
    return false
  }
  
  // 角色检查
  if (props.role) {
    const hasRole = Array.isArray(props.role) 
      ? props.role.some(r => authStore.hasRole(r))
      : authStore.hasRole(props.role)
    
    if (!hasRole) {
      return false
    }
  }
  
  // 权限检查
  if (props.permission) {
    if (Array.isArray(props.permission)) {
      if (props.requireAll) {
        // 需要全部权限
        return authStore.hasAllPermissions(props.permission)
      } else {
        // 任一权限即可
        return authStore.hasPermission(props.permission)
      }
    } else {
      // 单个权限
      return authStore.hasPermission(props.permission)
    }
  }
  
  // 如果没有指定权限要求，则允许访问
  return true
})

// 监听权限变化，决定是否重定向
if (props.redirectTo403) {
  watch(hasAccess, (newVal) => {
    if (!newVal && authStore.hasInitialized) {
      router.push('/403')
    }
  }, { immediate: true })
}

const goBack = () => {
  router.go(-1)
}

const goHome = () => {
  router.push('/index')
}
</script>

<style scoped>
.permission-wrapper {
  width: 100%;
  height: 100%;
}

.no-permission {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: 40px 20px;
}

.no-permission-content {
  text-align: center;
  max-width: 400px;
}

.no-permission-icon {
  margin-bottom: 24px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { 
    transform: scale(1); 
    opacity: 1;
  }
  50% { 
    transform: scale(1.05); 
    opacity: 0.8;
  }
}

.no-permission-title {
  font-size: 24px;
  color: #1D1D1F;
  margin: 0 0 16px 0;
  font-weight: 600;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
}

.no-permission-desc {
  font-size: 16px;
  color: #6E6E73;
  line-height: 1.5;
  margin: 0 0 32px 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
}

.no-permission-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.back-btn {
  background: #F2F2F7;
  border: 1px solid #E5E5EA;
  color: #1D1D1F;
  border-radius: 12px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    background: #E5E5EA;
    border-color: #D1D1D6;
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  }
}

.home-btn {
  background: linear-gradient(135deg, #007AFF, #5856D6);
  border: none;
  color: white;
  border-radius: 12px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    opacity: 0.9;
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(0, 122, 255, 0.3);
  }
}

@media (max-width: 768px) {
  .no-permission-actions {
    flex-direction: column;
  }
  
  .back-btn,
  .home-btn {
    width: 100%;
  }
}
</style>