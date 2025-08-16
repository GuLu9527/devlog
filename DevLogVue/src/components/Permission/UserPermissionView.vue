<template>
  <div class="user-permission-view">
    <el-card class="permission-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="user-info">
            <el-avatar :size="40" :src="userInfo?.avatar">
              <UserIcons type="avatar" :size="24" />
            </el-avatar>
            <div class="user-details">
              <div class="user-name">{{ userInfo?.realName || userInfo?.username }}</div>
              <div class="user-role">{{ userInfo?.roleName }}</div>
            </div>
          </div>
          <el-tag :type="userInfo?.status === 1 ? 'success' : 'danger'" size="large">
            {{ userInfo?.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </div>
      </template>

      <div class="permission-content">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="5" animated />
        </div>
        
        <div v-else-if="permissions.length === 0" class="no-permissions">
          <el-empty description="该用户暂无权限" :image-size="80">
            <template #image>
              <el-icon :size="80" color="#d3d3d3">
                <Key />
              </el-icon>
            </template>
          </el-empty>
        </div>

        <div v-else class="permissions-display">
          <!-- 权限统计 -->
          <div class="permission-stats">
            <div class="stat-item">
              <div class="stat-number">{{ permissions.length }}</div>
              <div class="stat-label">总权限数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ moduleCount }}</div>
              <div class="stat-label">涉及模块</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ readPermissionsCount }}</div>
              <div class="stat-label">查看权限</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ writePermissionsCount }}</div>
              <div class="stat-label">操作权限</div>
            </div>
          </div>

          <!-- 权限分组显示 -->
          <div class="permissions-by-module">
            <div 
              v-for="(modulePerms, moduleName) in permissionsByModule" 
              :key="moduleName"
              class="module-section"
            >
              <div class="module-header">
                <el-icon :size="16" class="module-icon">
                  <Folder />
                </el-icon>
                <span class="module-name">{{ getModuleName(moduleName) }}</span>
                <el-tag size="small" type="info">{{ modulePerms.length }}</el-tag>
              </div>
              
              <div class="module-permissions">
                <el-tag
                  v-for="permission in modulePerms"
                  :key="permission.id"
                  :type="getPermissionTagType(permission.code)"
                  size="small"
                  class="permission-tag"
                >
                  <el-icon :size="12" class="tag-icon">
                    <component :is="getPermissionIcon(permission.code)" />
                  </el-icon>
                  {{ permission.name }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Key, Folder, View, Edit, Delete, Plus } from '@element-plus/icons-vue'
import { UserIcons } from '@/components/SvgIcons'
import { getRolePermissions } from '@/api/permission'
import { 
  getModuleName, 
  getPermissionTagType, 
  getPermissionIcon,
  groupPermissionsByModule,
  getPermissionStats
} from '@/utils/permissionUtils'

const props = defineProps({
  userId: {
    type: [String, Number],
    required: true
  },
  userInfo: {
    type: Object,
    required: true
  }
})

// 响应式数据
const loading = ref(false)
const permissions = ref([])

// 计算属性
const permissionsByModule = computed(() => {
  return groupPermissionsByModule(permissions.value)
})

const permissionStats = computed(() => {
  return getPermissionStats(permissions.value)
})

const moduleCount = computed(() => {
  return permissionStats.value.modules
})

const readPermissionsCount = computed(() => {
  return permissionStats.value.read
})

const writePermissionsCount = computed(() => {
  return permissionStats.value.write
})


// 加载用户权限
const loadUserPermissions = async () => {
  if (!props.userInfo?.roleId) {
    ElMessage.warning('用户角色信息不完整')
    return
  }

  loading.value = true
  try {
    const response = await getRolePermissions(props.userInfo.roleId)
    if (response.code === 200) {
      permissions.value = response.data || []
    } else {
      ElMessage.error(response.message || '获取用户权限失败')
    }
  } catch (error) {
    console.error('加载用户权限失败:', error)
    ElMessage.error('加载用户权限失败')
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserPermissions()
})

// 暴露刷新方法
defineExpose({
  refresh: loadUserPermissions
})
</script>

<style scoped>
.user-permission-view {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.permission-card {
  border-radius: 12px;
  border: 1px solid #d1d1d6;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
}

.user-role {
  font-size: 14px;
  color: #8e8e93;
}

.permission-content {
  min-height: 200px;
}

.loading-container {
  padding: 20px;
}

.no-permissions {
  text-align: center;
  padding: 40px 20px;
}

.permission-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 16px;
  margin-bottom: 32px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #007AFF;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #8e8e93;
  font-weight: 500;
}

.permissions-by-module {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.module-section {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  overflow: hidden;
  background: #ffffff;
}

.module-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.module-icon {
  color: #64748b;
}

.module-name {
  font-weight: 600;
  color: #1d1d1f;
  flex: 1;
}

.module-permissions {
  padding: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.permission-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.tag-icon {
  flex-shrink: 0;
}

/* iOS 风格优化 */
:deep(.el-card__header) {
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  padding: 20px 24px;
}

:deep(.el-card__body) {
  padding: 24px;
}

:deep(.el-avatar) {
  border: 2px solid #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-tag) {
  border-radius: 6px;
  font-weight: 500;
}

:deep(.el-empty__description) {
  color: #8e8e93;
  font-size: 14px;
}
</style>