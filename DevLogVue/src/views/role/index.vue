<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus, Edit, Delete, Close, Check, Key } from '@element-plus/icons-vue';
import { queryPageApi, addRoleApi, updateRoleApi, deleteRoleApi, getRoleByIdApi } from '@/api/role';
import { ActionIcon } from '@/components/SvgIcons';
import RolePermissionAssign from '@/components/Permission/RolePermissionAssign.vue';

// 表格数据
const loading = ref(false);
const tableData = ref([]);
// 分页参数
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
});
// 搜索关键词
const searchName = ref('');
// 新增/编辑对话框
const dialogFormVisible = ref(false);
const dialogTitle = ref('新增角色');
const form = reactive({
  id: null,
  name: '',
  description: ''
});
// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入角色描述', trigger: 'blur' },
    { min: 2, max: 200, message: '长度在 2 到 200 个字符', trigger: 'blur' }
  ]
};
// 表单引用
const formRef = ref(null);
// 权限分配对话框
const permissionDialogVisible = ref(false);
const selectedRoleId = ref(null);

// 加载角色列表
const loadData = async () => {
  loading.value = true;
  try {
    const res = await queryPageApi(pagination.current, pagination.pageSize, searchName.value);
    if (res.code === 200) {
      tableData.value = res.data.records;
      pagination.total = res.data.total;
    }
  } catch (error) {
    console.error('加载角色列表失败:', error);
    ElMessage.error('加载角色列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  pagination.current = 1;
  loadData();
};

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增角色';
  form.id = null;
  form.name = '';
  form.description = '';
  dialogFormVisible.value = true;
};

// 编辑
const handleEdit = async (id) => {
  dialogTitle.value = '编辑角色';
  try {
    const res = await getRoleByIdApi(id);
    if (res.code === 200) {
      form.id = res.data.id;
      form.name = res.data.name;
      form.description = res.data.description;
      dialogFormVisible.value = true;
    }
  } catch (error) {
    console.error('获取角色详情失败:', error);
    ElMessage.error('获取角色详情失败');
  }
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
    let res = null;
    if (form.id) {
      res = await updateRoleApi(form);
    } else {
      res = await addRoleApi(form);
    }
    if (res && res.code === 200) {
      ElMessage.success(res.message);
      dialogFormVisible.value = false;
      loadData();
    } else {
      ElMessage.error(res?.message || '操作失败');
    }
  } catch (error) {
    console.error('表单验证失败:', error);
  }
};

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该角色吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteRoleApi(id);
      if (res.code === 200) {
        ElMessage.success(res.message);
        loadData();
      }
    } catch (error) {
      console.error('删除角色失败:', error);
      ElMessage.error('删除角色失败');
    }
  });
};

// 处理分页
const handleSizeChange = (val) => {
  pagination.pageSize = val;
  loadData();
};

const handleCurrentChange = (val) => {
  pagination.current = val;
  loadData();
};

// 权限分配
const handlePermission = (roleId) => {
  selectedRoleId.value = roleId;
  permissionDialogVisible.value = true;
};

// 权限分配成功回调
const handlePermissionSuccess = () => {
  ElMessage.success('权限分配成功');
  // 这里可以添加其他需要的操作，比如刷新角色列表等
};

// 初始化
onMounted(() => {
  loadData();
});
</script>

<template>
  <div class="role-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">角色管理</span>
            <el-input
              v-model="searchName"
              placeholder="请输入角色名称搜索"
              class="search-input"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          <el-button type="primary" class="header-add-btn" @click="handleAdd">
            <ActionIcon action="add" :size="16" />
            新增角色
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        border
        :header-cell-style="{
          background: '#f5f7fa',
          color: '#606266',
          fontWeight: 500
        }"
      >
        <el-table-column prop="name" label="角色名称" min-width="120" />
        <el-table-column prop="description" label="角色描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-tooltip content="编辑角色" placement="top">
              <el-button type="primary" circle class="action-btn" @click="handleEdit(row.id)">
                <ActionIcon action="edit" :size="16" />
              </el-button>
            </el-tooltip>
            <el-tooltip content="权限分配" placement="top">
              <el-button type="warning" circle class="action-btn" @click="handlePermission(row.id)">
                <el-icon><Key /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="删除角色" placement="top">
              <el-button type="danger" circle class="action-btn" @click="handleDelete(row.id)">
                <ActionIcon action="delete" :size="16" />
              </el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogFormVisible"
      :title="dialogTitle"
      width="500px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="dialog-form"
      >
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="cancel-btn" @click="dialogFormVisible = false">
            <ActionIcon action="close" :size="14" />
            取消
          </el-button>
          <el-button type="primary" class="confirm-btn" @click="submitForm">
            <ActionIcon action="check" :size="14" />
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="角色权限分配"
      width="90%"
      max-width="1200px"
      destroy-on-close
      :close-on-click-modal="false"
      class="permission-dialog"
    >
      <RolePermissionAssign
        :role-id="selectedRoleId"
        @success="handlePermissionSuccess"
        @cancel="permissionDialogVisible = false"
      />
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.role-container {
  padding: 24px;
  background: #ffffff;
  
  .box-card {
    background: #ffffff;
    border: 1px solid #d1d1d6;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .header-left {
        display: flex;
        align-items: center;
        gap: 20px;
        
        .title {
          font-size: 18px;
          font-weight: 600;
          color: #1d1d1f;
        }
        
        .search-input {
          width: 240px;
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

.dialog-form {
  .el-form-item__label {
    font-weight: 500;
  }
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
  
  &.el-button--warning {
    background: #FF9500;
    border-color: #FF9500;
    
    &:hover {
      background: #E6850E;
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

:deep(.el-button) {
  border-radius: 12px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
}

// 权限分配对话框样式
:deep(.permission-dialog) {
  .el-dialog__header {
    background: #f8f9fa;
    border-bottom: 1px solid #e9ecef;
    padding: 20px 24px;
  }
  
  .el-dialog__body {
    padding: 24px;
  }
  
  .el-dialog {
    border-radius: 12px;
    overflow: hidden;
  }
}
</style>