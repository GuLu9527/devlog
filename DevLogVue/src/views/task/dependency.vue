<template>
  <div class="task-dependency-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="icon-dependency"></i>
          任务依赖管理
        </h1>
        <p class="page-description">管理任务之间的依赖关系，优化项目执行流程</p>
      </div>
    </div>
    
    <div class="page-content">
      <div class="task-selector-card">
        <div class="card-header">
          <h3>选择任务</h3>
          <p>请选择要管理依赖关系的任务</p>
        </div>
        <div class="card-body">
          <el-select
            v-model="selectedTaskId"
            placeholder="请选择任务"
            filterable
            clearable
            style="width: 100%"
            @change="handleTaskChange"
          >
            <el-option
              v-for="task in tasks"
              :key="task.id"
              :label="`${task.title} (${task.projectName})`"
              :value="task.id"
            />
          </el-select>
        </div>
      </div>
      
      <div v-if="selectedTaskId" class="dependency-manager-wrapper">
        <TaskDependencyManager :task-id="selectedTaskId" :key="selectedTaskId" />
      </div>
      
      <div v-else class="empty-state">
        <div class="empty-content">
          <i class="icon-empty"></i>
          <h3>请选择任务</h3>
          <p>选择一个任务来查看和管理其依赖关系</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import TaskDependencyManager from '@/components/Task/TaskDependencyManager.vue'
import { getSimpleTaskList } from '@/api/task'

const selectedTaskId = ref(null)
const tasks = ref([])
const loading = ref(false)

const handleTaskChange = (taskId) => {
  selectedTaskId.value = taskId
}

const loadTasks = async () => {
  try {
    loading.value = true
    const response = await getSimpleTaskList({
      pageNum: 1,
      pageSize: 100
      // 不传递status参数，获取所有状态的任务
    })
    
    if (response.code === 200 && response.data) {
      const taskList = Array.isArray(response.data) ? response.data : response.data.records || []
      tasks.value = taskList.map(task => ({
        id: task.id,
        title: task.title,
        projectName: task.projectName || '未分配'
      }))
    }
  } catch (error) {
    console.error('加载任务列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTasks()
})
</script>

<style lang="scss" scoped>
.task-dependency-page {
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

  .icon-dependency {
    width: 36px;
    height: 36px;
    background: linear-gradient(135deg, #34C759, #00D2FF);
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
      mask: url("data:image/svg+xml,%3Csvg viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M16 4h-2c-1.1 0-2 .9-2 2v2c0 1.1.9 2 2 2h2c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm-6 14h-2c-1.1 0-2-.9-2-2v-2c0-1.1.9-2 2-2h2c1.1 0 2 .9 2 2v2c0 1.1-.9 2-2 2zm3-8.5L11.5 8 10 9.5 8.5 8 7 9.5v5l1.5-1.5L10 14.5l1.5-1.5L13 14.5v-5z' fill='currentColor'/%3E%3C/svg%3E") no-repeat center;
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
  overflow: auto;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.task-selector-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  border: 1px solid #e5e5ea;
  overflow: hidden;

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

.dependency-manager-wrapper {
  animation: fadeIn 0.4s ease-out;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
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
        mask: url("data:image/svg+xml,%3Csvg viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z' fill='currentColor'/%3E%3C/svg%3E") no-repeat center;
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
    
    .icon-dependency {
      width: 32px;
      height: 32px;
      
      &::before {
        width: 16px;
        height: 16px;
      }
    }
  }

  .task-selector-card {
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
    
    .icon-dependency {
      width: 28px;
      height: 28px;
    }
  }
  
  .page-description {
    font-size: 14px;
  }

  .task-selector-card {
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
    
    .icon-dependency {
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