<template>
  <div class="task-template-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="icon-template"></i>
          任务模板
        </h1>
        <p class="page-description">创建和管理任务模板，提高项目执行效率</p>
      </div>
    </div>
    
    <div class="page-content">
      <TaskTemplateManager />
    </div>
  </div>
</template>

<script setup>
import TaskTemplateManager from '@/components/Task/TaskTemplateManager.vue'
</script>

<style lang="scss" scoped>
.task-template-page {
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

  .icon-template {
    width: 36px;
    height: 36px;
    background: linear-gradient(135deg, #5856D6, #007AFF);
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
      mask: url("data:image/svg+xml,%3Csvg viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6z' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3Cpath d='M14 2v6h6' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3Cpath d='M16 13H8' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3Cpath d='M16 17H8' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3Cpath d='M10 9H8' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E") no-repeat center;
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
    
    .icon-template {
      width: 32px;
      height: 32px;
      
      &::before {
        width: 16px;
        height: 16px;
      }
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
    
    .icon-template {
      width: 28px;
      height: 28px;
    }
  }
  
  .page-description {
    font-size: 14px;
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
    
    .icon-template {
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