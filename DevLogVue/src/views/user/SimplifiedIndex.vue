<!--
  使用通用CRUD组合式函数的简化版用户管理页面
  展示如何大幅减少重复代码
-->

<template>
  <div class="user-container">
    <PermissionCheck permission="user:list">
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <span>用户管理</span>
            <el-button 
              v-permission="'user:create'" 
              type="primary" 
              class="header-add-btn" 
              @click="handleAdd"
            >
              <ActionIcon action="add" :size="16" />
              新增用户
            </el-button>
          </div>
        </template>

        <!-- 搜索区域 -->
        <div class="search-container">
          <el-form :model="searchForm" inline>
            <el-form-item label="用户名">
              <el-input 
                v-model="searchForm.username" 
                placeholder="请输入用户名"
                clearable
                @keyup.enter="handleSearch"
              />
            </el-form-item>
            <el-form-item label="状态">
              <el-select 
                v-model="searchForm.status" 
                placeholder="请选择状态"
                clearable
              >
                <el-option label="启用" :value="1" />
                <el-option label="禁用" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">
                <ActionIcon action="search" :size="14" />
                搜索
              </el-button>
              <el-button @click="handleResetSearch">
                <ActionIcon action="reset" :size="14" />
                重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 表格区域 -->
        <el-table
          v-loading="loading"
          :data="tableData"
          style="width: 100%"
          border
        >
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="realName" label="真实姓名" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-switch
                v-model="row.status"
                :active-value="1"
                :inactive-value="0"
                @change="handleStatusChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-tooltip content="编辑用户" placement="top">
                <el-button 
                  v-permission="'user:update'" 
                  type="primary" 
                  circle 
                  class="action-btn" 
                  @click="handleEdit(row)"
                >
                  <ActionIcon action="edit" :size="16" />
                </el-button>
              </el-tooltip>
              <el-tooltip content="删除用户" placement="top">
                <el-button 
                  v-permission="'user:delete'" 
                  type="danger" 
                  circle 
                  class="action-btn" 
                  @click="handleDelete(row, 'id', 'realName')"
                >
                  <ActionIcon action="delete" :size="16" />
                </el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
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
    </PermissionCheck>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
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
          <el-button class="cancel-btn" @click="handleCancel">
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
import { onMounted } from 'vue'
import { ActionIcon } from '@/components/SvgIcons'
import PermissionCheck from '@/components/Permission/PermissionCheck.vue'
import { useCrudPage } from '@/composables/useCrudPage'
import {
  getUserList,
  createUser,
  updateUser,
  deleteUser,
  updateUserStatus
} from '@/api/user'

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 默认表单数据
const defaultForm = {
  username: '',
  realName: '',
  email: '',
  phone: '',
  status: 1
}

// 使用通用CRUD组合式函数
const {
  // 状态
  loading,
  tableData,
  currentPage,
  pageSize,
  total,
  searchForm,
  dialogVisible,
  dialogType,
  formRef,
  form,
  
  // 方法
  getList,
  handleSearch,
  handleResetSearch,
  handleAdd,
  handleEdit,
  handleDelete,
  handleSubmit,
  handleCancel,
  handleSizeChange,
  handleCurrentChange,
  handleStatusChange: baseHandleStatusChange
} = useCrudPage({
  module: 'user',
  apiMethods: {
    list: getUserList,
    create: createUser,
    update: updateUser,
    delete: deleteUser,
    updateStatus: updateUserStatus
  },
  defaultForm,
  formRules: rules
})

// 自定义状态切换处理
const handleStatusChange = (row) => {
  baseHandleStatusChange(row, 'status', 'id')
}

// 初始化搜索表单
Object.assign(searchForm, {
  username: '',
  status: ''
})

// 页面初始化
onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
@import '@/assets/button-style-guide.scss';

.user-container {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .search-container {
    margin-bottom: 20px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 12px;
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

.header-add-btn {
  @include primary-button;
  gap: 8px;
}

.action-btn {
  @include circle-button;
  margin: 0 4px;
  
  &.el-button--primary {
    background: #007AFF;
    border-color: #007AFF;
  }
  
  &.el-button--danger {
    background: #FF3B30;
    border-color: #FF3B30;
  }
}

.cancel-btn {
  @include secondary-button;
  gap: 8px;
}

.confirm-btn {
  @include primary-button;
  gap: 8px;
}
</style>