<template>
  <div class="worklog-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form ref="searchFormRef" :model="searchForm" inline>
        <el-form-item label="任务ID" prop="taskId">
          <el-input
            v-model="searchForm.taskId"
            placeholder="请输入任务ID"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="提交人" prop="userId">
          <el-select
            v-model="searchForm.userId"
            placeholder="请选择提交人"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="user in userOptions"
              :key="user.value"
              :label="user.label"
              :value="user.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态" prop="reviewStatus">
          <el-select
            v-model="searchForm.reviewStatus"
            placeholder="请选择审核状态"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="status in reviewStatusOptions"
              :key="status.value"
              :label="status.label"
              :value="status.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="内容关键词" prop="content">
          <el-input
            v-model="searchForm.content"
            placeholder="请输入内容关键词"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="search-btn" @click="handleSearch">
            <ActionIcon action="search" :size="16" />
            搜索
          </el-button>
          <el-button class="reset-btn" @click="handleReset">
            <ActionIcon action="refresh" :size="16" />
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作区域 -->
    <el-card class="operation-card" shadow="never">
      <div class="operation-header">
        <div class="operation-left">
          <el-button type="primary" class="header-add-btn" @click="handleAdd">
            <ActionIcon action="add" :size="16" />
            <span style="margin-left: 6px;">新增日志</span>
          </el-button>
          <el-button 
            type="success" 
            class="action-btn success"
            :disabled="!multipleSelection.length"
            @click="handleBatchReview('已通过')"
          >
            <ActionIcon action="check" :size="16" />
            <span style="margin-left: 6px;">批量通过</span>
          </el-button>
          <el-button 
            type="danger" 
            class="action-btn danger"
            :disabled="!multipleSelection.length"
            @click="handleBatchReview('已拒绝')"
          >
            <ActionIcon action="close" :size="16" />
            <span style="margin-left: 6px;">批量拒绝</span>
          </el-button>
          <el-button type="info" class="action-btn secondary" @click="handleExport">
            <ActionIcon action="upload" :size="16" />
            <span style="margin-left: 6px;">导出</span>
          </el-button>
        </div>
        <div class="operation-right">
          <el-button-group class="view-mode-group">
            <el-tooltip content="表格视图" placement="top">
              <el-button 
                circle
                class="view-mode-btn"
                :class="{ active: viewMode === 'table' }"
                @click="viewMode = 'table'"
              >
                <ActionIcon action="view" :size="16" />
              </el-button>
            </el-tooltip>
            <el-tooltip content="卡片视图" placement="top">
              <el-button 
                circle
                class="view-mode-btn"
                :class="{ active: viewMode === 'card' }"
                @click="viewMode = 'card'"
              >
                <ActionIcon action="notification" :size="16" />
              </el-button>
            </el-tooltip>
          </el-button-group>
        </div>
      </div>
    </el-card>

    <!-- 工时统计图表 -->
    <el-card class="chart-card" shadow="never">
      <WorkHoursChart :data="workHoursData" />
    </el-card>

    <!-- 数据展示区域 -->
    <el-card class="data-card" shadow="never">
      <!-- 表格视图 -->
      <div v-if="viewMode === 'table'" v-loading="loading">
        <el-table
          :data="workLogList"
          @selection-change="handleSelectionChange"
          style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="taskTitle" label="关联任务" width="200" show-overflow-tooltip />
          <el-table-column prop="userName" label="提交人" width="120" />
          <el-table-column prop="content" label="日志内容" min-width="300" show-overflow-tooltip />
          <el-table-column prop="hours" label="工时(h)" width="100" />
          <el-table-column prop="logDate" label="日志日期" width="120" />
          <el-table-column prop="reviewStatus" label="审核状态" width="120">
            <template #default="{ row }">
              <StatusIcon 
                :status="getStatusType(row.reviewStatus)" 
                :size="20" 
                :animate="row.reviewStatus === '待审核'"
              />
              <span style="margin-left: 8px;">{{ row.reviewStatusText }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="reviewerName" label="审核人" width="120" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <div class="table-actions">
                <el-tooltip content="查看详情" placement="top">
                  <el-button type="info" circle class="action-btn" @click="handleView(row)">
                    <ActionIcon action="view" :size="16" />
                  </el-button>
                </el-tooltip>
                <el-tooltip v-if="row.editable" content="编辑日志" placement="top">
                  <el-button type="primary" circle class="action-btn" @click="handleEdit(row)">
                    <ActionIcon action="edit" :size="16" />
                  </el-button>
                </el-tooltip>
                <el-tooltip v-if="row.reviewStatus === '待审核'" content="审核通过" placement="top">
                  <el-button type="success" circle class="action-btn" @click="handleReview(row, '已通过')">
                    <ActionIcon action="check" :size="16" />
                  </el-button>
                </el-tooltip>
                <el-tooltip v-if="row.reviewStatus === '待审核'" content="审核拒绝" placement="top">
                  <el-button type="warning" circle class="action-btn" @click="handleReview(row, '已拒绝')">
                    <ActionIcon action="close" :size="16" />
                  </el-button>
                </el-tooltip>
                <el-tooltip v-if="row.deletable" content="删除日志" placement="top">
                  <el-button type="danger" circle class="action-btn" @click="handleDelete(row)">
                    <ActionIcon action="delete" :size="16" />
                  </el-button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 卡片视图 -->
      <div v-else v-loading="loading" class="card-view">
        <div class="card-grid">
          <WorkLogCard
            v-for="workLog in workLogList"
            :key="workLog.id"
            :work-log="workLog"
            :selected="selectedWorkLogs.includes(workLog.id)"
            @view="handleView"
            @edit="handleEdit"
            @approve="(log) => handleReview(log, '已通过')"
            @reject="(log) => handleReview(log, '已拒绝')"
            @delete="handleDelete"
            @click="toggleWorkLogSelection(workLog)"
          />
        </div>
        
        <!-- 空状态 -->
        <div v-if="!workLogList.length" class="empty-state">
          <svg width="120" height="120" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="10" stroke="#d9d9d9" stroke-width="2"/>
            <path d="M8 12h8M12 8v8" stroke="#d9d9d9" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <p>暂无工作日志数据</p>
        </div>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; text-align: right"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="关联任务" prop="taskId">
              <el-select
                v-model="form.taskId"
                placeholder="请选择关联任务"
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="task in taskOptions"
                  :key="task.value"
                  :label="task.label"
                  :value="task.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="日志日期" prop="logDate">
              <el-date-picker
                v-model="form.logDate"
                type="date"
                placeholder="请选择日志日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工时(小时)" prop="hours">
              <el-input-number
                v-model="form.hours"
                :min="0.1"
                :max="24"
                :step="0.1"
                :precision="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属小组" prop="groupId">
              <el-select
                v-model="form.groupId"
                placeholder="请选择所属小组"
                style="width: 100%"
              >
                <el-option
                  v-for="group in groupOptions"
                  :key="group.value"
                  :label="group.label"
                  :value="group.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="日志内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="6"
            placeholder="请输入详细的工作内容..."
            maxlength="2000"
            show-word-limit
          />
          <div class="form-hint" v-if="!form.id">
            💡 系统将每30秒自动保存草稿，您也可以手动点击"保存草稿"按钮
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <div class="footer-left">
            <el-button class="draft-btn" @click="saveDraft" v-if="!form.id">
              <ActionIcon action="save" :size="14" />
              保存草稿
            </el-button>
            <el-button class="draft-btn" @click="loadDraft" v-if="hasDraft && !form.id">
              <ActionIcon action="refresh" :size="14" />
              恢复草稿
            </el-button>
          </div>
          <div class="footer-right">
            <el-button class="cancel-btn" @click="dialogVisible = false">
              <ActionIcon action="close" :size="14" />
              取消
            </el-button>
            <el-button type="primary" class="confirm-btn" @click="handleSubmit">
              <ActionIcon action="check" :size="14" />
              确定
            </el-button>
          </div>
        </span>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="工作日志详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志ID">{{ viewData.id }}</el-descriptions-item>
        <el-descriptions-item label="关联任务">{{ viewData.taskTitle }}</el-descriptions-item>
        <el-descriptions-item label="提交人">{{ viewData.userName }}</el-descriptions-item>
        <el-descriptions-item label="工时">{{ viewData.hours }}小时</el-descriptions-item>
        <el-descriptions-item label="日志日期">{{ viewData.logDate }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getReviewStatusType(viewData.reviewStatus)">
            {{ viewData.reviewStatusText }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核人">{{ viewData.reviewerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ viewData.reviewTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ viewData.updateTime }}</el-descriptions-item>
        <el-descriptions-item label="日志内容" :span="2">
          <div style="white-space: pre-wrap;">{{ viewData.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item v-if="viewData.reviewComment" label="审核意见" :span="2">
          <div style="white-space: pre-wrap;">{{ viewData.reviewComment }}</div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="cancel-btn" @click="viewDialogVisible = false">
            <ActionIcon action="close" :size="14" />
            关闭
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      title="审核工作日志"
      width="600px"
    >
      <el-form
        ref="reviewFormRef"
        :model="reviewForm"
        :rules="reviewFormRules"
        label-width="100px"
      >
        <el-form-item label="审核状态" prop="reviewStatus">
          <el-radio-group v-model="reviewForm.reviewStatus">
            <el-radio label="已通过" :value="'已通过'">通过</el-radio>
            <el-radio label="已拒绝" :value="'已拒绝'">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="reviewComment">
          <el-input
            v-model="reviewForm.reviewComment"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="cancel-btn" @click="reviewDialogVisible = false">
            <ActionIcon action="close" :size="14" />
            取消
          </el-button>
          <el-button type="primary" class="confirm-btn" @click="submitReview">
            <ActionIcon action="check" :size="14" />
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { StatusIcon, ActionIcon, WorkLogCard, LoadingIcon, WorkHoursChart } from '@/components/SvgIcons';
import { 
  getWorkLogList, getWorkLogDetail, createWorkLog, updateWorkLog, 
  deleteWorkLog, reviewWorkLog, batchReviewWorkLog 
} from '@/api/worklog';

// 界面控制
const viewMode = ref('table'); // 'table' | 'card'
const loading = ref(false);

// 表格数据
const workLogList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const multipleSelection = ref([]);
const selectedWorkLogs = ref([]); // 卡片模式下的选择

// 审核状态选项
const reviewStatusOptions = [
  { label: '待审核', value: '待审核', type: 'warning' },
  { label: '已通过', value: '已通过', type: 'success' },
  { label: '已拒绝', value: '已拒绝', type: 'danger' }
];

// 搜索表单
const searchFormRef = ref(null);
const searchForm = reactive({
  taskId: '',
  userId: '',
  reviewStatus: '',
  content: ''
});

const dateRange = ref([]);

// 选项数据
const userOptions = ref([]);
const taskOptions = ref([]);
const groupOptions = ref([]);

// 草稿功能相关
const DRAFT_STORAGE_KEY = 'worklog_draft';
const hasDraft = ref(false);
let autoSaveTimer = null;

// 工时统计数据
const workHoursData = ref([]);

// 对话框控制
const dialogVisible = ref(false);
const dialogTitle = ref('');
const viewDialogVisible = ref(false);
const reviewDialogVisible = ref(false);

// 表单数据
const formRef = ref(null);
const form = reactive({
  id: null,
  taskId: '',
  content: '',
  hours: 1,
  logDate: '',
  groupId: ''
});

const formRules = {
  taskId: [{ required: true, message: '请选择关联任务', trigger: 'change' }],
  content: [{ required: true, message: '请输入日志内容', trigger: 'blur' }],
  hours: [{ required: true, message: '请输入工时', trigger: 'blur' }],
  logDate: [{ required: true, message: '请选择日志日期', trigger: 'change' }],
  groupId: [{ required: true, message: '请选择所属小组', trigger: 'change' }]
};

// 查看详情数据
const viewData = ref({});

// 审核表单
const reviewFormRef = ref(null);
const reviewForm = reactive({
  reviewStatus: '已通过',
  reviewComment: ''
});

const reviewFormRules = {
  reviewComment: [
    { 
      required: true, 
      message: '请输入审核意见', 
      trigger: 'blur' 
    }
  ]
};

// 当前审核的日志
const currentReviewLog = ref(null);
const batchReviewIds = ref([]);

// 获取审核状态类型
const getReviewStatusType = (status) => {
  const statusMap = {
    '待审核': 'warning',
    '已通过': 'success',
    '已拒绝': 'danger'
  };
  return statusMap[status] || 'info';
};

// 获取SVG状态类型
const getStatusType = (reviewStatus) => {
  const statusMap = {
    '待审核': 'pending',
    '已通过': 'approved',
    '已拒绝': 'rejected'
  };
  return statusMap[reviewStatus] || 'default';
};

// 切换工作日志选择（卡片模式）
const toggleWorkLogSelection = (workLog) => {
  const index = selectedWorkLogs.value.indexOf(workLog.id);
  if (index > -1) {
    selectedWorkLogs.value.splice(index, 1);
  } else {
    selectedWorkLogs.value.push(workLog.id);
  }
  // 同步更新multipleSelection以保持批量操作功能
  multipleSelection.value = workLogList.value.filter(log => 
    selectedWorkLogs.value.includes(log.id)
  );
};

// 获取工作日志列表
const getWorkLogData = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm
    };
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0];
      params.endDate = dateRange.value[1];
    }
    
    const response = await getWorkLogList(params);
    if (response.code === 200) {
      workLogList.value = response.data.records;
      total.value = response.data.total;
      
      // 加载完工作日志后更新工时统计
      await updateWorkHoursStatistics();
    }
  } catch (error) {
    console.error('获取工作日志列表失败:', error);
    ElMessage.error('获取工作日志列表失败');
  } finally {
    loading.value = false;
  }
};

// 更新工时统计
const updateWorkHoursStatistics = async () => {
  try {
    // 获取当前用户的工时统计数据
    const userInfo = JSON.parse(localStorage.getItem('loginUser') || '{}');
    const currentUserId = userInfo.id;
    
    // 计算最近7天的工时统计
    const statistics = await calculateWorkHoursStatistics(currentUserId);
    workHoursData.value = statistics;
  } catch (error) {
    console.error('更新工时统计失败:', error);
    // 使用默认数据作为后备
    workHoursData.value = generateDefaultWorkHours();
  }
};

// 计算工时统计
const calculateWorkHoursStatistics = async (userId) => {
  const endDate = new Date();
  const startDate = new Date();
  startDate.setDate(endDate.getDate() - 6); // 最近7天
  
  try {
    // 获取指定时间范围内的工作日志
    const response = await getWorkLogList({
      userId: userId,
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0],
      reviewStatus: '已通过', // 只统计已通过审核的工作日志
      pageSize: 1000 // 获取所有数据
    });
    
    if (response.code === 200) {
      const workLogs = response.data.records || [];
      
      // 按日期分组统计工时
      const hoursMap = new Map();
      const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      
      // 初始化最近7天的数据
      for (let i = 0; i < 7; i++) {
        const date = new Date(startDate);
        date.setDate(date.getDate() + i);
        const dateKey = date.toISOString().split('T')[0];
        const dayName = weekDays[date.getDay()];
        hoursMap.set(dateKey, { date: dayName, hours: 0, dateKey });
      }
      
      // 统计实际工时
      workLogs.forEach(log => {
        const logDate = log.logDate;
        if (hoursMap.has(logDate)) {
          const dayData = hoursMap.get(logDate);
          dayData.hours += parseFloat(log.hours || 0);
        }
      });
      
      // 转换为数组并保留一位小数
      return Array.from(hoursMap.values())
        .map(item => ({
          ...item,
          hours: Math.round(item.hours * 10) / 10
        }))
        .sort((a, b) => a.dateKey.localeCompare(b.dateKey));
    }
  } catch (error) {
    console.error('计算工时统计失败:', error);
  }
  
  // 如果计算失败，返回默认数据
  return generateDefaultWorkHours();
};

// 生成默认工时数据
const generateDefaultWorkHours = () => {
  const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
  return weekDays.map((day, index) => ({
    date: day,
    hours: Math.random() * 5 + 4, // 4-9小时随机数
    dateKey: new Date(Date.now() - (6-index) * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  }));
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  getWorkLogData();
};

// 重置搜索
const handleReset = () => {
  searchFormRef.value?.resetFields();
  dateRange.value = [];
  currentPage.value = 1;
  getWorkLogData();
};

// 页码改变
const handleCurrentChange = (page) => {
  currentPage.value = page;
  getWorkLogData();
};

// 页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  getWorkLogData();
};

// 多选
const handleSelectionChange = (selection) => {
  multipleSelection.value = selection;
};

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增工作日志';
  resetForm();
  dialogVisible.value = true;
  startAutoSave(); // 开启自动保存
};

// 编辑
const handleEdit = async (row) => {
  dialogTitle.value = '编辑工作日志';
  try {
    const response = await getWorkLogDetail(row.id);
    if (response.code === 200) {
      Object.assign(form, response.data);
      dialogVisible.value = true;
    }
  } catch (error) {
    console.error('获取工作日志详情失败:', error);
    ElMessage.error('获取工作日志详情失败');
  }
};

// 查看详情
const handleView = async (row) => {
  try {
    const response = await getWorkLogDetail(row.id);
    if (response.code === 200) {
      viewData.value = response.data;
      viewDialogVisible.value = true;
    }
  } catch (error) {
    console.error('获取工作日志详情失败:', error);
    ElMessage.error('获取工作日志详情失败');
  }
};

// 审核
const handleReview = (row, status) => {
  currentReviewLog.value = row;
  reviewForm.reviewStatus = status;
  reviewForm.reviewComment = '';
  reviewDialogVisible.value = true;
};

// 批量审核
const handleBatchReview = (status) => {
  if (!multipleSelection.value.length) {
    ElMessage.warning('请选择要审核的工作日志');
    return;
  }
  
  batchReviewIds.value = multipleSelection.value.map(item => item.id);
  reviewForm.reviewStatus = status;
  reviewForm.reviewComment = '';
  reviewDialogVisible.value = true;
};

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除这条工作日志吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await deleteWorkLog(row.id);
      if (response.code === 200) {
        ElMessage.success('删除成功');
        await getWorkLogData(); // 删除日志后需要更新工时统计
      }
    } catch (error) {
      console.error('删除工作日志失败:', error);
      ElMessage.error('删除工作日志失败');
    }
  });
};

// 导出
const handleExport = () => {
  ElMessage.info('导出功能开发中...');
};

// 重置表单
const resetForm = () => {
  form.id = null;
  form.taskId = '';
  form.content = '';
  form.hours = 1;
  form.logDate = '';
  form.groupId = '';
  formRef.value?.clearValidate();
};

// 对话框关闭
const handleDialogClose = () => {
  stopAutoSave(); // 停止自动保存
  resetForm();
};

// 草稿保存
const saveDraft = () => {
  if (!form.content.trim() && !form.taskId) {
    ElMessage.warning('请先填写一些内容再保存草稿');
    return;
  }
  
  const draftData = {
    taskId: form.taskId,
    content: form.content,
    hours: form.hours,
    logDate: form.logDate,
    groupId: form.groupId,
    savedAt: new Date().toISOString()
  };
  
  localStorage.setItem(DRAFT_STORAGE_KEY, JSON.stringify(draftData));
  hasDraft.value = true;
  ElMessage.success('草稿已保存');
};

// 加载草稿
const loadDraft = () => {
  const draftDataStr = localStorage.getItem(DRAFT_STORAGE_KEY);
  if (draftDataStr) {
    try {
      const draftData = JSON.parse(draftDataStr);
      
      ElMessageBox.confirm(
        `发现草稿（保存于：${new Date(draftData.savedAt).toLocaleString()}），是否恢复？`,
        '恢复草稿',
        {
          confirmButtonText: '恢复',
          cancelButtonText: '取消',
          type: 'info'
        }
      ).then(() => {
        form.taskId = draftData.taskId || '';
        form.content = draftData.content || '';
        form.hours = draftData.hours || 1;
        form.logDate = draftData.logDate || '';
        form.groupId = draftData.groupId || '';
        
        ElMessage.success('草稿已恢复');
      }).catch(() => {
        // 用户取消，不做任何操作
      });
    } catch (error) {
      console.error('解析草稿数据失败:', error);
      ElMessage.error('草稿数据损坏，无法恢复');
    }
  }
};

// 清除草稿
const clearDraft = () => {
  localStorage.removeItem(DRAFT_STORAGE_KEY);
  hasDraft.value = false;
};

// 检查是否有草稿
const checkDraft = () => {
  const draftDataStr = localStorage.getItem(DRAFT_STORAGE_KEY);
  hasDraft.value = !!draftDataStr;
};

// 自动保存草稿
const autoSaveDraft = () => {
  // 只在新增时自动保存，编辑时不自动保存
  if (form.id || (!form.content.trim() && !form.taskId)) {
    return;
  }
  
  const draftData = {
    taskId: form.taskId,
    content: form.content,
    hours: form.hours,
    logDate: form.logDate,
    groupId: form.groupId,
    savedAt: new Date().toISOString(),
    autoSaved: true
  };
  
  localStorage.setItem(DRAFT_STORAGE_KEY, JSON.stringify(draftData));
  hasDraft.value = true;
};

// 开启自动保存
const startAutoSave = () => {
  if (autoSaveTimer) {
    clearInterval(autoSaveTimer);
  }
  // 每30秒自动保存一次
  autoSaveTimer = setInterval(() => {
    autoSaveDraft();
  }, 30000);
};

// 停止自动保存
const stopAutoSave = () => {
  if (autoSaveTimer) {
    clearInterval(autoSaveTimer);
    autoSaveTimer = null;
  }
};

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate();
  if (!valid) return;
  
  try {
    let response;
    if (form.id) {
      response = await updateWorkLog(form.id, form);
    } else {
      response = await createWorkLog(form);
    }
    
    if (response.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '创建成功');
      // 成功提交后清除草稿
      if (!form.id) {
        clearDraft();
      }
      dialogVisible.value = false;
      await getWorkLogData();
      // 不需要单独调用updateWorkHoursStatistics，getWorkLogData中已经包含了
    }
  } catch (error) {
    console.error('提交工作日志失败:', error);
    ElMessage.error('提交工作日志失败');
  }
};

// 提交审核
const submitReview = async () => {
  const valid = await reviewFormRef.value?.validate();
  if (!valid) return;
  
  try {
    let response;
    if (batchReviewIds.value.length > 0) {
      // 批量审核
      response = await batchReviewWorkLog(batchReviewIds.value, reviewForm);
    } else {
      // 单个审核
      response = await reviewWorkLog(currentReviewLog.value.id, reviewForm);
    }
    
    if (response.code === 200) {
      ElMessage.success('审核成功');
      reviewDialogVisible.value = false;
      await getWorkLogData(); // 审核状态改变会影响工时统计
      // 重置状态
      currentReviewLog.value = null;
      batchReviewIds.value = [];
    }
  } catch (error) {
    console.error('审核工作日志失败:', error);
    ElMessage.error('审核工作日志失败');
  }
};

// 初始化选项数据
const initOptions = () => {
  // 模拟数据，实际应该从API获取
  userOptions.value = [
    { label: '张三', value: 1 },
    { label: '李四', value: 2 },
    { label: '王五', value: 3 }
  ];
  
  taskOptions.value = [
    { label: '任务1 - 登录功能开发', value: 1 },
    { label: '任务2 - 用户管理模块', value: 2 },
    { label: '任务3 - 权限系统设计', value: 3 }
  ];
  
  groupOptions.value = [
    { label: '前端组', value: 1 },
    { label: '后端组', value: 2 },
    { label: '测试组', value: 3 }
  ];
};

// 监听日期范围变化
watch(dateRange, (newVal) => {
  if (!newVal || newVal.length === 0) {
    searchForm.startDate = '';
    searchForm.endDate = '';
  }
});

// 组件挂载
onMounted(async () => {
  await getWorkLogData();
  initOptions();
  checkDraft(); // 检查是否有草稿
  
  // 如果获取工作日志失败，单独加载工时统计
  if (workHoursData.value.length === 0) {
    await updateWorkHoursStatistics();
  }
});
</script>

<style scoped>
.worklog-container {
  padding: 20px;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  min-height: 100vh;
}

.search-card,
.operation-card,
.data-card,
.chart-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: 1px solid rgba(148, 163, 184, 0.2);
  background: rgba(248, 250, 252, 0.8);
  backdrop-filter: blur(10px);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.operation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.operation-left {
  display: flex;
  gap: 12px;
}

.operation-right {
  display: flex;
  align-items: center;
}

.table-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.card-view {
  min-height: 400px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #64748b;
}

.empty-state p {
  margin-top: 16px;
  font-size: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.footer-left {
  display: flex;
  gap: 8px;
}

.footer-right {
  display: flex;
  gap: 12px;
}

.draft-btn {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border: none;
  color: white;
  font-size: 13px;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.draft-btn:hover {
  background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(217, 119, 6, 0.3);
}

.form-hint {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
  line-height: 1.4;
  opacity: 0.8;
}

:deep(.el-table .cell) {
  word-break: break-word;
}

:deep(.el-card__body) {
  padding: 24px;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  border-color: rgba(148, 163, 184, 0.3);
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #334155;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-date-editor--daterange.el-input) {
  border-radius: 8px;
}

:deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
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

.action-btn {
  border-radius: 12px;
  font-weight: 500;
  padding: 8px 16px;
  gap: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    transform: translateY(-1px);
  }

  &.success {
    background: #34C759;
    border-color: #34C759;
    
    &:hover {
      background: #30B351;
      box-shadow: 0 2px 8px rgba(52, 199, 89, 0.25);
    }
  }

  &.danger {
    background: #FF3B30;
    border-color: #FF3B30;
    
    &:hover {
      background: #E62117;
      box-shadow: 0 2px 8px rgba(255, 59, 48, 0.25);
    }
  }

  &.secondary {
    background: #8E8E93;
    border-color: #8E8E93;
    
    &:hover {
      background: #6D6D70;
      box-shadow: 0 2px 8px rgba(142, 142, 147, 0.25);
    }
  }

   /*圆形按钮样式*/
  &.el-button--info.is-circle {
    width: 36px;
    height: 36px;
    background: #8E8E93;
    border-color: #8E8E93;
    
    &:hover {
      background: #6D6D70;
      transform: scale(1.05);
    }
  }

  &.el-button--primary.is-circle {
    width: 36px;
    height: 36px;
    background: #007AFF;
    border-color: #007AFF;
    
    &:hover {
      background: #0056CC;
      transform: scale(1.05);
    }
  }

  &.el-button--success.is-circle {
    width: 36px;
    height: 36px;
    background: #34C759;
    border-color: #34C759;
    
    &:hover {
      background: #30B351;
      transform: scale(1.05);
    }
  }

  &.el-button--warning.is-circle {
    width: 36px;
    height: 36px;
    background: #FF9500;
    border-color: #FF9500;
    
    &:hover {
      background: #E6860A;
      transform: scale(1.05);
    }
  }

  &.el-button--danger.is-circle {
    width: 36px;
    height: 36px;
    background: #FF3B30;
    border-color: #FF3B30;
    
    &:hover {
      background: #E62117;
      transform: scale(1.05);
    }
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

.view-mode-group {
  .view-mode-btn {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    padding: 0;
    margin: 0 2px;
    transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
    background: #F2F2F7;
    color: #6E6E73;
    border-color: #D1D1D6;
    
    &:hover {
      transform: scale(1.05);
      background: #E5E5EA;
    }
    
    &.active {
      background: #007AFF;
      color: white;
      border-color: #007AFF;
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

/* 响应式设计 */
@media (max-width: 1200px) {
  .card-grid {
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .worklog-container {
    padding: 16px;
  }
  
  .operation-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .operation-left {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .card-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .worklog-container {
    padding: 12px;
  }
  
  :deep(.el-form--inline .el-form-item) {
    display: block;
    margin-bottom: 12px;
  }
  
  :deep(.el-form-item__content) {
    margin-left: 0 !important;
  }
}
</style>