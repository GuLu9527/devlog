<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useGlobalPermissionWatcher } from '@/composables/usePermissionWatcher'

const authStore = useAuthStore()

// 全局权限监听
useGlobalPermissionWatcher()

onMounted(() => {
  // 应用启动时初始化用户信息
  authStore.initializeFromStorage()
})
</script>

<template>
  <router-view>
    
  </router-view>
</template>

<style scoped>

</style>
