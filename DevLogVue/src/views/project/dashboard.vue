<template>
  <div class="project-dashboard-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="icon-dashboard"></i>
          项目仪表板
        </h1>
        <p class="page-description">综合查看项目进度、任务分布和团队效率分析</p>
      </div>
    </div>
    
    <div class="page-content">
      <ProjectDashboard />
    </div>
  </div>
</template>

<script setup>
import ProjectDashboard from '@/components/Project/ProjectDashboard.vue'
</script>

<style lang="scss" scoped>
.project-dashboard-page {
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

  .icon-dashboard {
    width: 36px;
    height: 36px;
    background: linear-gradient(135deg, #007AFF, #5856D6);
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
      mask: url("data:image/svg+xml,%3Csvg viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z' fill='currentColor'/%3E%3C/svg%3E") no-repeat center;
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
    
    .icon-dashboard {
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
    
    .icon-dashboard {
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
    
    .icon-dashboard {
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