<template>
  <PermissionCheck 
    :permission="['user:list', 'user:read']"
    no-permission-message="您没有用户管理权限"
  >
    <div class="user-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">用户管理</h1>
        <div class="page-subtitle">管理系统用户账户信息</div>
      </div>
      <div class="header-actions">
        <div class="view-toggle">
          <button 
            class="toggle-btn" 
            :class="{ active: viewMode === 'table' }"
            @click="viewMode = 'table'"
          >
            <UserIcons type="list-view" :size="16" />
          </button>
          <button 
            class="toggle-btn" 
            :class="{ active: viewMode === 'card' }"
            @click="viewMode = 'card'"
          >
            <UserIcons type="card-view" :size="16" />
          </button>
        </div>
        <PermissionCheck permission="user:create">
          <el-button type="primary" class="header-add-btn" @click="handleAdd">
            <UserIcons type="add-user" :size="16" />
            新增用户
          </el-button>
        </PermissionCheck>
      </div>
    </div>

    <!-- 搜索过滤区域 -->
    <div class="search-section">
      <div class="search-form">
        <div class="search-row">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <el-input
            v-model="searchForm.email"
            placeholder="请输入邮箱"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入手机号"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <el-input
            v-model="searchForm.realName"
            placeholder="请输入真实姓名"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="search-row">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            class="search-input"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
          <el-select
            v-model="searchForm.roleId"
            placeholder="请选择角色"
            clearable
            class="search-input"
          >
            <el-option
              v-for="role in roleList"
              :key="role.id"
              :label="role.name"
              :value="role.id"
            />
          </el-select>
          <el-select
            v-model="searchForm.groupId"
            placeholder="请选择工作组"
            clearable
            class="search-input"
          >
            <el-option
              v-for="group in groupList"
              :key="group.id"
              :label="group.name"
              :value="group.id"
            />
          </el-select>
          <div class="search-actions">
            <el-button type="primary" class="search-btn" @click="handleSearch">
              <ActionIcon action="search" :size="16" />
              搜索
            </el-button>
            <el-button class="reset-btn" @click="resetSearch">
              <ActionIcon action="refresh" :size="16" />
              重置
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="content-section">

      <!-- 表格视图 -->
      <div v-if="viewMode === 'table'" class="table-view">
        <div 
          v-loading="loading"
          class="user-table"
          element-loading-text="加载中..."
          element-loading-background="rgba(248, 250, 252, 0.8)"
        >
          <div class="table-header">
            <div class="header-cell">用户信息</div>
            <div class="header-cell">联系方式</div>
            <div class="header-cell">部门角色</div>
            <div class="header-cell">技能标签</div>
            <div class="header-cell">状态</div>
            <div class="header-cell">操作</div>
          </div>
          <div class="table-body">
            <div v-for="user in tableData" :key="user.id" class="table-row">
              <div class="table-cell user-info">
                <div class="user-avatar">
                  {{ user.realName?.charAt(0)?.toUpperCase() || 'U' }}
                </div>
                <div class="user-details">
                  <div class="username">{{ user.username }}</div>
                  <div class="real-name">{{ user.realName || '-' }}</div>
                </div>
              </div>
              <div class="table-cell contact-info">
                <div class="contact-item">
                  <span class="contact-label">邮箱:</span>
                  <span class="contact-value">{{ user.email || '-' }}</span>
                </div>
                <div class="contact-item">
                  <span class="contact-label">手机:</span>
                  <span class="contact-value">{{ user.phone || '-' }}</span>
                </div>
              </div>
              <div class="table-cell org-info">
                <div class="org-item">
                  <UserIcons type="department" :size="14" />
                  <span>{{ getDepartmentName(user.departmentId) }}</span>
                </div>
                <div class="org-item">
                  <UserIcons type="role" :size="14" />
                  <span>{{ getRoleName(user.roleId) }}</span>
                </div>
                <div class="org-item" v-if="user.groupId">
                  <UserIcons type="group" :size="14" />
                  <span>{{ getGroupName(user.groupId) }}</span>
                </div>
              </div>
              <div class="table-cell skills-cell">
                <div class="skills-container">
                  <div v-if="user.skills && user.skills.length > 0" class="skill-tags">
                    <div class="skill-tags-main">
                      <el-tag
                        v-for="skill in user.skills.slice(0, 2)"
                        :key="skill.id"
                        class="skill-tag"
                        :type="getSkillTagType(skill.proficiency_level || skill.proficiency)"
                        size="small"
                        :title="`调试: ${JSON.stringify(skill)}`"
                      >
                        <span class="skill-name">{{ skill.tag_name || skill.skillTag?.name || '未知技能' }}</span>
                        <span class="skill-level">{{ getProficiencyText(skill.proficiency_level || skill.proficiency) }}</span>
                      </el-tag>
                    </div>
                    <div v-if="user.skills.length > 2" class="skill-more">
                      <el-popover
                        placement="top"
                        :width="280"
                        trigger="hover"
                        popper-class="skills-popover"
                      >
                        <template #reference>
                          <el-tag class="skill-tag more-tag" size="small" type="info">
                            <el-icon :size="12"><MoreFilled /></el-icon>
                            <span>+{{ user.skills.length - 2 }}</span>
                          </el-tag>
                        </template>
                        <div class="all-skills">
                          <div class="popover-title">全部技能 ({{ user.skills.length }})</div>
                          <div class="skills-grid">
                            <el-tag
                              v-for="skill in user.skills"
                              :key="skill.id"
                              class="skill-tag popover-skill"
                              :type="getSkillTagType(skill.proficiency_level || skill.proficiency)"
                              size="small"
                            >
                              <span class="skill-name">{{ skill.tag_name || skill.skillTag?.name }}</span>
                              <span class="skill-level">{{ getProficiencyText(skill.proficiency_level || skill.proficiency) }}</span>
                            </el-tag>
                          </div>
                        </div>
                      </el-popover>
                    </div>
                  </div>
                  <div v-else class="no-skills">
                    <el-icon class="no-skills-icon" :size="14"><Tools /></el-icon>
                    <span class="no-skills-text">暂无技能</span>
                  </div>
                </div>
              </div>
              <div class="table-cell status-cell">
                <div class="status-badge" :class="user.status === 1 ? 'active' : 'inactive'">
                  <UserIcons type="status" :size="12" />
                  <span>{{ user.status === 1 ? '启用' : '禁用' }}</span>
                </div>
              </div>
              <div class="table-cell actions">
                <div class="actions-container">
                  <!-- 主要操作 -->
                  <div class="primary-actions">
                    <PermissionCheck permission="user:update">
                      <el-tooltip content="编辑用户" placement="top">
                        <el-button type="primary" circle class="action-btn primary" @click="handleEdit(user)">
                          <UserIcons type="edit" :size="16" />
                        </el-button>
                      </el-tooltip>
                    </PermissionCheck>
                    <el-tooltip content="技能管理" placement="top">
                      <el-button type="success" circle class="action-btn" @click="handleManageSkills(user)">
                        <SkillIcons type="tag" :size="16" />
                      </el-button>
                    </el-tooltip>
                  </div>
                  
                  <!-- 次要操作 -->
                  <div class="secondary-actions">
                    <el-tooltip content="查看权限" placement="top">
                      <el-button type="info" circle class="action-btn secondary" @click="handleViewPermissions(user)">
                        <el-icon><Key /></el-icon>
                      </el-button>
                    </el-tooltip>
                    
                    <!-- 更多操作下拉菜单 -->
                    <el-dropdown trigger="hover" placement="bottom" popper-class="user-actions-dropdown">
                      <el-button circle class="action-btn more-btn">
                        <el-icon :size="16"><MoreFilled /></el-icon>
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <PermissionCheck permission="user:reset">
                            <el-dropdown-item @click="handleResetPassword(user)">
                              <el-icon class="dropdown-icon"><RefreshRight /></el-icon>
                              <span>重置密码</span>
                            </el-dropdown-item>
                          </PermissionCheck>
                          <PermissionCheck permission="user:delete">
                            <el-dropdown-item @click="handleDelete(user)" class="danger-item" divided>
                              <el-icon class="dropdown-icon"><Delete /></el-icon>
                              <span>删除用户</span>
                            </el-dropdown-item>
                          </PermissionCheck>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 卡片视图 -->
      <div v-else class="card-view">
        <div v-loading="loading" class="user-cards">
          <div v-for="user in tableData" :key="user.id" class="user-card">
            <div class="card-header">
              <div class="user-avatar">
                {{ user.realName?.charAt(0)?.toUpperCase() || 'U' }}
              </div>
              <div class="user-basic">
                <h3 class="username">{{ user.username }}</h3>
                <p class="real-name">{{ user.realName || '-' }}</p>
              </div>
              <div class="status-badge" :class="user.status === 1 ? 'active' : 'inactive'">
                <UserIcons type="status" :size="12" />
                <span>{{ user.status === 1 ? '启用' : '禁用' }}</span>
              </div>
            </div>
            <div class="card-body">
              <div class="info-item">
                <span class="info-label">邮箱</span>
                <span class="info-value">{{ user.email || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">手机</span>
                <span class="info-value">{{ user.phone || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">部门</span>
                <span class="info-value">
                  <UserIcons type="department" :size="14" />
                  {{ getDepartmentName(user.departmentId) }}
                </span>
              </div>
              <div class="info-item">
                <span class="info-label">角色</span>
                <span class="info-value">
                  <UserIcons type="role" :size="14" />
                  {{ getRoleName(user.roleId) }}
                </span>
              </div>
              <div class="info-item" v-if="user.groupId">
                <span class="info-label">小组</span>
                <span class="info-value">
                  <UserIcons type="group" :size="14" />
                  {{ getGroupName(user.groupId) }}
                </span>
              </div>
            </div>
            <div class="card-actions">
              <PermissionCheck permission="user:update">
                <SvgButton 
                  size="small" 
                  type="info"
                  :icon="EditIcon" 
                  text="编辑"
                  @click="handleEdit(user)"
                />
              </PermissionCheck>
              <SvgButton 
                size="small" 
                type="success"
                :icon="SkillIcon" 
                text="技能"
                @click="handleManageSkills(user)"
              />
              <PermissionCheck permission="user:reset">
                <SvgButton 
                  size="small" 
                  type="warning"
                  :icon="ResetIcon" 
                  text="重置密码"
                  @click="handleResetPassword(user)"
                />
              </PermissionCheck>
              <PermissionCheck permission="user:delete">
                <SvgButton 
                  size="small" 
                  type="danger"
                  :icon="DeleteIcon" 
                  text="删除"
                  @click="handleDelete(user)"
                />
              </PermissionCheck>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增用户' : '编辑用户'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username" v-if="dialogType === 'add'">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="所属部门" prop="departmentId">
          <el-tree-select
            v-model="form.departmentId"
            :data="departmentTree"
            :props="defaultProps"
            placeholder="请选择所属部门"
            check-strictly
            clearable
            @change="handleDepartmentChange"
            class="department-select"
            node-key="id"
          />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="item in roleList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="所属小组" prop="groupId">
          <el-select v-model="form.groupId" placeholder="请选择所属小组" style="width: 100%">
            <el-option
              v-for="item in groupList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="cancel-btn" @click="dialogVisible = false">
            <ActionIcon action="close" :size="14" />
            取消
          </el-button>
          <el-button type="primary" class="confirm-btn" @click="handleSubmit">
            <ActionIcon action="check" :size="14" />
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog
      v-model="resetPasswordVisible"
      title="重置密码"
      width="400px"
      destroy-on-close
    >
      <el-form
        ref="resetPasswordFormRef"
        :model="resetPasswordForm"
        :rules="resetPasswordRules"
        label-width="100px"
      >
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="resetPasswordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="cancel-btn" @click="resetPasswordVisible = false">
            <ActionIcon action="close" :size="14" />
            取消
          </el-button>
          <el-button type="primary" class="confirm-btn" @click="handleResetPasswordSubmit">
            <UserIcons type="reset-password" :size="14" />
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 用户技能管理对话框 -->
    <el-dialog
      v-model="skillDialogVisible"
      :title="`${currentUser?.realName || currentUser?.username} - 技能管理`"
      width="800px"
      destroy-on-close
    >
      <UserSkillManager
        v-if="skillDialogVisible && currentUser"
        :user-id="currentUser.id"
        @skill-updated="handleSkillUpdated"
      />
    </el-dialog>

    <!-- 权限查看对话框 -->
    <el-dialog
      v-model="permissionViewVisible"
      title="用户权限查看"
      width="90%"
      max-width="900px"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <UserPermissionView
        v-if="permissionViewVisible && selectedUserForPermission"
        :user-id="selectedUserForPermission.id"
        :user-info="selectedUserForPermission"
      />
    </el-dialog>
    </div>
  </PermissionCheck>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserIcons, SkillIcons, ActionIcon } from '@/components/SvgIcons'
import UserSkillManager from '@/components/UserSkill/UserSkillManager.vue'
import PermissionCheck from '@/components/Permission/PermissionCheck.vue'
import UserPermissionView from '@/components/Permission/UserPermissionView.vue'
import { Edit, Delete, RefreshRight, UserFilled, Search, Plus, Close, Check, Key, MoreFilled, Tools } from '@element-plus/icons-vue'
import {
  getUserList,
  createUser,
  updateUser,
  deleteUser,
  resetPassword
} from '@/api/user'
import { getDepartmentTree } from '@/api/department'
import { findAllApi as getRoleList } from '@/api/role'
import { getGroupList } from '@/api/group'
import { getUserSkills } from '@/api/userSkill'

// 搜索表单
const searchForm = ref({
  username: '',
  email: '',
  phone: '',
  realName: '',
  status: '',
  roleId: '',
  groupId: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 视图模式
const viewMode = ref('table')

// 部门树数据
const departmentTree = ref([])
const defaultProps = {
  children: 'children',
  label: 'name',
  value: 'id'
}

// 表单数据
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const form = ref({
  username: '',
  email: '',
  phone: '',
  realName: '',
  password: '',
  departmentId: null,
  status: 1,
  roleId: '',
  groupId: null
})

// 重置密码表单
const resetPasswordVisible = ref(false)

// 权限查看对话框
const permissionViewVisible = ref(false)
const selectedUserForPermission = ref(null)
const resetPasswordFormRef = ref(null)
const resetPasswordForm = ref({
  userId: null,
  newPassword: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  roleId: [
    { required: true, message: '请选择用户角色', trigger: 'change' }
  ],
  groupId: [
    { required: true, message: '请选择所属小组', trigger: 'change' }
  ]
}

// 重置密码验证规则
const resetPasswordRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
    { 
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{6,20}$/, 
      message: '密码必须包含大小写字母和数字，不能包含特殊字符', 
      trigger: 'blur' 
    }
  ]
}

// 角色列表
const roleList = ref([])
// 工作组列表
const groupList = ref([])

// 技能管理相关
const skillDialogVisible = ref(false)
const currentUser = ref(null)


// 获取角色列表
const getRoles = async () => {
  try {
    const res = await getRoleList()
    if (res.code === 200) {
      roleList.value = res.data
    } else {
      ElMessage.error(res.message || '获取角色列表失败')
    }
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败')
  }
}

// 获取工作组列表
const getGroups = async () => {
  try {
    const res = await getGroupList()
    if (res.code === 200) {
      groupList.value = res.data
    } else {
      ElMessage.error(res.message || '获取工作组列表失败')
    }
  } catch (error) {
    console.error('获取工作组列表失败:', error)
    ElMessage.error('获取工作组列表失败')
  }
}

// 获取部门树
const getTree = async () => {
  try {
    const res = await getDepartmentTree()
    if (res.code === 200) {
      // 处理部门树数据，确保每个节点都有id和name属性
      const processTreeData = (nodes) => {
        return nodes.map(node => ({
          id: node.id,
          name: node.name,
          children: node.children ? processTreeData(node.children) : []
        }))
      }
      departmentTree.value = processTreeData(res.data)
    } else {
      ElMessage.error(res.message || '获取部门树失败')
    }
  } catch (error) {
    console.error('获取部门树失败:', error)
    ElMessage.error('获取部门树失败')
  }
}

// 获取部门名称
const getDepartmentName = (departmentId) => {
  if (!departmentId) return '无'
  
  const findDepartment = (tree) => {
    for (const node of tree) {
      if (node.id === departmentId) return node.name
      if (node.children && node.children.length > 0) {
        const name = findDepartment(node.children)
        if (name) return name
      }
    }
    return null
  }
  return findDepartment(departmentTree.value) || '无'
}

// 获取用户列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm.value
    })
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
      
      // 获取每个用户的技能数据
      await loadUsersSkills()
    } else {
      ElMessage.error(res.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载用户技能数据
const loadUsersSkills = async () => {
  for (const user of tableData.value) {
    try {
      const skillRes = await getUserSkills(user.id)
      console.log(`用户 ${user.id} 技能数据:`, skillRes)
      if (skillRes.code === 200) {
        user.skills = skillRes.data || []
        console.log(`用户 ${user.id} 技能:`, user.skills)
      } else {
        user.skills = []
      }
    } catch (error) {
      console.error(`获取用户 ${user.id} 技能失败:`, error)
      user.skills = []
    }
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    username: '',
    email: '',
    phone: '',
    realName: '',
    status: '',
    roleId: '',
    groupId: ''
  }
  handleSearch()
}

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    username: '',
    email: '',
    phone: '',
    realName: '',
    password: '',
    departmentId: null,
    status: 1,
    roleId: '',
    groupId: null
  }
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = {
    id: row.id,
    username: row.username,
    email: row.email,
    phone: row.phone,
    realName: row.realName,
    departmentId: row.departmentId,
    status: row.status,
    roleId: row.roleId,
    groupId: row.groupId
  }
  dialogVisible.value = true
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确认删除该用户吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除用户失败:', error)
    }
  })
}

// 处理重置密码
const handleResetPassword = (row) => {
  resetPasswordForm.value = {
    userId: row.id,
    newPassword: ''
  }
  resetPasswordVisible.value = true
}

// 提交重置密码
const handleResetPasswordSubmit = async () => {
  if (!resetPasswordFormRef.value) return
  
  await resetPasswordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await resetPassword(resetPasswordForm.value.userId, {
          newPassword: resetPasswordForm.value.newPassword
        })
        ElMessage.success('密码重置成功')
        resetPasswordVisible.value = false
      } catch (error) {
        console.error('重置密码失败:', error)
      }
    }
  })
}

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await createUser(form.value)
          ElMessage.success('新增成功')
        } else {
          await updateUser(form.value.id, form.value)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(error.response?.data?.message || '操作失败')
      }
    }
  })
}

// 处理分页
const handleSizeChange = (val) => {
  pageSize.value = val
  getList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getList()
}

// 处理部门选择
const handleDepartmentChange = (value) => {
  form.value.departmentId = value
}

// 获取角色类型
const getRoleType = (roleId) => {
  const role = roleList.value.find(item => item.id === roleId)
  return role ? (role.status === 1 ? 'success' : 'info') : 'info'
}

// 获取角色名称
const getRoleName = (roleId) => {
  const role = roleList.value.find(item => item.id === roleId)
  return role ? role.name : '未知角色'
}

// 获取小组名称
const getGroupName = (groupId) => {
  if (!groupId) return '无'
  const group = groupList.value.find(item => item.id === groupId)
  return group ? group.name : '无'
}

// 根据熟练度获取标签类型
const getSkillTagType = (proficiency) => {
  // 处理字符串类型的熟练度
  if (typeof proficiency === 'string') {
    switch (proficiency) {
      case '初级':
        return '' // 默认灰色
      case '中级':
        return 'warning' // 橙色
      case '高级':
        return 'success' // 绿色
      case '资深':
        return 'success' // 绿色
      case '专家':
        return 'danger' // 红色 (专家级用红色表示突出)
      default:
        return ''
    }
  }
  
  // 兼容数字类型的熟练度
  switch (proficiency) {
    case 1:
      return '' // 默认灰色
    case 2:
      return 'warning' // 橙色
    case 3:
      return 'success' // 绿色
    case 4:
      return 'success' // 绿色
    case 5:
      return 'danger' // 红色 (专家级用红色表示突出)
    default:
      return ''
  }
}

// 获取熟练度文本
const getProficiencyText = (proficiency) => {
  // 如果已经是字符串，直接返回
  if (typeof proficiency === 'string') {
    return proficiency
  }
  
  // 兼容数字类型的熟练度
  const proficiencyMap = {
    1: '初级',
    2: '中级',
    3: '高级',
    4: '资深',
    5: '专家'
  }
  return proficiencyMap[proficiency] || '未知'
}

// 处理技能管理
const handleManageSkills = (user) => {
  currentUser.value = user
  skillDialogVisible.value = true
}

// 处理查看权限
const handleViewPermissions = (user) => {
  selectedUserForPermission.value = user
  permissionViewVisible.value = true
}

// 技能更新回调
const handleSkillUpdated = async () => {
  // 技能更新后重新获取该用户的技能数据
  if (currentUser.value && currentUser.value.id) {
    try {
      const skillRes = await getUserSkills(currentUser.value.id)
      if (skillRes.code === 200) {
        // 更新列表中对应用户的技能数据
        const userIndex = tableData.value.findIndex(user => user.id === currentUser.value.id)
        if (userIndex !== -1) {
          tableData.value[userIndex].skills = skillRes.data || []
        }
      }
    } catch (error) {
      console.error('刷新用户技能失败:', error)
    }
  }
}

// 初始化
onMounted(() => {
  getTree()
  getList()
  getRoles()
  getGroups()
})
</script>

<style lang="scss" scoped>
.user-container {
  padding: 24px;
  background: #ffffff;
}

// 页面头部
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 16px;
  color: #6e6e73;
  font-weight: 400;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 8px;
}

.view-toggle {
  display: flex;
  background: #f2f2f7;
  border-radius: 8px;
  padding: 4px;
  border: 1px solid #d1d1d6;

  .toggle-btn {
    padding: 8px 12px;
    border: none;
    background: transparent;
    color: #6e6e73;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;

    &:hover {
      color: #1d1d1f;
      background: #e5e5ea;
    }

    &.active {
      background: #1d1d1f;
      color: white;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
}


// 搜索区域
.search-section {
  background: #ffffff;
  border: 1px solid #d1d1d6;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-row {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;

  .search-input {
    width: 200px;
    flex-shrink: 0;
  }

  .search-actions {
    display: flex;
    gap: 12px;
    margin-left: auto;
  }
}


// 内容区域
.content-section {
  background: #ffffff;
  border: 1px solid #d1d1d6;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

// 表格视图
.table-view {
  .user-table {
    width: 100%;
  }

  .table-header {
    display: grid;
    grid-template-columns: 2fr 2fr 1.8fr 2fr 1fr 2fr;
    background: rgba(148, 163, 184, 0.08);
    border-bottom: 1px solid rgba(148, 163, 184, 0.15);

    .header-cell {
      padding: 16px 20px;
      font-weight: 600;
      color: #334155;
      font-size: 14px;
    }
  }

  .table-body {
    .table-row {
      display: grid;
      grid-template-columns: 2fr 2fr 1.8fr 2fr 1fr 2fr;
      border-bottom: 1px solid rgba(148, 163, 184, 0.1);
      transition: all 0.3s ease;

      &:hover {
        background: rgba(148, 163, 184, 0.05);
      }

      &:last-child {
        border-bottom: none;
      }
    }

    .table-cell {
      padding: 16px 20px;
      display: flex;
      align-items: center;
      min-height: 72px;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .user-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: linear-gradient(135deg, #64748b 0%, #475569 100%);
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 600;
        font-size: 16px;
        flex-shrink: 0;
      }

      .user-details {
        flex: 1;
        min-width: 0;

        .username {
          font-weight: 600;
          color: #0d47a1;
          font-size: 14px;
          margin-bottom: 2px;
        }

        .real-name {
          color: #64748b;
          font-size: 13px;
        }
      }
    }

    .contact-info {
      flex-direction: column;
      align-items: flex-start;
      gap: 4px;

      .contact-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 13px;

        .contact-label {
          color: #94a3b8;
          font-weight: 500;
          min-width: 36px;
        }

        .contact-value {
          color: #64748b;
        }
      }
    }

    .org-info {
      flex-direction: column;
      align-items: flex-start;
      gap: 8px;

      .org-item {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 13px;
        color: #64748b;
        padding: 4px 8px;
        background: rgba(148, 163, 184, 0.1);
        border-radius: 6px;
      }
    }

    .status-cell {
      .status-badge {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 500;

        &.active {
          background: rgba(34, 197, 94, 0.1);
          color: #16a34a;
          border: 1px solid rgba(34, 197, 94, 0.2);
        }

        &.inactive {
          background: rgba(148, 163, 184, 0.1);
          color: #64748b;
          border: 1px solid rgba(148, 163, 184, 0.2);
        }
      }
    }

    .actions {
      gap: 8px;
      flex-wrap: wrap;
      justify-content: flex-start;

      :deep(.el-button--link) {
        padding: 4px 8px;
        
        .el-icon {
          margin-right: 4px;
        }
      }
    }
  }
}

// 卡片视图
.card-view {
  padding: 24px;

  .user-cards {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 20px;
  }

  .user-card {
    background: rgba(255, 255, 255, 0.95);
    border: 1px solid rgba(25, 118, 210, 0.25);
    border-radius: 12px;
    padding: 20px;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 3px;
      background: linear-gradient(90deg, #1976d2, #42a5f5, #66bb6a);
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(25, 118, 210, 0.15);
      border-color: #1976d2;

      &::before {
        opacity: 0.6;
      }
    }

    .card-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;

      .user-avatar {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        background: linear-gradient(135deg, #64748b 0%, #475569 100%);
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 600;
        font-size: 18px;
        flex-shrink: 0;
      }

      .user-basic {
        flex: 1;
        min-width: 0;

        .username {
          font-weight: 600;
          color: #0d47a1;
          font-size: 16px;
          margin: 0 0 4px 0;
        }

        .real-name {
          color: #64748b;
          font-size: 14px;
          margin: 0;
        }
      }

      .status-badge {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 500;

        &.active {
          background: rgba(34, 197, 94, 0.1);
          color: #16a34a;
          border: 1px solid rgba(34, 197, 94, 0.2);
        }

        &.inactive {
          background: rgba(148, 163, 184, 0.1);
          color: #64748b;
          border: 1px solid rgba(148, 163, 184, 0.2);
        }
      }
    }

    .card-body {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-bottom: 20px;

      .info-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px 0;
        border-bottom: 1px solid rgba(148, 163, 184, 0.1);

        &:last-child {
          border-bottom: none;
        }

        .info-label {
          color: #64748b;
          font-size: 13px;
          font-weight: 500;
          min-width: 48px;
        }

        .info-value {
          color: #424242;
          font-size: 13px;
          display: flex;
          align-items: center;
          gap: 6px;
          text-align: right;
          flex: 1;
          justify-content: flex-end;
        }
      }
    }

    .card-actions {
      display: flex;
      gap: 8px;
      padding-top: 16px;
      border-top: 1px solid rgba(148, 163, 184, 0.1);

      .action-btn {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 6px;
        padding: 8px 12px;
        border: 1px solid rgba(148, 163, 184, 0.3);
        border-radius: 6px;
        background: rgba(248, 250, 252, 0.8);
        color: #64748b;
        font-size: 12px;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        &.edit:hover {
          border-color: #3b82f6;
          color: #3b82f6;
          background: rgba(59, 130, 246, 0.1);
        }

        &.skill:hover {
          border-color: #0ea5e9;
          color: #0ea5e9;
          background: rgba(14, 165, 233, 0.1);
        }

        &.reset:hover {
          border-color: #f59e0b;
          color: #f59e0b;
          background: rgba(245, 158, 11, 0.1);
        }

        &.delete:hover {
          border-color: #ef4444;
          color: #ef4444;
          background: rgba(239, 68, 68, 0.1);
        }
      }
    }
  }
}

// 分页
.pagination-container {
  margin-top: 24px;
  padding: 0 24px 24px;
  display: flex;
  justify-content: flex-end;
}

// 对话框样式优化
:deep(.el-dialog) {
  background: rgba(248, 250, 252, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

:deep(.el-dialog__header) {
  padding: 20px 24px 16px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.15);
}

:deep(.el-dialog__title) {
  color: #0d47a1;
  font-weight: 600;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-form-item__label) {
  color: #1565c0;
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  background: rgba(248, 250, 252, 0.8);
  border: 1px solid rgba(148, 163, 184, 0.3);
  border-radius: 6px;
  transition: all 0.3s ease;

  &:hover {
    border-color: #94a3b8;
  }

  &.is-focus {
    border-color: #1976d2;
    box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.1);
  }
}

:deep(.el-select) {
  .el-input__wrapper {
    background: rgba(248, 250, 252, 0.8);
    border: 1px solid rgba(148, 163, 184, 0.3);
    border-radius: 6px;
  }
}

:deep(.el-tree-select) {
  .el-input__wrapper {
    background: rgba(248, 250, 252, 0.8);
    border: 1px solid rgba(148, 163, 184, 0.3);
    border-radius: 6px;
  }
}

:deep(.el-radio-group) {
  .el-radio {
    margin-right: 20px;

    .el-radio__label {
      color: #64748b;
    }

    &.is-checked .el-radio__label {
      color: #1976d2;
    }
  }
}

// iOS风格按钮样式
.header-add-btn {
  border-radius: 12px;
  font-weight: 500;
  font-size: 14px;
  padding: 12px 20px;
  gap: 8px;
  background: #007AFF;
  border-color: #007AFF;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    background: #0056CC;
    border-color: #0056CC;
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(0, 122, 255, 0.3);
  }
  
  &:active {
    transform: translateY(0);
  }
}

.search-btn {
  border-radius: 12px;
  font-weight: 500;
  padding: 10px 18px;
  gap: 6px;
  background: #007AFF;
  border-color: #007AFF;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    background: #0056CC;
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 122, 255, 0.25);
  }
}

.reset-btn {
  border-radius: 12px;
  font-weight: 500;
  padding: 10px 18px;
  gap: 6px;
  background: #F2F2F7;
  color: #6E6E73;
  border-color: #D1D1D6;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    background: #E5E5EA;
    color: #1D1D1F;
    border-color: #C7C7CC;
    transform: translateY(-1px);
  }
}

.action-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  padding: 0;
  margin: 0 4px;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  border: 1px solid transparent;
  
  &:hover {
    transform: scale(1.05);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  }
  
  &:active {
    transform: scale(0.95);
  }
  
  &.el-button--primary {
    background: #007AFF;
    border-color: #007AFF;
    
    &:hover {
      background: #0056CC;
    }
  }
  
  &.el-button--success {
    background: #34C759;
    border-color: #34C759;
    
    &:hover {
      background: #28A745;
    }
  }
  
  &.el-button--info {
    background: #5AC8FA;
    border-color: #5AC8FA;
    
    &:hover {
      background: #32ADE6;
    }
  }
  
  &.el-button--warning {
    background: #FF9500;
    border-color: #FF9500;
    
    &:hover {
      background: #E8800A;
    }
  }
  
  &.el-button--danger {
    background: #FF3B30;
    border-color: #FF3B30;
    
    &:hover {
      background: #E62117;
    }
  }
}

.cancel-btn, .confirm-btn {
  border-radius: 12px;
  font-weight: 500;
  padding: 10px 20px;
  gap: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    transform: translateY(-1px);
  }
}

.cancel-btn {
  background: #F2F2F7;
  color: #6E6E73;
  border-color: #D1D1D6;
  
  &:hover {
    background: #E5E5EA;
    color: #1D1D1F;
  }
}

.confirm-btn {
  background: #007AFF;
  border-color: #007AFF;
  
  &:hover {
    background: #0056CC;
    box-shadow: 0 2px 8px rgba(0, 122, 255, 0.25);
  }
}

:deep(.el-button) {
  border-radius: 12px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);

  &.el-button--primary {
    background: #007AFF;
    border-color: #007AFF;

    &:hover {
      background: #0056CC;
      border-color: #0056CC;
      transform: translateY(-1px);
      box-shadow: 0 4px 16px rgba(0, 122, 255, 0.3);
    }
  }

  &.el-button--default {
    background: #F2F2F7;
    border-color: #D1D1D6;
    color: #6E6E73;

    &:hover {
      background: #E5E5EA;
      border-color: #C7C7CC;
      color: #1D1D1F;
    }
  }
}

:deep(.el-pagination) {
  .el-pager li {
    background: rgba(248, 250, 252, 0.8);
    border: 1px solid rgba(148, 163, 184, 0.2);
    color: #64748b;
    border-radius: 6px;
    margin: 0 2px;

    &:hover {
      background: rgba(148, 163, 184, 0.1);
      color: #475569;
    }

    &.is-active {
      background: #1976d2;
      border-color: #1976d2;
      color: white;
    }
  }

  .btn-prev, .btn-next {
    background: rgba(248, 250, 252, 0.8);
    border: 1px solid rgba(148, 163, 184, 0.2);
    color: #64748b;
    border-radius: 6px;

    &:hover {
      background: rgba(148, 163, 184, 0.1);
      color: #475569;
    }
  }

  .el-select .el-input {
    .el-input__wrapper {
      background: rgba(248, 250, 252, 0.8);
      border: 1px solid rgba(148, 163, 184, 0.2);
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .table-view .table-header,
  .table-view .table-row {
    grid-template-columns: 1.5fr 1.5fr 1.5fr 1fr 1fr;
  }

  .user-cards {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  }
}

@media (max-width: 768px) {
  .user-container {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .header-actions {
    justify-content: space-between;
    margin-top: 0;
  }

  .search-row {
    flex-direction: column;
    align-items: stretch;

    .search-input {
      width: 100%;
    }

    .search-actions {
      margin-left: 0;
      justify-content: center;
    }
  }

  .table-view {
    overflow-x: auto;

    .table-header,
    .table-row {
      min-width: 800px;
    }
  }

  .user-cards {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .user-container {
    padding: 12px;
  }

  .search-section {
    padding: 16px;
  }

  .view-toggle {
    order: 2;
  }

  .add-btn {
    flex: 1;
    order: 1;
    justify-content: center;
  }

  .user-card {
    padding: 16px;

    .card-actions {
      flex-direction: column;

      .action-btn {
        justify-content: flex-start;
      }
    }
  }
}

// 技能标签样式
.skills-cell {
  .skills-container {
    width: 100%;
    
    .skill-tags {
      display: flex;
      flex-direction: column;
      gap: 8px;
      
      .skill-tags-main {
        display: flex;
        flex-wrap: wrap;
        gap: 6px;
        align-items: center;
      }
      
      .skill-more {
        display: flex;
        align-items: center;
      }
      
      .skill-tag {
        font-size: 12px;
        border-radius: 8px;
        padding: 4px 10px;
        font-weight: 500;
        border: none;
        display: flex;
        align-items: center;
        gap: 4px;
        transition: all 0.3s ease;
        
        .skill-name {
          font-weight: 600;
        }
        
        .skill-level {
          font-size: 10px;
          opacity: 0.8;
          background: rgba(255, 255, 255, 0.2);
          padding: 1px 4px;
          border-radius: 4px;
          margin-left: 2px;
        }
        
        &.more-tag {
          background: #5AC8FA !important;
          color: #ffffff !important;
          cursor: pointer;
          display: flex;
          align-items: center;
          gap: 4px;
          
          &:hover {
            background: #32ADE6 !important;
            transform: scale(1.05);
          }
        }
        
        &.popover-skill {
          margin-bottom: 6px;
        }
      }
    }
    
    .no-skills {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #8e8e93;
      
      .no-skills-icon {
        opacity: 0.6;
      }
      
      .no-skills-text {
        font-size: 13px;
        font-style: italic;
      }
    }
  }
}

// 技能弹窗样式
:deep(.skills-popover) {
  .all-skills {
    .popover-title {
      font-weight: 600;
      color: #1d1d1f;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 1px solid #e9ecef;
    }
    
    .skills-grid {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      max-height: 200px;
      overflow-y: auto;
    }
  }
}

// Element Plus标签样式覆盖
:deep(.el-tag) {
  border: none !important;
  font-weight: 500 !important;
  
  &.el-tag--success {
    background: #34c759 !important;
    color: #ffffff !important;
  }
  
  &.el-tag--warning {
    background: #ff9500 !important;
    color: #ffffff !important;
  }
  
  &.el-tag--danger {
    background: #ff3b30 !important;
    color: #ffffff !important;
  }
  
  &.el-tag--info {
    background: #8e8e93 !important;
    color: #ffffff !important;
  }
  
  &:not(.el-tag--success):not(.el-tag--warning):not(.el-tag--danger):not(.el-tag--info) {
    background: #636366 !important;
    color: #ffffff !important;
  }
}

// 操作按钮样式优化
.actions {
  .actions-container {
    display: flex;
    gap: 8px;
    align-items: center;
    justify-content: flex-end;
    
    .primary-actions {
      display: flex;
      gap: 6px;
      align-items: center;
    }
    
    .secondary-actions {
      display: flex;
      gap: 6px;
      align-items: center;
      margin-left: 8px;
      padding-left: 8px;
      border-left: 1px solid #e9ecef;
    }
    
    .action-btn {
      &.primary {
        box-shadow: 0 2px 8px rgba(0, 122, 255, 0.3);
        
        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba(0, 122, 255, 0.4);
        }
      }
      
      &.secondary {
        opacity: 0.8;
        
        &:hover {
          opacity: 1;
        }
      }
      
      &.more-btn {
        background: #f2f2f7;
        border-color: #d1d1d6;
        color: #8e8e93;
        
        &:hover {
          background: #e5e5ea;
          color: #1d1d1f;
          transform: translateY(-1px);
        }
      }
    }
  }
}

// 下拉菜单样式
:deep(.user-actions-dropdown) {
  .el-dropdown-menu__item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 16px;
    transition: all 0.3s ease;
    
    .dropdown-icon {
      font-size: 16px;
    }
    
    &:hover {
      background: #f8f9fa;
      color: #007AFF;
      
      .dropdown-icon {
        color: #007AFF;
      }
    }
    
    &.danger-item {
      &:hover {
        background: #fff5f5;
        color: #FF3B30;
        
        .dropdown-icon {
          color: #FF3B30;
        }
      }
    }
  }
  
  .el-dropdown-menu {
    border-radius: 8px;
    border: 1px solid #d1d1d6;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  }
}
</style> 