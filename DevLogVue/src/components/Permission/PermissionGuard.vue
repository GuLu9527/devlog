<!--
  权限保护组件
  提供更好的用户体验和加载状态
-->

<template>
  <div class="permission-guard">
    <!-- 权限验证中 -->
    <div v-if="isLoading" class="permission-loading">
      <el-skeleton :rows="5" animated />
      <div class="loading-text">
        <el-icon class="loading-icon"><Loading /></el-icon>
        正在验证权限...
      </div>
    </div>
    
    <!-- 有权限 -->
    <div v-else-if="hasAccess" class="permission-content">
      <slot />
    </div>
    
    <!-- 无权限 -->
    <div v-else class="permission-denied">
      <slot name="denied" :error="permissionError">
        <div class="denied-content">
          <div class="denied-icon">
            <el-icon size="48" color="#FF9500">
              <Lock />
            </el-icon>
          </div>
          <h3 class="denied-title">{{ permissionError.title }}</h3>
          <p class="denied-message">{{ permissionError.message }}</p>
          
          <!-- 权限详情 -->
          <el-collapse v-if="showDetails" class="permission-details">
            <el-collapse-item title="权限详情" name="details">
              <div class="details-content">
                <div class="detail-item">
                  <span class="label">需要权限：</span>
                  <el-tag v-if="typeof requiredPermission === 'string'" type="info">
                    {{ requiredPermission }}
                  </el-tag>
                  <div v-else>
                    <el-tag 
                      v-for="perm in requiredPermission" 
                      :key="perm" 
                      type="info" 
                      class="permission-tag"
                    >
                      {{ perm }}
                    </el-tag>
                  </div>
                </div>
                <div class="detail-item">
                  <span class="label">当前角色：</span>
                  <el-tag type="primary">{{ userRole || '未分配' }}</el-tag>
                </div>
                <div class="detail-item">
                  <span class="label">用户权限：</span>
                  <div class="user-permissions">
                    <el-tag 
                      v-for="perm in userPermissions.slice(0, 5)" 
                      :key="perm" 
                      size="small"
                      class="permission-tag"
                    >
                      {{ perm }}
                    </el-tag>
                    <el-tag v-if="userPermissions.length > 5" size="small" type="info">
                      +{{ userPermissions.length - 5 }} 更多
                    </el-tag>
                    <span v-if="userPermissions.length === 0" class="no-permissions">
                      暂无权限
                    </span>
                  </div>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
          
          <!-- 操作按钮 -->
          <div class="denied-actions">
            <el-button @click="handleRefresh" :loading="refreshing">
              <el-icon><Refresh /></el-icon>
              刷新权限
            </el-button>
            <el-button @click="handleContact" type="primary">
              <el-icon><Message /></el-icon>
              联系管理员
            </el-button>
          </div>
        </div>
      </slot>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { Lock, Loading, Refresh, Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  // 需要的权限
  permission: {
    type: [String, Array],
    default: null
  },
  // 需要的角色
  role: {
    type: [String, Array],
    default: null
  },
  // 是否需要全部权限
  requireAll: {
    type: Boolean,
    default: false
  },
  // 是否显示详细信息
  showDetails: {
    type: Boolean,
    default: true
  },
  // 自定义错误信息
  customError: {
    type: Object,
    default: null
  },
  // 权限验证延迟（毫秒）
  delay: {
    type: Number,
    default: 300
  }
})

const emit = defineEmits(['access-granted', 'access-denied'])

const authStore = useAuthStore()
const isLoading = ref(true)
const refreshing = ref(false)

// 获取需要的权限（用于显示）
const requiredPermission = computed(() => {
  return props.permission || '未指定权限'
})

// 用户角色
const userRole = computed(() => {
  return authStore.user?.roleName || authStore.user?.role
})

// 用户权限列表
const userPermissions = computed(() => {
  return authStore.userPermissions || []
})

// 权限检查
const hasAccess = computed(() => {
  if (!authStore.hasInitialized) {
    return false
  }
  
  let hasPermission = true
  
  // 角色检查
  if (props.role) {
    const hasRole = Array.isArray(props.role) 
      ? props.role.some(r => authStore.hasRole(r))
      : authStore.hasRole(props.role)
    
    if (!hasRole) {
      hasPermission = false
    }
  }
  
  // 权限检查
  if (props.permission && hasPermission) {
    if (Array.isArray(props.permission)) {
      if (props.requireAll) {
        hasPermission = authStore.hasAllPermissions(props.permission)
      } else {
        hasPermission = authStore.hasPermission(props.permission)
      }
    } else {
      hasPermission = authStore.hasPermission(props.permission)
    }
  }
  
  return hasPermission
})

// 权限错误信息
const permissionError = computed(() => {
  if (props.customError) {
    return props.customError
  }
  
  let title = '访问受限'
  let message = '您当前没有访问此内容的权限'
  
  if (props.role && !props.permission) {
    title = '角色权限不足'
    message = `需要 ${Array.isArray(props.role) ? props.role.join(' 或 ') : props.role} 角色权限`
  } else if (props.permission && !props.role) {
    const permText = Array.isArray(props.permission) 
      ? (props.requireAll ? '全部权限' : '任一权限')
      : '指定权限'
    title = '功能权限不足'
    message = `需要 ${permText} 才能访问此功能`
  } else if (props.role && props.permission) {
    title = '权限验证失败'
    message = '您的角色和权限都不满足访问要求'
  }
  
  return { title, message }
})

// 刷新权限
const handleRefresh = async () => {
  refreshing.value = true
  try {
    const success = await authStore.refreshPermissions()
    if (success) {
      ElMessage.success('权限已刷新')
    } else {
      ElMessage.warning('权限刷新失败，请稍后重试')
    }
  } catch (error) {
    ElMessage.error('刷新权限时发生错误')
  } finally {
    refreshing.value = false
  }
}

// 联系管理员
const handleContact = () => {
  ElMessage.info('请联系系统管理员申请相应权限')
  // 这里可以添加具体的联系方式或跳转逻辑
}

// 监听权限变化
watch(hasAccess, (newVal) => {
  if (newVal) {
    emit('access-granted')
  } else {
    emit('access-denied', permissionError.value)
  }
}, { immediate: true })

// 模拟权限验证延迟
onMounted(() => {
  setTimeout(() => {
    isLoading.value = false
  }, props.delay)
})

// 监听权限初始化状态
watch(() => authStore.hasInitialized, (initialized) => {
  if (initialized) {
    isLoading.value = false
  }
})
</script>

<style scoped>
.permission-guard {
  width: 100%;
  height: 100%;
}

.permission-loading {
  padding: 40px 20px;
  text-align: center;
  
  .loading-text {
    margin-top: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    color: #6E6E73;
    font-size: 14px;
    
    .loading-icon {
      animation: spin 1s linear infinite;
    }
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.permission-content {
  width: 100%;
  height: 100%;
}

.permission-denied {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  padding: 40px 20px;
}

.denied-content {
  text-align: center;
  max-width: 500px;
  width: 100%;
}

.denied-icon {
  margin-bottom: 24px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

.denied-title {
  font-size: 24px;
  color: #1D1D1F;
  margin: 0 0 16px 0;
  font-weight: 600;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
}

.denied-message {
  font-size: 16px;
  color: #6E6E73;
  line-height: 1.5;
  margin: 0 0 32px 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', system-ui;
}

.permission-details {
  margin: 24px 0;
  text-align: left;
  
  .details-content {
    padding: 16px 0;
  }
  
  .detail-item {
    margin-bottom: 16px;
    
    .label {
      display: inline-block;
      width: 80px;
      font-weight: 500;
      color: #1D1D1F;
      font-size: 14px;
    }
    
    .permission-tag {
      margin: 2px 4px 2px 0;
    }
    
    .user-permissions {
      display: inline-block;
      max-width: 300px;
    }
    
    .no-permissions {
      color: #8E8E93;
      font-style: italic;
    }
  }
}

.denied-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .denied-actions {
    flex-direction: column;
    align-items: center;
    
    .el-button {
      width: 200px;
    }
  }
  
  .permission-details {
    .detail-item .label {
      width: 60px;
      font-size: 13px;
    }
  }
}
</style>