# SVG UI 组件库

一套基于SVG的iOS风格UI组件库，提供美观的视觉效果和流畅的交互体验。

## 特点

- 🎨 **iOS风格设计** - 采用苹果设计语言，金属色调，圆角设计
- 🔧 **完全基于SVG** - 矢量图形，任意缩放不失真
- 📱 **响应式设计** - 完美适配各种屏幕尺寸
- ⚡ **流畅动画** - 丝滑的交互动画和过渡效果
- 🎯 **易于使用** - 简洁的API设计，开箱即用
- ♿ **可访问性** - 良好的键盘导航和屏幕阅读器支持

## 安装使用

### 1. 全量引入

```javascript
// main.js
import SvgUI from '@/components/SvgUI'
import '@/components/SvgUI/styles/index.css' // 如果有独立样式文件

createApp(App).use(SvgUI).mount('#app')
```

```vue
<!-- 在组件中直接使用 -->
<template>
  <SvgButton type="primary" text="确定" />
  <SvgInput v-model="value" placeholder="请输入" />
  <SvgCard title="标题">内容</SvgCard>
</template>
```

### 2. 按需引入

```javascript
// 推荐方式
import { SvgButton, SvgInput, SvgCard } from '@/components/SvgUI'

export default {
  components: {
    SvgButton,
    SvgInput,
    SvgCard
  }
}
```

### 3. 单独引入

```javascript
import SvgButton from '@/components/SvgUI/SvgButton.vue'
```

## 组件文档

### SvgButton 按钮组件

基于SVG的金属质感按钮，支持多种类型、尺寸和状态。

#### Props

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| text | String | '' | 按钮文本 |
| type | String | 'primary' | 按钮类型：primary/secondary/success/warning/danger/info/text |
| size | String | 'medium' | 按钮尺寸：mini/small/medium/large |
| disabled | Boolean | false | 是否禁用 |
| loading | Boolean | false | 是否显示加载状态 |
| icon | String/Object | null | 图标组件 |
| round | Boolean | false | 是否圆角按钮 |
| circle | Boolean | false | 是否圆形按钮 |
| plain | Boolean | false | 是否朴素按钮 |

#### Events

| 事件名 | 说明 | 回调参数 |
|--------|------|----------|
| click | 点击事件 | (event) |

#### 基础用法

```vue
<template>
  <!-- 基础按钮 -->
  <SvgButton type="primary" text="主要按钮" />
  <SvgButton type="secondary" text="次要按钮" />
  <SvgButton type="success" text="成功按钮" />
  
  <!-- 不同尺寸 -->
  <SvgButton size="mini" text="迷你" />
  <SvgButton size="small" text="小型" />
  <SvgButton size="medium" text="中等" />
  <SvgButton size="large" text="大型" />
  
  <!-- 带图标 -->
  <SvgButton type="primary" :icon="EditIcon" text="编辑" />
  <SvgButton circle :icon="AddIcon" />
  
  <!-- 特殊状态 -->
  <SvgButton :loading="true" text="加载中" />
  <SvgButton :disabled="true" text="禁用" />
  <SvgButton round text="圆角按钮" />
  <SvgButton plain text="朴素按钮" />
</template>

<script setup>
import { SvgButton } from '@/components/SvgUI'

const EditIcon = { 
  template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="..."/></svg>' 
}
const AddIcon = { 
  template: '<svg viewBox="0 0 24 24"><path fill="currentColor" d="..."/></svg>' 
}
</script>
```

### SvgInput 输入框组件

美观的输入框组件，支持多种输入类型和验证状态。

#### Props

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| modelValue | String/Number | '' | 输入值（v-model） |
| type | String | 'text' | 输入类型：text/password/email/number/tel/url/search |
| placeholder | String | '' | 占位符 |
| size | String | 'medium' | 尺寸：small/medium/large |
| disabled | Boolean | false | 是否禁用 |
| readonly | Boolean | false | 是否只读 |
| maxlength | Number | null | 最大长度 |
| prefixIcon | String/Object | null | 前置图标 |
| suffixIcon | String/Object | null | 后置图标 |
| clearable | Boolean | false | 是否可清除 |
| showPassword | Boolean | false | 是否显示密码切换 |
| errorMessage | String | '' | 错误信息 |
| helpText | String | '' | 帮助文本 |
| validateStatus | String | '' | 验证状态：success/warning/error |

#### Events

| 事件名 | 说明 | 回调参数 |
|--------|------|----------|
| update:modelValue | 值更新事件 | (value) |
| focus | 获得焦点事件 | (event) |
| blur | 失去焦点事件 | (event) |
| input | 输入事件 | (event) |
| change | 值改变事件 | (event) |
| clear | 清除事件 | - |

#### 基础用法

```vue
<template>
  <!-- 基础输入框 -->
  <SvgInput v-model="text" placeholder="请输入文本" />
  <SvgInput v-model="password" type="password" placeholder="请输入密码" />
  
  <!-- 不同尺寸 -->
  <SvgInput v-model="small" size="small" placeholder="小尺寸" />
  <SvgInput v-model="large" size="large" placeholder="大尺寸" />
  
  <!-- 带图标 -->
  <SvgInput v-model="search" :prefix-icon="SearchIcon" placeholder="搜索" />
  <SvgInput v-model="user" :suffix-icon="UserIcon" placeholder="用户名" />
  
  <!-- 功能性 -->
  <SvgInput v-model="clearable" clearable placeholder="可清除" />
  <SvgInput v-model="pwd" type="password" show-password placeholder="显示密码" />
  
  <!-- 验证状态 -->
  <SvgInput 
    v-model="success" 
    validate-status="success" 
    help-text="输入正确" 
    placeholder="成功状态" 
  />
  <SvgInput 
    v-model="error" 
    validate-status="error" 
    error-message="输入格式错误" 
    placeholder="错误状态" 
  />
</template>

<script setup>
import { ref } from 'vue'
import { SvgInput } from '@/components/SvgUI'

const text = ref('')
const password = ref('')
const search = ref('')
// ... 其他数据
</script>
```

### SvgCard 卡片组件

精美的卡片容器组件，支持多种类型和交互效果。

#### Props

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| title | String | '' | 卡片标题 |
| subtitle | String | '' | 卡片副标题 |
| icon | String/Object | null | 标题图标 |
| type | String | 'default' | 卡片类型：default/primary/success/warning/danger/info |
| size | String | 'medium' | 卡片大小：small/medium/large |
| clickable | Boolean | false | 是否可点击 |
| glowOnHover | Boolean | false | 悬停时是否发光 |
| loading | Boolean | false | 是否显示加载状态 |
| shadow | String | 'medium' | 阴影级别：none/small/medium/large |
| bordered | Boolean | true | 是否显示边框 |
| width | String/Number | null | 自定义宽度 |
| height | String/Number | null | 自定义高度 |

#### Events

| 事件名 | 说明 | 回调参数 |
|--------|------|----------|
| click | 点击事件（需设置clickable为true） | (event) |

#### Slots

| 插槽名 | 说明 |
|--------|------|
| default | 卡片内容 |
| header | 自定义头部 |
| actions | 头部操作区域 |
| footer | 卡片底部 |

#### 基础用法

```vue
<template>
  <!-- 基础卡片 -->
  <SvgCard title="基础卡片" subtitle="这是副标题">
    <p>这是卡片的内容区域</p>
  </SvgCard>
  
  <!-- 不同类型 -->
  <SvgCard title="成功" type="success" :icon="SuccessIcon">
    <p>操作成功</p>
  </SvgCard>
  
  <!-- 可点击卡片 -->
  <SvgCard 
    title="点击卡片" 
    clickable 
    glow-on-hover
    @click="handleClick"
  >
    <p>这是一个可点击的卡片</p>
  </SvgCard>
  
  <!-- 自定义头部和底部 -->
  <SvgCard>
    <template #header>
      <div style="display: flex; justify-content: space-between;">
        <h3>自定义头部</h3>
        <SvgButton size="small" text="操作" />
      </div>
    </template>
    
    <p>卡片内容</p>
    
    <template #footer>
      <div style="text-align: right;">
        <SvgButton size="small" text="确定" />
      </div>
    </template>
  </SvgCard>
  
  <!-- 加载状态 -->
  <SvgCard title="加载中" :loading="true">
    <p>内容正在加载...</p>
  </SvgCard>
</template>

<script setup>
import { SvgCard } from '@/components/SvgUI'

const handleClick = () => {
  console.log('卡片被点击了')
}
</script>
```

## 样式定制

### CSS变量

组件支持通过CSS变量进行样式定制：

```css
:root {
  /* 主色调 */
  --svg-ui-primary: #636366;
  --svg-ui-primary-hover: #515154;
  --svg-ui-primary-active: #44444a;
  
  /* 成功色 */
  --svg-ui-success: #34c759;
  --svg-ui-success-hover: #30b149;
  
  /* 警告色 */
  --svg-ui-warning: #ff9500;
  --svg-ui-warning-hover: #e8820e;
  
  /* 危险色 */
  --svg-ui-danger: #ff3b30;
  --svg-ui-danger-hover: #e8261a;
  
  /* 信息色 */
  --svg-ui-info: #007aff;
  --svg-ui-info-hover: #0056cc;
  
  /* 文本颜色 */
  --svg-ui-text-primary: #1d1d1f;
  --svg-ui-text-secondary: #6e6e73;
  --svg-ui-text-placeholder: #8e8e93;
  
  /* 边框颜色 */
  --svg-ui-border: #d1d1d6;
  --svg-ui-border-hover: #c7c7cc;
  
  /* 背景色 */
  --svg-ui-bg: #ffffff;
  --svg-ui-bg-secondary: #f2f2f7;
  
  /* 圆角 */
  --svg-ui-radius-small: 6px;
  --svg-ui-radius-medium: 8px;
  --svg-ui-radius-large: 12px;
  
  /* 阴影 */
  --svg-ui-shadow-small: 0 1px 3px rgba(0, 0, 0, 0.05);
  --svg-ui-shadow-medium: 0 2px 8px rgba(0, 0, 0, 0.1);
  --svg-ui-shadow-large: 0 4px 16px rgba(0, 0, 0, 0.15);
}
```

### 主题切换

支持深色模式主题切换：

```css
/* 深色主题 */
[data-theme="dark"] {
  --svg-ui-primary: #8e8e93;
  --svg-ui-text-primary: #ffffff;
  --svg-ui-text-secondary: #a1a1a6;
  --svg-ui-bg: #1c1c1e;
  --svg-ui-bg-secondary: #2c2c2e;
  --svg-ui-border: #38383a;
}
```

## 最佳实践

### 1. 图标使用

推荐使用统一的图标系统：

```javascript
// icons.js
export const icons = {
  add: { template: '<svg viewBox="0 0 24 24">...</svg>' },
  edit: { template: '<svg viewBox="0 0 24 24">...</svg>' },
  delete: { template: '<svg viewBox="0 0 24 24">...</svg>' },
  // ...
}

// 在组件中使用
import { icons } from './icons'

<SvgButton :icon="icons.add" text="添加" />
```

### 2. 表单验证

结合表单验证库使用：

```vue
<template>
  <form @submit="handleSubmit">
    <SvgInput
      v-model="form.email"
      type="email"
      placeholder="请输入邮箱"
      :validate-status="emailStatus"
      :error-message="emailError"
    />
    <SvgButton type="primary" text="提交" />
  </form>
</template>

<script setup>
import { computed } from 'vue'

const emailStatus = computed(() => {
  if (!form.email) return ''
  return /\S+@\S+\.\S+/.test(form.email) ? 'success' : 'error'
})

const emailError = computed(() => {
  return emailStatus.value === 'error' ? '请输入正确的邮箱格式' : ''
})
</script>
```

### 3. 响应式布局

配合CSS Grid/Flexbox实现响应式布局：

```vue
<template>
  <div class="card-grid">
    <SvgCard
      v-for="item in items"
      :key="item.id"
      :title="item.title"
      clickable
      @click="handleCardClick(item)"
    >
      {{ item.content }}
    </SvgCard>
  </div>
</template>

<style scoped>
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

@media (max-width: 768px) {
  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
```

## 浏览器兼容性

- Chrome >= 60
- Firefox >= 55
- Safari >= 12
- Edge >= 79

## 更新日志

### v1.0.0
- 🎉 首次发布
- ✨ SvgButton 按钮组件
- ✨ SvgInput 输入框组件
- ✨ SvgCard 卡片组件
- 🎨 iOS风格设计系统
- 📱 响应式设计支持

## 贡献指南

欢迎提交 Issue 和 Pull Request！

### 开发环境

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 运行测试
npm run test

# 构建
npm run build
```

### 组件开发规范

1. 遵循Vue 3 Composition API规范
2. 使用TypeScript类型定义
3. 保持iOS设计风格一致性
4. 添加完整的文档和示例
5. 确保可访问性支持

## 许可证

MIT License

## 联系我们

如有问题或建议，请提交Issue或联系开发团队。