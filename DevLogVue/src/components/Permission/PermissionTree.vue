<template>
  <div class="permission-tree-container">
    <div class="tree-header">
      <div class="header-actions">
        <el-button size="small" @click="expandAll">
          <ActionIcon action="expand" :size="14" />
          全部展开
        </el-button>
        <el-button size="small" @click="collapseAll">
          <ActionIcon action="collapse" :size="14" />
          全部折叠
        </el-button>
        <el-button size="small" @click="selectAll">
          <ActionIcon action="check" :size="14" />
          全选
        </el-button>
        <el-button size="small" @click="clearAll">
          <ActionIcon action="close" :size="14" />
          清空
        </el-button>
      </div>
    </div>
    
    <div class="tree-content">
      <el-tree
        ref="permissionTreeRef"
        :data="treeData"
        :props="treeProps"
        node-key="id"
        show-checkbox
        check-strictly
        :check-on-click-node="false"
        :default-checked-keys="checkedKeys"
        @check="handleCheck"
        class="permission-tree"
      >
        <template #default="{ node, data }">
          <div class="tree-node">
            <div class="node-content">
              <span class="node-icon">
                <el-icon v-if="!node.isLeaf" :size="16">
                  <Folder v-if="!node.expanded" />
                  <FolderOpened v-else />
                </el-icon>
                <el-icon v-else :size="16">
                  <Key />
                </el-icon>
              </span>
              <span class="node-label">{{ data.name }}</span>
              <span class="node-code" v-if="data.code">{{ data.code }}</span>
            </div>
            <div class="node-meta" v-if="data.method || data.url">
              <el-tag v-if="data.method" :type="getMethodType(data.method)" size="small">
                {{ data.method }}
              </el-tag>
              <span class="node-url" v-if="data.url">{{ data.url }}</span>
            </div>
          </div>
        </template>
      </el-tree>
    </div>
    
    <div class="selected-info">
      <span class="selected-count">已选择: {{ checkedKeys.length }} 项</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { Folder, FolderOpened, Key } from '@element-plus/icons-vue'
import { ActionIcon } from '@/components/SvgIcons'
import { getModuleName, getPermissionTagType, groupPermissionsByModule } from '@/utils/permissionUtils'

const props = defineProps({
  permissions: {
    type: Array,
    default: () => []
  },
  checkedPermissions: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:checkedPermissions'])

const permissionTreeRef = ref(null)
const checkedKeys = ref([])

const treeProps = {
  children: 'children',
  label: 'name',
  disabled: 'disabled'
}

// 将权限列表转换为树形结构
const treeData = computed(() => {
  const groups = groupPermissionsByModule(props.permissions)
  const result = []
  
  Object.entries(groups).forEach(([module, permissions]) => {
    result.push({
      id: `group_${module}`,
      name: getModuleName(module),
      code: module,
      children: permissions.map(permission => ({
        id: permission.id,
        name: permission.name,
        code: permission.code,
        url: permission.url,
        method: permission.method,
        description: permission.description,
        isGroup: false
      })),
      isGroup: true
    })
  })
  
  return result
})

// 获取请求方法类型
const getMethodType = (method) => {
  const types = {
    'GET': 'success',
    'POST': 'primary', 
    'PUT': 'warning',
    'DELETE': 'danger'
  }
  return types[method] || 'info'
}

// 处理权限选择
const handleCheck = (data, { checkedKeys: keys }) => {
  checkedKeys.value = keys.filter(key => !key.toString().startsWith('group_'))
  emit('update:checkedPermissions', checkedKeys.value)
}

// 全部展开
const expandAll = () => {
  const tree = permissionTreeRef.value
  if (tree) {
    treeData.value.forEach(node => {
      tree.store.nodesMap[node.id].expanded = true
    })
  }
}

// 全部折叠
const collapseAll = () => {
  const tree = permissionTreeRef.value
  if (tree) {
    treeData.value.forEach(node => {
      tree.store.nodesMap[node.id].expanded = false
    })
  }
}

// 全选
const selectAll = () => {
  const allPermissionIds = props.permissions.map(p => p.id)
  checkedKeys.value = allPermissionIds
  nextTick(() => {
    permissionTreeRef.value?.setCheckedKeys(allPermissionIds)
  })
  emit('update:checkedPermissions', allPermissionIds)
}

// 清空
const clearAll = () => {
  checkedKeys.value = []
  nextTick(() => {
    permissionTreeRef.value?.setCheckedKeys([])
  })
  emit('update:checkedPermissions', [])
}

// 监听外部传入的已选权限
watch(() => props.checkedPermissions, (newVal) => {
  checkedKeys.value = newVal || []
  nextTick(() => {
    permissionTreeRef.value?.setCheckedKeys(checkedKeys.value)
  })
}, { immediate: true })
</script>

<style scoped>
.permission-tree-container {
  border: 1px solid #d1d1d6;
  border-radius: 12px;
  background: #ffffff;
  overflow: hidden;
}

.tree-header {
  padding: 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.header-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tree-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 12px;
}

.permission-tree {
  background: transparent;
}

:deep(.el-tree-node__content) {
  height: auto;
  min-height: 40px;
  padding: 8px 12px;
  border-radius: 8px;
  margin-bottom: 4px;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

:deep(.el-tree-node__content:hover) {
  background: #f2f2f7;
  border-color: #d1d1d6;
}

:deep(.el-tree-node.is-checked > .el-tree-node__content) {
  background: rgba(0, 122, 255, 0.1);
  border-color: #007AFF;
}

.tree-node {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.node-content {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.node-icon {
  display: flex;
  align-items: center;
  color: #64748b;
  flex-shrink: 0;
}

.node-label {
  font-weight: 500;
  color: #1d1d1f;
  flex-shrink: 0;
}

.node-code {
  color: #6e6e73;
  font-size: 12px;
  font-family: 'SF Mono', Monaco, 'Cascadia Code', monospace;
  background: #f2f2f7;
  padding: 2px 6px;
  border-radius: 4px;
  margin-left: auto;
}

.node-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 24px;
}

.node-url {
  color: #8e8e93;
  font-size: 11px;
  font-family: 'SF Mono', Monaco, 'Cascadia Code', monospace;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.selected-info {
  padding: 12px 16px;
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
  text-align: center;
}

.selected-count {
  color: #6e6e73;
  font-size: 14px;
  font-weight: 500;
}

/* iOS 风格按钮 */
:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
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

/* El-tree 样式优化 */
:deep(.el-tree) {
  background: transparent;
  
  .el-tree-node {
    .el-tree-node__expand-icon {
      color: #64748b;
      
      &.is-leaf {
        display: none;
      }
    }
    
    .el-checkbox {
      .el-checkbox__input {
        .el-checkbox__inner {
          border-radius: 4px;
          border-color: #d1d1d6;
          
          &:hover {
            border-color: #007AFF;
          }
        }
        
        &.is-checked .el-checkbox__inner {
          background-color: #007AFF;
          border-color: #007AFF;
        }
      }
    }
  }
}
</style>