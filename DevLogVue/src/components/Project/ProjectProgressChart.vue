<template>
  <div class="project-progress-chart">
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span class="chart-title">项目进度概览</span>
          <el-select 
            v-model="selectedProject" 
            placeholder="选择项目"
            clearable
            style="width: 200px"
            @change="handleProjectChange"
          >
            <el-option
              v-for="project in projects"
              :key="project.id"
              :label="project.name"
              :value="project.id"
            />
          </el-select>
        </div>
      </template>
      
      <div class="chart-container">
        <div class="progress-summary" v-if="projectData">
          <div class="summary-item">
            <div class="summary-label">总任务数</div>
            <div class="summary-value total">{{ projectData.totalTasks }}</div>
          </div>
          <div class="summary-item">
            <div class="summary-label">已完成</div>
            <div class="summary-value completed">{{ projectData.completedTasks }}</div>
          </div>
          <div class="summary-item">
            <div class="summary-label">进行中</div>
            <div class="summary-value inprogress">{{ projectData.inProgressTasks }}</div>
          </div>
          <div class="summary-item">
            <div class="summary-label">待处理</div>
            <div class="summary-value pending">{{ projectData.pendingTasks }}</div>
          </div>
          <div class="summary-item">
            <div class="summary-label">总进度</div>
            <div class="summary-value progress">{{ projectData.overallProgress }}%</div>
          </div>
        </div>
        
        <div class="charts-row">
          <div class="chart-section">
            <h4 class="chart-section-title">任务状态分布</h4>
            <div ref="pieChartRef" class="chart-box pie-chart"></div>
          </div>
          <div class="chart-section">
            <h4 class="chart-section-title">任务进度趋势</h4>
            <div ref="lineChartRef" class="chart-box line-chart"></div>
          </div>
        </div>
        
        <div class="chart-section full-width">
          <h4 class="chart-section-title">任务优先级分布</h4>
          <div ref="barChartRef" class="chart-box bar-chart"></div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import * as echarts from 'echarts';
import { getAllProjects, getProjectTaskStats, getProjectProgressTrend } from '@/api/project';

// 组件状态
const selectedProject = ref('');
const projects = ref([]);
const projectData = ref(null);

// 图表引用
const pieChartRef = ref(null);
const lineChartRef = ref(null);
const barChartRef = ref(null);

// 图表实例
let pieChart = null;
let lineChart = null;
let barChart = null;

// 获取项目列表
const fetchProjects = async () => {
  try {
    const response = await getAllProjects();
    if (response.code === 200) {
      projects.value = response.data || [];
      if (projects.value.length > 0) {
        selectedProject.value = projects.value[0].id;
        await loadProjectData();
      }
    } else {
      ElMessage.error('获取项目列表失败');
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
    ElMessage.error('获取项目列表失败，请检查网络连接');
  }
};

// 加载项目数据
const loadProjectData = async () => {
  if (!selectedProject.value) {
    projectData.value = null;
    return;
  }

  try {
    const [statsResponse, trendResponse] = await Promise.all([
      getProjectTaskStats(selectedProject.value),
      getProjectProgressTrend(selectedProject.value)
    ]);

    if (statsResponse.code === 200) {
      projectData.value = statsResponse.data;
      await nextTick();
      initCharts();
    } else {
      ElMessage.error('获取项目统计数据失败');
    }
  } catch (error) {
    console.error('加载项目数据失败:', error);
    ElMessage.error('加载项目数据失败，请检查后端API是否正常');
  }
};

// 项目切换处理
const handleProjectChange = () => {
  loadProjectData();
};

// 初始化图表
const initCharts = () => {
  initPieChart();
  initLineChart();
  initBarChart();
};

// 初始化饼图
const initPieChart = () => {
  if (pieChart) {
    pieChart.dispose();
  }

  pieChart = echarts.init(pieChartRef.value);
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    color: ['#28a745', '#ffc107', '#6c757d'],
    series: [
      {
        name: '任务状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: projectData.value?.completedTasks || 0, name: '已完成' },
          { value: projectData.value?.inProgressTasks || 0, name: '进行中' },
          { value: projectData.value?.pendingTasks || 0, name: '待处理' }
        ]
      }
    ]
  };

  pieChart.setOption(option);
};

// 初始化折线图
const initLineChart = () => {
  if (lineChart) {
    lineChart.dispose();
  }

  lineChart = echarts.init(lineChartRef.value);
  
  // 模拟进度趋势数据（实际项目中应从API获取）
  const dates = [];
  const progressData = [];
  const today = new Date();
  
  for (let i = 6; i >= 0; i--) {
    const date = new Date(today);
    date.setDate(date.getDate() - i);
    dates.push(date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }));
    progressData.push(Math.min(100, (projectData.value?.overallProgress || 0) - i * 5 + Math.random() * 10));
  }

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: '完成进度',
        type: 'line',
        smooth: true,
        itemStyle: {
          color: '#007AFF'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0, color: 'rgba(0, 122, 255, 0.3)'
            }, {
              offset: 1, color: 'rgba(0, 122, 255, 0.1)'
            }]
          }
        },
        data: progressData
      }
    ]
  };

  lineChart.setOption(option);
};

// 初始化柱状图
const initBarChart = () => {
  if (barChart) {
    barChart.dispose();
  }

  barChart = echarts.init(barChartRef.value);
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['高优先级', '中优先级', '低优先级']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '任务数量',
        type: 'bar',
        barWidth: '60%',
        itemStyle: {
          color: function(params) {
            const colors = ['#dc3545', '#ffc107', '#28a745'];
            return colors[params.dataIndex];
          },
          borderRadius: [4, 4, 0, 0]
        },
        data: [
          projectData.value?.highPriorityTasks || 0,
          projectData.value?.mediumPriorityTasks || 0,
          projectData.value?.lowPriorityTasks || 0
        ]
      }
    ]
  };

  barChart.setOption(option);
};

// 窗口大小改变时重新调整图表
const handleResize = () => {
  pieChart?.resize();
  lineChart?.resize();
  barChart?.resize();
};

// 组件挂载
onMounted(() => {
  fetchProjects();
  window.addEventListener('resize', handleResize);
});

// 组件卸载
onUnmounted(() => {
  pieChart?.dispose();
  lineChart?.dispose();
  barChart?.dispose();
  window.removeEventListener('resize', handleResize);
});
</script>

<style lang="scss" scoped>
.project-progress-chart {
  .chart-card {
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(148, 163, 184, 0.2);
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .chart-title {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
      }
    }
  }
  
  .chart-container {
    .progress-summary {
      display: grid;
      grid-template-columns: repeat(5, 1fr);
      gap: 20px;
      margin-bottom: 30px;
      padding: 20px;
      background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
      border-radius: 12px;
      
      .summary-item {
        text-align: center;
        
        .summary-label {
          font-size: 13px;
          color: #6c757d;
          margin-bottom: 8px;
        }
        
        .summary-value {
          font-size: 24px;
          font-weight: 700;
          
          &.total {
            color: #495057;
          }
          
          &.completed {
            color: #28a745;
          }
          
          &.inprogress {
            color: #ffc107;
          }
          
          &.pending {
            color: #6c757d;
          }
          
          &.progress {
            color: #007AFF;
          }
        }
      }
    }
    
    .charts-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 30px;
      margin-bottom: 30px;
    }
    
    .chart-section {
      background: white;
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      border: 1px solid #f0f2f5;
      
      &.full-width {
        grid-column: 1 / -1;
      }
      
      .chart-section-title {
        margin: 0 0 16px 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        text-align: center;
      }
    }
    
    .chart-box {
      &.pie-chart,
      &.line-chart {
        height: 300px;
      }
      
      &.bar-chart {
        height: 250px;
      }
    }
  }
}
</style>