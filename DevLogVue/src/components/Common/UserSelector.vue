<template>
  <div class="user-selector">
    <el-select
      v-model="selectedUserId"
      placeholder="请选择负责人"
      filterable
      remote
      remote-show-suffix
      :remote-method="searchUsers"
      :loading="loading"
      clearable
      @change="handleUserChange"
      style="width: 100%"
    >
      <!-- 快速选择自己 -->
      <el-option-group label="快速选择">
        <el-option
          :value="currentUser.id"
          :label="currentUser.realName || currentUser.username"
          class="user-option myself"
        >
          <div class="user-item">
            <div class="user-avatar">
              <img v-if="currentUser.avatar" :src="currentUser.avatar" :alt="currentUser.realName" />
              <div v-else class="avatar-placeholder">
                {{ (currentUser.realName || currentUser.username).charAt(0) }}
              </div>
            </div>
            <div class="user-info">
              <div class="user-name">
                {{ currentUser.realName || currentUser.username }}
                <el-tag size="small" type="success">我自己</el-tag>
              </div>
              <div class="user-meta">
                {{ currentUser.departmentName || '未分配部门' }}
              </div>
            </div>
          </div>
        </el-option>
      </el-option-group>

      <!-- 项目成员 -->
      <el-option-group v-if="projectMembers.length > 0" label="项目成员">
        <el-option
          v-for="user in projectMembers"
          :key="`project-${user.id}`"
          :value="user.id"
          :label="user.realName || user.username"
          class="user-option"
        >
          <div class="user-item">
            <div class="user-avatar">
              <img v-if="user.avatar" :src="user.avatar" :alt="user.realName" />
              <div v-else class="avatar-placeholder">
                {{ (user.realName || user.username).charAt(0) }}
              </div>
            </div>
            <div class="user-info">
              <div class="user-name">{{ user.realName || user.username }}</div>
              <div class="user-meta">
                {{ user.departmentName || '未分配部门' }} 
                <span v-if="user.roleName" class="role-tag">· {{ user.roleName }}</span>
              </div>
            </div>
          </div>
        </el-option>
      </el-option-group>

      <!-- 其他用户 -->
      <el-option-group v-if="otherUsers.length > 0" label="其他用户">
        <el-option
          v-for="user in otherUsers"
          :key="`other-${user.id}`"
          :value="user.id"
          :label="user.realName || user.username"
          class="user-option"
        >
          <div class="user-item">
            <div class="user-avatar">
              <img v-if="user.avatar" :src="user.avatar" :alt="user.realName" />
              <div v-else class="avatar-placeholder">
                {{ (user.realName || user.username).charAt(0) }}
              </div>
            </div>
            <div class="user-info">
              <div class="user-name">{{ user.realName || user.username }}</div>
              <div class="user-meta">
                {{ user.departmentName || '未分配部门' }}
                <span v-if="user.roleName" class="role-tag">· {{ user.roleName }}</span>
              </div>
            </div>
          </div>
        </el-option>
      </el-option-group>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-option">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>搜索用户中...</span>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && allUsers.length === 0" class="empty-option">
        <ActionIcon action="user" :size="20" style="color: #c0c4cc;" />
        <span>暂无用户数据</span>
      </div>
    </el-select>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { Loading } from '@element-plus/icons-vue';
import { ActionIcon } from '@/components/SvgIcons';
import { getUserList } from '@/api/user';
import { useAuthStore } from '@/stores/auth';

const props = defineProps({
  modelValue: {
    type: [Number, String],
    default: null
  },
  projectId: {
    type: [Number, String],
    default: null
  },
  placeholder: {
    type: String,
    default: '请选择负责人'
  }
});

const emit = defineEmits(['update:modelValue', 'change']);

const authStore = useAuthStore();
const loading = ref(false);
const allUsers = ref([]);
const searchKeyword = ref('');

// 当前用户信息
const currentUser = computed(() => authStore.user || {});

// 选中的用户ID
const selectedUserId = computed({
  get() {
    return props.modelValue;
  },
  set(value) {
    emit('update:modelValue', value);
  }
});

// 项目成员（暂时用部门相同的用户模拟）
const projectMembers = computed(() => {
  if (!currentUser.value.departmentId) return [];
  return allUsers.value.filter(user => 
    user.departmentId === currentUser.value.departmentId && 
    user.id !== currentUser.value.id
  ).slice(0, 10); // 限制显示数量
});

// 其他用户
const otherUsers = computed(() => {
  const projectMemberIds = new Set([currentUser.value.id, ...projectMembers.value.map(u => u.id)]);
  return allUsers.value.filter(user => !projectMemberIds.has(user.id)).slice(0, 10);
});

// 加载用户数据
const loadUsers = async (keyword = '') => {
  loading.value = true;
  try {
    const response = await getUserList({
      pageNum: 1,
      pageSize: 50,
      realName: keyword,
      username: keyword,
      status: 1 // 只获取启用的用户
    });
    
    if (response.code === 200) {
      allUsers.value = (response.data?.records || response.data || []).map(user => ({
        ...user,
        departmentName: user.departmentName || getDepartmentName(user.departmentId),
        roleName: user.roleName || getRoleName(user.roleId)
      }));
    } else {
      // 使用模拟数据作为后备
      allUsers.value = getMockUsers();
    }
  } catch (error) {
    console.error('加载用户失败:', error);
    // 使用模拟数据作为后备
    allUsers.value = getMockUsers();
  } finally {
    loading.value = false;
  }
};

// 搜索用户
const searchUsers = async (keyword) => {
  searchKeyword.value = keyword;
  await loadUsers(keyword);
};

// 处理用户选择变化
const handleUserChange = (userId) => {
  const selectedUser = allUsers.value.find(u => u.id === userId) || currentUser.value;
  emit('change', selectedUser);
};

// 获取部门名称（模拟数据）
const getDepartmentName = (departmentId) => {
  const deptMap = {
    1: '技术部',
    2: '产品部',
    3: '前端开发组',
    4: '后端开发组',
    5: '测试组'
  };
  return deptMap[departmentId] || '未知部门';
};

// 获取角色名称（模拟数据）
const getRoleName = (roleId) => {
  const roleMap = {
    1: '管理员',
    2: '部门经理', 
    3: '项目组长',
    4: '普通成员'
  };
  return roleMap[roleId] || '普通用户';
};

// 模拟用户数据
const getMockUsers = () => [
  {
    id: 1,
    username: 'admin',
    realName: '管理员',
    email: 'admin@example.com',
    departmentId: 1,
    departmentName: '技术部',
    roleId: 1,
    roleName: '管理员',
    avatar: null
  },
  {
    id: 2,
    username: 'leader1',
    realName: '前端组长',
    email: 'leader1@example.com',
    departmentId: 3,
    departmentName: '前端开发组',
    roleId: 3,
    roleName: '项目组长',
    avatar: null
  },
  {
    id: 3,
    username: 'leader2', 
    realName: '后端组长',
    email: 'leader2@example.com',
    departmentId: 4,
    departmentName: '后端开发组',
    roleId: 3,
    roleName: '项目组长',
    avatar: null
  },
  {
    id: 4,
    username: 'user1',
    realName: '前端开发1',
    email: 'user1@example.com',
    departmentId: 3,
    departmentName: '前端开发组',
    roleId: 4,
    roleName: '普通成员',
    avatar: null
  },
  {
    id: 6,
    username: 'user3',
    realName: '后端开发1',
    email: 'user3@example.com', 
    departmentId: 4,
    departmentName: '后端开发组',
    roleId: 4,
    roleName: '普通成员',
    avatar: null
  }
];

// 组件挂载时加载初始数据
onMounted(() => {
  loadUsers();
});

// 监听项目ID变化，重新加载项目成员
watch(() => props.projectId, () => {
  if (props.projectId) {
    // 这里可以调用获取项目成员的API
    // 现在先用部门成员模拟
  }
}, { immediate: true });
</script>

<style lang="scss" scoped>
.user-selector {
  .user-option {
    height: auto !important;
    line-height: normal !important;
    padding: 0 !important;
    
    &.myself {
      background: rgba(64, 158, 255, 0.05);
    }
  }
  
  .user-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 8px 20px;
    transition: all 0.2s ease;
    
    &:hover {
      background: #f5f7fa;
    }
  }
  
  .user-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    overflow: hidden;
    flex-shrink: 0;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .avatar-placeholder {
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      font-weight: 500;
    }
  }
  
  .user-info {
    flex: 1;
    min-width: 0;
    
    .user-name {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 2px;
    }
    
    .user-meta {
      font-size: 12px;
      color: #909399;
      display: flex;
      align-items: center;
      
      .role-tag {
        color: #409eff;
      }
    }
  }
  
  .loading-option {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 16px 20px;
    color: #909399;
    font-size: 14px;
  }
  
  .empty-option {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 20px;
    color: #c0c4cc;
    font-size: 14px;
  }
}

// 覆盖 Element Plus 默认样式
:deep(.el-select-dropdown__item) {
  height: auto !important;
  line-height: normal !important;
  padding: 0 !important;
  
  &.hover {
    background-color: transparent !important;
  }
  
  &.selected {
    background-color: rgba(64, 158, 255, 0.1) !important;
    
    .user-item {
      background-color: transparent;
    }
  }
}

:deep(.el-select-group__title) {
  padding: 8px 20px 4px;
  font-size: 12px;
  color: #909399;
  font-weight: 600;
}

:deep(.el-select-group__wrap:not(:last-of-type)::after) {
  margin: 8px 20px;
  background-color: #f0f2f5;
}
</style>