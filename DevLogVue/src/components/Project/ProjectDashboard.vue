<template>
  <div class="project-dashboard">
    <!-- 项目选择器 -->
    <el-card class="selector-card">
      <div class="selector-header">
        <h3>项目仪表板</h3>
        <div class="controls">
          <el-select 
            v-model="selectedProjectId" 
            placeholder="选择项目"
            clearable
            style="width: 240px"
            @change="handleProjectChange"
          >
            <el-option
              v-for="project in projects"
              :key="project.id"
              :label="project.name"
              :value="project.id"
            />
          </el-select>
          <el-button 
            type="primary" 
            :loading="loading" 
            @click="refreshData"
          >
            刷新数据
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 项目信息和统计 -->
    <div v-if="selectedProject && stats" class="dashboard-content">
      <!-- 项目基础信息 -->
      <el-card class="project-info">
        <h2>{{ selectedProject.name }}</h2>
        <p class="description">{{ selectedProject.description }}</p>
        <div class="meta-info">
          <el-tag :type="getStatusType(selectedProject.status)">
            {{ selectedProject.status }}
          </el-tag>
          <span class="date-range">
            {{ formatDate(selectedProject.startTime) }} - {{ formatDate(selectedProject.endTime) }}
          </span>
        </div>
      </el-card>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.totalTasks }}</div>
            <div class="stat-label">总任务数</div>
          </div>
        </el-card>
        
        <el-card class="stat-card completed">
          <div class="stat-content">
            <div class="stat-number">{{ stats.completedTasks }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </el-card>
        
        <el-card class="stat-card progress">
          <div class="stat-content">
            <div class="stat-number">{{ stats.inProgressTasks }}</div>
            <div class="stat-label">进行中</div>
          </div>
        </el-card>
        
        <el-card class="stat-card pending">
          <div class="stat-content">
            <div class="stat-number">{{ stats.pendingTasks }}</div>
            <div class="stat-label">待处理</div>
          </div>
        </el-card>

        <el-card class="stat-card overall">
          <div class="stat-content">
            <div class="stat-number">{{ stats.overallProgress }}%</div>
            <div class="stat-label">整体进度</div>
          </div>
        </el-card>
      </div>

      <!-- 优先级分布 -->
      <el-card class="priority-card">
        <template #header>
          <span>任务优先级分布</span>
        </template>
        <div class="priority-stats">
          <div class="priority-item high">
            <span class="priority-label">高优先级</span>
            <span class="priority-count">{{ stats.highPriorityTasks }}</span>
          </div>
          <div class="priority-item medium">
            <span class="priority-label">中优先级</span>
            <span class="priority-count">{{ stats.mediumPriorityTasks }}</span>
          </div>
          <div class="priority-item low">
            <span class="priority-label">低优先级</span>
            <span class="priority-count">{{ stats.lowPriorityTasks }}</span>
          </div>
        </div>
      </el-card>

      <!-- 进度条 -->
      <el-card class="progress-card">
        <template #header>
          <span>项目进度</span>
        </template>
        <div class="progress-container">
          <el-progress
            :percentage="stats.overallProgress"
            :stroke-width="20"
            :color="getProgressColor(stats.overallProgress)"
          >
            <span class="progress-text">{{ stats.overallProgress }}%</span>
          </el-progress>
        </div>
      </el-card>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!selectedProject" class="empty-state">
      <el-empty description="请选择一个项目查看统计信息" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getProjectList, getProjectTaskStats } from '@/api/project'

// 响应式数据
const loading = ref(false)
const selectedProjectId = ref(null)
const projects = ref([])
const stats = ref(null)

// 计算属性
const selectedProject = computed(() => {
  return projects.value.find(p => p.id === selectedProjectId.value)
})

// 获取项目列表
const loadProjects = async () => {
  try {
    const response = await getProjectList({ pageSize: 1000 })
    if (response.code === 200) {
      projects.value = response.data?.records || []
    } else {
      throw new Error(response.message || '获取项目列表失败')
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
    ElMessage.error('加载项目列表失败')
    projects.value = []
  }
}

// 获取项目统计
const loadStats = async (projectId) => {
  if (!projectId) return
  
  loading.value = true
  try {
    const response = await getProjectTaskStats(projectId)
    if (response.code === 200) {
      stats.value = response.data
    } else {
      throw new Error(response.message || '获取项目统计失败')
    }
  } catch (error) {
    console.error('加载项目统计失败:', error)
    ElMessage.error('加载项目统计失败')
    stats.value = null
  } finally {
    loading.value = false
  }
}

// 处理项目切换
const handleProjectChange = (projectId) => {
  if (projectId) {
    loadStats(projectId)
  } else {
    stats.value = null
  }
}

// 刷新数据
const refreshData = async () => {
  if (selectedProjectId.value) {
    await loadStats(selectedProjectId.value)
    ElMessage.success('数据刷新成功')
  } else {
    ElMessage.warning('请先选择项目')
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    '规划中': 'info',
    '开发中': 'primary', 
    '测试中': 'warning',
    '已上线': 'success',
    '维护中': 'info',
    '已关闭': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取进度颜色
const getProgressColor = (percentage) => {
  if (percentage >= 80) return '#67c23a'
  if (percentage >= 60) return '#e6a23c' 
  if (percentage >= 40) return '#409eff'
  return '#f56c6c'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

// 组件挂载
onMounted(() => {
  loadProjects()
})
</script>

<style lang="scss" scoped>
.project-dashboard {
  padding: 20px;

  .selector-card {
    margin-bottom: 20px;
    border-radius: 8px;

    .selector-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      h3 {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
        color: #303133;
      }

      .controls {
        display: flex;
        gap: 12px;
        align-items: center;
      }
    }
  }

  .dashboard-content {
    .project-info {
      margin-bottom: 20px;
      border-radius: 8px;

      h2 {
        margin: 0 0 8px 0;
        font-size: 20px;
        font-weight: 600;
        color: #303133;
      }

      .description {
        margin: 0 0 12px 0;
        color: #606266;
        line-height: 1.6;
      }

      .meta-info {
        display: flex;
        align-items: center;
        gap: 12px;

        .date-range {
          font-size: 14px;
          color: #909399;
        }
      }
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;
      margin-bottom: 20px;

      .stat-card {
        border-radius: 8px;
        transition: transform 0.2s ease;

        &:hover {
          transform: translateY(-2px);
        }

        .stat-content {
          text-align: center;
          padding: 8px 0;

          .stat-number {
            font-size: 28px;
            font-weight: 600;
            color: #303133;
            line-height: 1;
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 14px;
            color: #909399;
          }
        }

        &.completed .stat-number {
          color: #67c23a;
        }

        &.progress .stat-number {
          color: #e6a23c;
        }

        &.pending .stat-number {
          color: #909399;
        }

        &.overall .stat-number {
          color: #409eff;
        }
      }
    }

    .priority-card {
      margin-bottom: 20px;
      border-radius: 8px;

      .priority-stats {
        display: flex;
        gap: 24px;

        .priority-item {
          flex: 1;
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 12px 16px;
          border-radius: 6px;
          
          .priority-label {
            font-size: 14px;
            font-weight: 500;
          }

          .priority-count {
            font-size: 18px;
            font-weight: 600;
          }

          &.high {
            background-color: #fef0f0;
            border-left: 4px solid #f56c6c;

            .priority-label {
              color: #f56c6c;
            }

            .priority-count {
              color: #f56c6c;
            }
          }

          &.medium {
            background-color: #fdf6ec;
            border-left: 4px solid #e6a23c;

            .priority-label {
              color: #e6a23c;
            }

            .priority-count {
              color: #e6a23c;
            }
          }

          &.low {
            background-color: #ecf5ff;
            border-left: 4px solid #409eff;

            .priority-label {
              color: #409eff;
            }

            .priority-count {
              color: #409eff;
            }
          }
        }
      }
    }

    .progress-card {
      border-radius: 8px;

      .progress-container {
        padding: 20px 0;

        .progress-text {
          font-size: 14px;
          font-weight: 600;
        }
      }
    }
  }

  .empty-state {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 300px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .project-dashboard {
    padding: 16px;

    .selector-card .selector-header {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;
    }

    .dashboard-content {
      .stats-grid {
        grid-template-columns: repeat(2, 1fr);
        gap: 12px;
      }

      .priority-card .priority-stats {
        flex-direction: column;
        gap: 12px;
      }
    }
  }
}
</style>