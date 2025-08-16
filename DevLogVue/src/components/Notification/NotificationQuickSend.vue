<template>
  <div class="notification-quick-send">
    <!-- 快速发送按钮 -->
    <el-dropdown @command="handleQuickSend" trigger="click">
      <el-button type="primary" :icon="Bell">
        发送通知
        <el-icon class="el-icon--right"><ArrowDown /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="custom">
            <el-icon><Edit /></el-icon>
            自定义通知
          </el-dropdown-item>
          <el-dropdown-item divided command="task_assign">
            <el-icon><Calendar /></el-icon>
            任务分配通知
          </el-dropdown-item>
          <el-dropdown-item command="worklog_review">
            <el-icon><Document /></el-icon>
            日志审核通知
          </el-dropdown-item>
          <el-dropdown-item command="task_deadline">
            <el-icon><Warning /></el-icon>
            任务截止提醒
          </el-dropdown-item>
          <el-dropdown-item command="project_update">
            <el-icon><Folder /></el-icon>
            项目更新通知
          </el-dropdown-item>
          <el-dropdown-item divided command="system_broadcast">
            <el-icon><ChatDotRound /></el-icon>
            系统广播
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 发送通知表单 -->
    <NotificationSendForm
      v-model="showSendForm"
      :preset-type="presetType"
      :preset-receivers="presetReceivers"
      :preset-related="presetRelated"
      @success="handleSendSuccess"
    />

    <!-- 系统广播对话框 -->
    <el-dialog v-model="showBroadcastDialog" title="系统广播" width="500px">
      <el-form ref="broadcastFormRef" :model="broadcastForm" :rules="broadcastRules" label-width="80px">
        <el-form-item label="广播标题" prop="title">
          <el-input v-model="broadcastForm.title" placeholder="请输入广播标题" />
        </el-form-item>
        <el-form-item label="广播内容" prop="content">
          <el-input
            v-model="broadcastForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入广播内容"
          />
        </el-form-item>
        <el-form-item label="广播级别" prop="level">
          <el-select v-model="broadcastForm.level" style="width: 100%">
            <el-option value="普通" label="普通" />
            <el-option value="重要" label="重要" />
            <el-option value="紧急" label="紧急" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBroadcastDialog = false">取消</el-button>
        <el-button type="primary" :loading="broadcasting" @click="handleBroadcast">
          {{ broadcasting ? '发送中...' : '发送广播' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Bell, 
  ArrowDown, 
  Edit, 
  Calendar, 
  Document, 
  Warning, 
  Folder, 
  ChatDotRound 
} from '@element-plus/icons-vue'
import NotificationSendForm from './NotificationSendForm.vue'
import { batchSendNotification } from '@/api/notification'
import { getUserList } from '@/api/user'

// 响应式数据
const showSendForm = ref(false)
const showBroadcastDialog = ref(false)
const broadcasting = ref(false)
const broadcastFormRef = ref(null)

const presetType = ref('')
const presetReceivers = ref([])
const presetRelated = ref({})

const broadcastForm = reactive({
  title: '',
  content: '',
  level: '普通'
})

const broadcastRules = {
  title: [
    { required: true, message: '请输入广播标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入广播内容', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择广播级别', trigger: 'change' }
  ]
}

// 方法
const handleQuickSend = (command) => {
  // 重置预设数据
  presetType.value = ''
  presetReceivers.value = []
  presetRelated.value = {}

  switch (command) {
    case 'custom':
      showSendForm.value = true
      break
      
    case 'task_assign':
      presetType.value = 'TASK_ASSIGN'
      showSendForm.value = true
      break
      
    case 'worklog_review':
      presetType.value = 'WORKLOG_REVIEW'
      showSendForm.value = true
      break
      
    case 'task_deadline':
      presetType.value = 'TASK_DEADLINE'
      showSendForm.value = true
      break
      
    case 'project_update':
      presetType.value = 'PROJECT_UPDATE'
      showSendForm.value = true
      break
      
    case 'system_broadcast':
      showBroadcastDialog.value = true
      resetBroadcastForm()
      break
      
    default:
      break
  }
}

const resetBroadcastForm = () => {
  Object.assign(broadcastForm, {
    title: '',
    content: '',
    level: '普通'
  })
  
  if (broadcastFormRef.value) {
    broadcastFormRef.value.clearValidate()
  }
}

const handleBroadcast = async () => {
  if (!broadcastFormRef.value) return
  
  try {
    const valid = await broadcastFormRef.value.validate()
    if (!valid) return
    
    // 确认发送广播
    await ElMessageBox.confirm(
      '系统广播将发送给所有用户，确认发送吗？',
      '确认广播',
      {
        confirmButtonText: '确认发送',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    broadcasting.value = true
    
    // 获取所有用户ID
    const response = await getUserList({
      pageNum: 1,
      pageSize: 1000 // 获取大量用户，实际应该分页处理
    })
    
    if (!response.data || !response.data.records || response.data.records.length === 0) {
      ElMessage.warning('没有找到可发送的用户')
      return
    }
    
    const allUserIds = response.data.records.map(user => user.id)
    
    console.log('准备发送广播，用户ID列表：', allUserIds)
    console.log('广播内容：', {
      title: broadcastForm.title,
      content: broadcastForm.content,
      type: 'SYSTEM_MESSAGE',
      level: broadcastForm.level
    })
    
    // 发送广播通知
    await batchSendNotification(allUserIds, {
      title: broadcastForm.title,
      content: broadcastForm.content,
      type: 'SYSTEM_MESSAGE',
      level: broadcastForm.level
    })
    
    ElMessage.success(`系统广播发送成功，共发送给 ${allUserIds.length} 个用户`)
    showBroadcastDialog.value = false
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发送广播失败: ' + (error.message || '未知错误'))
    }
  } finally {
    broadcasting.value = false
  }
}

const handleSendSuccess = () => {
  // 发送成功后的处理
  ElMessage.success('通知发送成功')
}

// 提供给父组件调用的方法
const openSendForm = (options = {}) => {
  presetType.value = options.type || ''
  presetReceivers.value = options.receivers || []
  presetRelated.value = options.related || {}
  showSendForm.value = true
}

// 暴露方法给父组件
defineExpose({
  openSendForm
})
</script>

<style scoped>
.notification-quick-send {
  display: inline-block;
}

:deep(.el-dropdown) {
  vertical-align: top;
}
</style>