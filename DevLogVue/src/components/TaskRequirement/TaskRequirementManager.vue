<template>
  <div class="task-requirement-manager">
    <div class="requirement-header">
      <h3 class="requirement-title">
        <SkillIcons type="requirement" :size="20" />
        技能需求管理
      </h3>
      <button class="add-requirement-btn" @click="showAddRequirementDialog">
        <SkillIcons type="tag" :size="16" />
        <span>添加需求</span>
      </button>
    </div>

    <!-- 技能需求列表 -->
    <div v-loading="loading" class="requirement-list">
      <div
        v-for="requirement in taskRequirements"
        :key="requirement.tagId"
        class="requirement-item"
      >
        <div class="requirement-basic">
          <div class="requirement-name-group">
            <span
              class="requirement-color"
              :style="{ backgroundColor: requirement.color }"
            ></span>
            <span class="requirement-name">{{ requirement.tagName }}</span>
            <span v-if="requirement.isRequired === 1" class="required-badge">必需</span>
            <span v-else class="optional-badge">可选</span>
          </div>
          <div class="requirement-category">{{ requirement.category }}</div>
        </div>

        <div class="requirement-details">
          <div class="requirement-level">
            <SkillIcons type="level" :size="14" />
            <span>{{ requirement.requiredLevel }}</span>
          </div>
          <div class="requirement-priority">
            <div class="priority-indicator" :class="getPriorityClass(requirement.priority)">
              <span class="priority-dot"></span>
            </div>
            <span class="priority-text">{{ getPriorityText(requirement.priority) }}</span>
          </div>
          <div class="requirement-experience" v-if="requirement.minExperience">
            <SkillIcons type="experience" :size="14" />
            <span>{{ requirement.minExperience }} 年+</span>
          </div>
        </div>

        <div class="requirement-actions">
          <button class="action-btn edit" @click="editRequirement(requirement)">
            <UserIcons type="edit" :size="14" />
          </button>
          <button class="action-btn delete" @click="deleteRequirement(requirement)">
            <UserIcons type="delete" :size="14" />
          </button>
        </div>
      </div>

      <div v-if="taskRequirements.length === 0" class="empty-state">
        <SkillIcons type="requirement" :size="48" />
        <p>暂未设置技能需求</p>
        <button class="add-first-requirement" @click="showAddRequirementDialog">
          添加第一个技能需求
        </button>
      </div>
    </div>

    <!-- 添加/编辑技能需求对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加技能需求' : '编辑技能需求'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="技能标签" prop="tagId">
          <el-select
            v-model="form.tagId"
            placeholder="请选择技能标签"
            filterable
            style="width: 100%"
            @change="handleTagChange"
          >
            <el-option-group
              v-for="category in groupedTags"
              :key="category.category"
              :label="category.category"
            >
              <el-option
                v-for="tag in category.tags"
                :key="tag.tagId"
                :label="tag.tagName"
                :value="tag.tagId"
                :disabled="isTagExists(tag.tagId)"
              >
                <div class="tag-option">
                  <span
                    class="tag-color"
                    :style="{ backgroundColor: tag.color }"
                  ></span>
                  <span>{{ tag.tagName }}</span>
                  <span v-if="isTagExists(tag.tagId)" class="tag-exists">已添加</span>
                </div>
              </el-option>
            </el-option-group>
          </el-select>
        </el-form-item>

        <el-form-item label="必需程度" prop="isRequired">
          <el-radio-group v-model="form.isRequired">
            <el-radio :label="1">必需技能</el-radio>
            <el-radio :label="0">可选技能</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="要求等级" prop="requiredLevel">
          <el-radio-group v-model="form.requiredLevel">
            <el-radio label="初级">初级</el-radio>
            <el-radio label="中级">中级</el-radio>
            <el-radio label="高级">高级</el-radio>
            <el-radio label="专家">专家</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="最少经验" prop="minExperience">
          <el-input-number
            v-model="form.minExperience"
            :min="0"
            :max="20"
            style="width: 100%"
            placeholder="年"
          />
        </el-form-item>

        <el-form-item label="优先级" prop="priority">
          <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
            <el-option label="低" :value="1">
              <div class="priority-option">
                <span class="priority-dot low"></span>
                <span>低优先级</span>
              </div>
            </el-option>
            <el-option label="中" :value="2">
              <div class="priority-option">
                <span class="priority-dot medium"></span>
                <span>中优先级</span>
              </div>
            </el-option>
            <el-option label="高" :value="3">
              <div class="priority-option">
                <span class="priority-dot high"></span>
                <span>高优先级</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { SkillIcons, UserIcons } from '@/components/SvgIcons'
import { getTaskRequirements, updateTaskRequirements, removeTaskRequirement } from '@/api/taskRequirement'
import { getAllEnabledSkillTags } from '@/api/skillTag'

const props = defineProps({
  taskId: {
    type: [String, Number],
    required: true
  }
})

const emit = defineEmits(['requirement-updated'])

// 数据
const loading = ref(false)
const taskRequirements = ref([])
const availableTags = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const form = ref({
  tagId: null,
  isRequired: 1,
  requiredLevel: '中级',
  minExperience: 0,
  priority: 2,
  remark: ''
})

// 表单验证规则
const rules = {
  tagId: [
    { required: true, message: '请选择技能标签', trigger: 'change' }
  ],
  isRequired: [
    { required: true, message: '请选择必需程度', trigger: 'change' }
  ],
  requiredLevel: [
    { required: true, message: '请选择要求等级', trigger: 'change' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ]
}

// 按分类分组的标签
const groupedTags = computed(() => {
  const groups = {}
  availableTags.value.forEach(tag => {
    if (!groups[tag.category]) {
      groups[tag.category] = {
        category: tag.category,
        tags: []
      }
    }
    groups[tag.category].tags.push(tag)
  })
  return Object.values(groups)
})

// 获取任务技能需求列表
const getTaskRequirementList = async () => {
  loading.value = true
  try {
    const res = await getTaskRequirements(props.taskId)
    if (res.code === 200) {
      taskRequirements.value = res.data
    } else {
      ElMessage.error(res.message || '获取任务技能需求失败')
    }
  } catch (error) {
    console.error('获取任务技能需求失败:', error)
    ElMessage.error('获取任务技能需求失败')
  } finally {
    loading.value = false
  }
}

// 获取可用标签列表
const getAvailableTags = async () => {
  try {
    const res = await getAllEnabledSkillTags()
    if (res.code === 200) {
      availableTags.value = res.data
    } else {
      ElMessage.error(res.message || '获取标签列表失败')
    }
  } catch (error) {
    console.error('获取标签列表失败:', error)
    ElMessage.error('获取标签列表失败')
  }
}

// 检查标签是否已存在
const isTagExists = (tagId) => {
  return taskRequirements.value.some(req => req.tagId === tagId && req.tagId !== form.value.originalTagId)
}

// 获取优先级样式类
const getPriorityClass = (priority) => {
  switch (priority) {
    case 1: return 'low'
    case 2: return 'medium' 
    case 3: return 'high'
    default: return 'medium'
  }
}

// 获取优先级文本
const getPriorityText = (priority) => {
  switch (priority) {
    case 1: return '低'
    case 2: return '中'
    case 3: return '高'
    default: return '中'
  }
}

// 显示添加技能需求对话框
const showAddRequirementDialog = () => {
  dialogType.value = 'add'
  form.value = {
    tagId: null,
    isRequired: 1,
    requiredLevel: '中级',
    minExperience: 0,
    priority: 2,
    remark: ''
  }
  dialogVisible.value = true
}

// 编辑技能需求
const editRequirement = (requirement) => {
  dialogType.value = 'edit'
  form.value = {
    ...requirement,
    originalTagId: requirement.tagId
  }
  dialogVisible.value = true
}

// 删除技能需求
const deleteRequirement = (requirement) => {
  ElMessageBox.confirm(
    `确认删除技能需求"${requirement.tagName}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await removeTaskRequirement(props.taskId, requirement.tagId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getTaskRequirementList()
        emit('requirement-updated')
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      console.error('删除技能需求失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 处理标签选择
const handleTagChange = (tagId) => {
  const tag = availableTags.value.find(t => t.tagId === tagId)
  if (tag) {
    // 可以根据标签设置一些默认值
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 构建需求数据
        const requirementData = {
          ...form.value
        }
        
        // 获取当前所有需求，然后更新
        const currentRequirements = [...taskRequirements.value]
        
        if (dialogType.value === 'add') {
          currentRequirements.push(requirementData)
        } else {
          const index = currentRequirements.findIndex(r => r.tagId === form.value.originalTagId)
          if (index > -1) {
            currentRequirements[index] = { ...currentRequirements[index], ...requirementData }
          }
        }

        const updateData = {
          taskId: props.taskId,
          requirements: currentRequirements.map(req => ({
            tagId: req.tagId,
            isRequired: req.isRequired || 0,
            requiredLevel: req.requiredLevel || '中级',
            minExperience: req.minExperience || 0,
            priority: req.priority || 2,
            remark: req.remark || ''
          }))
        }

        const res = await updateTaskRequirements(updateData)
        if (res.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
          dialogVisible.value = false
          getTaskRequirementList()
          emit('requirement-updated')
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

// 初始化
onMounted(() => {
  getTaskRequirementList()
  getAvailableTags()
})

// 暴露刷新方法
defineExpose({
  refresh: getTaskRequirementList
})
</script>

<style lang="scss" scoped>
.task-requirement-manager {
  .requirement-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .requirement-title {
      display: flex;
      align-items: center;
      gap: 8px;
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #0d47a1;
    }

    .add-requirement-btn {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 10px 18px;
      background: #1976d2;
      color: white;
      border: none;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      box-shadow: 0 2px 6px rgba(25, 118, 210, 0.2);

      &:hover {
        background: #1565c0;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
      }
    }
  }

  .requirement-list {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .requirement-item {
      display: flex;
      align-items: center;
      padding: 16px;
      background: rgba(255, 255, 255, 0.95);
      border: 1px solid rgba(25, 118, 210, 0.25);
      border-radius: 10px;
      transition: all 0.3s ease;
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);

      &:hover {
        border-color: #1976d2;
        box-shadow: 0 4px 12px rgba(25, 118, 210, 0.15);
        transform: translateY(-1px);
      }

      .requirement-basic {
        flex: 1;
        min-width: 0;

        .requirement-name-group {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 4px;

          .requirement-color {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            flex-shrink: 0;
          }

          .requirement-name {
            font-weight: 600;
            color: #0d47a1;
            font-size: 16px;
          }

          .required-badge {
            padding: 2px 6px;
            background: #ef4444;
            color: white;
            border-radius: 10px;
            font-size: 10px;
            font-weight: 500;
          }

          .optional-badge {
            padding: 2px 6px;
            background: #94a3b8;
            color: white;
            border-radius: 10px;
            font-size: 10px;
            font-weight: 500;
          }
        }

        .requirement-category {
          color: #1565c0;
          font-size: 13px;
          margin-left: 20px;
        }
      }

      .requirement-details {
        display: flex;
        align-items: center;
        gap: 16px;
        margin-right: 16px;

        .requirement-level, .requirement-experience {
          display: flex;
          align-items: center;
          gap: 4px;
          color: #1565c0;
          font-size: 13px;
        }

        .requirement-priority {
          display: flex;
          align-items: center;
          gap: 6px;

          .priority-indicator {
            display: flex;
            align-items: center;
            
            .priority-dot {
              width: 8px;
              height: 8px;
              border-radius: 50%;
            }

            &.low .priority-dot {
              background: #94a3b8;
            }

            &.medium .priority-dot {
              background: #f59e0b;
            }

            &.high .priority-dot {
              background: #ef4444;
            }
          }

          .priority-text {
            color: #1565c0;
            font-size: 12px;
            font-weight: 500;
          }
        }
      }

      .requirement-actions {
        display: flex;
        gap: 8px;

        .action-btn {
          width: 28px;
          height: 28px;
          display: flex;
          align-items: center;
          justify-content: center;
          border: 1px solid rgba(148, 163, 184, 0.3);
          border-radius: 4px;
          background: rgba(248, 250, 252, 0.8);
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

          &.delete:hover {
            border-color: #ef4444;
            color: #ef4444;
            background: rgba(239, 68, 68, 0.1);
          }
        }
      }
    }
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    color: #94a3b8;

    p {
      margin: 16px 0;
      font-size: 16px;
    }

    .add-first-requirement {
      padding: 12px 24px;
      background: #1976d2;
      color: white;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      box-shadow: 0 2px 6px rgba(25, 118, 210, 0.2);

      &:hover {
        background: #1565c0;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
      }
    }
  }
}

// 标签选项样式
.tag-option {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;

  .tag-color {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    flex-shrink: 0;
  }

  .tag-exists {
    margin-left: auto;
    color: #94a3b8;
    font-size: 12px;
  }
}

// 优先级选项样式
.priority-option {
  display: flex;
  align-items: center;
  gap: 8px;

  .priority-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;

    &.low {
      background: #94a3b8;
    }

    &.medium {
      background: #f59e0b;
    }

    &.high {
      background: #ef4444;
    }
  }
}

// 对话框样式
:deep(.el-dialog) {
  background: rgba(248, 250, 252, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(148, 163, 184, 0.2);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

:deep(.el-form-item__label) {
  color: #475569;
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  background: rgba(248, 250, 252, 0.8);
  border: 1px solid rgba(148, 163, 184, 0.3);
  border-radius: 6px;

  &:hover {
    border-color: #94a3b8;
  }

  &.is-focus {
    border-color: #0ea5e9;
    box-shadow: 0 0 0 2px rgba(14, 165, 233, 0.1);
  }
}

:deep(.el-radio-group) {
  .el-radio {
    margin-right: 20px;

    .el-radio__label {
      color: #64748b;
    }

    &.is-checked .el-radio__label {
      color: #475569;
    }
  }
}

:deep(.el-switch.is-checked .el-switch__core) {
  background-color: #0ea5e9;
}
</style>