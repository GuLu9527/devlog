<template>
  <div class="skill-tag-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">技能标签管理</h1>
        <div class="page-subtitle">管理系统技术栈标签和分类</div>
      </div>
      <div class="header-actions">
        <el-button 
          v-permission="'skilltag:create'"
          class="action-btn secondary" 
          @click="handleImport">
          <ActionIcon action="upload" :size="18" />
          <span>批量导入</span>
        </el-button>
        <el-button 
          v-permission="'skilltag:list'"
          class="action-btn secondary" 
          @click="handleExport">
          <ActionIcon action="upload" :size="18" />
          <span>导出数据</span>
        </el-button>
        <el-button 
          v-permission="'skilltag:create'"
          type="primary" 
          class="add-btn" 
          @click="handleAdd">
          <ActionIcon action="add" :size="18" />
          <span>新增标签</span>
        </el-button>
      </div>
    </div>

    <!-- 搜索过滤区域 -->
    <div class="search-section">
      <div class="search-form">
        <div class="search-row">
          <el-input
            v-model="searchForm.tagName"
            placeholder="请输入标签名称"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <el-select
            v-model="searchForm.category"
            placeholder="请选择分类"
            clearable
            class="search-input"
          >
            <el-option
              v-for="category in categories"
              :key="category"
              :label="category"
              :value="category"
            />
          </el-select>
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            class="search-input"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
          <div class="search-actions">
            <el-button type="primary" class="search-btn" @click="handleSearch">
              <ActionIcon action="search" :size="16" />
              <span>搜索</span>
            </el-button>
            <el-button class="reset-btn" @click="resetSearch">
              <ActionIcon action="refresh" :size="16" />
              <span>重置</span>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 标签列表 -->
    <div class="content-section">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-checkbox
            v-model="selectAll"
            :indeterminate="indeterminate"
            @change="handleSelectAll"
          >
            全选
          </el-checkbox>
          <el-button
            v-permission="'skilltag:delete'"
            type="danger"
            class="batch-btn"
            :disabled="selectedTags.length === 0"
            @click="handleBatchDelete"
          >
            <ActionIcon action="delete" :size="16" />
            <span>批量删除</span>
          </el-button>
        </div>
        <div class="toolbar-right">
          <span class="total-info">共 {{ total }} 个标签</span>
        </div>
      </div>

      <!-- 标签卡片网格 -->
      <div v-loading="loading" class="tag-grid">
        <div
          v-for="tag in tableData"
          :key="tag.tagId"
          class="tag-card"
          :class="{ selected: selectedTags.includes(tag.tagId) }"
        >
          <div class="card-header">
            <el-checkbox
              :model-value="selectedTags.includes(tag.tagId)"
              @change="handleTagSelect(tag.tagId, $event)"
            />
            <div class="tag-info">
              <div class="tag-visual">
                <span
                  class="tag-color"
                  :style="{ backgroundColor: tag.color }"
                ></span>
                <span class="tag-name">{{ tag.tagName }}</span>
              </div>
              <div class="tag-category">{{ tag.category }}</div>
            </div>
            <div class="status-badge" :class="tag.status === 1 ? 'active' : 'inactive'">
              <SkillIcons type="skill" :size="12" />
              <span>{{ tag.status === 1 ? '启用' : '禁用' }}</span>
            </div>
          </div>

          <div class="card-body">
            <div class="tag-description">
              {{ tag.description || '暂无描述' }}
            </div>
            <div class="tag-stats">
              <div class="stat-item">
                <SkillIcons type="statistics" :size="14" />
                <span>使用: {{ tag.userCount || 0 }} 人</span>
              </div>
              <div class="stat-item">
                <SkillIcons type="requirement" :size="14" />
                <span>需求: {{ tag.taskCount || 0 }} 个</span>
              </div>
            </div>
          </div>

          <div class="card-actions">
            <el-tooltip content="编辑标签" placement="top">
              <el-button 
                v-permission="'skilltag:update'"
                type="primary" 
                circle 
                class="action-btn" 
                @click="handleEdit(tag)">
                <ActionIcon action="edit" :size="16" />
              </el-button>
            </el-tooltip>
            <el-tooltip content="查看用户" placement="top">
              <el-button 
                v-permission="'skilltag:read'"
                type="info" 
                circle 
                class="action-btn" 
                @click="handleViewUsers(tag)">
                <ActionIcon action="view" :size="16" />
              </el-button>
            </el-tooltip>
            <el-tooltip content="删除标签" placement="top">
              <el-button 
                v-permission="'skilltag:delete'"
                type="danger" 
                circle 
                class="action-btn" 
                @click="handleDelete(tag)">
                <ActionIcon action="delete" :size="16" />
              </el-button>
            </el-tooltip>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[12, 24, 48, 96]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑标签对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增标签' : '编辑标签'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="标签名称" prop="tagName">
          <el-input v-model="form.tagName" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="标签描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入标签描述"
          />
        </el-form-item>
        <el-form-item label="标签颜色" prop="color">
          <el-color-picker v-model="form.color" show-alpha />
        </el-form-item>
        <el-form-item label="标签分类" prop="category">
          <el-select
            v-model="form.category"
            placeholder="请选择或输入分类"
            filterable
            allow-create
            style="width: 100%"
          >
            <el-option
              v-for="category in categories"
              :key="category"
              :label="category"
              :value="category"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序权重" prop="sortOrder">
          <el-input-number
            v-model="form.sortOrder"
            :min="0"
            :max="999"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="cancel-btn" @click="dialogVisible = false">
            <ActionIcon action="close" :size="14" />
            取消
          </el-button>
          <el-button type="primary" class="confirm-btn" @click="handleSubmit">
            <ActionIcon action="check" :size="14" />
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户列表对话框 -->
    <el-dialog
      v-model="userListVisible"
      :title="`拥有 ${currentTag?.tagName} 技能的用户`"
      width="800px"
    >
      <div v-loading="userListLoading" class="user-list">
        <div
          v-for="user in userList"
          :key="user.userId"
          class="user-item"
        >
          <div class="user-avatar">
            {{ user.realName?.charAt(0)?.toUpperCase() || 'U' }}
          </div>
          <div class="user-info">
            <div class="user-name">{{ user.realName || user.username }}</div>
            <div class="user-level">{{ user.proficiencyLevel }} · {{ user.selfRating }}/10分</div>
          </div>
          <div class="user-experience">
            {{ user.yearsOfExperience || 0 }} 年经验
          </div>
        </div>
        <div v-if="userList.length === 0" class="empty-state">
          <SkillIcons type="skill" :size="48" />
          <p>暂无用户掌握此技能</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { SkillIcons, UserIcons, ActionIcon } from '@/components/SvgIcons'
import {
  getSkillTagList,
  createSkillTag,
  updateSkillTag,
  deleteSkillTag,
  batchDeleteSkillTags,
  getSkillTagCategories
} from '@/api/skillTag'
import { getUsersBySkillTag } from '@/api/userSkill'

// 搜索表单
const searchForm = ref({
  tagName: '',
  category: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(24)
const total = ref(0)

// 分类数据
const categories = ref([])

// 选择相关
const selectedTags = ref([])
const selectAll = ref(false)
const indeterminate = computed(() => {
  return selectedTags.value.length > 0 && selectedTags.value.length < tableData.value.length
})

// 表单数据
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const form = ref({
  tagName: '',
  description: '',
  color: '#0ea5e9',
  category: '',
  sortOrder: 0,
  status: 1
})

// 用户列表对话框
const userListVisible = ref(false)
const userListLoading = ref(false)
const userList = ref([])
const currentTag = ref(null)

// 表单验证规则
const rules = {
  tagName: [
    { required: true, message: '请输入标签名称', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 255, message: '长度不能超过255个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择标签分类', trigger: 'change' }
  ],
  sortOrder: [
    { type: 'number', message: '排序权重必须是数字', trigger: 'blur' }
  ]
}

// 获取标签列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm.value
    }
    const res = await getSkillTagList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.message || '获取标签列表失败')
    }
  } catch (error) {
    console.error('获取标签列表失败:', error)
    ElMessage.error('获取标签列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const getCategories = async () => {
  try {
    const res = await getSkillTagCategories()
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
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
    tagName: '',
    category: '',
    status: ''
  }
  handleSearch()
}

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    tagName: '',
    description: '',
    color: '#0ea5e9',
    category: '',
    sortOrder: 0,
    status: 1
  }
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确认删除标签"${row.tagName}"吗？此操作不可撤销。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await deleteSkillTag(row.tagId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      console.error('删除标签失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 查看用户
const handleViewUsers = async (tag) => {
  currentTag.value = tag
  userListVisible.value = true
  userListLoading.value = true
  
  try {
    const res = await getUsersBySkillTag(tag.tagId)
    if (res.code === 200) {
      userList.value = res.data
    } else {
      ElMessage.error(res.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    userListLoading.value = false
  }
}

// 标签选择
const handleTagSelect = (tagId, checked) => {
  if (checked) {
    selectedTags.value.push(tagId)
  } else {
    const index = selectedTags.value.indexOf(tagId)
    if (index > -1) {
      selectedTags.value.splice(index, 1)
    }
  }
}

// 全选
const handleSelectAll = (checked) => {
  if (checked) {
    selectedTags.value = tableData.value.map(tag => tag.tagId)
  } else {
    selectedTags.value = []
  }
}

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(
    `确认删除选中的 ${selectedTags.value.length} 个标签吗？此操作不可撤销。`,
    '批量删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await batchDeleteSkillTags(selectedTags.value)
      if (res.code === 200) {
        ElMessage.success('批量删除成功')
        selectedTags.value = []
        getList()
      } else {
        ElMessage.error(res.message || '批量删除失败')
      }
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  })
}

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (dialogType.value === 'add') {
          res = await createSkillTag(form.value)
        } else {
          res = await updateSkillTag(form.value.tagId, form.value)
        }
        
        if (res.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '新增成功' : '更新成功')
          dialogVisible.value = false
          getList()
          getCategories() // 更新分类列表
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

// 导入导出
const handleImport = () => {
  ElMessage.info('批量导入功能开发中...')
}

const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  getList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getList()
}

// 初始化
onMounted(() => {
  getList()
  getCategories()
})
</script>

<style lang="scss" scoped>
.skill-tag-container {
  padding: 24px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 30%, #f5f5f7 70%, #e5e5ea 100%);
  min-height: calc(100vh - 64px);
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(circle at 25% 25%, rgba(0, 0, 0, 0.02) 0%, transparent 50%),
                radial-gradient(circle at 75% 75%, rgba(0, 0, 0, 0.01) 0%, transparent 50%);
    pointer-events: none;
  }
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
  letter-spacing: -0.5px;
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

.action-btn {
  border-radius: 12px;
  font-weight: 500;
  font-size: 14px;
  padding: 12px 20px;
  gap: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    transform: translateY(-1px);
  }
  
  &:active {
    transform: translateY(0);
  }
  
  &.secondary {
    background: linear-gradient(135deg, #f2f2f7 0%, #e5e5ea 100%);
    color: #6E6E73;
    border-color: #D1D1D6;
    
    &:hover {
      background: linear-gradient(135deg, #e5e5ea 0%, #d1d1d6 100%);
      color: #1D1D1F;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
}

.add-btn {
  border-radius: 12px;
  font-weight: 500;
  font-size: 14px;
  padding: 12px 20px;
  gap: 8px;
  background: linear-gradient(135deg, #1d1d1f 0%, #2c2c2e 100%);
  border-color: #1d1d1f;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    background: linear-gradient(135deg, #2c2c2e 0%, #48484a 100%);
    border-color: #2c2c2e;
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(29, 29, 31, 0.3);
  }
  
  &:active {
    transform: translateY(0);
  }
}

// 搜索区域
.search-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
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

.search-btn, .reset-btn {
  border-radius: 12px;
  font-weight: 500;
  font-size: 14px;
  padding: 10px 16px;
  gap: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
  
  &:hover {
    transform: translateY(-1px);
  }
  
  &:active {
    transform: translateY(0);
  }
}

.search-btn {
  background: linear-gradient(135deg, #1d1d1f 0%, #2c2c2e 100%);
  color: white;
  border-color: #1d1d1f;

  &:hover {
    background: linear-gradient(135deg, #2c2c2e 0%, #48484a 100%);
    border-color: #2c2c2e;
    box-shadow: 0 2px 8px rgba(29, 29, 31, 0.25);
  }
}

.reset-btn {
  background: linear-gradient(135deg, #f2f2f7 0%, #e5e5ea 100%);
  color: #6E6E73;
  border-color: #D1D1D6;
  
  &:hover {
    background: linear-gradient(135deg, #e5e5ea 0%, #d1d1d6 100%);
    color: #1D1D1F;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}

// 内容区域
.content-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: rgba(0, 0, 0, 0.02);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.batch-btn {
  border-radius: 12px;
  font-weight: 500;
  font-size: 13px;
  padding: 8px 16px;
  gap: 6px;
  background: rgba(220, 38, 38, 0.1);
  color: #DC2626;
  border-color: #DC2626;
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);

  &:hover:not(:disabled) {
    background: linear-gradient(135deg, #DC2626 0%, #B91C1C 100%);
    color: white;
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(220, 38, 38, 0.25);
  }
  
  &:active:not(:disabled) {
    transform: translateY(0);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.total-info {
  color: #6e6e73;
  font-size: 14px;
}

// 标签网格
.tag-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  padding: 24px;
}

.tag-card {
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid rgba(0, 0, 0, 0.08);
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
    background: linear-gradient(90deg, #1d1d1f 0%, #48484a 50%, #636366 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  &:hover, &.selected {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
    border-color: #48484a;

    &::before {
      opacity: 0.8;
    }
  }

  &.selected {
    border-color: #1d1d1f;
    background: rgba(0, 0, 0, 0.04);
  }

  .card-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;

    .tag-info {
      flex: 1;
      min-width: 0;

      .tag-visual {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 4px;

        .tag-color {
          width: 12px;
          height: 12px;
          border-radius: 50%;
          flex-shrink: 0;
        }

        .tag-name {
          font-weight: 600;
          color: #1d1d1f;
          font-size: 16px;
        }
      }

      .tag-category {
        color: #6e6e73;
        font-size: 13px;
        font-weight: 500;
      }
    }

    .status-badge {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 4px 8px;
      border-radius: 16px;
      font-size: 12px;
      font-weight: 500;

      &.active {
        background: rgba(36, 138, 61, 0.1);
        color: #248A3D;
        border: 1px solid rgba(36, 138, 61, 0.2);
      }

      &.inactive {
        background: rgba(0, 0, 0, 0.06);
        color: #6e6e73;
        border: 1px solid rgba(0, 0, 0, 0.08);
      }
    }
  }

  .card-body {
    margin-bottom: 16px;

    .tag-description {
      color: #48484a;
      font-size: 14px;
      line-height: 1.5;
      margin-bottom: 12px;
      min-height: 42px;
    }

    .tag-stats {
      display: flex;
      gap: 16px;

      .stat-item {
        display: flex;
        align-items: center;
        gap: 4px;
        color: #6e6e73;
        font-size: 12px;
      }
    }
  }

  .card-actions {
    display: flex;
    gap: 8px;
    padding-top: 16px;
    border-top: 1px solid rgba(0, 0, 0, 0.06);

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
        background: linear-gradient(135deg, #1d1d1f 0%, #2c2c2e 100%);
        border-color: #1d1d1f;
        
        &:hover {
          background: linear-gradient(135deg, #2c2c2e 0%, #48484a 100%);
        }
      }
      
      &.el-button--info {
        background: linear-gradient(135deg, #248A3D 0%, #1E7A34 100%);
        border-color: #248A3D;
        
        &:hover {
          background: linear-gradient(135deg, #1E7A34 0%, #166A2B 100%);
        }
      }
      
      &.el-button--danger {
        background: linear-gradient(135deg, #DC2626 0%, #B91C1C 100%);
        border-color: #DC2626;
        
        &:hover {
          background: linear-gradient(135deg, #B91C1C 0%, #991B1B 100%);
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

// 用户列表
.user-list {
  max-height: 400px;
  overflow-y: auto;

  .user-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 0;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);

    &:last-child {
      border-bottom: none;
    }

    .user-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: linear-gradient(135deg, #1d1d1f 0%, #48484a 100%);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 600;
      font-size: 14px;
      flex-shrink: 0;
    }

    .user-info {
      flex: 1;

      .user-name {
        font-weight: 600;
        color: #1d1d1f;
        font-size: 14px;
        margin-bottom: 2px;
      }

      .user-level {
        color: #6e6e73;
        font-size: 12px;
      }
    }

    .user-experience {
      color: #6e6e73;
      font-size: 12px;
      font-weight: 500;
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #8e8e93;

  p {
    margin-top: 12px;
    font-size: 14px;
  }
}

// 对话框样式优化
:deep(.el-dialog) {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

:deep(.el-dialog__header) {
  padding: 20px 24px 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
}

:deep(.el-dialog__title) {
  color: #1d1d1f;
  font-weight: 600;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-form-item__label) {
  color: #1d1d1f;
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 6px;
  transition: all 0.3s ease;

  &:hover {
    border-color: #6e6e73;
  }

  &.is-focus {
    border-color: #1d1d1f;
    box-shadow: 0 0 0 2px rgba(29, 29, 31, 0.1);
  }
}

:deep(.el-select) {
  .el-input__wrapper {
    background: rgba(255, 255, 255, 0.9);
    border: 1px solid rgba(0, 0, 0, 0.1);
    border-radius: 6px;
  }
}

:deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 6px;

  &:hover {
    border-color: #6e6e73;
  }

  &:focus {
    border-color: #1d1d1f;
    box-shadow: 0 0 0 2px rgba(29, 29, 31, 0.1);
  }
}

:deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;

  &.el-button--primary {
    background: linear-gradient(135deg, #1d1d1f 0%, #2c2c2e 100%);
    border-color: #1d1d1f;

    &:hover {
      background: linear-gradient(135deg, #2c2c2e 0%, #48484a 100%);
      border-color: #2c2c2e;
    }
  }

  &.el-button--default {
    background: rgba(242, 242, 247, 0.8);
    border-color: rgba(0, 0, 0, 0.1);
    color: #6e6e73;

    &:hover {
      background: rgba(229, 229, 234, 0.8);
      border-color: #d1d1d6;
      color: #1d1d1f;
    }
  }
}

// 对话框footer样式
.dialog-footer {
  display: flex;
  gap: 12px;

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
    background: linear-gradient(135deg, #1d1d1f 0%, #2c2c2e 100%);
    border-color: #1d1d1f;
    
    &:hover {
      background: linear-gradient(135deg, #2c2c2e 0%, #48484a 100%);
      box-shadow: 0 2px 8px rgba(29, 29, 31, 0.25);
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .tag-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }
}

@media (max-width: 768px) {
  .skill-tag-container {
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

  .tag-grid {
    grid-template-columns: 1fr;
  }

  .toolbar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
}
</style>