<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getProjectList, createProject, updateProject, deleteProject } from '@/api/project'
import { getUserByRoleNameApi as getUsersByRole } from '@/api/role'
import { getDepartmentRootList } from '@/api/department'
import ProjectProgressChart from '@/components/Project/ProjectProgressChart.vue';
import { ActionIcon } from '@/components/SvgIcons';

// 搜索表单
const searchForm = ref({
  name: '',
  status: '',
  managerId: '',
  departmentId: '',
  startTime: '',
  endTime: ''
})

// 项目列表
const projectList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const dialogType = ref('add')
const projectForm = ref({
  name: '',
  description: '',
  status: '',
  managerId: '',
  departmentId: '',
  startTime: '',
  endTime: ''
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入项目描述', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择项目状态', trigger: 'change' }
  ],
  managerId: [
    { required: true, message: '请选择项目经理', trigger: 'change' }
  ],
  departmentId: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
}

// 状态选项
const statusOptions = [
  { value: '规划中', label: '规划中' },
  { value: '开发中', label: '开发中' },
  { value: '测试中', label: '测试中' },
  { value: '已上线', label: '已上线' },
  { value: '维护中', label: '维护中' },
  { value: '已关闭', label: '已关闭' }
]

// 用户列表和部门列表
const userOptions = ref([])
const departmentOptions = ref([])

// 项目经理列表
const managerOptions = ref([])

// 获取用户列表
const getUserOptions = async () => {
  try {
    const res = await getUsersByRole('MANAGER')
    if (res.code === 200) {
      managerOptions.value = res.data.map(user => ({
        value: user.id,
        label: user.realName || user.username
      }))
    } else {
      ElMessage.error(res.message || '获取项目经理列表失败')
    }
  } catch (error) {
    console.error('获取项目经理列表失败:', error)
    ElMessage.error('获取项目经理列表失败')
  }
}

// 获取部门列表
const getDepartmentOptions = async () => {
  try {
    const res = await getDepartmentRootList()
    departmentOptions.value = res.data.map(dept => ({
      value: dept.id,
      label: dept.name
    }))
  } catch (error) {
    console.error('获取部门列表失败:', error)
  }
}

// 获取项目列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm.value
    }
    // 处理日期范围
    if (params.startTime) {
      params.startTime = params.startTime
    }
    if (params.endTime) {
      params.endTime = params.endTime
    }
    const res = await getProjectList(params)
    projectList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取项目列表失败:', error)
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
const handleReset = () => {
  searchForm.value = {
    name: '',
    status: '',
    managerId: '',
    departmentId: '',
    startTime: '',
    endTime: ''
  }
  handleSearch()
}

// 新增项目
const handleAdd = () => {
  dialogType.value = 'add'
  dialogTitle.value = '新增项目'
  projectForm.value = {
    name: '',
    description: '',
    status: '',
    managerId: '',
    departmentId: '',
    startTime: '',
    endTime: ''
  }
  dialogVisible.value = true
}

// 编辑项目
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogTitle.value = '编辑项目'
  projectForm.value = { ...row }
  dialogVisible.value = true
}

// 删除项目
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确认删除该项目吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteProject(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除项目失败:', error)
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  try {
    if (dialogType.value === 'add') {
      await createProject(projectForm.value)
      ElMessage.success('新增成功')
    } else {
      await updateProject(projectForm.value.id, projectForm.value)
      ElMessage.success('编辑成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('保存项目失败:', error)
  }
}

// 分页变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  getList()
}

// 获取状态标签类型
const getStatusType = (status) => {
  const map = {
    '规划中': 'info',
    '开发中': 'primary',
    '测试中': 'warning',
    '已上线': 'success',
    '维护中': 'info',
    '已关闭': 'danger'
  }
  return map[status] || 'info'
}

// 获取状态标签文本
const getStatusText = (status) => {
  const map = {
    'PLANNING': '规划中',
    'IN_PROGRESS': '进行中',
    'COMPLETED': '已完成',
    'SUSPENDED': '已暂停'
  }
  return map[status] || '未知'
}

// 获取项目经理名称
const getManagerName = (managerId) => {
  const manager = managerOptions.value.find(item => item.value === managerId)
  return manager ? manager.label : '未知'
}

// 打开项目仪表板
const openDashboard = () => {
  // 这里可以使用路由跳转到仪表板页面
  console.log('打开项目仪表板');
  window.open('/dashboard', '_blank');
};

onMounted(() => {
  getList()
  getUserOptions()
  getDepartmentOptions()
})
</script>

<template>
  <div class="project-container">
    <!-- 项目进度可视化图表 -->
    <ProjectProgressChart />
    
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="项目名称">
          <el-input v-model="searchForm.name" placeholder="请输入项目名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="项目状态">
          <el-select v-model="searchForm.status" placeholder="请选择项目状态" clearable style="width: 200px">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="项目经理">
          <el-select v-model="searchForm.managerId" placeholder="请选择项目经理" clearable style="width: 200px">
            <el-option
              v-for="item in managerOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属部门">
          <el-select v-model="searchForm.departmentId" placeholder="请选择所属部门" clearable style="width: 200px">
            <el-option
              v-for="item in departmentOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="searchForm.startTime"
            type="date"
            placeholder="请选择开始时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="searchForm.endTime"
            type="date"
            placeholder="请选择结束时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>项目列表</span>
          <div class="header-actions">
            <el-button type="info" @click="openDashboard" class="dashboard-btn">
              <ActionIcon action="dashboard" :size="14" />
              项目仪表板
            </el-button>
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增项目</el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="projectList"
        border
        style="width: 100%"
      >
        <el-table-column prop="name" label="项目名称" min-width="150" />
        <el-table-column prop="description" label="项目描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="项目状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="managerId" label="项目经理" width="120">
          <template #default="{ row }">
            {{ getManagerName(row.managerId) }}
          </template>
        </el-table-column>
        <el-table-column prop="departmentId" label="所属部门" width="120" >
          <template #default="{ row }">
            {{ departmentOptions.find(item => item.value === row.departmentId)?.label }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-tooltip content="编辑项目" placement="top">
              <el-button
                type="primary"
                :icon="Edit"
                circle
                @click="handleEdit(row)"
              />
            </el-tooltip>
            <el-tooltip content="删除项目" placement="top">
              <el-button
                type="danger"
                :icon="Delete"
                circle
                @click="handleDelete(row)"
              />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getList"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="projectForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="projectForm.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="projectForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入项目描述"
          />
        </el-form-item>
        <el-form-item label="项目状态" prop="status">
          <el-select v-model="projectForm.status" placeholder="请选择项目状态" style="width: 100%">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="项目经理" prop="managerId">
          <el-select v-model="projectForm.managerId" placeholder="请选择项目经理" style="width: 100%">
            <el-option
              v-for="item in managerOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属部门" prop="departmentId">
          <el-select v-model="projectForm.departmentId" placeholder="请选择所属部门" style="width: 100%">
            <el-option
              v-for="item in departmentOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="projectForm.startTime"
            type="date"
            placeholder="请选择开始时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="projectForm.endTime"
            type="date"
            placeholder="请选择结束时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.project-container {
  padding: 20px;
  
  :deep(.chart-card) {
    margin-bottom: 24px;
  }

  .search-card {
    margin-bottom: 20px;

    :deep(.el-form-item) {
      margin-bottom: 16px;
      margin-right: 16px;
    }
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .header-actions {
        display: flex;
        gap: 12px;
        align-items: center;
        
        .dashboard-btn {
          background: #909399;
          border-color: #909399;
          color: white;
          
          &:hover {
            background: #73767A;
            border-color: #73767A;
          }
        }
      }
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style> 