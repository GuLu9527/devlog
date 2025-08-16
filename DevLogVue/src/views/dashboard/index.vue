<template>
  <div class="dashboard-page">
    <ProjectDashboard />
  </div>
</template>

<script setup>
import ProjectDashboard from '@/components/Project/ProjectDashboard.vue';
</script>

<style lang="scss" scoped>
.dashboard-page {
  min-height: 100vh;
  background: #f5f6fa;
}
</style>