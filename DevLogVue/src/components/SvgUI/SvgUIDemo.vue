<template>
  <div class="svg-ui-demo">
    <div class="demo-header">
      <h1>SVG UI 组件库演示</h1>
      <p>基于SVG的iOS风格UI组件库，提供美观的视觉效果和流畅的交互体验</p>
    </div>

    <!-- 按钮组件演示 -->
    <section class="demo-section">
      <h2>SvgButton 按钮组件</h2>
      
      <!-- 基础按钮 -->
      <div class="demo-group">
        <h3>基础用法</h3>
        <div class="demo-row">
          <SvgButton type="primary" text="主要按钮" />
          <SvgButton type="secondary" text="次要按钮" />
          <SvgButton type="success" text="成功按钮" />
          <SvgButton type="warning" text="警告按钮" />
          <SvgButton type="danger" text="危险按钮" />
          <SvgButton type="info" text="信息按钮" />
          <SvgButton type="text" text="文本按钮" />
        </div>
      </div>

      <!-- 不同尺寸 -->
      <div class="demo-group">
        <h3>不同尺寸</h3>
        <div class="demo-row">
          <SvgButton size="mini" text="迷你按钮" />
          <SvgButton size="small" text="小型按钮" />
          <SvgButton size="medium" text="中等按钮" />
          <SvgButton size="large" text="大型按钮" />
        </div>
      </div>

      <!-- 带图标的按钮 -->
      <div class="demo-group">
        <h3>带图标</h3>
        <div class="demo-row">
          <SvgButton type="primary" text="编辑" :icon="EditIcon" />
          <SvgButton type="success" text="保存" :icon="SaveIcon" />
          <SvgButton type="danger" text="删除" :icon="DeleteIcon" />
          <SvgButton circle :icon="AddIcon" />
        </div>
      </div>

      <!-- 特殊状态 -->
      <div class="demo-group">
        <h3>特殊状态</h3>
        <div class="demo-row">
          <SvgButton text="加载中" :loading="true" />
          <SvgButton text="禁用状态" :disabled="true" />
          <SvgButton text="圆角按钮" round />
          <SvgButton text="朴素按钮" plain />
        </div>
      </div>
    </section>

    <!-- 输入框组件演示 -->
    <section class="demo-section">
      <h2>SvgInput 输入框组件</h2>
      
      <!-- 基础输入框 -->
      <div class="demo-group">
        <h3>基础用法</h3>
        <div class="demo-grid">
          <SvgInput v-model="textValue" placeholder="请输入文本" />
          <SvgInput v-model="passwordValue" type="password" placeholder="请输入密码" />
          <SvgInput v-model="emailValue" type="email" placeholder="请输入邮箱" />
          <SvgInput v-model="numberValue" type="number" placeholder="请输入数字" />
        </div>
      </div>

      <!-- 不同尺寸 -->
      <div class="demo-group">
        <h3>不同尺寸</h3>
        <div class="demo-grid">
          <SvgInput v-model="sizeDemo1" size="small" placeholder="小尺寸输入框" />
          <SvgInput v-model="sizeDemo2" size="medium" placeholder="中等尺寸输入框" />
          <SvgInput v-model="sizeDemo3" size="large" placeholder="大尺寸输入框" />
        </div>
      </div>

      <!-- 带图标 -->
      <div class="demo-group">
        <h3>带图标</h3>
        <div class="demo-grid">
          <SvgInput v-model="searchValue" placeholder="搜索" :prefix-icon="SearchIcon" />
          <SvgInput v-model="userValue" placeholder="用户名" :suffix-icon="UserIcon" />
          <SvgInput v-model="clearableValue" placeholder="可清除" clearable />
          <SvgInput v-model="showPasswordValue" type="password" placeholder="显示密码" show-password />
        </div>
      </div>

      <!-- 验证状态 -->
      <div class="demo-group">
        <h3>验证状态</h3>
        <div class="demo-grid">
          <SvgInput v-model="successValue" placeholder="成功状态" validate-status="success" help-text="输入正确" />
          <SvgInput v-model="warningValue" placeholder="警告状态" validate-status="warning" help-text="请注意格式" />
          <SvgInput v-model="errorValue" placeholder="错误状态" validate-status="error" error-message="输入格式错误" />
          <SvgInput v-model="disabledValue" placeholder="禁用状态" disabled />
        </div>
      </div>
    </section>

    <!-- 卡片组件演示 -->
    <section class="demo-section">
      <h2>SvgCard 卡片组件</h2>
      
      <!-- 基础卡片 -->
      <div class="demo-group">
        <h3>基础用法</h3>
        <div class="demo-card-grid">
          <SvgCard title="默认卡片" subtitle="这是一张默认样式的卡片">
            <p>卡片内容区域，可以放置任意内容。支持文本、图片、表单等各种元素。</p>
          </SvgCard>
          
          <SvgCard title="主要卡片" type="primary" :icon="CardIcon">
            <p>这是一张主要样式的卡片，通常用于重要信息的展示。</p>
            <template #footer>
              <div style="text-align: right;">
                <SvgButton size="small" text="查看详情" />
              </div>
            </template>
          </SvgCard>
        </div>
      </div>

      <!-- 不同类型 -->
      <div class="demo-group">
        <h3>不同类型</h3>
        <div class="demo-card-grid">
          <SvgCard title="成功" type="success" :icon="SuccessIcon" size="small">
            <p>操作成功完成</p>
          </SvgCard>
          
          <SvgCard title="警告" type="warning" :icon="WarningIcon" size="small">
            <p>请注意相关事项</p>
          </SvgCard>
          
          <SvgCard title="错误" type="danger" :icon="ErrorIcon" size="small">
            <p>操作执行失败</p>
          </SvgCard>
          
          <SvgCard title="信息" type="info" :icon="InfoIcon" size="small">
            <p>一般信息提示</p>
          </SvgCard>
        </div>
      </div>

      <!-- 特殊功能 -->
      <div class="demo-group">
        <h3>特殊功能</h3>
        <div class="demo-card-grid">
          <SvgCard 
            title="可点击卡片" 
            subtitle="点击我试试" 
            clickable 
            glow-on-hover
            @click="handleCardClick"
          >
            <p>这张卡片是可点击的，同时具有悬停发光效果。</p>
          </SvgCard>
          
          <SvgCard title="加载状态" :loading="cardLoading">
            <p>展示加载状态的卡片</p>
            <template #footer>
              <SvgButton size="small" text="切换加载" @click="toggleCardLoading" />
            </template>
          </SvgCard>
        </div>
      </div>
    </section>

    <!-- 组合使用示例 -->
    <section class="demo-section">
      <h2>组合使用示例</h2>
      <SvgCard title="用户信息表单" type="primary" :icon="FormIcon">
        <div class="form-demo">
          <div class="form-row">
            <label>用户名：</label>
            <SvgInput v-model="formData.username" placeholder="请输入用户名" :prefix-icon="UserIcon" />
          </div>
          <div class="form-row">
            <label>邮箱：</label>
            <SvgInput v-model="formData.email" type="email" placeholder="请输入邮箱" />
          </div>
          <div class="form-row">
            <label>密码：</label>
            <SvgInput v-model="formData.password" type="password" placeholder="请输入密码" show-password />
          </div>
        </div>
        <template #footer>
          <div style="display: flex; gap: 12px; justify-content: flex-end;">
            <SvgButton text="重置" @click="resetForm" />
            <SvgButton type="primary" text="提交" @click="submitForm" />
          </div>
        </template>
      </SvgCard>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { SvgButton, SvgInput, SvgCard } from './index.js'

// 输入框数据
const textValue = ref('')
const passwordValue = ref('')
const emailValue = ref('')
const numberValue = ref('')
const sizeDemo1 = ref('')
const sizeDemo2 = ref('')
const sizeDemo3 = ref('')
const searchValue = ref('')
const userValue = ref('')
const clearableValue = ref('')
const showPasswordValue = ref('')
const successValue = ref('正确的输入')
const warningValue = ref('需要注意')
const errorValue = ref('错误的输入')
const disabledValue = ref('禁用状态')

// 卡片数据
const cardLoading = ref(false)

// 表单数据
const formData = ref({
  username: '',
  email: '',
  password: ''
})

// 模拟图标组件（实际使用时替换为真实图标）
const EditIcon = { template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M3 17.46v3.04c0 .28.22.5.5.5h3.04c.13 0 .26-.05.35-.15L17.81 9.94l-3.75-3.75L3.15 17.1c-.1.1-.15.22-.15.36zM20.71 7.04a.996.996 0 0 0 0-1.41l-2.34-2.34a.996.996 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/></svg>' }
const SaveIcon = { template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M17 3H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V7l-4-4zm2 16H5V5h11.17L19 7.83V19zm-7-7c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3zM6 6h9v4H6z"/></svg>' }
const DeleteIcon = { template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>' }
const AddIcon = { template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/></svg>' }
const SearchIcon = { template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/></svg>' }
const UserIcon = { template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>' }
const CardIcon = { template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14z"/></svg>' }
const SuccessIcon = { template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg>' }
const WarningIcon = { template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/></svg>' }
const ErrorIcon = { template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/></svg>' }
const InfoIcon = { template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/></svg>' }
const FormIcon = { template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm4 16H6V4h7v5h5v9z"/></svg>' }

// 事件处理
const handleCardClick = () => {
  alert('卡片被点击了！')
}

const toggleCardLoading = () => {
  cardLoading.value = !cardLoading.value
}

const resetForm = () => {
  formData.value = {
    username: '',
    email: '',
    password: ''
  }
}

const submitForm = () => {
  console.log('表单数据：', formData.value)
  alert('表单提交成功！')
}
</script>

<style scoped>
.svg-ui-demo {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.demo-header {
  text-align: center;
  margin-bottom: 48px;
}

.demo-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0 0 16px 0;
}

.demo-header p {
  font-size: 16px;
  color: #6e6e73;
  margin: 0;
}

.demo-section {
  margin-bottom: 48px;
}

.demo-section h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 24px 0;
  padding-bottom: 12px;
  border-bottom: 2px solid #f2f2f7;
}

.demo-group {
  margin-bottom: 32px;
}

.demo-group h3 {
  font-size: 18px;
  font-weight: 500;
  color: #1d1d1f;
  margin: 0 0 16px 0;
}

.demo-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.demo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.demo-card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.form-demo {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.form-row label {
  min-width: 80px;
  font-weight: 500;
  color: #1d1d1f;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .svg-ui-demo {
    padding: 16px;
  }
  
  .demo-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .demo-grid {
    grid-template-columns: 1fr;
  }
  
  .demo-card-grid {
    grid-template-columns: 1fr;
  }
  
  .form-row {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  
  .form-row label {
    min-width: auto;
  }
}
</style>