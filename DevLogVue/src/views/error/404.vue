<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-icon">
        <i class="icon-404">404</i>
      </div>
      <h2 class="error-title">页面未找到</h2>
      <p class="error-description">
        抱歉，您访问的页面可能已被删除、重命名或暂时不可用。
      </p>
      <div class="error-actions">
        <el-button type="primary" @click="$router.go(-1)">返回上页</el-button>
        <el-button @click="$router.push('/')">回到首页</el-button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.error-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  background: #f8f9fa;
  
  .error-content {
    text-align: center;
    padding: 40px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    
    .error-icon {
      margin-bottom: 24px;
      
      .icon-404 {
        font-size: 72px;
        font-weight: bold;
        color: #8e8e93;
      }
    }
    
    .error-title {
      font-size: 24px;
      color: #000;
      margin-bottom: 16px;
      font-weight: 600;
    }
    
    .error-description {
      font-size: 14px;
      color: #636366;
      margin-bottom: 32px;
      line-height: 1.5;
    }
    
    .error-actions {
      display: flex;
      gap: 16px;
      justify-content: center;
    }
  }
}
</style>