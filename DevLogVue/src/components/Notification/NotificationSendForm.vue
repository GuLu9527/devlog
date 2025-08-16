<template>
  <el-dialog
    v-model="visible"
    title="📨 发送通知"
    width="650px"
    :before-close="handleClose"
    class="notification-dialog"
  >
    <div class="form-container">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        label-position="top"
        class="notification-form"
      >
      <el-row :gutter="16">
        <el-col :span="24">
          <el-form-item label="通知标题" prop="title">
            <el-input
              v-model="form.title"
              placeholder="请输入通知标题"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 通知配置卡片 -->
      <div class="form-card">
        <div class="card-header">
          <h4>⚙️ 通知配置</h4>
        </div>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="通知类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择通知类型" style="width: 100%">
                <el-option value="TASK_ASSIGN" label="📋 任务分配" />
                <el-option value="WORKLOG_REVIEW" label="📝 日志审核" />
                <el-option value="TASK_DEADLINE" label="⏰ 任务截止" />
                <el-option value="PROJECT_UPDATE" label="📁 项目更新" />
                <el-option value="SYSTEM_MESSAGE" label="📢 系统消息" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="通知级别" prop="level">
              <el-select v-model="form.level" placeholder="请选择通知级别" style="width: 100%">
                <el-option value="普通" label="🔵 普通" />
                <el-option value="重要" label="🟡 重要" />
                <el-option value="紧急" label="🔴 紧急" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 接收人卡片 -->
      <div class="form-card">
        <div class="card-header">
          <h4>👥 接收人设置</h4>
        </div>
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="接收人" prop="receiverIds">
              <el-select
                v-model="form.receiverIds"
                placeholder="🔍 搜索并选择接收人"
                multiple
                filterable
                remote
                reserve-keyword
                :remote-method="searchUsers"
                :loading="usersLoading"
                style="width: 100%"
                collapse-tags
                collapse-tags-tooltip
              >
                <el-option
                  v-for="user in userOptions"
                  :key="user.id"
                  :label="user.realName || user.username"
                  :value="user.id"
                >
                  <div class="user-option">
                    <span class="user-name">{{ user.realName || user.username }}</span>
                    <span class="user-info">{{ user.departmentName || '未分配部门' }}</span>
                  </div>
                </el-option>
              </el-select>
              <div class="form-tip">
                💡 可选择多个用户，支持按姓名或用户名搜索
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 通知内容卡片 -->
      <div class="form-card">
        <div class="card-header">
          <h4>✏️ 通知内容</h4>
        </div>
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="通知内容" prop="content">
              <el-input
                v-model="form.content"
                type="textarea"
                :rows="6"
                placeholder="📝 请输入详细的通知内容..."
                maxlength="1000"
                show-word-limit
                class="content-textarea"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 关联业务卡片 -->
      <div class="form-card" v-if="form.type !== 'SYSTEM_MESSAGE'">
        <div class="card-header">
          <h4>🔗 关联业务</h4>
        </div>
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="关联业务" class="related-business-item">
              <div class="related-business-container">
                <el-select 
                  v-model="form.relatedType" 
                  placeholder="选择业务类型" 
                  style="width: 140px"
                  @change="handleRelatedTypeChange"
                >
                  <el-option value="task" label="📋 任务" />
                  <el-option value="worklog" label="📝 工作日志" />
                  <el-option value="project" label="📁 项目" />
                </el-select>
                
                <el-select
                  v-if="form.relatedType"
                  v-model="form.relatedId"
                  :placeholder="`🔍 搜索${getRelatedTypeText(form.relatedType)}`"
                  filterable
                  remote
                  reserve-keyword
                  :remote-method="searchRelatedItems"
                  :loading="relatedItemsLoading"
                  style="flex: 1; margin-left: 12px"
                  clearable
                >
                  <el-option
                    v-for="item in relatedItemOptions"
                    :key="item.id"
                    :label="item.title || item.name"
                    :value="item.id"
                  >
                    <div class="related-option">
                      <span class="option-title">{{ item.title || item.name }}</span>
                      <span class="option-info">{{ getRelatedItemInfo(item) }}</span>
                    </div>
                  </el-option>
                </el-select>
              </div>
              <div class="form-tip" v-if="form.relatedType">
                💡 选择与通知相关的{{ getRelatedTypeText(form.relatedType) }}，便于用户快速定位
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="sendingBatch" @click="handleSend">
          {{ sendingBatch ? '发送中...' : '发送通知' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { sendNotification, batchSendNotification } from '@/api/notification'
import { getUserList } from '@/api/user'
import { getTaskList } from '@/api/task'
import { getProjectList } from '@/api/project'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  // 预设接收人
  presetReceivers: {
    type: Array,
    default: () => []
  },
  // 预设通知类型
  presetType: {
    type: String,
    default: ''
  },
  // 预设关联信息
  presetRelated: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

// 响应式数据
const visible = ref(false)
const formRef = ref(null)
const sendingBatch = ref(false)
const usersLoading = ref(false)
const userOptions = ref([])
const relatedItemsLoading = ref(false)
const relatedItemOptions = ref([])

const form = reactive({
  title: '',
  content: '',
  type: 'SYSTEM_MESSAGE',
  level: '普通',
  receiverIds: [],
  relatedType: '',
  relatedId: null
})

const rules = {
  title: [
    { required: true, message: '请输入通知标题', trigger: 'blur' },
    { min: 1, max: 200, message: '标题长度在 1 到 200 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入通知内容', trigger: 'blur' },
    { min: 1, max: 1000, message: '内容长度在 1 到 1000 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择通知类型', trigger: 'change' }
  ],
  level: [
    { required: true, message: '请选择通知级别', trigger: 'change' }
  ],
  receiverIds: [
    { required: true, message: '请选择接收人', trigger: 'change' },
    { type: 'array', min: 1, message: '至少选择一个接收人', trigger: 'change' }
  ]
}

// 监听对话框显示状态
watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
  if (newVal) {
    resetForm()
    loadPresetData()
    loadInitialUsers()
  }
})

watch(visible, (newVal) => {
  emit('update:modelValue', newVal)
})

// 方法
const resetForm = () => {
  Object.assign(form, {
    title: '',
    content: '',
    type: 'SYSTEM_MESSAGE',
    level: '普通',
    receiverIds: [],
    relatedType: '',
    relatedId: null
  })
  
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

const loadPresetData = () => {
  // 加载预设数据
  if (props.presetType) {
    form.type = props.presetType
  }
  
  if (props.presetReceivers.length > 0) {
    form.receiverIds = props.presetReceivers.map(user => user.id || user)
    // 将预设用户添加到选项中
    userOptions.value = props.presetReceivers.filter(user => user.id && user.realName)
  }
  
  if (props.presetRelated.type) {
    form.relatedType = props.presetRelated.type
  }
  if (props.presetRelated.id) {
    form.relatedId = props.presetRelated.id
  }
}

const loadInitialUsers = async () => {
  try {
    usersLoading.value = true
    const response = await getUserList({
      pageNum: 1,
      pageSize: 50 // 加载前50个用户作为初始选项
    })
    
    if (response.data && response.data.records) {
      userOptions.value = response.data.records
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  } finally {
    usersLoading.value = false
  }
}

const searchUsers = async (query) => {
  if (!query) {
    loadInitialUsers()
    return
  }
  
  try {
    usersLoading.value = true
    const response = await getUserList({
      pageNum: 1,
      pageSize: 20,
      keyword: query // 假设接口支持关键字搜索
    })
    
    if (response.data && response.data.records) {
      userOptions.value = response.data.records
    }
  } catch (error) {
    console.error('搜索用户失败:', error)
  } finally {
    usersLoading.value = false
  }
}

const handleRelatedTypeChange = () => {
  // 清空关联ID和选项
  form.relatedId = null
  relatedItemOptions.value = []
  
  // 加载对应类型的初始数据
  if (form.relatedType) {
    loadInitialRelatedItems()
  }
}

const searchRelatedItems = async (query) => {
  if (!form.relatedType) return
  
  try {
    relatedItemsLoading.value = true
    let response = null
    
    switch (form.relatedType) {
      case 'task':
        response = await getTaskList({
          pageNum: 1,
          pageSize: 20,
          title: query || undefined
        })
        break
      case 'project':
        response = await getProjectList({
          pageNum: 1,
          pageSize: 20,
          name: query || undefined
        })
        break
      case 'worklog':
        // 工作日志暂时不实现搜索，或者可以添加相应的API
        relatedItemOptions.value = []
        return
    }
    
    if (response && response.data && response.data.records) {
      relatedItemOptions.value = response.data.records
    }
  } catch (error) {
    console.error('搜索关联业务失败:', error)
  } finally {
    relatedItemsLoading.value = false
  }
}

const loadInitialRelatedItems = async () => {
  await searchRelatedItems('')
}

const getRelatedTypeText = (type) => {
  const typeMap = {
    'task': '任务',
    'worklog': '工作日志',
    'project': '项目'
  }
  return typeMap[type] || type
}

const getRelatedItemInfo = (item) => {
  if (form.relatedType === 'task') {
    return `状态: ${item.status || '未知'} | 优先级: ${item.priority || '未知'}`
  } else if (form.relatedType === 'project') {
    return `状态: ${item.status || '未知'} | 创建时间: ${item.createTime ? new Date(item.createTime).toLocaleDateString() : '未知'}`
  } else if (form.relatedType === 'worklog') {
    return `日期: ${item.logDate || '未知'} | 工时: ${item.workHours || 0}h`
  }
  return ''
}

const handleSend = async () => {
  if (!formRef.value) return
  
  try {
    const valid = await formRef.value.validate()
    if (!valid) return
    
    sendingBatch.value = true
    
    // 构建通知数据
    const notificationData = {
      title: form.title,
      content: form.content,
      type: form.type,
      level: form.level,
      relatedType: form.relatedType || null,
      relatedId: form.relatedId || null
    }
    
    if (form.receiverIds.length === 1) {
      // 单个接收人，使用普通发送接口
      await sendNotification({
        ...notificationData,
        receiverId: form.receiverIds[0]
      })
    } else {
      // 多个接收人，使用批量发送接口
      await batchSendNotification(form.receiverIds, notificationData)
    }
    
    ElMessage.success(`通知发送成功，共发送给 ${form.receiverIds.length} 个用户`)
    handleClose()
    emit('success')
    
  } catch (error) {
    ElMessage.error('发送通知失败: ' + (error.message || '未知错误'))
  } finally {
    sendingBatch.value = false
  }
}

const handleClose = () => {
  visible.value = false
}

// 暴露方法给父组件
defineExpose({
  resetForm,
  loadPresetData
})
</script>

<style scoped>
/* 容器和卡片样式 */
.form-container {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin: -10px;
}

.form-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.form-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border-color: #e1e8f0;
}

.card-header {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #409eff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 8px;
}

/* 用户选项样式 */
.user-option {
  display: flex;
  flex-direction: column;
  line-height: 1.4;
}

.user-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.user-info {
  font-size: 12px;
  color: #909399;
}

/* 关联业务样式 */
.related-business-item {
  margin-bottom: 0;
}

.related-business-container {
  display: flex;
  align-items: center;
  width: 100%;
}

.related-option {
  display: flex;
  flex-direction: column;
  line-height: 1.4;
}

.option-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.option-info {
  font-size: 12px;
  color: #909399;
}

/* 表单布局优化 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #303133;
}

:deep(.el-form-item) {
  margin-bottom: 22px;
}

:deep(.el-textarea__inner) {
  resize: vertical;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: border-color 0.2s;
}

:deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

:deep(.el-input__inner) {
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: border-color 0.2s;
}

:deep(.el-input__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-select .el-input__inner) {
  border-radius: 8px;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-input-number .el-input__inner) {
  text-align: left;
  border-radius: 8px;
}

/* 对话框样式优化 */
:deep(.el-dialog) {
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
}

:deep(.el-dialog__header) {
  padding: 24px 24px 16px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px 24px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .related-business-container {
    flex-direction: column;
    gap: 12px;
  }
  
  .related-business-container .el-select:first-child {
    width: 100%;
    margin-left: 0 !important;
  }
  
  :deep(.el-dialog) {
    width: 95%;
    margin: 0 auto;
  }
  
  :deep(.el-dialog__body) {
    padding: 16px;
  }
  
  :deep(.el-dialog__header) {
    padding: 16px 16px 12px;
  }
  
  :deep(.el-dialog__footer) {
    padding: 12px 16px 16px;
  }
}
</style>