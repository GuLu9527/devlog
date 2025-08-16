<template>
  <div class="work-hours-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <div class="title-icon">
            <LayoutIcons type="clock" :size="24" />
          </div>
          <div class="title-text">
            <h1>工时统计</h1>
            <p>记录和分析工作时间，追踪团队效率</p>
          </div>
        </div>
        <el-button type="primary" @click="showAddDialog" class="add-btn">
          <LayoutIcons type="task" :size="16" />
          记录工时
        </el-button>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card today">
          <div class="stat-icon">
            <LayoutIcons type="clock" :size="20" />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ todayHours }}h</div>
            <div class="stat-label">今日工时</div>
          </div>
        </div>

        <div class="stat-card week">
          <div class="stat-icon">
            <LayoutIcons type="analytics" :size="20" />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ weekHours }}h</div>
            <div class="stat-label">本周工时</div>
          </div>
        </div>

        <div class="stat-card month">
          <div class="stat-icon">
            <LayoutIcons type="dashboard" :size="20" />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ monthHours }}h</div>
            <div class="stat-label">本月工时</div>
          </div>
        </div>

        <div class="stat-card pending">
          <div class="stat-icon">
            <LayoutIcons type="notification" :size="20" />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ pendingCount }}</div>
            <div class="stat-label">待审核</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 筛选器 -->
    <div class="filter-section">
      <el-card class="filter-card">
        <div class="filter-content">
          <div class="filter-group">
            <div class="filter-item">
              <span class="filter-label">时间范围</span>
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                @change="loadWorkHours"
              />
            </div>

            <div class="filter-item">
              <span class="filter-label">状态</span>
              <el-select v-model="statusFilter" placeholder="全部状态" @change="loadWorkHours">
                <el-option label="全部" value="" />
                <el-option label="待审核" value="PENDING" />
                <el-option label="已通过" value="APPROVED" />
                <el-option label="已驳回" value="REJECTED" />
              </el-select>
            </div>
          </div>

          <div class="filter-actions">
            <el-button @click="resetFilters">重置</el-button>
            <el-button type="primary" @click="loadWorkHours">查询</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 工时列表 -->
    <div class="table-section">
      <el-card class="table-card">
        <template #header>
          <div class="table-header">
            <span class="table-title">工时记录</span>
            <div class="batch-actions" v-if="selectedRows.length > 0">
              <el-button 
                type="primary" 
                size="small" 
                @click="batchApprove"
                :loading="batchLoading"
              >
                批量通过 ({{ selectedRows.length }})
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="batchReject"
                :loading="batchLoading"
              >
                批量驳回
              </el-button>
            </div>
          </div>
        </template>

        <el-table
          :data="workHourList"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          stripe
          class="work-hours-table"
        >
          <el-table-column type="selection" width="50" />
          
          <el-table-column prop="workDate" label="日期" width="120" sortable>
            <template #default="{ row }">
              <span class="date-text">{{ formatDate(row.workDate) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="task" label="任务" min-width="200">
            <template #default="{ row }">
              <div class="task-cell">
                <div class="task-title">{{ row.taskTitle || '暂无任务' }}</div>
                <div class="project-name">{{ row.projectName || '--' }}</div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="hours" label="工时" width="80" sortable>
            <template #default="{ row }">
              <span class="hours-badge">{{ row.hours }}h</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="type" label="类型" width="80">
            <template #default="{ row }">
              <el-tag :type="getTypeTagType(row.type)" size="small">
                {{ getTypeText(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="description" label="描述" min-width="150">
            <template #default="{ row }">
              <span class="desc-text">{{ row.description || '暂无描述' }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button 
                  v-if="row.status === 'PENDING'" 
                  type="success" 
                  size="small" 
                  @click="approveItem(row)"
                >
                  通过
                </el-button>
                <el-button 
                  v-if="row.status === 'PENDING'" 
                  type="warning" 
                  size="small" 
                  @click="rejectItem(row)"
                >
                  驳回
                </el-button>
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="editItem(row)"
                >
                  编辑
                </el-button>
                <el-button 
                  type="danger" 
                  size="small" 
                  @click="deleteItem(row)"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadWorkHours"
            @current-change="loadWorkHours"
          />
        </div>
      </el-card>
    </div>

    <!-- 添加/编辑工时对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑工时' : '添加工时'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="工作日期" prop="date">
          <el-date-picker
            v-model="formData.date"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="关联任务" prop="taskId">
          <el-select
            v-model="formData.taskId"
            placeholder="选择任务"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="task in taskOptions"
              :key="task.id"
              :label="`${task.title} (${task.projectName || '未分组'})`"
              :value="task.id"
              :disabled="!task.projectId"
            >
              <div style="display: flex; justify-content: space-between;">
                <span>{{ task.title }}</span>
                <span style="color: #8e8e93; font-size: 12px;">
                  {{ task.projectName || '未分组' }}
                </span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="工时数" prop="hours">
          <el-input-number
            v-model="formData.hours"
            :min="0.5"
            :max="24"
            :step="0.5"
            placeholder="工时数"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="工时类型" prop="type">
          <el-select v-model="formData.type" placeholder="选择类型" style="width: 100%">
            <el-option label="开发" value="DEVELOPMENT" />
            <el-option label="测试" value="TESTING" />
            <el-option label="会议" value="MEETING" />
            <el-option label="调研" value="RESEARCH" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="工作描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入工作内容描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveWorkHour" :loading="saving">
            {{ isEdit ? '更新' : '保存' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { LayoutIcons } from '@/components/SvgIcons'
import { getSimpleTaskList } from '@/api/task'
import {
  getUserWorkHours,
  createWorkHour,
  updateWorkHour,
  deleteWorkHour,
  reviewWorkHour,
  batchReviewWorkHours,
  getUserWorkHourStats,
  getUserDailyWorkHours
} from '@/api/workHour'
import { useAuthStore } from '@/stores/auth'

// 获取用户信息
const authStore = useAuthStore()
const currentUserId = computed(() => authStore.user?.id)

// 响应式状态
const loading = ref(false)
const saving = ref(false)
const batchLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)

// 统计数据
const todayHours = ref(0)
const weekHours = ref(0)
const monthHours = ref(0)
const pendingCount = ref(0)

// 筛选条件
const dateRange = ref([])
const statusFilter = ref('')

// 工时列表数据
const workHourList = ref([])
const selectedRows = ref([])
const taskOptions = ref([])

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单数据
const formRef = ref(null)
const formData = reactive({
  id: null,
  date: '',
  taskId: null,
  hours: 1,
  type: 'DEVELOPMENT',
  description: ''
})

// 表单验证规则
const formRules = {
  date: [{ required: true, message: '请选择工作日期', trigger: 'change' }],
  taskId: [{ required: true, message: '请选择关联任务', trigger: 'change' }],
  hours: [{ required: true, message: '请输入工时数', trigger: 'blur' }],
  type: [{ required: true, message: '请选择工时类型', trigger: 'change' }]
}

// 计算属性
const hasSelectedRows = computed(() => selectedRows.value.length > 0)

// 方法定义
const formatDate = (dateStr) => {
  if (!dateStr) return '--'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const getTypeText = (type) => {
  const map = {
    'DEVELOPMENT': '开发',
    'TESTING': '测试', 
    'MEETING': '会议',
    'RESEARCH': '调研',
    'OTHER': '其他'
  }
  return map[type] || type
}

const getTypeTagType = (type) => {
  const map = {
    'DEVELOPMENT': '',
    'TESTING': 'warning',
    'MEETING': 'info',
    'RESEARCH': 'success',
    'OTHER': 'info'
  }
  return map[type] || ''
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已驳回'
  }
  return map[status] || status
}

const getStatusTagType = (status) => {
  const map = {
    'PENDING': 'warning',
    'APPROVED': 'success', 
    'REJECTED': 'danger'
  }
  return map[status] || ''
}

// 事件处理
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const resetFilters = () => {
  dateRange.value = []
  statusFilter.value = ''
  pagination.current = 1
  loadWorkHours()
}

const showAddDialog = () => {
  isEdit.value = false
  Object.assign(formData, {
    id: null,
    date: new Date().toISOString().split('T')[0],
    taskId: null,
    hours: 1,
    type: 'DEVELOPMENT',
    description: ''
  })
  dialogVisible.value = true
}

const editItem = (row) => {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    date: row.workDate,
    taskId: row.taskId, // 直接使用taskId
    hours: row.hours,
    type: row.type,
    description: row.description || ''
  })
  dialogVisible.value = true
}

const saveWorkHour = async () => {
  try {
    await formRef.value.validate()
    saving.value = true
    
    // 找到选中的任务以获取项目ID
    const selectedTask = taskOptions.value.find(task => task.id === formData.taskId)
    
    const workHourData = {
      workDate: formData.date,
      taskId: formData.taskId,
      projectId: selectedTask?.projectId, // 使用正确的字段名
      hours: formData.hours,
      type: formData.type,
      description: formData.description || ''
    }
    
    // 验证必需字段
    if (!workHourData.projectId) {
      ElMessage.error('选中的任务缺少项目信息，请重新选择任务')
      return
    }
    
    if (isEdit.value && formData.id) {
      await updateWorkHour(formData.id, workHourData)
      ElMessage.success('更新成功')
    } else {
      await createWorkHour(workHourData)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    await loadWorkHours()
    await loadStatistics()
  } catch (error) {
    console.error('保存工时失败:', error)
    ElMessage.error('保存失败: ' + (error.response?.data?.message || error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}

const deleteItem = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条工时记录吗？', '确认删除', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    await deleteWorkHour(row.id)
    ElMessage.success('删除成功')
    await loadWorkHours()
    await loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除工时失败:', error)
      ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message || '未知错误'))
    }
  }
}

const approveItem = async (row) => {
  try {
    await reviewWorkHour(row.id, { status: 'APPROVED' })
    ElMessage.success('审核通过')
    await loadWorkHours()
    await loadStatistics()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败: ' + (error.response?.data?.message || error.message || '未知错误'))
  }
}

const rejectItem = async (row) => {
  try {
    await ElMessageBox.confirm('确定要驳回这条工时记录吗？', '确认驳回', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    await reviewWorkHour(row.id, { status: 'REJECTED' })
    ElMessage.success('已驳回')
    await loadWorkHours()
    await loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('驳回失败:', error)
      ElMessage.error('驳回失败: ' + (error.response?.data?.message || error.message || '未知错误'))
    }
  }
}

const batchApprove = async () => {
  try {
    batchLoading.value = true
    const workHourIds = selectedRows.value.map(row => row.id)
    
    await batchReviewWorkHours({
      workHourIds,
      status: 'APPROVED',
      reviewNote: '批量通过'
    })
    
    ElMessage.success(`成功通过 ${selectedRows.value.length} 条记录`)
    selectedRows.value = []
    await loadWorkHours()
    await loadStatistics()
  } catch (error) {
    console.error('批量操作失败:', error)
    ElMessage.error('批量操作失败: ' + (error.response?.data?.message || error.message || '未知错误'))
  } finally {
    batchLoading.value = false
  }
}

const batchReject = async () => {
  try {
    await ElMessageBox.confirm(`确定要驳回 ${selectedRows.value.length} 条记录吗？`, '确认批量驳回', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    batchLoading.value = true
    const workHourIds = selectedRows.value.map(row => row.id)
    
    await batchReviewWorkHours({
      workHourIds,
      status: 'REJECTED',
      reviewNote: '批量驳回'
    })
    
    ElMessage.success(`成功驳回 ${selectedRows.value.length} 条记录`)
    selectedRows.value = []
    await loadWorkHours()
    await loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量操作失败:', error)
      ElMessage.error('批量操作失败: ' + (error.response?.data?.message || error.message || '未知错误'))
    }
  } finally {
    batchLoading.value = false
  }
}

// 数据加载
const loadWorkHours = async () => {
  try {
    loading.value = true
    
    const params = {
      pageNum: pagination.current,
      pageSize: pagination.size
    }
    
    // 添加筛选条件
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    
    if (statusFilter.value) {
      params.status = statusFilter.value
    }
    
    const response = await getUserWorkHours(params)
    
    if (response.code === 200) {
      workHourList.value = response.data?.records || response.data || []
      pagination.total = response.data?.total || response.total || 0
    } else {
      ElMessage.error(response.message || '加载数据失败')
      workHourList.value = []
    }
    
  } catch (error) {
    console.error('加载工时记录失败:', error)
    ElMessage.error('加载数据失败: ' + (error.response?.data?.message || error.message || '网络错误'))
    workHourList.value = []
  } finally {
    loading.value = false
  }
}

const loadTasks = async () => {
  try {
    const response = await getSimpleTaskList({
      pageNum: 1,
      pageSize: 100
    })
    
    if (response.code === 200) {
      taskOptions.value = Array.isArray(response.data) ? response.data : response.data?.records || []
    }
  } catch (error) {
    console.error('加载任务列表失败:', error)
  }
}

// 加载统计数据
const loadStatistics = async () => {
  if (!currentUserId.value) return
  
  try {
    // 获取今日工时
    const today = new Date().toISOString().split('T')[0]
    const dailyResponse = await getUserDailyWorkHours(currentUserId.value, today)
    if (dailyResponse.code === 200) {
      todayHours.value = dailyResponse.data?.totalHours || 0
    }
    
    // 获取本周和本月统计
    const now = new Date()
    const startOfWeek = new Date(now.setDate(now.getDate() - now.getDay()))
    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)
    
    // 本周统计
    const weekResponse = await getUserWorkHourStats(currentUserId.value, {
      startDate: startOfWeek.toISOString().split('T')[0],
      endDate: new Date().toISOString().split('T')[0]
    })
    if (weekResponse.code === 200) {
      weekHours.value = weekResponse.data?.totalHours || 0
    }
    
    // 本月统计
    const monthResponse = await getUserWorkHourStats(currentUserId.value, {
      startDate: startOfMonth.toISOString().split('T')[0],
      endDate: new Date().toISOString().split('T')[0]
    })
    if (monthResponse.code === 200) {
      monthHours.value = monthResponse.data?.totalHours || 0
    }
    
    // 获取待审核数量
    const pendingResponse = await getUserWorkHours({
      status: 'PENDING',
      pageSize: 1  // 只获取总数
    })
    if (pendingResponse.code === 200) {
      pendingCount.value = pendingResponse.data?.total || pendingResponse.total || 0
    }
    
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 页面初始化
onMounted(async () => {
  await Promise.all([
    loadWorkHours(),
    loadTasks(),
    loadStatistics()
  ])
})
</script>

<style lang="scss" scoped>
.work-hours-container {
  min-height: 100vh;
  background: #f5f5f7;
  
  .page-header {
    background: #fff;
    padding: 24px 32px;
    border-bottom: 1px solid #e5e5e7;
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      max-width: 1200px;
      margin: 0 auto;
      
      .title-section {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .title-icon {
          width: 48px;
          height: 48px;
          background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
        }
        
        .title-text {
          h1 {
            margin: 0;
            font-size: 24px;
            font-weight: 600;
            color: #000;
            line-height: 1.2;
          }
          
          p {
            margin: 4px 0 0;
            font-size: 14px;
            color: #636366;
          }
        }
      }
      
      .add-btn {
        height: 40px;
        border-radius: 8px;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 8px;
        transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
        
        &:hover {
          transform: translateY(-1px);
        }
      }
    }
  }
  
  .stats-section {
    padding: 24px 32px;
    max-width: 1200px;
    margin: 0 auto;
    
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      
      .stat-card {
        background: #fff;
        border-radius: 12px;
        padding: 20px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
        border: 1px solid #e5e5e7;
        display: flex;
        align-items: center;
        gap: 16px;
        transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
        
        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
        }
        
        .stat-icon {
          width: 44px;
          height: 44px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
        }
        
        &.today .stat-icon {
          background: linear-gradient(135deg, #007AFF, #0056CC);
        }
        
        &.week .stat-icon {
          background: linear-gradient(135deg, #34C759, #28A745);
        }
        
        &.month .stat-icon {
          background: linear-gradient(135deg, #FF9500, #FF6B35);
        }
        
        &.pending .stat-icon {
          background: linear-gradient(135deg, #FF3B30, #E53E3E);
        }
        
        .stat-content {
          .stat-value {
            font-size: 24px;
            font-weight: 700;
            color: #000;
            line-height: 1;
            margin-bottom: 4px;
          }
          
          .stat-label {
            font-size: 14px;
            color: #8E8E93;
          }
        }
      }
    }
  }
  
  .filter-section {
    padding: 0 32px 24px;
    max-width: 1200px;
    margin: 0 auto;
    
    .filter-card {
      border-radius: 12px;
      border: 1px solid #e5e5e7;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      
      .filter-content {
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-wrap: wrap;
        gap: 20px;
        
        .filter-group {
          display: flex;
          gap: 24px;
          flex-wrap: wrap;
          
          .filter-item {
            display: flex;
            align-items: center;
            gap: 12px;
            
            .filter-label {
              font-size: 14px;
              color: #000;
              font-weight: 500;
              white-space: nowrap;
            }
          }
        }
        
        .filter-actions {
          display: flex;
          gap: 12px;
        }
      }
    }
  }
  
  .table-section {
    padding: 0 32px 32px;
    max-width: 1200px;
    margin: 0 auto;
    
    .table-card {
      border-radius: 12px;
      border: 1px solid #e5e5e7;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      
      .table-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .table-title {
          font-size: 18px;
          font-weight: 600;
          color: #000;
        }
        
        .batch-actions {
          display: flex;
          gap: 12px;
        }
      }
    }
  }
  
  .work-hours-table {
    .date-text {
      font-weight: 500;
      color: #000;
    }
    
    .task-cell {
      .task-title {
        font-weight: 500;
        color: #000;
        margin-bottom: 4px;
      }
      
      .project-name {
        font-size: 12px;
        color: #8E8E93;
      }
    }
    
    .hours-badge {
      background: #F2F2F7;
      color: #000;
      padding: 4px 8px;
      border-radius: 6px;
      font-weight: 600;
      font-size: 12px;
    }
    
    .desc-text {
      color: #636366;
      font-size: 14px;
    }
    
    .action-buttons {
      display: flex;
      gap: 8px;
      flex-wrap: wrap;
    }
  }
  
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

// 响应式设计
@media (max-width: 1024px) {
  .work-hours-container {
    .page-header .header-content {
      flex-direction: column;
      align-items: stretch;
      gap: 20px;
    }
    
    .stats-section .stats-grid {
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    }
    
    .filter-section .filter-card .filter-content {
      flex-direction: column;
      align-items: stretch;
      
      .filter-actions {
        justify-content: center;
      }
    }
  }
}

@media (max-width: 768px) {
  .work-hours-container {
    .page-header,
    .stats-section,
    .filter-section,
    .table-section {
      padding-left: 16px;
      padding-right: 16px;
    }
    
    .stats-section .stats-grid {
      grid-template-columns: 1fr;
    }
    
    .filter-section .filter-card .filter-content .filter-group {
      flex-direction: column;
      align-items: stretch;
      gap: 16px;
      
      .filter-item {
        flex-direction: column;
        align-items: stretch;
        gap: 8px;
      }
    }
  }
}
</style>