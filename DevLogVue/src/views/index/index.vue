<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import StatCard from '@/components/Charts/StatCard.vue'
import WorkHoursTrendChart from '@/components/Charts/WorkHoursTrendChart.vue'
import TaskStatusChart from '@/components/Charts/TaskStatusChart.vue'
import { DashboardIcons } from '@/components/SvgIcons'
import { getDashboardData, getPersonalOverview } from '@/api/statistics'

// 响应式数据
const loading = ref(false)
const dashboardData = ref({})
const personalData = ref({})

// 获取仪表板数据
const fetchDashboardData = async () => {
  loading.value = true
  try {
    const [dashboardResponse, personalResponse] = await Promise.all([
      getDashboardData(),
      getPersonalOverview()
    ])
    
    dashboardData.value = dashboardResponse.data || {}
    personalData.value = personalResponse.data || {}
  } catch (error) {
    console.error('获取仪表板数据失败:', error)
    ElMessage.error('获取仪表板数据失败')
  } finally {
    loading.value = false
  }
}

// 计算趋势文本
const getMonthTrend = (currentValue, type) => {
  // 这里可以根据实际数据计算趋势，暂时返回示例
  const trends = {
    tasks: '较上月 +12%',
    hours: '较上月 +8%',
    projects: '较上月 +5%',
    users: '较上月 +3%'
  }
  return trends[type] || ''
}

// 获取状态对应的CSS类名  
const getStatusClass = (status) => {
  return status.replace(/[^a-zA-Z0-9\u4e00-\u9fa5]/g, '')
}

// 刷新数据
const refresh = () => {
  fetchDashboardData()
}

// 生命周期
onMounted(() => {
  fetchDashboardData()
})
</script>

<template>
  <div class="dashboard-container">
    <!-- 页面头部 -->
    <div class="dashboard-header">
      <div class="header-content">
        <h1 class="dashboard-title">工作台</h1>
        <div class="dashboard-subtitle">
          欢迎回来，今天也要继续加油哦！
        </div>
      </div>
      <div class="header-actions">
        <button class="refresh-btn" @click="refresh">
          <DashboardIcons type="refresh" :size="18" />
          <span>刷新数据</span>
        </button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card" :class="{ loading: loading }">
        <div class="stat-icon">
          <DashboardIcons type="clock" :size="24" />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ personalData.todayHours || 0 }}<span class="stat-unit">小时</span></div>
          <div class="stat-label">今日工时</div>
          <div class="stat-extra">本周累计: {{ personalData.weekHours || 0 }} 小时</div>
        </div>
      </div>
      
      <div class="stat-card" :class="{ loading: loading }">
        <div class="stat-icon">
          <DashboardIcons type="document" :size="24" />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboardData.monthTasks?.totalTasks || 0 }}</div>
          <div class="stat-label">本月任务</div>
          <div class="stat-extra">{{ getMonthTrend(dashboardData.monthTasks?.totalTasks, 'tasks') }}</div>
        </div>
      </div>
      
      <div class="stat-card" :class="{ loading: loading }">
        <div class="stat-icon">
          <DashboardIcons type="folder" :size="24" />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboardData.totalProjects || 0 }}</div>
          <div class="stat-label">项目总数</div>
          <div class="stat-extra">{{ getMonthTrend(dashboardData.totalProjects, 'projects') }}</div>
        </div>
      </div>
      
      <div class="stat-card" :class="{ loading: loading }">
        <div class="stat-icon">
          <DashboardIcons type="user" :size="24" />
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboardData.totalUsers || 0 }}</div>
          <div class="stat-label">团队成员</div>
          <div class="stat-extra">{{ getMonthTrend(dashboardData.totalUsers, 'users') }}</div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 左侧图表列 -->
      <div class="charts-col">
        <!-- 工时趋势图 -->
        <div class="chart-item">
          <WorkHoursTrendChart 
            title="最近7天工时趋势"
            :date-range="[new Date(Date.now() - 7 * 24 * 60 * 60 * 1000), new Date()]"
          />
        </div>
        
        <!-- 个人工作概览 -->
        <div class="chart-item">
          <div class="personal-overview">
            <h3 class="overview-title">
              <DashboardIcons type="dashboard" :size="20" />
              个人工作概览
            </h3>
            <div class="overview-content">
              <div class="overview-item">
                <div class="overview-label">
                  <DashboardIcons type="timer" :size="16" />
                  本月工时
                </div>
                <div class="overview-value">
                  {{ personalData.monthHours || 0 }} 小时
                </div>
              </div>
              <div class="overview-item">
                <div class="overview-label">
                  <DashboardIcons type="document" :size="16" />
                  我的任务
                </div>
                <div class="overview-value">
                  {{ personalData.myTasks?.totalTasks || 0 }} 个
                </div>
              </div>
              <div class="overview-item">
                <div class="overview-label">
                  <DashboardIcons type="trend" :size="16" />
                  完成率
                </div>
                <div class="overview-value">
                  {{ (personalData.myTasks?.completionRate || 0).toFixed(1) }}%
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧图表列 -->
      <div class="charts-col">
        <!-- 任务状态分布 -->
        <div class="chart-item">
          <TaskStatusChart 
            title="本月任务状态分布"
            :date-range="[new Date(new Date().getFullYear(), new Date().getMonth(), 1), new Date()]"
          />
        </div>
        
        <!-- 项目状态概览 -->
        <div class="chart-item">
          <div class="project-overview">
            <h3 class="overview-title">
              <DashboardIcons type="folder" :size="20" />
              项目状态概览
            </h3>
            <div class="project-stats" v-if="dashboardData.projectStats?.statusStatistics">
              <div 
                v-for="(count, status) in dashboardData.projectStats.statusStatistics" 
                :key="status"
                class="project-stat-item"
              >
                <div class="stat-dot" :class="`stat-${getStatusClass(status)}`"></div>
                <span class="stat-label">{{ status }}</span>
                <span class="stat-count">{{ count }}</span>
              </div>
            </div>
            <div v-else class="empty-state">
              <DashboardIcons type="folder" :size="48" />
              <p>暂无项目数据</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <h3 class="section-title">
        <DashboardIcons type="dashboard" :size="20" />
        快速操作
      </h3>
      <div class="action-buttons">
        <button class="action-btn primary" @click="$router.push('/worklog')">
          <DashboardIcons type="document" :size="18" />
          <span>写日志</span>
        </button>
        <button class="action-btn secondary" @click="$router.push('/task')">
          <DashboardIcons type="folder" :size="18" />
          <span>查看任务</span>
        </button>
        <button class="action-btn secondary" @click="$router.push('/project')">
          <DashboardIcons type="calendar" :size="18" />
          <span>项目管理</span>
        </button>
        <button class="action-btn secondary" @click="$router.push('/user')">
          <DashboardIcons type="user" :size="18" />
          <span>成员管理</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f5f7 0%, #e5e5ea 100%);
  box-sizing: border-box;
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}

.header-content {
  flex: 1;
}

.dashboard-title {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.dashboard-subtitle {
  font-size: 17px;
  color: #6e6e73;
  font-weight: 400;
  line-height: 1.4;
}

.header-actions {
  margin-top: 8px;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: #ffffff;
  border: 1px solid #d1d1d6;
  border-radius: 12px;
  color: #1d1d1f;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.refresh-btn:hover {
  background: #f2f2f7;
  border-color: #c7c7cc;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.refresh-btn:active {
  transform: translateY(-1px) scale(0.98);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 24px;
  background: linear-gradient(135deg, #ffffff 0%, #fafafa 100%);
  border: 1px solid rgba(209, 209, 214, 0.6);
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #007AFF 0%, #0056CC 100%);
  opacity: 0;
  transition: opacity 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
}

.stat-card:nth-child(1)::before {
  background: linear-gradient(90deg, #34C759 0%, #28A745 100%);
}

.stat-card:nth-child(2)::before {
  background: linear-gradient(90deg, #007AFF 0%, #0056CC 100%);
}

.stat-card:nth-child(3)::before {
  background: linear-gradient(90deg, #FF9500 0%, #E8800A 100%);
}

.stat-card:nth-child(4)::before {
  background: linear-gradient(90deg, #8E8E93 0%, #6D6D70 100%);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  border-color: rgba(199, 199, 204, 0.8);
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-card:active {
  transform: translateY(-2px) scale(0.98);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.stat-card.loading {
  opacity: 0.7;
  pointer-events: none;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, rgba(0, 122, 255, 0.1) 0%, rgba(0, 122, 255, 0.05) 100%);
  border-radius: 12px;
  margin-right: 16px;
  flex-shrink: 0;
  position: relative;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  overflow: hidden;
}

.stat-icon::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s ease;
}

.stat-card:hover .stat-icon {
  transform: scale(1.05) rotate(5deg);
  background: linear-gradient(135deg, rgba(0, 122, 255, 0.15) 0%, rgba(0, 122, 255, 0.08) 100%);
}

.stat-card:hover .stat-icon::before {
  left: 100%;
}

.stat-card:nth-child(1) .stat-icon {
  background: linear-gradient(135deg, rgba(52, 199, 89, 0.1) 0%, rgba(52, 199, 89, 0.05) 100%);
}

.stat-card:nth-child(1):hover .stat-icon {
  background: linear-gradient(135deg, rgba(52, 199, 89, 0.15) 0%, rgba(52, 199, 89, 0.08) 100%);
}

.stat-card:nth-child(2) .stat-icon {
  background: linear-gradient(135deg, rgba(0, 122, 255, 0.1) 0%, rgba(0, 122, 255, 0.05) 100%);
}

.stat-card:nth-child(2):hover .stat-icon {
  background: linear-gradient(135deg, rgba(0, 122, 255, 0.15) 0%, rgba(0, 122, 255, 0.08) 100%);
}

.stat-card:nth-child(3) .stat-icon {
  background: linear-gradient(135deg, rgba(255, 149, 0, 0.1) 0%, rgba(255, 149, 0, 0.05) 100%);
}

.stat-card:nth-child(3):hover .stat-icon {
  background: linear-gradient(135deg, rgba(255, 149, 0, 0.15) 0%, rgba(255, 149, 0, 0.08) 100%);
}

.stat-card:nth-child(4) .stat-icon {
  background: linear-gradient(135deg, rgba(142, 142, 147, 0.1) 0%, rgba(142, 142, 147, 0.05) 100%);
}

.stat-card:nth-child(4):hover .stat-icon {
  background: linear-gradient(135deg, rgba(142, 142, 147, 0.15) 0%, rgba(142, 142, 147, 0.08) 100%);
}

.stat-content {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
  line-height: 1.2;
  margin-bottom: 4px;
}

.stat-unit {
  font-size: 14px;
  font-weight: 400;
  color: #64748b;
  margin-left: 4px;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
  margin-bottom: 4px;
}

.stat-extra {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 400;
}

.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 32px;
}

.charts-col {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.chart-item {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.9) 100%);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(209, 209, 214, 0.3);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  position: relative;
}

.chart-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #007AFF 0%, #34C759 50%, #FF9500 100%);
  opacity: 0;
  transition: opacity 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
}

.chart-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border-color: rgba(199, 199, 204, 0.5);
  background: linear-gradient(135deg, rgba(255, 255, 255, 1) 0%, rgba(248, 250, 252, 0.95) 100%);
}

.chart-item:hover::before {
  opacity: 1;
}

.chart-item:active {
  transform: translateY(-1px) scale(0.998);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
}

.personal-overview,
.project-overview {
  padding: 24px;
  background: transparent;
  border-radius: 0;
  box-shadow: none;
}

.overview-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #334155;
  margin: 0 0 20px 0;
}

.overview-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.overview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid rgba(148, 163, 184, 0.15);
}

.overview-item:last-child {
  border-bottom: none;
}

.overview-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.overview-value {
  font-size: 16px;
  font-weight: 600;
  color: #334155;
}

.project-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.project-stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  transition: all 0.2s ease;
}

.project-stat-item:hover {
  background: rgba(148, 163, 184, 0.05);
  border-radius: 8px;
  padding: 12px;
}

.stat-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.stat-规划中 { background-color: #94a3b8; }
.stat-开发中 { background-color: #64748b; }
.stat-测试中 { background-color: #475569; }
.stat-已上线 { background-color: #334155; }
.stat-维护中 { background-color: #64748b; }
.stat-已关闭 { background-color: #cbd5e1; }

.stat-label {
  flex: 1;
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.stat-count {
  font-weight: 600;
  color: #334155;
  font-size: 16px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #94a3b8;
}

.empty-state p {
  margin-top: 12px;
  font-size: 14px;
}

.quick-actions {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.9) 100%);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(209, 209, 214, 0.3);
  padding: 32px;
  border-radius: 16px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
}

.quick-actions::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #34C759 0%, #007AFF 50%, #FF9500 100%);
  opacity: 0.8;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0 0 24px 0;
  letter-spacing: -0.3px;
}

.action-buttons {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 24px;
  border: 1px solid rgba(209, 209, 214, 0.4);
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(248, 250, 252, 0.7) 100%);
  color: #1d1d1f;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
  border-color: rgba(199, 199, 204, 0.6);
  background: linear-gradient(135deg, rgba(255, 255, 255, 1) 0%, rgba(248, 250, 252, 0.9) 100%);
}

.action-btn:hover::before {
  left: 100%;
}

.action-btn:active {
  transform: translateY(-1px) scale(0.98);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.action-btn.primary {
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  color: #ffffff;
  border-color: #007AFF;
  box-shadow: 0 4px 16px rgba(0, 122, 255, 0.3);
}

.action-btn.primary:hover {
  background: linear-gradient(135deg, #0056CC 0%, #004499 100%);
  border-color: #0056CC;
  box-shadow: 0 8px 32px rgba(0, 122, 255, 0.4);
}

.action-btn.secondary {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.8) 100%);
  color: #6e6e73;
}

.action-btn.secondary:hover {
  background: linear-gradient(135deg, rgba(255, 255, 255, 1) 0%, rgba(242, 242, 247, 0.9) 100%);
  color: #1d1d1f;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .dashboard-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 16px;
  }
  
  .charts-grid {
    gap: 16px;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .stat-card {
    padding: 20px;
  }
  
  .stat-value {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .dashboard-container {
    padding: 12px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-btn {
    justify-content: center;
  }
}
</style>