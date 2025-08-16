import { createApp } from 'vue'

import App from './App.vue'
import router from './router'
import pinia from './stores'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import './assets/main.css'
import './assets/element-theme.css'

// 权限指令
import permissionDirective from './directives/permission'
// 权限调试工具
import { permissionDebugger } from './utils/permissionDebug'

const app = createApp(App)

// 初始化权限系统
import { useAuthStore } from './stores/auth'

app.use(pinia)

// 初始化权限数据
const authStore = useAuthStore()
authStore.initializeFromStorage()

app.use(router)
app.use(ElementPlus, {locale: zhCn})
app.use(permissionDirective)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 开发环境添加调试信息
if (process.env.NODE_ENV === 'development') {
  console.log('DevLog权限系统初始化完成')
  permissionDebugger.log('SYSTEM_INIT', '权限系统初始化', {
    hasInitialized: authStore.hasInitialized,
    userCount: authStore.user ? 1 : 0,
    permissionCount: authStore.permissions?.length || 0
  })
}

app.mount('#app')
