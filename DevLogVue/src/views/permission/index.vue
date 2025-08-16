<template>
  <div class="permission-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <div class="left">
            <el-input
              v-model="searchForm.name"
              placeholder="请输入权限名称"
              clearable
              class="search-input"
              @keyup.enter="handleSearch"
            />
            <el-input
              v-model="searchForm.code"
              placeholder="请输入权限编码"
              clearable
              class="search-input"
              @keyup.enter="handleSearch"
            />
            <el-input
              v-model="searchForm.url"
              placeholder="请输入URL"
              clearable
              class="search-input"
              @keyup.enter="handleSearch"
            />
            <el-select
              v-model="searchForm.method"
              placeholder="请选择请求方法"
              clearable
              class="search-input method-select"
            >
              <el-option label="GET" value="GET" />
              <el-option label="POST" value="POST" />
              <el-option label="PUT" value="PUT" />
              <el-option label="DELETE" value="DELETE" />
            </el-select>
            <el-button type="primary" class="search-btn" @click="handleSearch">
              <ActionIcon action="search" :size="16" />
              搜索
            </el-button>
            <el-button class="reset-btn" @click="resetSearch">
              <ActionIcon action="refresh" :size="16" />
              重置
            </el-button>
          </div>
          <el-button type="primary" class="header-add-btn" @click="handleAdd">
            <ActionIcon action="add" :size="16" />
            新增权限
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        border
      >
        <el-table-column prop="name" label="权限名称" min-width="150" />
        <el-table-column prop="code" label="权限编码" min-width="150" />
        <el-table-column prop="url" label="URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="method" label="请求方法" width="100">
          <template #default="{ row }">
            <el-tag :type="getMethodType(row.method)">
              {{ row.method }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-tooltip content="编辑权限" placement="top">
                <el-button type="primary" circle class="action-btn" @click="handleEdit(row)">
                  <ActionIcon action="edit" :size="16" />
                </el-button>
              </el-tooltip>
              <el-tooltip content="删除权限" placement="top">
                <el-button type="danger" circle class="action-btn" @click="handleDelete(row)">
                  <ActionIcon action="delete" :size="16" />
                </el-button>
              </el-tooltip>
            </div>
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
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增权限' : '编辑权限'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入权限编码" />
        </el-form-item>
        <el-form-item label="URL" prop="url">
          <el-input v-model="form.url" placeholder="请输入URL" />
        </el-form-item>
        <el-form-item label="请求方法" prop="method">
          <el-select v-model="form.method" placeholder="请选择请求方法" style="width: 100%">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ActionIcon } from '@/components/SvgIcons'
import {
  getPermissionList,
  createPermission,
  updatePermission,
  deletePermission
} from '@/api/permission'

// 搜索表单
const searchForm = ref({
  name: '',
  code: '',
  url: '',
  method: ''
})

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
  code: '',
  url: '',
  method: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入权限编码', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  url: [
    { required: true, message: '请输入URL', trigger: 'blur' }
  ],
  method: [
    { required: true, message: '请选择请求方法', trigger: 'change' }
  ]
}

// 获取请求方法对应的标签类型
const getMethodType = (method) => {
  const map = {
    'GET': 'success',
    'POST': 'primary',
    'PUT': 'warning',
    'DELETE': 'danger'
  }
  return map[method] || 'info'
}

// 获取权限列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getPermissionList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm.value
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取权限列表失败')
    }
  } catch (error) {
    console.error('获取权限列表失败:', error)
    ElMessage.error('获取权限列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    name: '',
    code: '',
    url: '',
    method: ''
  }
  handleSearch()
}

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    name: '',
    code: '',
    url: '',
    method: ''
  }
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = {
    id: row.id,
    name: row.name,
    code: row.code,
    url: row.url,
    method: row.method
  }
  dialogVisible.value = true
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确认删除该权限吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deletePermission(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      console.error('删除权限失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (dialogType.value === 'add') {
          res = await createPermission(form.value)
        } else {
          res = await updatePermission(form.value.id, form.value)
        }
        
        if (res.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '新增成功' : '更新成功')
          dialogVisible.value = false
          getList()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
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
  getList()
})
</script>

<style lang="scss" scoped>
.permission-container {
  padding: 24px;
  background: #ffffff;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .left {
      display: flex;
      gap: 12px;
      align-items: center;
      flex-wrap: wrap;
      
      .search-input {
        width: 180px;
        
        &.method-select {
          width: 150px;
        }
      }
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

:deep(.el-dialog__body) {
  padding: 20px 30px;
}

:deep(.el-tag) {
  text-transform: uppercase;
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
</style> 