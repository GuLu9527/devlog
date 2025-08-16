<template>
  <div class="role-permission-assign">
    <el-card class="assign-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <el-icon class="title-icon" :size="20">
              <Key />
            </el-icon>
            <span class="title-text">角色权限分配</span>
          </div>
          <el-button 
            v-if="!isEditing" 
            type="primary" 
            @click="startEdit"
            :loading="loading"
          >
            <el-icon><Edit /></el-icon>
            编辑权限
          </el-button>
        </div>
      </template>

      <!-- 角色选择 -->
      <div class="role-section">
        <div class="section-label">选择角色</div>
        <el-select
          v-model="selectedRoleId"
          placeholder="请选择角色"
          style="width: 300px"
          @change="handleRoleChange"
          :disabled="isEditing"
        >
          <el-option
            v-for="role in roles"
            :key="role.id"
            :label="role.name"
            :value="role.id"
          >
            <div class="role-option">
              <span class="role-name">{{ role.name }}</span>
              <span class="role-code">{{ role.code }}</span>
            </div>
          </el-option>
        </el-select>
      </div>

      <!-- 权限分配区域 -->
      <div v-if="selectedRoleId" class="permission-section">
        <div class="section-label">分配权限</div>
        
        <!-- 只读模式 -->
        <div v-if="!isEditing" class="readonly-permissions">
          <div v-if="currentPermissions.length === 0" class="empty-permissions">
            <el-empty description="该角色暂无权限" :image-size="80" />
          </div>
          <div v-else class="permission-list">
            <el-tag 
              v-for="permission in currentPermissions" 
              :key="permission.id"
              class="permission-tag"
              :type="getPermissionTagType(permission.code)"
            >
              {{ permission.name }}
            </el-tag>
          </div>
        </div>

        <!-- 编辑模式 -->
        <div v-if="isEditing" class="edit-permissions">
          <!-- 批量操作工具栏 -->
          <div class="batch-operations">
            <div class="batch-label">快速操作：</div>
            <div class="batch-buttons">
              <el-button 
                size="small" 
                type="success" 
                @click="applyPermissionTemplate('admin')"
              >
                <el-icon><UserFilled /></el-icon>
                管理员模板
              </el-button>
              <el-button 
                size="small" 
                type="primary" 
                @click="applyPermissionTemplate('editor')"
              >
                <el-icon><Edit /></el-icon>
                编辑者模板
              </el-button>
              <el-button 
                size="small" 
                type="info" 
                @click="applyPermissionTemplate('viewer')"
              >
                <el-icon><View /></el-icon>
                查看者模板
              </el-button>
              <el-dropdown @command="handleBatchOperation">
                <el-button size="small">
                  批量操作
                  <el-icon><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="selectReadOnly">选择所有只读权限</el-dropdown-item>
                    <el-dropdown-item command="selectWriteOnly">选择所有写权限</el-dropdown-item>
                    <el-dropdown-item command="selectByModule" divided>按模块选择</el-dropdown-item>
                    <el-dropdown-item command="copyFromRole">从其他角色复制</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>

          <PermissionTree
            :permissions="allPermissions"
            :checked-permissions="checkedPermissionIds"
            @update:checked-permissions="handlePermissionChange"
          />
          
          <div class="action-buttons">
            <el-button @click="cancelEdit">
              <el-icon><Close /></el-icon>
              取消
            </el-button>
            <el-button @click="resetPermissions">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
            <el-button 
              type="primary" 
              @click="savePermissions"
              :loading="saving"
            >
              <el-icon><Check /></el-icon>
              保存
            </el-button>
          </div>
        </div>
      </div>

      <!-- 角色未选择提示 -->
      <div v-if="!selectedRoleId" class="no-role-selected">
        <el-empty description="请先选择角色" :image-size="120" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Key, Edit, Check, Close, Refresh, UserFilled, View, ArrowDown } from '@element-plus/icons-vue'
import PermissionTree from './PermissionTree.vue'
import { findAllApi as getRoleList } from '@/api/role'
import { getPermissionTagType, PERMISSION_TEMPLATES, filterPermissionsByType, getModuleName } from '@/utils/permissionUtils'
import { 
  getAllPermissions, 
  getRolePermissions, 
  getRolePermissionIds,
  assignRolePermissions 
} from '@/api/permission'

const props = defineProps({
  roleId: {
    type: [String, Number],
    default: null
  }
})

const emit = defineEmits(['success', 'cancel'])

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const isEditing = ref(false)
const selectedRoleId = ref(props.roleId)
const roles = ref([])
const allPermissions = ref([])
const currentPermissions = ref([])
const checkedPermissionIds = ref([])
const originalPermissionIds = ref([])

// 计算属性
const selectedRole = computed(() => {
  return roles.value.find(role => role.id === selectedRoleId.value)
})


// 加载角色列表
const loadRoles = async () => {
  try {
    const response = await getRoleList()
    if (response.code === 200) {
      roles.value = response.data
    }
  } catch (error) {
    console.error('加载角色列表失败:', error)
    ElMessage.error('加载角色列表失败')
  }
}

// 加载所有权限
const loadAllPermissions = async () => {
  try {
    const response = await getAllPermissions()
    if (response.code === 200) {
      allPermissions.value = response.data.records || response.data
    }
  } catch (error) {
    console.error('加载权限列表失败:', error)
    ElMessage.error('加载权限列表失败')
  }
}

// 加载角色权限
const loadRolePermissions = async (roleId) => {
  if (!roleId) return
  
  loading.value = true
  try {
    // 同时获取权限详情和权限ID列表
    const [permissionsResponse, permissionIdsResponse] = await Promise.all([
      getRolePermissions(roleId),
      getRolePermissionIds(roleId)
    ])
    
    if (permissionsResponse.code === 200) {
      currentPermissions.value = permissionsResponse.data || []
    }
    
    if (permissionIdsResponse.code === 200) {
      const permissionIds = permissionIdsResponse.data || []
      checkedPermissionIds.value = [...permissionIds]
      originalPermissionIds.value = [...permissionIds]
    }
  } catch (error) {
    console.error('加载角色权限失败:', error)
    ElMessage.error('加载角色权限失败')
  } finally {
    loading.value = false
  }
}

// 处理角色变更
const handleRoleChange = (roleId) => {
  selectedRoleId.value = roleId
  if (roleId) {
    loadRolePermissions(roleId)
  } else {
    currentPermissions.value = []
    checkedPermissionIds.value = []
    originalPermissionIds.value = []
  }
  
  // 如果正在编辑，退出编辑模式
  if (isEditing.value) {
    isEditing.value = false
  }
}

// 开始编辑
const startEdit = () => {
  if (!selectedRoleId.value) {
    ElMessage.warning('请先选择角色')
    return
  }
  isEditing.value = true
}

// 取消编辑
const cancelEdit = () => {
  checkedPermissionIds.value = [...originalPermissionIds.value]
  isEditing.value = false
}

// 重置权限
const resetPermissions = () => {
  checkedPermissionIds.value = [...originalPermissionIds.value]
}

// 处理权限变更
const handlePermissionChange = (permissionIds) => {
  checkedPermissionIds.value = permissionIds
}

// 应用权限模板
const applyPermissionTemplate = async (templateKey) => {
  const template = PERMISSION_TEMPLATES[templateKey]
  if (!template) return

  try {
    await ElMessageBox.confirm(
      `确定要应用 ${template.name} 吗？\n${template.description}`,
      '应用权限模板',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    const newPermissionIds = template.getPermissions(allPermissions.value)
    checkedPermissionIds.value = newPermissionIds
    ElMessage.success(`已应用 ${template.name}`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('应用权限模板失败:', error)
    }
  }
}

// 处理批量操作
const handleBatchOperation = async (command) => {
  switch (command) {
    case 'selectReadOnly':
      selectPermissionsByType(['read', 'list', 'view'])
      break
    case 'selectWriteOnly':
      selectPermissionsByType(['create', 'add', 'update', 'edit', 'delete', 'remove'])
      break
    case 'selectByModule':
      await selectPermissionsByModule()
      break
    case 'copyFromRole':
      await copyPermissionsFromRole()
      break
  }
}

// 按权限类型选择
const selectPermissionsByType = (types) => {
  const typeMap = {
    'read': 'READ',
    'list': 'READ', 
    'view': 'READ',
    'create': 'WRITE',
    'add': 'WRITE',
    'update': 'WRITE',
    'edit': 'WRITE',
    'delete': 'WRITE',
    'remove': 'WRITE'
  }
  
  const mappedTypes = types.map(type => typeMap[type] || type)
  const filteredPermissions = filterPermissionsByType(allPermissions.value, mappedTypes)
  checkedPermissionIds.value = filteredPermissions.map(p => p.id)
  ElMessage.success(`已选择 ${filteredPermissions.length} 个权限`)
}

// 按模块选择权限
const selectPermissionsByModule = async () => {
  // 获取所有模块
  const modules = [...new Set(allPermissions.value.map(p => p.code.split(':')[0]))]
  const moduleNames = modules.map(module => ({
    label: getModuleName(module),
    value: module
  }))

  try {
    const { value: selectedModules } = await ElMessageBox.prompt(
      '请选择要包含的模块（多个模块用逗号分隔）：\n可选模块：' + 
      moduleNames.map(m => m.label).join('、'),
      '按模块选择权限',
      {
        inputValue: '',
        inputPlaceholder: '例如：user,role,permission'
      }
    )

    if (selectedModules) {
      const moduleList = selectedModules.split(',').map(s => s.trim())
      const selectedPermissions = allPermissions.value.filter(p => 
        moduleList.includes(p.code.split(':')[0])
      )
      checkedPermissionIds.value = selectedPermissions.map(p => p.id)
      ElMessage.success(`已选择 ${selectedPermissions.length} 个权限`)
    }
  } catch (error) {
    // 用户取消操作
  }
}

// 从其他角色复制权限
const copyPermissionsFromRole = async () => {
  try {
    const options = roles.value
      .filter(role => role.id !== selectedRoleId.value)
      .map(role => ({ label: role.name, value: role.id }))

    if (options.length === 0) {
      ElMessage.warning('没有其他角色可供复制')
      return
    }

    const { value: sourceRoleId } = await ElMessageBox.prompt(
      '请输入要复制权限的角色ID：\n' + 
      options.map(o => `${o.label}(ID: ${o.value})`).join('\n'),
      '从其他角色复制权限',
      {
        inputValue: '',
        inputPlaceholder: '输入角色ID'
      }
    )

    if (sourceRoleId) {
      const response = await getRolePermissionIds(parseInt(sourceRoleId))
      if (response.code === 200) {
        checkedPermissionIds.value = response.data || []
        ElMessage.success('权限复制成功')
      } else {
        ElMessage.error('复制权限失败')
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('复制权限失败:', error)
      ElMessage.error('复制权限失败')
    }
  }
}


// 保存权限
const savePermissions = async () => {
  if (!selectedRoleId.value) {
    ElMessage.warning('请先选择角色')
    return
  }

  // 检查是否有变更
  const hasChanges = JSON.stringify(checkedPermissionIds.value.sort()) !== 
                    JSON.stringify(originalPermissionIds.value.sort())
  
  if (!hasChanges) {
    ElMessage.info('权限未发生变更')
    isEditing.value = false
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要为角色 "${selectedRole.value?.name}" 分配 ${checkedPermissionIds.value.length} 个权限吗？`,
      '确认权限分配',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    saving.value = true
    
    const response = await assignRolePermissions(selectedRoleId.value, checkedPermissionIds.value)
    
    if (response.code === 200) {
      ElMessage.success('权限分配成功')
      
      // 更新本地数据
      originalPermissionIds.value = [...checkedPermissionIds.value]
      await loadRolePermissions(selectedRoleId.value)
      
      isEditing.value = false
      emit('success', {
        roleId: selectedRoleId.value,
        permissionIds: checkedPermissionIds.value
      })
    } else {
      ElMessage.error(response.message || '权限分配失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('保存权限失败:', error)
      ElMessage.error('保存权限失败')
    }
  } finally {
    saving.value = false
  }
}

// 组件挂载
onMounted(async () => {
  await Promise.all([
    loadRoles(),
    loadAllPermissions()
  ])
  
  if (selectedRoleId.value) {
    await loadRolePermissions(selectedRoleId.value)
  }
})

// 暴露方法给父组件
defineExpose({
  setRole: (roleId) => {
    selectedRoleId.value = roleId
    handleRoleChange(roleId)
  },
  refresh: () => {
    if (selectedRoleId.value) {
      loadRolePermissions(selectedRoleId.value)
    }
  }
})
</script>

<style scoped>
.role-permission-assign {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.assign-card {
  border-radius: 12px;
  border: 1px solid #d1d1d6;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  color: #007AFF;
}

.title-text {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}

.role-section {
  margin-bottom: 32px;
}

.section-label {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
}

.section-label:before {
  content: '';
  width: 4px;
  height: 16px;
  background: #007AFF;
  border-radius: 2px;
  margin-right: 8px;
}

.role-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.role-name {
  font-weight: 500;
  color: #1d1d1f;
}

.role-code {
  font-size: 12px;
  color: #8e8e93;
  font-family: 'SF Mono', Monaco, 'Cascadia Code', monospace;
}

.permission-section {
  margin-top: 32px;
}

.readonly-permissions {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.empty-permissions {
  text-align: center;
  padding: 40px 20px;
}

.permission-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.permission-tag {
  margin: 0;
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 6px;
  font-weight: 500;
}

.edit-permissions {
  margin-top: 16px;
}

.batch-operations {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 24px;
  border: 1px solid #e9ecef;
}

.batch-label {
  font-weight: 600;
  color: #1d1d1f;
  font-size: 14px;
  min-width: 80px;
}

.batch-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e9ecef;
}

.no-role-selected {
  text-align: center;
  padding: 80px 20px;
  color: #8e8e93;
}

/* iOS 风格按钮 */
:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &.el-button--primary {
    background: #007AFF;
    border-color: #007AFF;
    
    &:hover {
      background: #0051D5;
      border-color: #0051D5;
      transform: translateY(-1px);
    }
  }
  
  &.el-button--default {
    background: #f2f2f7;
    border-color: #d1d1d6;
    color: #1d1d1f;
    
    &:hover {
      background: #e5e5ea;
      border-color: #c7c7cc;
      transform: translateY(-1px);
    }
  }
}

/* El-select 样式 */
:deep(.el-select) {
  .el-input__wrapper {
    border-radius: 8px;
    border: 1px solid #d1d1d6;
    
    &:hover {
      border-color: #007AFF;
    }
    
    &.is-focus {
      border-color: #007AFF;
      box-shadow: 0 0 0 2px rgba(0, 122, 255, 0.2);
    }
  }
}

/* El-card 样式 */
:deep(.el-card__header) {
  padding: 20px 24px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

:deep(.el-card__body) {
  padding: 24px;
}
</style>