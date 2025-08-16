<template>
  <div class="gantt-chart">
    <el-card class="gantt-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">任务时间线 - 甘特图</span>
            <el-radio-group v-model="viewMode" class="view-mode-selector">
              <el-radio-button value="week">周视图</el-radio-button>
              <el-radio-button value="month">月视图</el-radio-button>
              <el-radio-button value="quarter">季度视图</el-radio-button>
            </el-radio-group>
          </div>
          <div class="header-actions">
            <el-button @click="resetView" class="action-btn reset-btn">
              <ActionIcon action="refresh" :size="16" />
              <span>重置视图</span>
            </el-button>
            <el-button type="primary" @click="refreshData" :loading="loading" class="action-btn primary-btn">
              <ActionIcon action="reload" :size="16" />
              <span>刷新数据</span>
            </el-button>
          </div>
        </div>
      </template>

      <div class="gantt-container" v-loading="loading">
        <!-- 甘特图工具栏 -->
        <div class="gantt-toolbar">
          <div class="toolbar-left">
            <el-select 
              v-model="selectedProject" 
              placeholder="选择项目" 
              clearable 
              class="project-selector"
              @change="loadTasks"
            >
              <el-option
                v-for="project in projects"
                :key="project.id"
                :label="project.name"
                :value="project.id"
              />
            </el-select>
            <el-select 
              v-model="statusFilter" 
              placeholder="筛选状态" 
              clearable 
              class="status-selector"
              @change="filterTasks"
            >
              <el-option label="全部状态" value="" />
              <el-option label="待处理" value="待处理" />
              <el-option label="进行中" value="进行中" />
              <el-option label="已完成" value="已完成" />
            </el-select>
          </div>
          <div class="toolbar-right">
            <el-tooltip content="显示关键路径" placement="top">
              <el-button 
                :class="['toolbar-btn', 'critical-path-btn', { active: showCriticalPath }]"
                @click="toggleCriticalPath"
              >
                <ActionIcon action="path" :size="16" />
              </el-button>
            </el-tooltip>
            <el-tooltip content="显示依赖关系" placement="top">
              <el-button 
                :class="['toolbar-btn', 'dependency-btn', { active: showDependencies }]"
                @click="toggleDependencies"
              >
                <ActionIcon action="dependency" :size="16" />
              </el-button>
            </el-tooltip>
            <el-tooltip content="全屏显示" placement="top">
              <el-button 
                class="toolbar-btn fullscreen-btn"
                @click="toggleFullscreen"
              >
                <ActionIcon action="fullscreen" :size="16" />
              </el-button>
            </el-tooltip>
          </div>
        </div>

        <!-- 甘特图主体 -->
        <div class="gantt-main" ref="ganttContainer">
          <!-- 左侧任务列表 -->
          <div class="gantt-sidebar">
            <div class="sidebar-header">
              <div class="column-header task-name">任务名称</div>
              <div class="column-header assignee">负责人</div>
              <div class="column-header duration">工期</div>
            </div>
            <div class="sidebar-body">
              <div 
                v-for="(task, index) in filteredTasks" 
                :key="task.id"
                class="task-row"
                :class="{ 
                  'critical-path': task.isCriticalPath && showCriticalPath,
                  'overdue': task.isOverdue,
                  'completed': task.status === '已完成'
                }"
                :style="{ height: `${taskRowHeight}px` }"
              >
                <div class="task-info">
                  <div class="task-name">
                    <span class="task-title">{{ task.title || '未命名任务' }}</span>
                    <el-tag 
                      :type="getStatusType(task.status)" 
                      size="small"
                      class="status-tag"
                    >
                      {{ task.status || '待处理' }}
                    </el-tag>
                  </div>
                  <div class="task-assignee">
                    <el-avatar :size="20" class="assignee-avatar">
                      <template #default>
                        <ActionIcon action="user" :size="12" />
                      </template>
                    </el-avatar>
                    <span class="assignee-name">{{ getAssigneeName(task.assigneeId) }}</span>
                  </div>
                  <div class="task-duration">
                    <span class="duration-text">{{ calculateDuration(task.startTime, task.deadline) }}天</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧时间轴和甘特条 -->
          <div class="gantt-timeline" ref="timelineContainer">
            <!-- 时间轴头部 -->
            <div class="timeline-header">
              <div class="date-scale">
                <div 
                  v-for="date in timelineData.dates" 
                  :key="date.key"
                  class="date-column"
                  :style="{ width: `${columnWidth}px` }"
                >
                  <div class="date-main">{{ date.main }}</div>
                  <div class="date-sub">{{ date.sub }}</div>
                </div>
              </div>
            </div>

            <!-- 甘特条区域 -->
            <div class="timeline-body">
              <div 
                v-for="(task, index) in filteredTasks" 
                :key="`bar-${task.id}`"
                class="gantt-row"
                :style="{ height: `${taskRowHeight}px` }"
              >
                <!-- 任务甘特条 -->
                <div 
                  class="gantt-bar"
                  :class="{
                    'critical-path': task.isCriticalPath && showCriticalPath,
                    'overdue': task.isOverdue,
                    'completed': task.status === '已完成',
                    'in-progress': task.status === '进行中',
                    'pending': task.status === '待处理'
                  }"
                  :style="getBarStyle(task)"
                  @click="handleTaskClick(task)"
                >
                  <div class="bar-content">
                    <div class="bar-progress" :style="{ width: `${task.progress}%` }"></div>
                    <div class="bar-text">{{ task.title }}</div>
                  </div>
                  
                  <!-- 任务详情弹窗触发 -->
                  <el-tooltip 
                    :content="`${task.title} (${task.progress}%)`" 
                    placement="top"
                  >
                    <div class="bar-tooltip-trigger"></div>
                  </el-tooltip>
                </div>

                <!-- 依赖关系连线 -->
                <svg 
                  v-if="showDependencies && task.dependencies" 
                  class="dependency-lines"
                  :style="{ width: '100%', height: `${taskRowHeight}px`, position: 'absolute', top: 0, left: 0, pointerEvents: 'none' }"
                >
                  <path
                    v-for="dep in task.dependencies"
                    :key="`dep-${task.id}-${dep.id}`"
                    :d="getDependencyPath(task, dep)"
                    stroke="#409EFF"
                    stroke-width="2"
                    fill="none"
                    marker-end="url(#arrowhead)"
                  />
                </svg>
              </div>
            </div>
          </div>
        </div>

        <!-- 今日线 -->
        <div 
          class="today-line"
          :style="{ left: `${todayLinePosition}px` }"
          v-if="todayLinePosition > sidebarWidth && todayLinePosition > 0"
        >
          <div class="today-marker">今日</div>
        </div>
      </div>

      <!-- 任务详情侧边栏 -->
      <el-drawer
        v-model="taskDetailVisible"
        :title="`任务详情 - ${selectedTask?.title}`"
        direction="rtl"
        size="400px"
      >
        <div v-if="selectedTask" class="task-detail">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="任务状态">
              <el-tag :type="getStatusType(selectedTask.status)">
                {{ selectedTask.status }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="优先级">
              <el-tag :type="getPriorityType(selectedTask.priority)">
                {{ selectedTask.priority }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="负责人">
              {{ getAssigneeName(selectedTask.assigneeId) }}
            </el-descriptions-item>
            <el-descriptions-item label="开始时间">
              {{ formatDate(selectedTask.startTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="截止时间">
              {{ formatDate(selectedTask.deadline) }}
            </el-descriptions-item>
            <el-descriptions-item label="进度">
              <el-progress :percentage="selectedTask.progress" :status="getProgressStatus(selectedTask.progress)" />
            </el-descriptions-item>
          </el-descriptions>
          
          <div class="task-actions" style="margin-top: 24px;">
            <el-button type="primary" @click="editTask(selectedTask)" class="detail-btn edit-btn">
              <ActionIcon action="edit" :size="16" />
              <span>编辑任务</span>
            </el-button>
            <el-button type="info" @click="manageDependencies(selectedTask)" class="detail-btn dependency-btn">
              <ActionIcon action="dependency" :size="16" />
              <span>依赖关系</span>
            </el-button>
          </div>
        </div>
      </el-drawer>
    </el-card>

    <!-- SVG 箭头定义 -->
    <svg width="0" height="0" style="position: absolute;">
      <defs>
        <marker id="arrowhead" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
          <polygon points="0 0, 10 3.5, 0 7" fill="#409EFF" />
        </marker>
      </defs>
    </svg>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { ActionIcon } from '@/components/SvgIcons';
import { getTaskList } from '@/api/task';
import { getProjectList } from '@/api/project';

const emit = defineEmits(['task-click', 'edit-task', 'manage-dependencies']);

// 响应式数据
const loading = ref(false);
const viewMode = ref('month'); // week, month, quarter
const showCriticalPath = ref(false);
const showDependencies = ref(true);
const selectedProject = ref('');
const statusFilter = ref('');
const taskDetailVisible = ref(false);
const selectedTask = ref(null);

// 数据
const tasks = ref([]);
const projects = ref([]);
const filteredTasks = ref([]);

// 甘特图配置
const taskRowHeight = 40;
const columnWidth = ref(60);
const sidebarWidth = 300;

// 时间轴数据
const timelineData = reactive({
  startDate: null,
  endDate: null,
  dates: []
});

// 今日线位置
const todayLinePosition = computed(() => {
  if (!timelineData.startDate || !timelineData.endDate) return -1;
  
  const today = new Date();
  today.setHours(0, 0, 0, 0); // 重置到当天开始
  
  const startDate = new Date(timelineData.startDate);
  const endDate = new Date(timelineData.endDate);
  
  // 检查今日是否在时间轴范围内
  if (today < startDate || today > endDate) return -1;
  
  let position = sidebarWidth;
  
  if (viewMode.value === 'quarter') {
    // 季度视图按月计算
    const monthsDiff = (today.getFullYear() - startDate.getFullYear()) * 12 + 
                      (today.getMonth() - startDate.getMonth());
    position += monthsDiff * columnWidth.value;
  } else {
    // 周视图和月视图按日计算
    const daysDiff = Math.floor((today - startDate) / (1000 * 60 * 60 * 24));
    position += daysDiff * columnWidth.value;
  }
  
  return position;
});

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
  if (progress >= 80) return '';
  if (progress >= 50) return 'warning';
  return 'exception';
};

// 获取负责人姓名
const getAssigneeName = (assigneeId) => {
  return assigneeId ? `用户${assigneeId}` : '未分配';
};

// 计算工期
const calculateDuration = (startTime, deadline) => {
  if (!startTime || !deadline) return 0;
  const start = new Date(startTime);
  const end = new Date(deadline);
  const timeDiff = end - start;
  return Math.ceil(timeDiff / (1000 * 60 * 60 * 24));
};

// 格式化日期
const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleDateString('zh-CN');
};

// 获取甘特条样式
const getBarStyle = (task) => {
  if (!task.startTime || !task.deadline || !timelineData.startDate) {
    return { display: 'none' };
  }
  
  const taskStart = new Date(task.startTime);
  const taskEnd = new Date(task.deadline);
  const timelineStart = new Date(timelineData.startDate);
  
  let startOffset, duration;
  
  if (viewMode.value === 'quarter') {
    // 季度视图按月计算
    const startMonthsDiff = (taskStart.getFullYear() - timelineStart.getFullYear()) * 12 + 
                           (taskStart.getMonth() - timelineStart.getMonth());
    const endMonthsDiff = (taskEnd.getFullYear() - timelineStart.getFullYear()) * 12 + 
                         (taskEnd.getMonth() - timelineStart.getMonth());
    
    startOffset = startMonthsDiff;
    duration = Math.max(endMonthsDiff - startMonthsDiff, 0.2); // 最小0.2个月的显示宽度
  } else {
    // 周视图和月视图按日计算
    startOffset = Math.floor((taskStart - timelineStart) / (1000 * 60 * 60 * 24));
    duration = Math.ceil((taskEnd - taskStart) / (1000 * 60 * 60 * 24));
    duration = Math.max(duration, 1); // 最小1天的显示宽度
  }
  
  const left = startOffset * columnWidth.value;
  const width = Math.max(duration * columnWidth.value, 24); // 最小24px宽度
  
  // 检查是否在可视范围内
  const timelineWidth = timelineData.dates.length * columnWidth.value;
  if (left + width < 0 || left > timelineWidth) {
    return { display: 'none' };
  }
  
  return {
    left: `${Math.max(left, 0)}px`,
    width: `${width}px`,
    top: '6px',
    height: `${taskRowHeight - 12}px`,
    minWidth: '24px'
  };
};

// 获取依赖关系路径
const getDependencyPath = (fromTask, toTask) => {
  // 简化的依赖线绘制逻辑
  return `M 0,${taskRowHeight/2} L 100,${taskRowHeight/2}`;
};

// 生成时间轴
const generateTimeline = () => {
  if (filteredTasks.value.length === 0) {
    timelineData.dates = [];
    timelineData.startDate = null;
    timelineData.endDate = null;
    return;
  }
  
  // 找到最早和最晚的日期
  const dates = filteredTasks.value
    .filter(task => task.startTime && task.deadline)
    .flatMap(task => [new Date(task.startTime), new Date(task.deadline)]);
    
  if (dates.length === 0) {
    // 如果没有日期信息，使用当前月份
    const today = new Date();
    const monthStart = new Date(today.getFullYear(), today.getMonth(), 1);
    const monthEnd = new Date(today.getFullYear(), today.getMonth() + 1, 0);
    dates.push(monthStart, monthEnd);
  }
  
  const minDate = new Date(Math.min(...dates));
  const maxDate = new Date(Math.max(...dates));
  
  // 根据视图模式调整日期范围和列宽
  let adjustedStart, adjustedEnd;
  
  switch (viewMode.value) {
    case 'week':
      // 向前后各扩展一周，按日显示
      adjustedStart = new Date(minDate);
      adjustedStart.setDate(adjustedStart.getDate() - 7);
      adjustedEnd = new Date(maxDate);
      adjustedEnd.setDate(adjustedEnd.getDate() + 7);
      columnWidth.value = 50;
      break;
    case 'quarter':
      // 按月显示，向前后各扩展一个月
      adjustedStart = new Date(minDate.getFullYear(), minDate.getMonth() - 1, 1);
      adjustedEnd = new Date(maxDate.getFullYear(), maxDate.getMonth() + 2, 0);
      columnWidth.value = 80;
      break;
    default: // month
      // 向前后各扩展两周，按日显示
      adjustedStart = new Date(minDate);
      adjustedStart.setDate(adjustedStart.getDate() - 14);
      adjustedEnd = new Date(maxDate);
      adjustedEnd.setDate(adjustedEnd.getDate() + 14);
      columnWidth.value = 35;
  }
  
  timelineData.startDate = adjustedStart;
  timelineData.endDate = adjustedEnd;
  
  // 生成日期列
  const dateList = [];
  const currentDate = new Date(adjustedStart);
  let iterationCount = 0;
  const maxIterations = 1000; // 防止无限循环
  
  while (currentDate <= adjustedEnd && iterationCount < maxIterations) {
    let mainLabel, subLabel;
    const dateKey = currentDate.toISOString().split('T')[0];
    
    switch (viewMode.value) {
      case 'week':
        mainLabel = `${currentDate.getMonth() + 1}/${currentDate.getDate()}`;
        subLabel = ['日', '一', '二', '三', '四', '五', '六'][currentDate.getDay()];
        currentDate.setDate(currentDate.getDate() + 1);
        break;
      case 'quarter':
        mainLabel = `${currentDate.getMonth() + 1}月`;
        subLabel = `${currentDate.getFullYear()}`;
        // 移动到下个月的第一天
        currentDate.setMonth(currentDate.getMonth() + 1, 1);
        break;
      default: // month
        mainLabel = `${currentDate.getDate()}`;
        subLabel = currentDate.getDate() === 1 ? `${currentDate.getMonth() + 1}月` : '';
        currentDate.setDate(currentDate.getDate() + 1);
    }
    
    dateList.push({
      key: dateKey,
      main: mainLabel,
      sub: subLabel,
      date: new Date(currentDate.getTime() - (viewMode.value === 'quarter' ? 0 : 24 * 60 * 60 * 1000))
    });
    
    iterationCount++;
  }
  
  if (iterationCount >= maxIterations) {
    console.warn('时间轴生成达到最大迭代次数，可能存在无限循环');
  }
  
  timelineData.dates = dateList;
};

// 加载项目数据
const loadProjects = async () => {
  try {
    const response = await getProjectList();
    if (response.code === 200) {
      projects.value = response.data.records || [];
    }
  } catch (error) {
    console.error('加载项目列表失败:', error);
  }
};

// 加载任务数据
const loadTasks = async () => {
  loading.value = true;
  try {
    const params = {
      pageSize: 1000,
      projectId: selectedProject.value || undefined
    };
    
    const response = await getTaskList(params);
    if (response.code === 200) {
      const records = response.data.records || [];
      
      tasks.value = records
        .filter(task => task && task.title) // 过滤掉空数据或无标题的任务
        .map(task => ({
          ...task,
          isOverdue: task.deadline && new Date(task.deadline) < new Date() && task.status !== '已完成',
          isCriticalPath: false // 需要后端计算关键路径
        }));
      
      console.log('加载的任务数据:', tasks.value);
      
      filterTasks();
      await nextTick();
      generateTimeline();
    } else {
      ElMessage.error(response.message || '加载任务数据失败');
    }
  } catch (error) {
    console.error('加载任务数据失败:', error);
    ElMessage.error('加载任务数据失败');
  } finally {
    loading.value = false;
  }
};

// 筛选任务
const filterTasks = () => {
  let filtered = tasks.value.filter(task => {
    // 验证任务数据完整性
    if (!task || !task.id || !task.title || task.title.includes('选择项目')) return false;
    
    // 状态筛选
    if (statusFilter.value && task.status !== statusFilter.value) return false;
    
    // 项目筛选
    if (selectedProject.value && task.projectId !== selectedProject.value) return false;
    
    // 只显示有开始时间和截止时间的任务
    if (!task.startTime || !task.deadline) return false;
    
    return true;
  });
  
  // 按开始时间排序，确保任务按时间顺序显示
  filtered.sort((a, b) => {
    const dateA = new Date(a.startTime);
    const dateB = new Date(b.startTime);
    if (dateA.getTime() === dateB.getTime()) {
      // 如果开始时间相同，按优先级排序（高优先级在前）
      const priorityMap = { '高': 3, '中': 2, '低': 1 };
      return (priorityMap[b.priority] || 1) - (priorityMap[a.priority] || 1);
    }
    return dateA - dateB;
  });
  
  filteredTasks.value = filtered;
};

// 任务点击处理
const handleTaskClick = (task) => {
  selectedTask.value = task;
  taskDetailVisible.value = true;
  emit('task-click', task);
};

// 编辑任务
const editTask = (task) => {
  emit('edit-task', task);
};

// 管理依赖关系
const manageDependencies = (task) => {
  emit('manage-dependencies', task);
};

// 切换关键路径显示
const toggleCriticalPath = () => {
  showCriticalPath.value = !showCriticalPath.value;
  ElMessage.success(showCriticalPath.value ? '已显示关键路径' : '已隐藏关键路径');
};

// 切换依赖关系显示
const toggleDependencies = () => {
  showDependencies.value = !showDependencies.value;
  ElMessage.success(showDependencies.value ? '已显示依赖关系' : '已隐藏依赖关系');
};

// 全屏切换
const toggleFullscreen = () => {
  const element = document.documentElement;
  
  if (!document.fullscreenElement) {
    // 进入全屏
    if (element.requestFullscreen) {
      element.requestFullscreen();
    } else if (element.webkitRequestFullscreen) {
      element.webkitRequestFullscreen();
    } else if (element.mozRequestFullScreen) {
      element.mozRequestFullScreen();
    } else if (element.msRequestFullscreen) {
      element.msRequestFullscreen();
    }
    ElMessage.success('已进入全屏模式');
  } else {
    // 退出全屏
    if (document.exitFullscreen) {
      document.exitFullscreen();
    } else if (document.webkitExitFullscreen) {
      document.webkitExitFullscreen();
    } else if (document.mozCancelFullScreen) {
      document.mozCancelFullScreen();
    } else if (document.msExitFullscreen) {
      document.msExitFullscreen();
    }
    ElMessage.success('已退出全屏模式');
  }
};

// 重置视图
const resetView = () => {
  selectedProject.value = '';
  statusFilter.value = '';
  viewMode.value = 'month';
  showCriticalPath.value = false;
  showDependencies.value = true;
  loadTasks();
};

// 刷新数据
const refreshData = () => {
  loadTasks();
};

// 监听视图模式变化
watch(viewMode, () => {
  generateTimeline();
});

// 组件挂载
onMounted(() => {
  loadProjects();
  loadTasks();
});
</script>

<style lang="scss" scoped>
.gantt-chart {
  .gantt-card {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .header-left {
        display: flex;
        align-items: center;
        gap: 20px;
        
        .title {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }
        
        .view-mode-selector {
          background: #f8f9fa;
          border-radius: 12px;
          padding: 4px;
          
          :deep(.el-radio-button) {
            &:first-child .el-radio-button__inner {
              border-radius: 10px;
              border-top-right-radius: 4px;
              border-bottom-right-radius: 4px;
            }
            
            &:last-child .el-radio-button__inner {
              border-radius: 10px;
              border-top-left-radius: 4px;
              border-bottom-left-radius: 4px;
            }
            
            .el-radio-button__inner {
              border: none;
              border-radius: 8px;
              margin: 0;
              padding: 8px 16px;
              font-size: 13px;
              font-weight: 500;
              color: #6b7280;
              background: transparent;
              transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
              
              &:hover {
                color: #374151;
                background: rgba(255, 255, 255, 0.6);
              }
            }
            
            &.is-active .el-radio-button__inner {
              background: #ffffff;
              color: #007AFF;
              box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
              transform: translateY(-1px);
            }
          }
        }
      }
      
      .header-actions {
        display: flex;
        gap: 12px;
        
        .action-btn {
          height: 40px;
          border-radius: 12px;
          font-weight: 500;
          padding: 0 20px;
          display: flex;
          align-items: center;
          gap: 8px;
          border: 1px solid #e5e5e7;
          background: #ffffff;
          color: #636366;
          transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
          
          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
            border-color: #d1d1d6;
            color: #000000;
          }
          
          &:active {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }
          
          span {
            font-size: 14px;
          }
          
          &.primary-btn {
            background: linear-gradient(135deg, #007AFF 0%, #0051D5 100%);
            border-color: #007AFF;
            color: #ffffff;
            
            &:hover {
              background: linear-gradient(135deg, #0051D5 0%, #003d99 100%);
              border-color: #0051D5;
              color: #ffffff;
            }
          }
          
          &.reset-btn {
            background: #f2f2f7;
            color: #8e8e93;
            border-color: #e5e5ea;
            
            &:hover {
              background: #e5e5ea;
              color: #636366;
              border-color: #d1d1d6;
            }
          }
        }
      }
    }
  }
  
  .gantt-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 0;
    border-bottom: 1px solid #e4e7ed;
    margin-bottom: 20px;
    
    .toolbar-left {
      display: flex;
      gap: 16px;
      align-items: center;
      
      .project-selector, .status-selector {
        min-width: 180px;
        
        :deep(.el-select__wrapper) {
          border-radius: 10px;
          border: 1px solid #e5e5e7;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
          transition: all 0.3s ease;
          
          &:hover {
            border-color: #d1d1d6;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
          }
          
          &.is-focused {
            border-color: #007AFF;
            box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
          }
          
          .el-select__placeholder {
            color: #8e8e93;
          }
          
          .el-input__inner {
            color: #000000;
          }
        }
      }
      
      .status-selector {
        min-width: 140px;
      }
    }
    
    .toolbar-right {
      display: flex;
      gap: 8px;
      align-items: center;
      
      .toolbar-btn {
        width: 44px;
        height: 44px;
        border-radius: 12px;
        padding: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 1px solid #e5e5e7;
        background: #ffffff;
        color: #8e8e93;
        transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
          border-color: #d1d1d6;
          color: #636366;
        }
        
        &:active {
          transform: translateY(-1px);
        }
        
        &.active {
          border-color: #007AFF;
          background: rgba(0, 122, 255, 0.1);
          color: #007AFF;
          
          &:hover {
            background: rgba(0, 122, 255, 0.15);
          }
        }
        
        &.critical-path-btn.active {
          border-color: #FF3B30;
          background: rgba(255, 59, 48, 0.1);
          color: #FF3B30;
          
          &:hover {
            background: rgba(255, 59, 48, 0.15);
          }
        }
        
        &.dependency-btn.active {
          border-color: #34C759;
          background: rgba(52, 199, 89, 0.1);
          color: #34C759;
          
          &:hover {
            background: rgba(52, 199, 89, 0.15);
          }
        }
        
        &.fullscreen-btn {
          &:hover {
            border-color: #8e8e93;
            color: #636366;
            background: #f2f2f7;
          }
          
          &:active {
            background: #e5e5ea;
          }
        }
      }
    }
  }
  
  .gantt-main {
    display: flex;
    position: relative;
    overflow-x: auto;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    
    .gantt-sidebar {
      width: 300px;
      flex-shrink: 0;
      border-right: 2px solid #e4e7ed;
      background: #fafbfc;
      
      .sidebar-header {
        height: 60px;
        display: flex;
        border-bottom: 1px solid #e4e7ed;
        background: #f5f6fa;
        
        .column-header {
          display: flex;
          align-items: center;
          justify-content: center;
          font-weight: 600;
          color: #606266;
          font-size: 13px;
          border-right: 1px solid #e4e7ed;
          
          &.task-name {
            flex: 1;
            justify-content: flex-start;
            padding-left: 16px;
          }
          
          &.assignee {
            width: 80px;
          }
          
          &.duration {
            width: 60px;
          }
        }
      }
      
      .sidebar-body {
        .task-row {
          display: flex;
          border-bottom: 1px solid #f0f2f5;
          transition: all 0.3s ease;
          
          &:hover {
            background: rgba(64, 158, 255, 0.05);
          }
          
          &.critical-path {
            background: rgba(245, 108, 108, 0.1);
            border-left: 4px solid #f56c6c;
          }
          
          &.overdue {
            background: rgba(245, 108, 108, 0.05);
          }
          
          &.completed {
            opacity: 0.7;
            background: rgba(103, 194, 58, 0.05);
          }
          
          .task-info {
            display: flex;
            width: 100%;
            align-items: center;
            
            .task-name {
              flex: 1;
              padding: 8px 16px;
              display: flex;
              flex-direction: column;
              gap: 4px;
              
              .task-title {
                font-weight: 500;
                color: #303133;
                font-size: 13px;
                line-height: 1.4;
              }
              
              .status-tag {
                align-self: flex-start;
              }
            }
            
            .task-assignee {
              width: 80px;
              display: flex;
              flex-direction: column;
              align-items: center;
              gap: 2px;
              padding: 8px 4px;
              
              .assignee-avatar {
                background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
              }
              
              .assignee-name {
                font-size: 10px;
                color: #909399;
                text-align: center;
                line-height: 1.2;
              }
            }
            
            .task-duration {
              width: 60px;
              display: flex;
              justify-content: center;
              align-items: center;
              
              .duration-text {
                font-size: 12px;
                color: #606266;
                font-weight: 500;
              }
            }
          }
        }
      }
    }
    
    .gantt-timeline {
      flex: 1;
      position: relative;
      
      .timeline-header {
        height: 60px;
        border-bottom: 1px solid #e4e7ed;
        background: #f5f6fa;
        
        .date-scale {
          display: flex;
          height: 100%;
          
          .date-column {
            border-right: 1px solid #e4e7ed;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            font-size: 12px;
            
            .date-main {
              font-weight: 600;
              color: #303133;
              margin-bottom: 2px;
            }
            
            .date-sub {
              color: #909399;
              font-size: 10px;
            }
          }
        }
      }
      
      .timeline-body {
        position: relative;
        
        .gantt-row {
          border-bottom: 1px solid #f0f2f5;
          position: relative;
          
          .gantt-bar {
            position: absolute;
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.3s ease;
            
            &.pending {
              background: linear-gradient(135deg, #909399 0%, #73767A 100%);
            }
            
            &.in-progress {
              background: linear-gradient(135deg, #e6a23c 0%, #d19c0f 100%);
            }
            
            &.completed {
              background: linear-gradient(135deg, #67c23a 0%, #5dab2f 100%);
            }
            
            &.critical-path {
              background: linear-gradient(135deg, #f56c6c 0%, #e03d3d 100%);
              box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3);
            }
            
            &.overdue {
              border: 2px solid #f56c6c;
              background: linear-gradient(135deg, rgba(245, 108, 108, 0.2) 0%, rgba(245, 108, 108, 0.1) 100%);
            }
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            }
            
            .bar-content {
              position: relative;
              height: 100%;
              border-radius: 6px;
              overflow: hidden;
              
              .bar-progress {
                position: absolute;
                top: 0;
                left: 0;
                height: 100%;
                background: rgba(255, 255, 255, 0.3);
                border-radius: 6px 0 0 6px;
                transition: width 0.3s ease;
              }
              
              .bar-text {
                position: absolute;
                top: 50%;
                left: 8px;
                transform: translateY(-50%);
                color: white;
                font-size: 12px;
                font-weight: 500;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                max-width: calc(100% - 16px);
              }
            }
            
            .bar-tooltip-trigger {
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
            }
          }
        }
      }
    }
  }
  
  .today-line {
    position: absolute;
    top: 60px;
    bottom: 0;
    width: 2px;
    background: #ff4757;
    z-index: 10;
    pointer-events: none;
    
    .today-marker {
      position: absolute;
      top: -20px;
      left: -20px;
      background: #ff4757;
      color: white;
      padding: 2px 8px;
      border-radius: 12px;
      font-size: 11px;
      font-weight: 500;
      white-space: nowrap;
    }
  }
}

.task-detail {
  .task-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
    
    .detail-btn {
      height: 48px;
      border-radius: 12px;
      font-weight: 500;
      padding: 0 24px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      border: 1px solid #e5e5e7;
      transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
      }
      
      &:active {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
      
      span {
        font-size: 15px;
        font-weight: 500;
      }
      
      &.edit-btn {
        background: linear-gradient(135deg, #007AFF 0%, #0051D5 100%);
        border-color: #007AFF;
        color: #ffffff;
        
        &:hover {
          background: linear-gradient(135deg, #0051D5 0%, #003d99 100%);
          border-color: #0051D5;
          color: #ffffff;
        }
      }
      
      &.dependency-btn {
        background: linear-gradient(135deg, #34C759 0%, #28A745 100%);
        border-color: #34C759;
        color: #ffffff;
        
        &:hover {
          background: linear-gradient(135deg, #28A745 0%, #1e7e34 100%);
          border-color: #28A745;
          color: #ffffff;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .gantt-chart {
    .gantt-main {
      .gantt-sidebar {
        width: 250px;
        
        .sidebar-header .column-header {
          &.assignee {
            width: 60px;
          }
          
          &.duration {
            width: 50px;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .gantt-chart {
    .gantt-toolbar {
      flex-direction: column;
      gap: 12px;
      align-items: stretch;
      
      .toolbar-left, .toolbar-right {
        justify-content: center;
      }
    }
    
    .gantt-main {
      .gantt-sidebar {
        width: 200px;
        
        .sidebar-header .column-header {
          font-size: 11px;
          
          &.assignee {
            display: none;
          }
          
          &.duration {
            width: 40px;
          }
        }
        
        .sidebar-body .task-row .task-info .task-assignee {
          display: none;
        }
      }
    }
  }
}
</style>