<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus, Edit, Delete, Refresh, Calendar, User, Folder, Upload, Document, Picture, Bell, Close, Check } from '@element-plus/icons-vue';
import { getTaskList, createTask, getTaskDetail, updateTask, deleteTask, updateTaskProgress, recalculateTaskProgress, batchDeleteTask, updateTaskStatus } from '@/api/task';
import { uploadTaskAttachmentApi, getTaskAttachmentsApi, deleteTaskAttachmentApi, downloadTaskAttachmentApi } from '@/api/taskAttachment';
import { getUserList } from '@/api/user';
import { getGroupList } from '@/api/group';
import NotificationQuickSend from '@/components/Notification/NotificationQuickSend.vue';
import TaskRequirementManager from '@/components/TaskRequirement/TaskRequirementManager.vue';
import TaskProgressCard from '@/components/Task/TaskProgressCard.vue';
import TaskDependencyManager from '@/components/Task/TaskDependencyManager.vue';
import TaskGanttChart from '@/components/Task/TaskGanttChart.vue';
import TaskTemplateManager from '@/components/Task/TaskTemplateManager.vue';
import { ActionIcon, SkillIcons } from '@/components/SvgIcons';

// 表格数据
const loading = ref(false);
const taskList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 批量操作
const tableRef = ref(null);
const selectedTaskIds = ref([]);
const batchStatusDialogVisible = ref(false);
const batchStatus = ref('');

// 状态选项
const statusOptions = [
  { label: '待处理', value: '待处理', type: 'info' },
  { label: '进行中', value: '进行中', type: 'warning' },
  { label: '已完成', value: '已完成', type: 'success' }
];

// 优先级选项
const priorityOptions = [
  { label: '高', value: '高', type: 'danger' },
  { label: '中', value: '中', type: 'warning' },
  { label: '低', value: '低', type: 'info' }
];

// 搜索表单
const searchForm = reactive({
  title: '',
  status: '',
  priority: '',
  assigneeId: ''
});

// 负责人选项（管理员和组长）
const assigneeOptions = ref([]);

// 工作组选项
const groupOptions = ref([]);

// 表单数据
const formRef = ref(null);
const form = reactive({
  id: null,
  title: '',
  description: '',
  status: '待处理',
  priority: '中',
  projectId: 1,
  creatorId: 1,
  assigneeId: '',
  departmentId: 1,
  groupId: 1,
  startTime: '',
  deadline: '',
  estimatedHours: 0,
  actualHours: 0,
  progress: 0,
  dependTaskId: ''
});

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入任务标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入任务描述', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  deadline: [
    { required: true, message: '请选择截止时间', trigger: 'change' }
  ]
};

// 对话框可见状态
const dialogFormVisible = ref(false);
// 对话框标题
const dialogTitle = ref('');

// 附件相关数据
const attachmentList = ref([]);
const uploadLoading = ref(false);
const uploadRef = ref(null);

// 通知相关
const quickSendRef = ref(null);

// 技能需求相关
const requirementDialogVisible = ref(false);
const currentTask = ref(null);

// 任务依赖关系相关
const dependencyDialogVisible = ref(false);
const currentDependencyTask = ref(null);

// 甘特图相关
const ganttChartVisible = ref(false);

// 任务模板相关
const templateManagerVisible = ref(false);

// 文件类型限制
const allowedFileTypes = [
  // 图片文件
  'image/jpeg',
  'image/png',
  'image/gif',
  'image/bmp',
  'image/webp',
  // 文档文件
  'application/pdf',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  'application/vnd.ms-excel',
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  'text/plain',
  'application/zip',
  'application/x-rar-compressed'
];

// 文件大小限制（10MB）
const maxFileSize = 10 * 1024 * 1024;

// 获取文件类型图标
const getFileIcon = (fileType) => {
  if (fileType.startsWith('image/')) {
    return 'Picture';
  } else if (fileType.includes('pdf')) {
    return 'Document';
  } else if (fileType.includes('word')) {
    return 'Document';
  } else if (fileType.includes('excel')) {
    return 'Document';
  } else if (fileType.includes('text')) {
    return 'Document';
  } else if (fileType.includes('zip') || fileType.includes('rar')) {
    return 'Folder';
  }
  return 'Document';
};

// 格式化文件大小
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + 'B';
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + 'KB';
  } else {
    return (size / (1024 * 1024)).toFixed(2) + 'MB';
  }
};

// 上传前校验
const beforeUpload = (file) => {
  // 检查文件类型
  if (!allowedFileTypes.includes(file.type)) {
    ElMessage.error('不支持的文件类型！');
    return false;
  }
  
  // 检查文件大小
  if (file.size > maxFileSize) {
    ElMessage.error('文件大小不能超过10MB！');
    return false;
  }
  
  return true;
};

// 获取用户信息
const getUserInfo = () => {
  const userInfo = localStorage.getItem('loginUser');
  return userInfo ? JSON.parse(userInfo) : null;
};

// 查询任务列表  
const loadTasks = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      title: searchForm.title || undefined,
      status: searchForm.status || undefined,
      priority: searchForm.priority || undefined,
      assigneeId: searchForm.assigneeId ? Number(searchForm.assigneeId) : undefined
    };
    
    // 移除所有undefined的参数
    Object.keys(params).forEach(key => {
      if (params[key] === undefined) {
        delete params[key];
      }
    });
    
    const response = await getTaskList(params);
    if (response.code === 200) {
      taskList.value = response.data.records;
      total.value = response.data.total;
    } else {
      ElMessage.error(response.message || '查询失败');
    }
  } catch (error) {
    console.error('查询任务列表失败:', error);
    ElMessage.error('查询任务列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  loadTasks();
};

// 重置搜索
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = '';
  });
  handleSearch();
};

// 新增
const handleAdd = () => {
  dialogTitle.value = '新建任务';
  Object.keys(form).forEach(key => {
    if (key === 'status') form[key] = '待处理';
    else if (key === 'priority') form[key] = '中';
    else if (key === 'progress') form[key] = 0;
    else if (key === 'creatorId') form[key] = 1;
    else form[key] = '';
  });
  form.id = null;
  // 清空附件列表
  attachmentList.value = [];
  dialogFormVisible.value = true;
};

// 编辑
const handleEdit = async (id) => {
  dialogTitle.value = '编辑任务';
  try {
    const res = await getTaskDetail(id);
    if (res.code === 200) {
      const taskData = res.data;
      Object.keys(form).forEach(key => {
        if (key !== 'id') {
          form[key] = taskData[key] ?? '';
        }
      });
      form.id = Number(taskData.id);
      dialogFormVisible.value = true;
      // 获取附件列表
      fetchAttachments(form.id);
    } else {
      ElMessage.error(res.message || '获取任务详情失败');
    }
  } catch (error) {
    console.error('获取任务详情失败:', error);
    ElMessage.error('获取任务详情失败');
  }
};

// 删除任务及其附件
const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除该任务吗？删除后将同时删除所有相关附件', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 先获取任务的所有附件
      const attachmentsRes = await getTaskAttachmentsApi(id);
      if (attachmentsRes.code === 200 && attachmentsRes.data.length > 0) {
        // 删除所有附件
        const userInfo = getUserInfo();
        if (!userInfo || !userInfo.id) {
          ElMessage.error('用户信息不存在，请重新登录');
          return;
        }
        
        // 使用Promise.all并行删除所有附件
        await Promise.all(
          attachmentsRes.data.map(attachment => 
            deleteTaskAttachmentApi(attachment.id, userInfo.id)
          )
        );
      }
      
      // 删除任务
      const response = await deleteTask(id);
      if (response.code === 200) {
        ElMessage.success('删除成功');
        loadTasks();
      } else {
        ElMessage.error(response.message || '删除失败');
      }
    } catch (error) {
      console.error('删除任务失败:', error);
      ElMessage.error('删除失败');
    }
  });
};

// 发送任务通知
const handleSendNotification = (task) => {
  if (!quickSendRef.value) return;
  
  // 获取任务负责人信息
  const assignee = assigneeOptions.value.find(user => user.id === task.assigneeId);
  const receivers = assignee ? [assignee] : [];
  
  // 打开发送通知表单，预设任务相关信息
  quickSendRef.value.openSendForm({
    type: 'TASK_ASSIGN',
    receivers: receivers,
    related: {
      type: 'task',
      id: task.id
    }
  });
};

// 处理技能需求管理
const handleManageRequirements = (task) => {
  currentTask.value = task;
  requirementDialogVisible.value = true;
};

// 处理任务依赖关系管理
const handleManageDependencies = (task) => {
  currentDependencyTask.value = task;
  dependencyDialogVisible.value = true;
};

// 打开甘特图
const openGanttChart = () => {
  ganttChartVisible.value = true;
};

// 打开任务模板管理
const openTemplateManager = () => {
  templateManagerVisible.value = true;
};

// 甘特图任务点击处理
const handleGanttTaskClick = (task) => {
  console.log('甘特图任务点击:', task);
};

// 甘特图编辑任务
const handleGanttEditTask = (task) => {
  handleEdit(task.id);
  ganttChartVisible.value = false;
};

// 甘特图管理依赖关系
const handleGanttManageDependencies = (task) => {
  handleManageDependencies(task);
  ganttChartVisible.value = false;
};

// 模板应用处理
const handleTemplateApplied = (data) => {
  ElMessage.success(`已从模板"${data.template.name}"创建 ${data.tasks.length} 个任务`);
  templateManagerVisible.value = false;
  loadTasks(); // 刷新任务列表
};

// 技能需求更新回调
const handleRequirementUpdated = () => {
  // 技能需求更新后可以刷新任务列表或其他操作
  console.log('任务技能需求已更新');
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
    const submitData = {
      id: form.id ? Number(form.id) : null,
      title: form.title,
      description: form.description,
      status: form.status,
      priority: form.priority,
      projectId: form.projectId ? Number(form.projectId) : null,
      creatorId: form.creatorId ? Number(form.creatorId) : 1,
      assigneeId: form.assigneeId ? Number(form.assigneeId) : null,
      departmentId: form.departmentId ? Number(form.departmentId) : 1,
      groupId: form.groupId ? Number(form.groupId) : null,
      startTime: form.startTime || null,
      deadline: form.deadline || null,
      estimatedHours: form.estimatedHours ? Number(form.estimatedHours) : 0,
      actualHours: form.actualHours ? Number(form.actualHours) : 0,
      progress: form.progress ? Number(form.progress) : 0,
      dependTaskId: form.dependTaskId ? Number(form.dependTaskId) : null
    };
    
    let response;
    if (form.id) {
      response = await updateTask(submitData.id, submitData);
    } else {
      response = await createTask(submitData);
    }
    
    if (response.code === 200) {
      ElMessage.success(response.message || '操作成功');
      dialogFormVisible.value = false;
      loadTasks();
    } else {
      ElMessage.error(response.message || '操作失败');
    }
  } catch (error) {
    console.error('操作失败:', error);
    ElMessage.error('操作失败');
  }
};

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    '待处理': 'info',
    '进行中': 'warning',
    '已完成': 'success'
  };
  return map[status] || 'info';
};

// 获取优先级类型
const getPriorityType = (priority) => {
  const map = {
    '高': 'danger',
    '中': 'warning',
    '低': 'info'
  };
  return map[priority] || 'info';
};

// 获取进度状态
const getProgressStatus = (progress) => {
  if (progress === 100) return 'success';
  if (progress >= 80) return 'warning';
  return '';
};

// 格式化日期显示
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 获取截止日期样式类
const getDeadlineClass = (deadline) => {
  if (!deadline) return '';
  const date = new Date(deadline);
  const now = new Date();
  const diffTime = date - now;
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  
  if (diffDays < 0) return 'deadline-overdue';
  if (diffDays <= 3) return 'deadline-urgent';
  if (diffDays <= 7) return 'deadline-warning';
  return 'deadline-normal';
};

// 获取表格行样式
const getRowStyle = ({ row }) => {
  return {
    transition: 'all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1)',
    '--row-status': row.status,
    '--row-priority': row.priority
  };
};

// 获取表格行类名
const getRowClassName = ({ row }) => {
  let className = '';
  
  // 添加截止日期警告类
  if (row.deadline) {
    const deadline = getDeadlineClass(row.deadline);
    if (deadline === 'deadline-urgent' || deadline === 'deadline-overdue') {
      className += 'deadline-urgent ';
    }
  }
  
  // 添加状态类
  className += `status-${row.status.replace(/\s/g, '-')} `;
  
  // 添加优先级类  
  className += `priority-${row.priority}`;
  
  return className.trim();
};

// 处理分页
const handleSizeChange = (val) => {
  pageSize.value = val;
  loadTasks();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  loadTasks();
};

// 获取任务附件列表
const fetchAttachments = async (taskId) => {
  try {
    const res = await getTaskAttachmentsApi(taskId);
    if (res.code === 200) {
      console.log('附件列表数据:', res.data);
      // 确保文件类型正确
      attachmentList.value = res.data.map(item => ({
        ...item,
        fileType: item.fileType || getFileTypeFromName(item.fileName)
      }));
    } else {
      ElMessage.error(res.message || '获取附件列表失败');
    }
  } catch (error) {
    console.error('获取附件列表失败:', error);
    ElMessage.error('获取附件列表失败');
  }
};

// 根据文件名获取文件类型
const getFileTypeFromName = (fileName) => {
  const extension = fileName.split('.').pop().toLowerCase();
  const typeMap = {
    'jpg': 'image/jpeg',
    'jpeg': 'image/jpeg',
    'png': 'image/png',
    'gif': 'image/gif',
    'bmp': 'image/bmp',
    'webp': 'image/webp',
    'pdf': 'application/pdf',
    'doc': 'application/msword',
    'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'xls': 'application/vnd.ms-excel',
    'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'txt': 'text/plain',
    'zip': 'application/zip',
    'rar': 'application/x-rar-compressed'
  };
  return typeMap[extension] || 'application/octet-stream';
};

// 上传附件
const handleUpload = async (file) => {
  if (!form.id) {
    ElMessage.warning('请先保存任务信息');
    return false;
  }
  
  if (!beforeUpload(file.raw)) {
    return false;
  }
  
  const userInfo = getUserInfo();
  if (!userInfo || !userInfo.id) {
    ElMessage.error('用户信息不存在，请重新登录');
    return false;
  }
  
  uploadLoading.value = true;
  try {
    const formData = new FormData();
    formData.append('file', file.raw);
    formData.append('uploaderId', userInfo.id);
    
    const res = await uploadTaskAttachmentApi(form.id, formData);
    if (res.code === 200) {
      ElMessage.success('上传成功');
      fetchAttachments(form.id);
    } else {
      ElMessage.error(res.message || '上传失败');
    }
  } catch (error) {
    console.error('上传附件失败:', error);
    ElMessage.error('上传失败');
  } finally {
    uploadLoading.value = false;
  }
  return false;
};

// 预览附件
const handlePreview = (file) => {
  if (file.fileType.startsWith('image/')) {
    // 图片预览使用el-image组件
    return;
  } else {
    // 其他文件下载
    handleDownload(file);
  }
};

// 下载附件
const handleDownload = (file) => {
  // 直接使用浏览器下载
  window.open(`/api/task-attachments/download/${file.id}`, '_blank');
};

// 删除附件
const handleDeleteAttachment = (id) => {
  ElMessageBox.confirm('确定删除该附件吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const userInfo = getUserInfo();
      const res = await deleteTaskAttachmentApi(id, userInfo.id);
      if (res.code === 200) {
        ElMessage.success('删除成功');
        fetchAttachments(form.id);
      } else {
        ElMessage.error(res.message || '删除失败');
      }
    } catch (error) {
      console.error('删除附件失败:', error);
      ElMessage.error('删除失败');
    }
  });
};

// TaskProgressCard 相关处理方法
const handleUpdateProgress = (row) => {
  ElMessageBox.prompt('请输入新的进度百分比 (0-100)', '更新任务进度', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^(?:100|\d{1,2})$/,
    inputErrorMessage: '请输入0-100之间的数字',
    inputValue: row.progress?.toString() || '0'
  }).then(async ({ value }) => {
    try {
      const progressValue = parseInt(value);
      await updateTaskProgress(row.id, progressValue);
      ElMessage.success('进度更新成功');
      loadTasks();
    } catch (error) {
      console.error('更新进度失败:', error);
      ElMessage.error('更新进度失败');
    }
  }).catch(() => {
    // 用户取消操作
  });
};

const handleRecalculateProgress = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要重新计算任务进度吗？系统将根据已通过审核的工作日志重新计算进度。',
      '确认重新计算',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    await recalculateTaskProgress(row.id);
    ElMessage.success('进度重新计算成功');
    loadTasks();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重新计算进度失败:', error);
      ElMessage.error('重新计算进度失败');
    }
  }
};

// 根据用户ID获取用户姓名
const getAssigneeName = (assigneeId) => {
  if (!assigneeId) return '未分配';
  const assignee = assigneeOptions.value.find(item => item.value === assigneeId);
  return assignee ? assignee.label : '未知用户';
};

// 批量操作相关方法
const handleSelectionChange = (selection) => {
  selectedTaskIds.value = selection.map(item => item.id);
};

const clearSelection = () => {
  if (tableRef.value) {
    tableRef.value.clearSelection();
  }
  selectedTaskIds.value = [];
};

const handleBatchDelete = () => {
  if (selectedTaskIds.value.length === 0) {
    ElMessage.warning('请先选择要删除的任务');
    return;
  }

  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedTaskIds.value.length} 个任务吗？删除后无法恢复。`,
    '批量删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: true
    }
  ).then(async () => {
    try {
      const response = await batchDeleteTask(selectedTaskIds.value);
      if (response.code === 200) {
        ElMessage.success(`成功删除 ${selectedTaskIds.value.length} 个任务`);
        clearSelection();
        loadTasks();
      } else {
        ElMessage.error('批量删除失败：' + response.message);
      }
    } catch (error) {
      console.error('批量删除失败:', error);
      ElMessage.error('批量删除失败');
    }
  });
};

const handleBatchStatusChange = () => {
  if (selectedTaskIds.value.length === 0) {
    ElMessage.warning('请先选择要修改状态的任务');
    return;
  }
  batchStatus.value = '';
  batchStatusDialogVisible.value = true;
};

const confirmBatchStatusChange = async () => {
  if (!batchStatus.value) {
    ElMessage.warning('请选择要修改的状态');
    return;
  }

  try {
    const promises = selectedTaskIds.value.map(id => 
      updateTaskStatus(id, batchStatus.value)
    );
    
    await Promise.all(promises);
    ElMessage.success(`成功修改 ${selectedTaskIds.value.length} 个任务状态`);
    batchStatusDialogVisible.value = false;
    clearSelection();
    loadTasks();
  } catch (error) {
    console.error('批量修改状态失败:', error);
    ElMessage.error('批量修改状态失败');
  }
};

// 获取负责人选项（管理员和组长）
const fetchAssigneeOptions = async () => {
  try {
    // 获取角色为管理员(1)和组长(2)的用户
    const [adminResponse, leaderResponse] = await Promise.all([
      getUserList({ roleId: 1, pageSize: 1000 }), // 管理员
      getUserList({ roleId: 2, pageSize: 1000 })  // 组长
    ]);
    
    const adminUsers = adminResponse.data?.records || [];
    const leaderUsers = leaderResponse.data?.records || [];
    
    // 合并管理员和组长，去重
    const allUsers = [...adminUsers, ...leaderUsers];
    const uniqueUsers = allUsers.filter((user, index, self) => 
      index === self.findIndex(u => u.id === user.id)
    );
    
    assigneeOptions.value = uniqueUsers.map(user => ({
      label: user.realName || user.username,
      value: user.id
    }));
  } catch (error) {
    console.error('获取负责人选项失败:', error);
    ElMessage.error('获取负责人选项失败');
  }
};

// 获取工作组选项
const fetchGroupOptions = async () => {
  try {
    const response = await getGroupList();
    groupOptions.value = (response.data || []).map(group => ({
      label: group.name,
      value: group.id
    }));
  } catch (error) {
    console.error('获取工作组选项失败:', error);
    ElMessage.error('获取工作组选项失败');
  }
};

// 初始化
onMounted(() => {
  loadTasks();
  fetchAssigneeOptions();
  fetchGroupOptions();
});
</script>

<template>
  <div class="task-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">任务管理</span>
            <el-input
              v-model="searchForm.title"
              placeholder="请输入任务标题搜索"
              class="search-input"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          <div class="header-actions">
            <el-button type="warning" class="template-btn" @click="openTemplateManager">
              <ActionIcon action="template" :size="16" />
              任务模板
            </el-button>
            <el-button type="info" class="gantt-btn" @click="openGanttChart">
              <ActionIcon action="timeline" :size="16" />
              甘特图
            </el-button>
            <el-button type="primary" class="header-add-btn" @click="handleAdd">
              <ActionIcon action="add" :size="16" />
              新建任务
            </el-button>
          </div>
        </div>
      </template>

      <div class="filter-container">
        <el-form :model="searchForm" inline>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 180px">
              <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
                <el-tag :type="item.type" size="small">{{ item.label }}</el-tag>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="优先级">
            <el-select v-model="searchForm.priority" placeholder="请选择优先级" clearable style="width: 180px">
              <el-option
                v-for="item in priorityOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
                <el-tag :type="item.type" size="small">{{ item.label }}</el-tag>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="负责人">
            <el-select 
              v-model="searchForm.assigneeId" 
              placeholder="请选择负责人"
              clearable
              style="width: 180px"
            >
              <el-option
                v-for="item in assigneeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
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
      </div>

      <!-- 批量操作工具栏 -->
      <div class="batch-toolbar" v-if="selectedTaskIds.length > 0">
        <div class="batch-info">
          <span class="selection-count">已选择 {{ selectedTaskIds.length }} 项</span>
        </div>
        <div class="batch-actions">
          <el-button 
            type="warning" 
            class="batch-btn"
            @click="handleBatchStatusChange"
          >
            <ActionIcon action="edit" :size="14" />
            批量修改状态
          </el-button>
          <el-button 
            type="danger" 
            class="batch-btn"
            @click="handleBatchDelete"
          >
            <ActionIcon action="delete" :size="14" />
            批量删除
          </el-button>
          <el-button 
            class="batch-btn cancel-btn"
            @click="clearSelection"
          >
            <ActionIcon action="close" :size="14" />
            取消选择
          </el-button>
        </div>
      </div>

      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="taskList"
        style="width: 100%"
        border
        :header-cell-style="{
          background: '#f8f9fa',
          color: '#495057',
          fontWeight: 600,
          fontSize: '14px',
          textAlign: 'center'
        }"
        :cell-style="{
          padding: '12px'
        }"
        :row-style="getRowStyle"
        :row-class-name="getRowClassName"
        stripe
        highlight-current-row
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="任务标题" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="task-title-cell">
              <div class="title-content">
                <div class="task-name">{{ row.title }}</div>
                <div class="task-meta">
                  <el-tag size="small" :type="getPriorityType(row.priority)" class="priority-tag">
                    <ActionIcon 
                      :action="row.priority === '高' ? 'important' : row.priority === '中' ? 'medium' : 'low'" 
                      :size="12" 
                    />
                    {{ row.priority }}
                  </el-tag>
                  <span class="task-id">#{{ row.id }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assigneeId" label="负责人" width="150">
          <template #default="{ row }">
            <div class="assignee-cell">
              <el-avatar :size="32" class="assignee-avatar">
                <template #default>
                  <ActionIcon action="user" :size="16" />
                </template>
              </el-avatar>
              <div class="assignee-info">
                <div class="assignee-name">{{ getAssigneeName(row.assigneeId) }}</div>
                <div class="assignee-role" v-if="row.assigneeId">负责人</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="时间安排" width="200">
          <template #default="{ row }">
            <div class="time-schedule-cell">
              <div class="time-row">
                <ActionIcon action="start" :size="12" class="time-icon" />
                <span class="time-label">开始:</span>
                <span class="time-value">{{ formatDate(row.startTime) || '未设置' }}</span>
              </div>
              <div class="time-row">
                <ActionIcon action="deadline" :size="12" class="time-icon deadline" />
                <span class="time-label">截止:</span>
                <span class="time-value" :class="getDeadlineClass(row.deadline)">{{ formatDate(row.deadline) || '未设置' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="任务进度" width="260">
          <template #default="{ row }">
            <TaskProgressCard
              :progress="row.progress"
              :status="row.status"
              :estimated-hours="row.estimatedHours"
              :actual-hours="row.actualHours"
              :show-details="true"
              :show-actions="true"
              @update-progress="handleUpdateProgress(row)"
              @recalculate-progress="handleRecalculateProgress(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-tooltip content="编辑任务" placement="top">
              <el-button type="primary" circle class="action-btn" @click="handleEdit(row.id)">
                <ActionIcon action="edit" :size="16" />
              </el-button>
            </el-tooltip>
            <el-tooltip content="技能需求管理" placement="top">
              <el-button type="warning" circle class="action-btn" @click="handleManageRequirements(row)">
                <SkillIcons type="tag" :size="16" />
              </el-button>
            </el-tooltip>
            <el-tooltip content="依赖关系管理" placement="top">
              <el-button type="info" circle class="action-btn" @click="handleManageDependencies(row)">
                <ActionIcon action="dependency" :size="16" />
              </el-button>
            </el-tooltip>
            <el-tooltip content="发送通知" placement="top">
              <el-button type="success" circle class="action-btn" @click="handleSendNotification(row)">
                <ActionIcon action="notification" :size="16" />
              </el-button>
            </el-tooltip>
            <el-tooltip content="删除任务" placement="top">
              <el-button type="danger" circle class="action-btn" @click="handleDelete(row.id)">
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
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogFormVisible"
      :title="dialogTitle"
      width="60%"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="任务标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入任务标题" />
        </el-form-item>
        <el-form-item label="任务描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入任务描述"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option
                  v-for="item in statusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                  <el-tag :type="item.type" size="small">{{ item.label }}</el-tag>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
                <el-option
                  v-for="item in priorityOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                  <el-tag :type="item.type" size="small">{{ item.label }}</el-tag>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="form.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止时间" prop="deadline">
              <el-date-picker
                v-model="form.deadline"
                type="datetime"
                placeholder="选择截止时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="进度" prop="progress">
          <el-slider
            v-model="form.progress"
            :step="10"
            show-stops
            :marks="{
              0: '0%',
              25: '25%',
              50: '50%',
              75: '75%',
              100: '100%'
            }"
          />
        </el-form-item>
        <el-form-item label="负责人" prop="assigneeId">
          <el-select 
            v-model="form.assigneeId" 
            placeholder="请选择负责人"
            clearable
            style="width: 100%"
          >
            <el-option 
              v-for="item in assigneeOptions" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工作组" prop="groupId">
          <el-select 
            v-model="form.groupId" 
            placeholder="请选择工作组"
            clearable
            style="width: 100%"
          >
            <el-option 
              v-for="item in groupOptions" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="附件">
          <div class="attachment-container">
            <el-upload
              ref="uploadRef"
              :auto-upload="false"
              :on-change="handleUpload"
              :show-file-list="false"
              :disabled="!form.id"
              accept=".jpg,.jpeg,.png,.gif,.bmp,.webp,.pdf,.doc,.docx,.xls,.xlsx,.txt,.zip,.rar"
            >
              <el-button
                type="primary"
                class="upload-btn"
                :loading="uploadLoading"
                :disabled="!form.id"
              >
                <ActionIcon action="upload" :size="16" />
                上传附件
              </el-button>
              <template #tip>
                <div class="upload-tip">
                  支持的文件类型：图片(jpg/png/gif/bmp/webp)、文档(pdf/doc/docx/xls/xlsx/txt)、压缩包(zip/rar)，单个文件大小不超过10MB
                </div>
              </template>
            </el-upload>
            
            <div class="attachment-list" v-if="attachmentList.length > 0">
              <div v-for="item in attachmentList" :key="item.id" class="attachment-item">
                <el-icon><component :is="getFileIcon(item.fileType)" /></el-icon>
                <template v-if="item.fileType.startsWith('image/')">
                  <el-image
                    :src="item.ossPath"
                    :preview-src-list="[item.ossPath]"
                    fit="cover"
                    class="preview-image"
                    :preview-teleported="true"
                  >
                    <template #error>
                      <div class="image-error">
                        <el-icon><Picture /></el-icon>
                        <span>加载失败</span>
                      </div>
                    </template>
                  </el-image>
                  <span class="file-name" @click="handlePreview(item)">{{ item.fileName }}</span>
                </template>
                <template v-else>
                  <span class="file-name" @click="handlePreview(item)">{{ item.fileName }}</span>
                </template>
                <span class="file-size">({{ formatFileSize(item.fileSize) }})</span>
                <el-button
                  type="danger"
                  link
                  @click="handleDeleteAttachment(item.id)"
                >
                  删除
                </el-button>
              </div>
            </div>
            <div v-else class="no-attachment">
              暂无附件
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="cancel-btn" @click="dialogFormVisible = false">
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

    <!-- 通知发送组件 -->
    <NotificationQuickSend ref="quickSendRef" />

    <!-- 任务技能需求管理对话框 -->
    <el-dialog
      v-model="requirementDialogVisible"
      :title="`${currentTask?.title} - 技能需求管理`"
      width="800px"
      destroy-on-close
    >
      <TaskRequirementManager
        v-if="requirementDialogVisible && currentTask"
        :task-id="currentTask.id"
        @requirement-updated="handleRequirementUpdated"
      />
    </el-dialog>

    <!-- 任务依赖关系管理对话框 -->
    <el-dialog
      v-model="dependencyDialogVisible"
      :title="`${currentDependencyTask?.title} - 任务依赖关系`"
      width="900px"
      destroy-on-close
    >
      <TaskDependencyManager
        v-if="dependencyDialogVisible && currentDependencyTask"
        :task-id="currentDependencyTask.id"
      />
    </el-dialog>

    <!-- 批量状态修改对话框 -->
    <el-dialog
      v-model="batchStatusDialogVisible"
      title="批量修改状态"
      width="400px"
    >
      <el-form label-width="80px">
        <el-form-item label="选择状态">
          <el-select v-model="batchStatus" placeholder="请选择新状态" style="width: 100%">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
              <el-tag :type="item.type" size="small">{{ item.label }}</el-tag>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <div class="batch-summary">
            <span class="summary-text">将为 {{ selectedTaskIds.length }} 个任务修改状态</span>
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button class="cancel-btn" @click="batchStatusDialogVisible = false">
            <ActionIcon action="close" :size="14" />
            取消
          </el-button>
          <el-button type="primary" class="confirm-btn" @click="confirmBatchStatusChange">
            <ActionIcon action="check" :size="14" />
            确定修改
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 甘特图全屏对话框 -->
    <el-dialog
      v-model="ganttChartVisible"
      title="任务时间线 - 甘特图"
      width="95%"
      :close-on-click-modal="false"
      destroy-on-close
      fullscreen
    >
      <TaskGanttChart
        v-if="ganttChartVisible"
        @task-click="handleGanttTaskClick"
        @edit-task="handleGanttEditTask"
        @manage-dependencies="handleGanttManageDependencies"
      />
    </el-dialog>

    <!-- 任务模板管理对话框 -->
    <el-dialog
      v-model="templateManagerVisible"
      title="任务模板管理"
      width="95%"
      :close-on-click-modal="false"
      destroy-on-close
      fullscreen
    >
      <TaskTemplateManager
        v-if="templateManagerVisible"
        @template-applied="handleTemplateApplied"
      />
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.task-container {
  padding: 20px;
  
  .box-card {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    
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
          color: #303133;
        }
        
        .search-input {
          width: 240px;
        }
      }
      
      .header-actions {
        display: flex;
        gap: 12px;
        align-items: center;
      }
    }
    
    .filter-container {
      margin: 20px 0;
      padding: 20px;
      background: #f8f9fa;
      border-radius: 8px;
      
      .el-form {
        display: flex;
        flex-wrap: wrap;
        gap: 16px;
        
        .el-form-item {
          margin-bottom: 0;
        }
      }
    }
  }
  
  // 任务标题单元格样式
  .task-title-cell {
    .title-content {
      .task-name {
        font-weight: 600;
        color: #303133;
        margin-bottom: 4px;
        line-height: 1.4;
      }
      
      .task-meta {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .priority-tag {
          display: inline-flex;
          align-items: center;
          gap: 4px;
          padding: 2px 8px;
          border-radius: 12px;
          font-size: 11px;
          font-weight: 500;
        }
        
        .task-id {
          font-size: 11px;
          color: #909399;
          font-weight: 500;
          background: #f5f7fa;
          padding: 2px 6px;
          border-radius: 6px;
        }
      }
    }
  }
  
  // 负责人单元格样式
  .assignee-cell {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .assignee-avatar {
      background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
      
      :deep(.el-avatar__inner) {
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
    
    .assignee-info {
      flex: 1;
      
      .assignee-name {
        font-weight: 500;
        color: #303133;
        font-size: 13px;
        line-height: 1.2;
      }
      
      .assignee-role {
        font-size: 11px;
        color: #909399;
        line-height: 1.2;
      }
    }
  }
  
  // 时间安排单元格样式
  .time-schedule-cell {
    .time-row {
      display: flex;
      align-items: center;
      gap: 4px;
      margin-bottom: 4px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .time-icon {
        color: #909399;
        flex-shrink: 0;
        
        &.deadline {
          color: #f56c6c;
        }
      }
      
      .time-label {
        font-size: 11px;
        color: #909399;
        min-width: 30px;
        flex-shrink: 0;
      }
      
      .time-value {
        font-size: 12px;
        color: #606266;
        font-weight: 500;
        
        &.deadline-overdue {
          color: #f56c6c;
          font-weight: 600;
        }
        
        &.deadline-urgent {
          color: #e6a23c;
          font-weight: 600;
        }
        
        &.deadline-warning {
          color: #f56c6c;
        }
        
        &.deadline-normal {
          color: #67c23a;
        }
      }
    }
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  // 批量操作工具栏
  .batch-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff3cd;
    border: 1px solid #ffc107;
    border-radius: 6px;
    padding: 12px 16px;
    margin-bottom: 16px;
    
    .batch-info {
      .selection-count {
        font-size: 14px;
        font-weight: 600;
        color: #856404;
      }
    }
    
    .batch-actions {
      display: flex;
      gap: 8px;
      
      .batch-btn {
        border-radius: 4px;
        font-weight: 500;
        padding: 6px 12px;
        gap: 4px;
        font-size: 13px;
        
        &:hover {
          opacity: 0.8;
        }
      }
    }
  }
  
  
  .batch-summary {
    .summary-text {
      color: #6b7280;
      font-size: 14px;
    }
  }

  // 表格单元格样式
  .task-title-cell {
    .title-content {
      display: flex;
      flex-direction: column;
      gap: 6px;
      
      .task-name {
        font-weight: 600;
        font-size: 14px;
        color: #303133;
        line-height: 1.4;
      }
      
      .task-meta {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .priority-tag {
          display: flex;
          align-items: center;
          gap: 4px;
          border-radius: 12px;
          padding: 3px 8px;
          font-size: 11px;
          font-weight: 500;
        }
        
        .task-id {
          font-size: 12px;
          color: #909399;
          font-weight: 500;
        }
      }
    }
  }

  .assignee-cell {
    display: flex;
    align-items: center;
    gap: 10px;
    
    .assignee-avatar {
      background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
      
      .el-icon {
        color: white;
      }
    }
    
    .assignee-info {
      display: flex;
      flex-direction: column;
      gap: 2px;
      
      .assignee-name {
        font-weight: 500;
        font-size: 13px;
        color: #303133;
      }
      
      .assignee-role {
        font-size: 11px;
        color: #909399;
      }
    }
  }

  .time-schedule-cell {
    display: flex;
    flex-direction: column;
    gap: 6px;
    
    .time-row {
      display: flex;
      align-items: center;
      gap: 6px;
      
      .time-icon {
        color: #909399;
        
        &.deadline {
          color: #f56c6c;
        }
      }
      
      .time-label {
        font-size: 11px;
        color: #909399;
        min-width: 30px;
      }
      
      .time-value {
        font-size: 12px;
        color: #606266;
        font-weight: 500;
        
        &.deadline-overdue {
          color: #f56c6c;
          font-weight: 600;
        }
        
        &.deadline-urgent {
          color: #e6a23c;
          font-weight: 600;
        }
        
        &.deadline-warning {
          color: #f56c6c;
        }
      }
    }
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

:deep(.el-button--link) {
  padding: 4px 8px;
  
  .el-icon {
    margin-right: 4px;
  }
}

:deep(.el-slider__marks-text) {
  font-size: 12px;
}

:deep(.el-tag) {
  border-radius: 4px;
}

:deep(.el-progress-bar__outer) {
  border-radius: 4px;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  
  .el-table__header {
    th {
      background: #f8f9fa;
      font-weight: 600;
      font-size: 14px;
      color: #495057;
      border-bottom: 1px solid #dee2e6;
      text-align: center;
    }
  }
  
  .el-table__body {
    tr {
      transition: background-color 0.2s;
      
      &:hover {
        background: #f8f9fa;
      }
      
      // 根据任务状态进行简单颜色编码
      &.status-已完成 {
        background: rgba(52, 199, 89, 0.02);
        border-left: 2px solid #34C759;
      }
      
      &.status-进行中 {
        background: rgba(255, 149, 0, 0.02);
        border-left: 2px solid #FF9500;
      }
      
      &.status-待处理 {
        background: rgba(142, 142, 147, 0.02);
        border-left: 2px solid #8E8E93;
      }
      
      // 截止日期警告样式
      &.deadline-urgent {
        .time-value.deadline-urgent, .time-value.deadline-overdue {
          background: #fff2f0;
          color: #ff4d4f;
          padding: 2px 6px;
          border-radius: 4px;
          font-weight: 600;
        }
      }
      
      td {
        padding: 12px;
        border-bottom: 1px solid #f0f2f5;
        vertical-align: middle;
      }
    }
    
    .el-table__row--striped {
      background: #fafbfc;
      
      &:hover {
        background: #f8f9fa;
      }
    }
  }
}


.attachment-container {
  width: 100%;
  
  .upload-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 8px;
  }
}

.attachment-list {
  margin-top: 16px;
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  
  .attachment-item {
    display: flex;
    align-items: center;
    padding: 12px;
    border-bottom: 1px solid #ebeef5;
    transition: background-color 0.3s;
    
    &:hover {
      background-color: #f5f7fa;
    }
    
    .el-icon {
      font-size: 20px;
      color: #909399;
      margin-right: 8px;
    }
    
    .preview-image {
      width: 40px;
      height: 40px;
      border-radius: 4px;
      margin-right: 8px;
      cursor: pointer;
      
      :deep(.el-image__inner) {
        border-radius: 4px;
      }
    }
    
    .image-error {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #909399;
      font-size: 12px;
      
      .el-icon {
        font-size: 20px;
        margin-bottom: 4px;
      }
    }
    
    .file-name {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      color: #409eff;
      cursor: pointer;
      
      &:hover {
        text-decoration: underline;
      }
    }
    
    .file-size {
      color: #909399;
      margin: 0 12px;
      font-size: 13px;
    }
  }
  
  .attachment-item:last-child {
    border-bottom: none;
  }
}

.no-attachment {
  color: #909399;
  text-align: center;
  padding: 30px 0;
  font-size: 14px;
}

// 简化按钮样式
.header-add-btn {
  border-radius: 6px;
  font-weight: 500;
  font-size: 14px;
  padding: 8px 16px;
  gap: 6px;
  
  &:hover {
    opacity: 0.8;
  }
}

.search-btn {
  border-radius: 4px;
  font-weight: 500;
  padding: 8px 16px;
  gap: 4px;
  
  &:hover {
    opacity: 0.8;
  }
}

.reset-btn {
  border-radius: 4px;
  font-weight: 500;
  padding: 8px 16px;
  gap: 4px;
  
  &:hover {
    opacity: 0.8;
  }
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 4px;
  padding: 0;
  margin: 0 2px;
  
  &:hover {
    opacity: 0.8;
  }
}

.upload-btn {
  border-radius: 4px;
  font-weight: 500;
  padding: 8px 16px;
  gap: 4px;
  
  &:hover {
    opacity: 0.8;
  }
  
  &:disabled {
    opacity: 0.6;
  }
}

.cancel-btn, .confirm-btn {
  border-radius: 4px;
  font-weight: 500;
  padding: 8px 16px;
  gap: 6px;
  
  &:hover {
    opacity: 0.8;
  }
}

.gantt-btn {
  border-radius: 6px;
  font-weight: 500;
  font-size: 14px;
  padding: 8px 16px;
  gap: 6px;
  
  &:hover {
    opacity: 0.8;
  }
}

.template-btn {
  border-radius: 6px;
  font-weight: 500;
  font-size: 14px;
  padding: 8px 16px;
  gap: 6px;
  
  &:hover {
    opacity: 0.8;
  }
}
</style>