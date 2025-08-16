<script setup>
import { ElMessage, ElMessageBox } from 'element-plus';
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { LayoutIcons, SkillIcons } from '@/components/SvgIcons';
import NotificationDropdown from '@/components/Notification/NotificationDropdown.vue';
import SvgScrollbar from '@/components/SvgScrollbar.vue';
import { useAuthStore } from '@/stores/auth';
import { useMenu } from '@/composables/useMenu';

// 当前登录员工
const loginName = ref('');
const avatarUrl = ref('');

const router = useRouter();
const authStore = useAuthStore();
const { filteredMenuItems, defaultOpeneds } = useMenu();

// 处理菜单选择
const handleMenuSelect = async (path) => {
  if (path && path !== router.currentRoute.value.path) {
    try {
      await router.push(path);
    } catch (error) {
      console.error('导航失败:', error);
      ElMessage.error(`页面跳转失败: ${path}`);
      
      // 尝试强制刷新页面作为后备方案
      if (confirm('页面加载失败，是否刷新页面重试？')) {
        window.location.reload();
      }
    }
  }
};

onMounted(() => {
  // 从store获取用户信息
  if (authStore.user && authStore.user.realName) {
    loginName.value = authStore.user.realName;
  } else {
    // 兜底从localStorage获取
    const loginUser = JSON.parse(localStorage.getItem('loginUser') || localStorage.getItem('userInfo'));
    if (loginUser && loginUser.realName) {
      loginName.value = loginUser.realName;
    }
  }
})

// 退出登录
const loginOut = () => {
  // 弹出确认框
  ElMessageBox.confirm(
    '您确认退出吗？',
    '提示',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => { // 确认
      ElMessage.success('退出成功!');
      localStorage.removeItem('loginUser');
      localStorage.removeItem('token');
      // 跳转页面 - 登录
      router.push('/login');
    })
    .catch(() => { // 取消
      ElMessage.info("您已取消退出");
    })
}
</script>

<template>
  <div class="common-layout">
    <el-container>
      <!-- Header 区域 -->
      <el-header class="header">
        <div class="header-left">
          <span class="title">DevLog</span>
          <div class="menu-toggle">
            <LayoutIcons type="menu" :size="20" />
          </div>
        </div>
        <div class="header-right">
          <NotificationDropdown />
          <el-dropdown trigger="click">
            <div class="user-info">
              <el-avatar :size="32" :src="avatarUrl" class="user-avatar">
                {{ loginName?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <span class="username">{{ loginName }}</span>
              <LayoutIcons type="chevron-down" :size="16" />
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item class="dropdown-btn">
                  <LayoutIcons type="user" :size="16" />
                  <span>个人信息</span>
                </el-dropdown-item>
                <el-dropdown-item class="dropdown-btn">
                  <LayoutIcons type="setting" :size="16" />
                  <span>系统设置</span>
                </el-dropdown-item>
                <el-dropdown-item divided class="dropdown-btn logout-btn" @click="loginOut">
                  <LayoutIcons type="logout" :size="16" />
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-container class="main-container">
        <!-- 左侧菜单 -->
        <el-aside width="240px" class="aside">
          <div class="menu-header">
            <span class="menu-title">功能导航</span>
          </div>
          <div class="menu-container">
            <SvgScrollbar :scrollbar-width="10">
              <el-menu 
                :default-active="$route.path"
                class="menu"
                :default-openeds="defaultOpeneds"
                @select="handleMenuSelect"
              >
                <template v-for="item in filteredMenuItems" :key="item.key">
                  <!-- 带子菜单的项 -->
                  <el-sub-menu v-if="item.children && item.children.length > 0" :index="item.key">
                    <template #title>
                      <LayoutIcons :type="item.icon" :size="18" />
                      <span>{{ item.title }}</span>
                    </template>
                    <el-menu-item 
                      v-for="child in item.children" 
                      :key="child.key"
                      :index="child.path"
                    >
                      <LayoutIcons :type="child.icon" :size="16" />
                      <span>{{ child.title }}</span>
                    </el-menu-item>
                  </el-sub-menu>
                  
                  <!-- 单个菜单项 -->
                  <el-menu-item v-else :index="item.path">
                    <LayoutIcons :type="item.icon" :size="18" />
                    <span>{{ item.title }}</span>
                  </el-menu-item>
                </template>
            </el-menu>
          </SvgScrollbar>
          </div>
        </el-aside>

        <el-main class="main">
          <SvgScrollbar :scrollbar-width="12">
            <router-view v-slot="{ Component }">
              <transition name="fade" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </SvgScrollbar>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="scss" scoped>
.common-layout {
  height: 100vh;
  background: #ffffff;
}

.header {
  height: 64px;
  background: #ffffff;
  border-bottom: 1px solid #d1d1d6;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;

  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;

    .title {
      font-size: 26px;
      font-weight: 700;
      color: #1d1d1f;
      letter-spacing: -0.5px;
    }

    .menu-toggle {
      display: none;
      cursor: pointer;
      padding: 8px;
      border-radius: 8px;
      transition: all 0.3s ease;
      color: #6e6e73;

      &:hover {
        background: #f2f2f7;
        color: #1d1d1f;
        transform: scale(1.05);
      }
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 10px;
      cursor: pointer;
      padding: 8px 16px;
      border-radius: 12px;
      transition: all 0.3s ease;
      border: 1px solid transparent;

      &:hover {
        background: #f2f2f7;
        border-color: #d1d1d6;
        transform: translateY(-1px);
      }

      .user-avatar {
        background: #636366;
        background-image: linear-gradient(135deg, #636366 0%, #5a5a5d 100%);
        color: #ffffff;
        font-weight: 600;
        border: 2px solid #d1d1d6;
      }

      .username {
        font-size: 14px;
        color: #1d1d1f;
        font-weight: 500;
      }
    }
  }
}

.main-container {
  margin-top: 64px;
  height: calc(100vh - 64px);
}

.aside {
  background: #ffffff;
  border-right: 1px solid #d1d1d6;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  position: fixed;
  left: 0;
  top: 64px;
  bottom: 0;
  z-index: 999;
  overflow: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;
  
  &::-webkit-scrollbar {
    display: none;
  }

  .menu-header {
    height: 56px;
    display: flex;
    align-items: center;
    padding: 0 28px;
    border-bottom: 1px solid #d1d1d6;
    background: #fafafa;

    .menu-title {
      font-size: 16px;
      font-weight: 600;
      color: #1d1d1f;
      letter-spacing: 0.5px;
    }
  }

  .menu-container {
    height: calc(100vh - 120px);
    overflow: hidden;
  }

  .menu {
    border-right: none;
    padding: 12px 0;
    background: transparent;
    min-height: 100%;

    :deep(.el-menu-item) {
      height: 48px;
      line-height: 48px;
      margin: 6px 16px;
      border-radius: 12px;
      color: #6e6e73;
      font-weight: 500;
      transition: all 0.3s ease;
      border: 1px solid transparent;
      position: relative;
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 3px;
        height: 0;
        background: linear-gradient(135deg, #636366, #5a5a5d);
        border-radius: 0 3px 3px 0;
        transition: height 0.3s ease;
      }

      &:hover {
        background: #f2f2f7;
        color: #1d1d1f;
        transform: translateX(4px);
        border-color: #d1d1d6;
      }

      &:hover::before {
        height: 20px;
      }

      &.is-active {
        background: #f2f2f7;
        color: #1d1d1f;
        font-weight: 600;
        transform: translateX(6px);
        border-color: #d1d1d6;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      }

      &.is-active::before {
        height: 24px;
      }

      span {
        margin-left: 12px;
        font-size: 14px;
      }
    }

    :deep(.el-sub-menu) {
      .el-sub-menu__title {
        height: 48px;
        line-height: 48px;
        margin: 6px 16px;
        border-radius: 12px;
        color: #6e6e73;
        font-weight: 600;
        transition: all 0.3s ease;
        border: 1px solid transparent;
        position: relative;
        overflow: hidden;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 3px;
          height: 0;
          background: linear-gradient(135deg, #636366, #5a5a5d);
          border-radius: 0 3px 3px 0;
          transition: height 0.3s ease;
        }

        &:hover {
          background: #f2f2f7;
          color: #1d1d1f;
          transform: translateX(4px);
          border-color: #d1d1d6;
        }

        &:hover::before {
          height: 20px;
        }

        span {
          margin-left: 12px;
          font-size: 14px;
        }

        .el-sub-menu__icon-arrow {
          margin-right: 16px;
          color: #8e8e93;
          transition: all 0.3s ease;
        }
      }

      &.is-opened .el-sub-menu__title {
        background: #f2f2f7;
        color: #1d1d1f;
        transform: translateX(4px);
        border-color: #d1d1d6;

        &::before {
          height: 20px;
        }

        .el-sub-menu__icon-arrow {
          color: #6e6e73;
        }
      }

      .el-menu {
        background: transparent;
        
        .el-menu-item {
          height: 42px;
          line-height: 42px;
          margin: 4px 24px 4px 32px;
          padding-left: 20px !important;
          font-size: 13px;
          
          &::before {
            width: 2px;
            left: 4px;
          }

          &:hover {
            transform: translateX(2px);
          }

          &.is-active {
            transform: translateX(4px);
            background: #f2f2f7;
          }
        }
      }
    }
  }
}

.main {
  margin-left: 240px;
  padding: 0;
  background: transparent;
  height: calc(100vh - 64px);
  overflow: hidden;
}

// 路由切换动画
.fade-enter-active,
.fade-leave-active {
  transition: all 0.4s cubic-bezier(0.55, 0, 0.1, 1);
}

.fade-enter-from {
  opacity: 0;
  transform: translate(30px, 0);
}

.fade-leave-to {
  opacity: 0;
  transform: translate(-30px, 0);
}

// 下拉菜单样式优化 - iOS风格
:deep(.el-dropdown-menu) {
  background: #ffffff;
  border: 1px solid #d1d1d6;
  border-radius: 16px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.12);
  padding: 12px;
  min-width: 180px;

  .el-dropdown-menu__item {
    display: flex;
    align-items: center;
    padding: 12px 18px;
    border-radius: 12px;
    color: #6e6e73;
    font-weight: 500;
    transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
    margin-bottom: 4px;
    border: 1px solid transparent;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 0;
      background: linear-gradient(135deg, #007AFF, #5856D6);
      border-radius: 0 2px 2px 0;
      transition: height 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
    }

    &:hover {
      background: #f2f2f7;
      color: #1d1d1f;
      transform: translateX(4px);
      border-color: #e5e5ea;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      &::before {
        height: 20px;
      }
    }

    &:not(.is-divided) {
      margin-bottom: 4px;
    }

    &.is-divided {
      border-top: 1px solid #e5e5ea;
      margin-top: 12px;
      padding-top: 16px;
    }

    &.logout-btn {
      color: #FF3B30;
      
      &:hover {
        background: rgba(255, 59, 48, 0.08);
        color: #FF3B30;
        
        &::before {
          background: linear-gradient(135deg, #FF3B30, #FF2D92);
        }
      }
    }

    span {
      margin-left: 12px;
      font-size: 14px;
      font-weight: 500;
    }
  }
}

// 响应式设计
@media screen and (max-width: 1024px) {
  .header {
    padding: 0 20px;
    
    .header-left {
      gap: 16px;
      
      .title {
        font-size: 22px;
      }
    }
    
    .header-right {
      gap: 16px;
    }
  }
  
  .aside {
    width: 220px;
  }
  
  .main {
    margin-left: 220px;
  }
}

@media screen and (max-width: 768px) {
  .header {
    height: 56px;
    padding: 0 16px;
    
    .header-left {
      .title {
        font-size: 20px;
      }
      
      .menu-toggle {
        display: flex;
      }
    }
    
    .header-right {
      gap: 12px;
      
      .user-info {
        padding: 6px 12px;
        
        .username {
          display: none;
        }
      }
    }
  }
  
  .main-container {
    margin-top: 56px;
    height: calc(100vh - 56px);
  }
  
  .aside {
    top: 56px;
    transform: translateX(-100%);
    width: 280px;
    
    &.show {
      transform: translateX(0);
    }
  }

  .main {
    margin-left: 0;
  }
}

@media screen and (max-width: 480px) {
  .header {
    padding: 0 12px;
    
    .header-left {
      gap: 12px;
      
      .title {
        font-size: 18px;
      }
    }
  }
  
  .aside {
    width: 100vw;
    
    .menu-header {
      padding: 0 20px;
    }
    
    .menu {
      :deep(.el-menu-item) {
        margin: 4px 12px;
        
        span {
          font-size: 16px;
        }
      }
    }
  }
}
</style>