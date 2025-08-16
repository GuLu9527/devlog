<template>
  <div class="department-container">
    <PermissionCheck permission="department:list">
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <span>部门管理</span>
            <el-button 
              v-permission="'department:create'" 
              type="primary" 
              class="header-add-btn" 
              @click="handleAdd"
            >
              <ActionIcon action="add" :size="16" />
              新增部门
            </el-button>
          </div>
        </template>
      
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="tree-container">
            <el-input
              v-model="filterText"
              placeholder="请输入关键字进行过滤"
              clearable
              class="filter-input"
            />
            <el-tree
              ref="treeRef"
              :data="treeData"
              :props="defaultProps"
              :filter-node-method="filterNode"
              node-key="id"
              default-expand-all
              highlight-current
              @node-click="handleNodeClick"
            >
              <template #default="{ node, data }">
                <div class="custom-tree-node">
                  <span>{{ node.label }}</span>
                  <span class="node-status" :class="{ 'status-active': data.status === 1 }">
                    {{ data.status === 1 ? '启用' : '禁用' }}
                  </span>
                </div>
              </template>
            </el-tree>
          </div>
        </el-col>
        
        <el-col :span="18">
          <div class="table-container">
            <el-table
              v-loading="loading"
              :data="tableData"
              style="width: 100%"
              border
            >
              <el-table-column prop="name" label="部门名称" />
              <el-table-column prop="parentId" label="上级部门">
                <template #default="{ row }">
                  {{ getParentName(row.parentId) }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 1 ? 'success' : 'info'">
                    {{ row.status === 1 ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="180" />
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="{ row }">
                  <el-tooltip content="编辑部门" placement="top">
                    <el-button 
                      v-permission="'department:update'" 
                      type="primary" 
                      circle 
                      class="action-btn" 
                      @click="handleEdit(row)"
                    >
                      <ActionIcon action="edit" :size="16" />
                    </el-button>
                  </el-tooltip>
                  <el-tooltip content="删除部门" placement="top">
                    <el-button 
                      v-permission="'department:delete'" 
                      type="danger" 
                      circle 
                      class="action-btn" 
                      @click="handleDelete(row)"
                    >
                      <ActionIcon action="delete" :size="16" />
                    </el-button>
                  </el-tooltip>
                </template>
              </el-table-column>
            </el-table>
            
            <div class="pagination-container">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
    </PermissionCheck>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增部门' : '编辑部门'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="上级部门" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="getFilteredTreeData"
            :props="defaultProps"
            placeholder="请选择上级部门"
            check-strictly
            clearable
            :disabled="dialogType === 'edit' && form.id === form.parentId"
            value-key="id"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="cancel-btn" @click="dialogVisible = false">
            <ActionIcon action="close" :size="14" />
            取消
          </el-button>
          <el-button type="primary" class="confirm-btn" @click="handleSubmit">
            <ActionIcon action="check" :size="14" />
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ActionIcon } from '@/components/SvgIcons'
import PermissionCheck from '@/components/Permission/PermissionCheck.vue'
import { usePermissionCheck } from '@/composables/usePermissionCheck'
import {
  getDepartmentList,
  getDepartmentTree,
  createDepartment,
  updateDepartment,
  deleteDepartment
} from '@/api/department'

const { permissions } = usePermissionCheck()

// 树形数据
const treeData = ref([])
const filterText = ref('')
const treeRef = ref(null)
const defaultProps = {
  children: 'children',
  label: 'name'
}

// 表格数据
const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表单数据
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const form = ref({
  name: '',
  parentId: null,
  status: 1
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  parentId: [
    { required: true, message: '请选择上级部门', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 监听过滤文本变化
watch(filterText, (val) => {
  treeRef.value?.filter(val)
})

// 过滤节点方法
const filterNode = (value, data) => {
  if (!value) return true
  return data.name.includes(value)
}

// 获取部门树
const getTree = async () => {
  try {
    const res = await getDepartmentTree()
    treeData.value = res.data
  } catch (error) {
    console.error('获取部门树失败:', error)
  }
}

// 获取部门列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getDepartmentList({
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取部门列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取上级部门名称
const getParentName = (parentId) => {
  if (!parentId) return '无'
  
  const findParent = (tree) => {
    for (const node of tree) {
      if (node.id === parentId) return node.name
      if (node.children) {
        const name = findParent(node.children)
        if (name) return name
      }
    }
    return null
  }
  return findParent(treeData.value) || '无'
}

// 处理节点点击
const handleNodeClick = (data) => {
  // 可以在这里处理节点点击事件
}

// 处理新增
const handleAdd = () => {
  if (!permissions.canCreate('department')) return
  
  dialogType.value = 'add'
  form.value = {
    name: '',
    parentId: 0, // 默认为无上级部门
    status: 1
  }
  dialogVisible.value = true
}

// 获取过滤后的树形数据（排除当前部门及其子部门）
const getFilteredTreeData = computed(() => {
  if (dialogType.value === 'add') {
    return [
      {
        id: 0,
        name: '无',
        children: treeData.value
      }
    ]
  }
  
  const filterChildren = (tree, id) => {
    return tree.filter(node => {
      if (node.id === id) return false
      if (node.children) {
        node.children = filterChildren(node.children, id)
      }
      return true
    })
  }
  
  const filteredData = filterChildren(JSON.parse(JSON.stringify(treeData.value)), form.value.id)
  return [
    {
      id: 0,
      name: '无',
      children: filteredData
    }
  ]
})

// 处理编辑
const handleEdit = (row) => {
  if (!permissions.canUpdate('department')) return
  
  dialogType.value = 'edit'
  form.value = {
    id: row.id,
    name: row.name,
    parentId: row.parentId || 0, // 如果没有上级部门，设置为0
    status: row.status
  }
  dialogVisible.value = true
}

// 处理删除
const handleDelete = (row) => {
  if (!permissions.canDelete('department')) return
  
  ElMessageBox.confirm(
    '确认删除该部门吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteDepartment(row.id)
      ElMessage.success('删除成功')
      getList()
      getTree()
    } catch (error) {
      console.error('删除部门失败:', error)
    }
  })
}

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = {
          ...form.value,
          parentId: form.value.parentId === 0 ? null : form.value.parentId // 如果是0，则设置为null
        }
        
        if (dialogType.value === 'add') {
          await createDepartment(submitData)
          ElMessage.success('新增成功')
        } else {
          await updateDepartment(submitData.id, submitData)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        getTree()
        getList()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(error.response?.data?.message || '操作失败')
      }
    }
  })
}

// 处理分页
const handleSizeChange = (val) => {
  pageSize.value = val
  getList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getList()
}

// 初始化
onMounted(() => {
  getTree()
  getList()
})
</script>

<style lang="scss" scoped>
.department-container {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .tree-container {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    
    .filter-input {
      margin-bottom: 20px;
    }
    
    .custom-tree-node {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 14px;
      padding-right: 8px;
      
      .node-status {
        font-size: 12px;
        color: #909399;
        
        &.status-active {
          color: #67c23a;
        }
      }
    }
  }
  
  .table-container {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    
    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}

:deep(.el-tree-node__content) {
  height: 40px;
}

:deep(.el-dialog__body) {
  padding: 20px 30px;
}

// iOS风格按钮样式
.header-add-btn {
  border-radius: 12px;
  font-weight: 500;
  font-size: 14px;
  padding: 12px 20px;
  gap: 8px;
  background: #007AFF;
  border-color: #007AFF;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    background: #0056CC;
    border-color: #0056CC;
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(0, 122, 255, 0.3);
  }
  
  &:active {
    transform: translateY(0);
  }
}

.action-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  padding: 0;
  margin: 0 4px;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  border: 1px solid transparent;
  
  &:hover {
    transform: scale(1.05);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  }
  
  &:active {
    transform: scale(0.95);
  }
  
  &.el-button--primary {
    background: #007AFF;
    border-color: #007AFF;
    
    &:hover {
      background: #0056CC;
    }
  }
  
  &.el-button--danger {
    background: #FF3B30;
    border-color: #FF3B30;
    
    &:hover {
      background: #E62117;
    }
  }
}

.cancel-btn, .confirm-btn {
  border-radius: 12px;
  font-weight: 500;
  padding: 10px 20px;
  gap: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    transform: translateY(-1px);
  }
}

.cancel-btn {
  background: #F2F2F7;
  color: #6E6E73;
  border-color: #D1D1D6;
  
  &:hover {
    background: #E5E5EA;
    color: #1D1D1F;
  }
}

.confirm-btn {
  background: #007AFF;
  border-color: #007AFF;
  
  &:hover {
    background: #0056CC;
    box-shadow: 0 2px 8px rgba(0, 122, 255, 0.25);
  }
}
</style> 