<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ActionIcon } from '@/components/SvgIcons'
import {
  getGroupPage,
  createGroup,
  updateGroup,
  deleteGroup,
  updateGroupStatus
} from '@/api/group'
import { getDepartmentTree } from '@/api/department'
import { getUserByRoleNameApi } from '@/api/role'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const dialogType = ref('')
const currentId = ref(null)

// 部门列表
const departmentOptions = ref([])
// 组长列表
const leaderOptions = ref([])

const queryParams = reactive({
  current: 1,
  size: 10,
  name: '',
  departmentId: undefined,
  leaderId: undefined,
  status: undefined
})

const total = ref(0)

const formRef = ref(null)
const form = reactive({
  name: '',
  departmentId: undefined,
  leaderId: undefined,
  description: '',
  status: 1
})

const rules = {
  name: [
    { required: true, message: '请输入小组名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ],
  leaderId: [
    { required: true, message: '请选择组长', trigger: 'change' }
  ]
}

// 获取部门树形数据
const getDepartments = async () => {
  try {
    const res = await getDepartmentTree()
    if (res.code === 200) {
      departmentOptions.value = res.data
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
    ElMessage.error('获取部门列表失败')
  }
}

// 获取组长列表
const getLeaders = async () => {
  try {
    const res = await getUserByRoleNameApi('LEADER')
    if (res.code === 200) {
      leaderOptions.value = res.data.map(user => ({
        value: user.id,
        label: user.realName
      }))
    }
  } catch (error) {
    console.error('获取组长列表失败:', error)
    ElMessage.error('获取组长列表失败')
  }
}

// 获取小组列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getGroupPage(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取小组列表失败:', error)
    ElMessage.error('获取小组列表失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const resetQuery = () => {
  queryParams.name = ''
  queryParams.departmentId = undefined
  queryParams.leaderId = undefined
  queryParams.status = undefined
  queryParams.current = 1
  getList()
}

// 处理查询
const handleQuery = () => {
  queryParams.current = 1
  getList()
}

// 处理分页
const handleCurrentChange = (val) => {
  queryParams.current = val
  getList()
}

// 处理每页条数变化
const handleSizeChange = (val) => {
  queryParams.size = val
  queryParams.current = 1
  getList()
}

// 打开新增对话框
const handleAdd = () => {
  dialogType.value = 'add'
  dialogTitle.value = '新增小组'
  dialogVisible.value = true
  // 重置表单
  Object.assign(form, {
    name: '',
    departmentId: undefined,
    leaderId: undefined,
    description: '',
    status: 1
  })
  // 重置表单验证
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 打开编辑对话框
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogTitle.value = '编辑小组'
  dialogVisible.value = true
  currentId.value = row.id
  
  // 重置表单
  nextTick(() => {
    Object.assign(form, {
      name: row.name,
      departmentId: row.departmentId,
      leaderId: row.leaderId,
      description: row.description || '',
      status: row.status
    })
  })
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该小组吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteGroup(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getList()
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 获取部门名称
const getDepartmentName = (departmentId) => {
  const findDepartment = (departments, id) => {
    for (const dept of departments) {
      if (dept.id === id) {
        return dept.name
      }
      if (dept.children && dept.children.length > 0) {
        const found = findDepartment(dept.children, id)
        if (found) return found
      }
    }
    return null
  }
  
  const name = findDepartment(departmentOptions.value, departmentId)
  return name || '-'
}

// 处理状态变更
const handleStatusChange = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    const res = await updateGroupStatus(row.id, newStatus)
    if (res.code === 200) {
      ElMessage.success('状态更新成功')
      row.status = newStatus
    }
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    const api = dialogType.value === 'add' ? createGroup : updateGroup
    
    // 构造提交参数
    const submitData = {
      name: form.name,
      departmentId: form.departmentId,
      leaderId: form.leaderId,
      description: form.description,
      status: form.status
    }
    
    let res
    if (dialogType.value === 'add') {
      res = await api(submitData)
    } else {
      res = await api(currentId.value, submitData)
    }
    
    if (res.code === 200) {
      ElMessage.success(dialogType.value === 'add' ? '新增成功' : '更新成功')
      dialogVisible.value = false
      // 重新获取列表和组长列表，确保数据最新
      await Promise.all([getList(), getLeaders()])
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  }
}

onMounted(() => {
  getList()
  getDepartments()
  getLeaders()
})
</script>

<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-card class="search-wrapper">
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="小组名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入小组名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="所属部门" prop="departmentId">
          <el-cascader
            v-model="queryParams.departmentId"
            :options="departmentOptions"
            :props="{
              checkStrictly: true,
              value: 'id',
              label: 'name',
              children: 'children',
              emitPath: false
            }"
            placeholder="请选择部门"
            clearable
            class="department-cascader"
          />
        </el-form-item>
        <el-form-item label="组长" prop="leaderId">
          <el-select v-model="queryParams.leaderId" placeholder="请选择组长" clearable>
            <el-option
              v-for="item in leaderOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-buttons">
          <el-button type="primary" class="search-btn" @click="handleQuery">
            <ActionIcon action="search" :size="16" />
            搜索
          </el-button>
          <el-button class="reset-btn" @click="resetQuery">
            <ActionIcon action="refresh" :size="16" />
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="table-wrapper">
      <template #header>
        <el-button type="primary" class="header-add-btn" @click="handleAdd">
          <ActionIcon action="add" :size="16" />
          新增
        </el-button>
      </template>

      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="小组名称" min-width="120" />
        <el-table-column prop="departmentId" label="所属部门" min-width="120">
          <template #default="{ row }">
            {{ getDepartmentName(row.departmentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="leaderId" label="组长" min-width="120">
          <template #default="{ row }">
            {{ leaderOptions.find(item => item.value === row.leaderId)?.label || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-tooltip content="编辑小组" placement="top">
                <el-button type="primary" circle class="action-btn" @click="handleEdit(row)">
                  <ActionIcon action="edit" :size="16" />
                </el-button>
              </el-tooltip>
              <el-tooltip content="删除小组" placement="top">
                <el-button type="danger" circle class="action-btn" @click="handleDelete(row)">
                  <ActionIcon action="delete" :size="16" />
                </el-button>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      append-to-body
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="小组名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入小组名称" />
        </el-form-item>
        <el-form-item label="所属部门" prop="departmentId">
          <el-cascader
            v-model="form.departmentId"
            :options="departmentOptions"
            :props="{
              checkStrictly: true,
              value: 'id',
              label: 'name',
              children: 'children',
              emitPath: false
            }"
            placeholder="请选择所属部门"
            class="department-cascader"
          />
        </el-form-item>
        <el-form-item label="组长" prop="leaderId">
          <el-select v-model="form.leaderId" placeholder="请选择组长">
            <el-option
              v-for="item in leaderOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入小组描述"
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
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="dialogVisible = false">
            <ActionIcon action="close" :size="14" />
            取消
          </el-button>
          <el-button type="primary" class="confirm-btn" @click="submitForm">
            <ActionIcon action="check" :size="14" />
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
  
  .search-wrapper {
    margin-bottom: 20px;
    
    .search-form {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;
      
      :deep(.el-form-item) {
        margin-bottom: 0;
        margin-right: 0;
        
        .el-form-item__label {
          width: 80px;
          text-align: right;
        }
        
        .el-input,
        .el-select,
        .el-cascader {
          width: 240px;
        }
      }
      
      .search-buttons {
        margin-left: auto;
        
        .el-button {
          margin-left: 10px;
        }
      }
    }
  }
  
  .table-wrapper {
    .pagination-wrapper {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
  
  .dialog-footer {
    text-align: right;
  }

  :deep(.el-dialog) {
    .el-form-item {
      .el-form-item__label {
        width: 100px;
        text-align: right;
      }
      
      .el-input,
      .el-select,
      .el-cascader {
        width: 100%;
      }
    }
  }

  :deep(.el-tag) {
    min-width: 60px;
    text-align: center;
  }

  /* iOS风格按钮样式 */
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

  .search-btn, .reset-btn {
    border-radius: 8px;
    font-weight: 500;
    padding: 8px 16px;
    gap: 8px;
    transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
    
    &:hover {
      transform: translateY(-1px);
    }
  }

  .search-btn {
    background: #007AFF;
    border-color: #007AFF;
    
    &:hover {
      background: #0056CC;
      box-shadow: 0 2px 8px rgba(0, 122, 255, 0.25);
    }
  }

  .reset-btn {
    background: #F2F2F7;
    color: #6E6E73;
    border-color: #D1D1D6;
    
    &:hover {
      background: #E5E5EA;
      color: #1D1D1F;
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

  .table-actions {
    display: flex;
    gap: 8px;
    align-items: center;
    justify-content: center;
  }
}
</style> 