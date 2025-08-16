<template>
  <div class="task-gantt-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="icon-gantt"></i>
          甘特图
        </h1>
        <p class="page-description">可视化任务时间线，掌控项目进度节奏</p>
      </div>
    </div>
    
    <div class="page-content">
      <div class="project-selector-card">
        <div class="card-header">
          <h3>选择项目</h3>
          <p>选择一个项目来查看其任务甘特图</p>
        </div>
        <div class="card-body">
          <el-select
            v-model="selectedProjectId"
            placeholder="请选择项目"
            filterable
            clearable
            style="width: 100%"
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
      </div>
      
      <div v-if="selectedProjectId" class="gantt-chart-wrapper">
        <TaskGanttChart :project-id="selectedProjectId" :key="selectedProjectId" />
      </div>
      
      <div v-else class="empty-state">
        <div class="empty-content">
          <i class="icon-empty"></i>
          <h3>请选择项目</h3>
          <p>选择一个项目来查看其任务甘特图</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import TaskGanttChart from '@/components/Task/TaskGanttChart.vue'
import { getProjectList } from '@/api/project'

const selectedProjectId = ref(null)
const projects = ref([])
const loading = ref(false)

const handleProjectChange = (projectId) => {
  selectedProjectId.value = projectId
}

const loadProjects = async () => {
  try {
    loading.value = true
    const response = await getProjectList({
      pageNum: 1,
      pageSize: 100
      // 不传递status参数，获取所有状态的项目
    })
    
    if (response.code === 200 && response.data?.records) {
      projects.value = response.data.records.map(project => ({
        id: project.id,
        name: project.name
      }))
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadProjects()
})
</script>

<style lang="scss" scoped>
.task-gantt-page {
  height: 100vh;
  background: #f5f5f7;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.page-header {
  background: #ffffff;
  border-bottom: 1px solid #d1d1d6;
  padding: 32px 40px;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
  z-index: 10;
  position: relative;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
  display: flex;
  align-items: center;
  gap: 16px;

  .icon-gantt {
    width: 36px;
    height: 36px;
    background: linear-gradient(135deg, #FF9500, #FF6347);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;

    &::before {
      content: '';
      width: 18px;
      height: 18px;
      background: #ffffff;
      mask: url("data:image/svg+xml,%3Csvg viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M4 6h16v2H4V6zm0 5h16v2H4v-2zm0 5h16v2H4v-2z' fill='currentColor'/%3E%3C/svg%3E") no-repeat center;
      mask-size: contain;
    }
  }
}

.page-description {
  font-size: 16px;
  color: #6e6e73;
  margin: 0;
  font-weight: 400;
  line-height: 1.5;
}

.page-content {
  flex: 1;
  padding: 24px 40px 40px;
  overflow: hidden;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.project-selector-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  border: 1px solid #e5e5ea;
  overflow: hidden;
  flex-shrink: 0;

  .card-header {
    padding: 24px 32px 16px;
    border-bottom: 1px solid #f2f2f7;

    h3 {
      font-size: 18px;
      font-weight: 600;
      color: #1d1d1f;
      margin: 0 0 4px 0;
    }

    p {
      font-size: 14px;
      color: #6e6e73;
      margin: 0;
    }
  }

  .card-body {
    padding: 16px 32px 24px;
  }
}

.gantt-chart-wrapper {
  flex: 1;
  animation: fadeIn 0.4s ease-out;
  overflow: hidden;
}

.empty-state {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 1px solid #e5e5ea;

  .empty-content {
    text-align: center;
    max-width: 320px;

    .icon-empty {
      width: 80px;
      height: 80px;
      background: #f2f2f7;
      border-radius: 50%;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 24px;
      position: relative;

      &::before {
        content: '';
        width: 40px;
        height: 40px;
        background: #8e8e93;
        mask: url("data:image/svg+xml,%3Csvg viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z' fill='currentColor'/%3E%3C/svg%3E") no-repeat center;
        mask-size: contain;
      }
    }

    h3 {
      font-size: 20px;
      font-weight: 600;
      color: #1d1d1f;
      margin: 0 0 8px 0;
    }

    p {
      font-size: 16px;
      color: #6e6e73;
      margin: 0;
      line-height: 1.5;
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式设计
@media screen and (max-width: 1024px) {
  .page-header {
    padding: 24px 32px;
  }
  
  .page-content {
    padding: 20px 32px 32px;
  }
  
  .page-title {
    font-size: 28px;
    
    .icon-gantt {
      width: 32px;
      height: 32px;
      
      &::before {
        width: 16px;
        height: 16px;
      }
    }
  }

  .project-selector-card {
    .card-header {
      padding: 20px 24px 12px;
    }

    .card-body {
      padding: 12px 24px 20px;
    }
  }
}

@media screen and (max-width: 768px) {
  .page-header {
    padding: 20px 24px;
  }
  
  .page-content {
    padding: 16px 24px 24px;
  }
  
  .page-title {
    font-size: 24px;
    gap: 12px;
    
    .icon-gantt {
      width: 28px;
      height: 28px;
    }
  }
  
  .page-description {
    font-size: 14px;
  }

  .project-selector-card {
    .card-header {
      padding: 16px 20px 8px;

      h3 {
        font-size: 16px;
      }

      p {
        font-size: 13px;
      }
    }

    .card-body {
      padding: 8px 20px 16px;
    }
  }
}

@media screen and (max-width: 480px) {
  .page-header {
    padding: 16px 20px;
  }
  
  .page-content {
    padding: 12px 20px 20px;
  }
  
  .page-title {
    font-size: 22px;
    gap: 8px;
    
    .icon-gantt {
      width: 24px;
      height: 24px;
      
      &::before {
        width: 12px;
        height: 12px;
      }
    }
  }
}
</style>