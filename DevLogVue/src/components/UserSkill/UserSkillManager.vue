<template>
  <div class="user-skill-manager">
    <div class="skill-header">
      <h3 class="skill-title">
        <SkillIcons type="skill" :size="20" />
        技能管理
      </h3>
      <button class="add-skill-btn" @click="showAddSkillDialog">
        <SkillIcons type="tag" :size="16" />
        <span>添加技能</span>
      </button>
    </div>

    <!-- 技能列表 -->
    <div v-loading="loading" class="skill-list">
      <div
        v-for="skill in userSkills"
        :key="skill.tagId"
        class="skill-item"
      >
        <div class="skill-basic">
          <div class="skill-name-group">
            <span
              class="skill-color"
              :style="{ backgroundColor: skill.color }"
            ></span>
            <span class="skill-name">{{ skill.tagName }}</span>
            <span v-if="skill.isPrimary === 1" class="primary-badge">主要</span>
          </div>
          <div class="skill-category">{{ skill.category }}</div>
        </div>

        <div class="skill-details">
          <div class="skill-level">
            <SkillIcons type="level" :size="14" />
            <span>{{ skill.proficiencyLevel }}</span>
          </div>
          <div class="skill-rating">
            <div class="rating-stars">
              <span
                v-for="star in 10"
                :key="star"
                class="star"
                :class="{ active: star <= skill.selfRating }"
              >★</span>
            </div>
            <span class="rating-text">{{ skill.selfRating }}/10</span>
          </div>
          <div class="skill-experience">
            <SkillIcons type="experience" :size="14" />
            <span>{{ skill.yearsOfExperience || 0 }} 年</span>
          </div>
        </div>

        <div class="skill-actions">
          <button class="action-btn edit" @click="editSkill(skill)">
            <UserIcons type="edit" :size="14" />
          </button>
          <button class="action-btn delete" @click="deleteSkill(skill)">
            <UserIcons type="delete" :size="14" />
          </button>
        </div>
      </div>

      <div v-if="userSkills.length === 0" class="empty-state">
        <SkillIcons type="skill" :size="48" />
        <p>暂未添加技能信息</p>
        <button class="add-first-skill" @click="showAddSkillDialog">
          添加第一个技能
        </button>
      </div>
    </div>

    <!-- 添加/编辑技能对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加技能' : '编辑技能'"
      width="80%"
      top="5vh"
      destroy-on-close
      class="skill-dialog"
      :close-on-click-modal="false"
    >
      <div class="skill-form-container">
        <!-- 步骤1: 选择技能 -->
        <div class="form-step" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
          <div class="step-header">
            <div class="step-number">1</div>
            <div class="step-title">选择技能</div>
          </div>
          <div v-if="currentStep === 1" class="step-content">
            <div class="skill-search">
              <el-input
                v-model="skillSearchText"
                placeholder="搜索技能标签..."
                prefix-icon="Search"
                clearable
                @input="filterSkillTags"
              />
            </div>
            <div class="skill-categories">
              <div
                v-for="category in filteredCategories"
                :key="category.category"
                class="category-group"
              >
                <div class="category-title">{{ category.category }}</div>
                <div class="skill-tags-grid">
                  <div
                    v-for="tag in category.tags"
                    :key="tag.tagId"
                    class="skill-tag-card"
                    :class="{ 
                      selected: form.tagId === tag.tagId,
                      disabled: isTagExists(tag.tagId) && dialogType === 'add'
                    }"
                    @click="selectSkillTag(tag)"
                  >
                    <div class="tag-indicator" :style="{ backgroundColor: tag.color }"></div>
                    <div class="tag-content">
                      <div class="tag-name">{{ tag.tagName }}</div>
                      <div class="tag-desc">{{ tag.description || '暂无描述' }}</div>
                    </div>
                    <div v-if="isTagExists(tag.tagId) && dialogType === 'add'" class="tag-status">已添加</div>
                  </div>
                </div>
              </div>
            </div>
            <div class="step-actions">
              <el-button @click="dialogVisible = false">取消</el-button>
              <el-button 
                type="primary" 
                :disabled="!form.tagId"
                @click="nextStep"
              >
                下一步
              </el-button>
            </div>
          </div>
        </div>

        <!-- 步骤2: 技能详情 -->
        <div class="form-step" :class="{ active: currentStep >= 2 }">
          <div class="step-header">
            <div class="step-number">2</div>
            <div class="step-title">技能详情</div>
          </div>
          <div v-if="currentStep === 2" class="step-content">
            <div class="selected-skill-info">
              <div class="selected-tag">
                <div class="tag-indicator" :style="{ backgroundColor: selectedTag?.color }"></div>
                <div class="tag-name">{{ selectedTag?.tagName }}</div>
                <div class="tag-category">{{ selectedTag?.category }}</div>
              </div>
            </div>

            <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-width="90px"
              class="skill-detail-form"
            >
              <div class="form-row">
                <el-form-item label="熟练程度" prop="proficiencyLevel" class="proficiency-item">
                  <div class="proficiency-selector">
                    <div
                      v-for="level in proficiencyLevels"
                      :key="level.value"
                      class="proficiency-option"
                      :class="{ selected: form.proficiencyLevel === level.value }"
                      @click="form.proficiencyLevel = level.value"
                    >
                      <div class="level-icon" :class="level.icon"></div>
                      <div class="level-text">
                        <div class="level-name">{{ level.label }}</div>
                        <div class="level-desc">{{ level.desc }}</div>
                      </div>
                    </div>
                  </div>
                </el-form-item>
              </div>

              <div class="form-row">
                <el-form-item label="使用年限" prop="yearsOfExperience" class="half-item">
                  <div class="experience-input">
                    <el-slider
                      v-model="form.yearsOfExperience"
                      :min="0"
                      :max="20"
                      :step="0.5"
                      :format-tooltip="formatExperienceTooltip"
                      class="exp-slider"
                    />
                    <div class="exp-display">{{ form.yearsOfExperience }} 年</div>
                  </div>
                </el-form-item>
                
                <el-form-item label="自评分数" prop="selfRating" class="half-item">
                  <div class="rating-input">
                    <el-rate
                      v-model="form.selfRating"
                      :max="5"
                      show-score
                      text-color="#ff9900"
                      score-template="{value}/5"
                    />
                  </div>
                </el-form-item>
              </div>

              <div class="form-row">
                <el-form-item label="最后使用" prop="lastUsed" class="half-item">
                  <el-date-picker
                    v-model="form.lastUsed"
                    type="date"
                    placeholder="选择日期"
                    style="width: 100%"
                    :clearable="true"
                  />
                </el-form-item>

                <el-form-item label="主要技能" prop="isPrimary" class="half-item">
                  <div class="primary-switch">
                    <el-switch
                      v-model="form.isPrimary"
                      :active-value="1"
                      :inactive-value="0"
                      active-text="是"
                      inactive-text="否"
                    />
                    <div class="switch-hint">设为核心技能</div>
                  </div>
                </el-form-item>
              </div>

              <el-form-item label="备注" prop="remark">
                <el-input
                  v-model="form.remark"
                  type="textarea"
                  :rows="2"
                  placeholder="描述技能应用场景、项目经验等..."
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>
            </el-form>

            <div class="step-actions">
              <el-button @click="prevStep">上一步</el-button>
              <el-button @click="dialogVisible = false">取消</el-button>
              <el-button type="primary" @click="handleSubmit">
                {{ dialogType === 'add' ? '添加技能' : '保存修改' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { SkillIcons, UserIcons } from '@/components/SvgIcons'
import { getUserSkills, updateUserSkills, removeUserSkill } from '@/api/userSkill'
import { getAllEnabledSkillTags } from '@/api/skillTag'

const props = defineProps({
  userId: {
    type: [String, Number],
    required: true
  }
})

const emit = defineEmits(['skill-updated'])

// 数据
const loading = ref(false)
const userSkills = ref([])
const availableTags = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)
const form = ref({
  tagId: null,
  proficiencyLevel: '中级',
  yearsOfExperience: 0,
  lastUsed: null,
  isPrimary: 0,
  selfRating: 5,
  remark: ''
})

// 步骤相关
const currentStep = ref(1)
const skillSearchText = ref('')
const selectedTag = ref(null)

// 熟练度选项
const proficiencyLevels = ref([
  {
    value: '入门',
    label: '入门',
    desc: '刚开始接触，了解基本概念',
    icon: 'beginner'
  },
  {
    value: '初级',
    label: '初级',
    desc: '掌握基础知识，能完成简单任务',
    icon: 'basic'
  },
  {
    value: '中级',
    label: '中级',
    desc: '熟练运用，能独立解决常见问题',
    icon: 'intermediate'
  },
  {
    value: '高级',
    label: '高级',
    desc: '精通技能，能处理复杂场景',
    icon: 'advanced'
  },
  {
    value: '专家',
    label: '专家',
    desc: '专业级别，能指导他人和优化方案',
    icon: 'expert'
  }
])

// 表单验证规则
const rules = {
  tagId: [
    { required: true, message: '请选择技能标签', trigger: 'change' }
  ],
  proficiencyLevel: [
    { required: true, message: '请选择熟练程度', trigger: 'change' }
  ],
  yearsOfExperience: [
    { type: 'number', message: '使用年限必须是数字', trigger: 'blur' }
  ],
  selfRating: [
    { type: 'number', min: 1, max: 10, message: '自评分数必须在1-10之间', trigger: 'blur' }
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

// 过滤后的分类标签
const filteredCategories = computed(() => {
  if (!skillSearchText.value.trim()) {
    return groupedTags.value
  }
  
  const searchText = skillSearchText.value.toLowerCase()
  return groupedTags.value.map(category => ({
    ...category,
    tags: category.tags.filter(tag => 
      tag.tagName.toLowerCase().includes(searchText) ||
      tag.description?.toLowerCase().includes(searchText) ||
      category.category.toLowerCase().includes(searchText)
    )
  })).filter(category => category.tags.length > 0)
})

// 获取用户技能列表
const getUserSkillList = async () => {
  loading.value = true
  try {
    const res = await getUserSkills(props.userId)
    if (res.code === 200) {
      userSkills.value = res.data
    } else {
      ElMessage.error(res.message || '获取用户技能失败')
    }
  } catch (error) {
    console.error('获取用户技能失败:', error)
    ElMessage.error('获取用户技能失败')
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
  return userSkills.value.some(skill => skill.tagId === tagId && skill.tagId !== form.value.originalTagId)
}

// 显示添加技能对话框
const showAddSkillDialog = () => {
  dialogType.value = 'add'
  currentStep.value = 1
  skillSearchText.value = ''
  selectedTag.value = null
  form.value = {
    tagId: null,
    proficiencyLevel: '中级',
    yearsOfExperience: 0,
    lastUsed: null,
    isPrimary: 0,
    selfRating: 5,
    remark: ''
  }
  dialogVisible.value = true
}

// 编辑技能
const editSkill = (skill) => {
  dialogType.value = 'edit'
  currentStep.value = 2 // 编辑时直接进入第二步
  skillSearchText.value = ''
  
  // 设置选中的标签
  selectedTag.value = availableTags.value.find(tag => tag.tagId === skill.tagId)
  
  form.value = {
    ...skill,
    originalTagId: skill.tagId,
    lastUsed: skill.lastUsed ? new Date(skill.lastUsed) : null
  }
  dialogVisible.value = true
}

// 删除技能
const deleteSkill = (skill) => {
  ElMessageBox.confirm(
    `确认删除技能"${skill.tagName}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await removeUserSkill(props.userId, skill.tagId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getUserSkillList()
        emit('skill-updated')
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      console.error('删除技能失败:', error)
      ElMessage.error('删除失败')
    }
  })
}

// 步骤控制
const nextStep = () => {
  if (currentStep.value < 2) {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

// 选择技能标签
const selectSkillTag = (tag) => {
  if (isTagExists(tag.tagId) && dialogType.value === 'add') {
    return // 如果是添加模式且标签已存在，不允许选择
  }
  
  form.value.tagId = tag.tagId
  selectedTag.value = tag
}

// 过滤技能标签
const filterSkillTags = () => {
  // 搜索过滤由computed属性处理
}

// 格式化经验提示
const formatExperienceTooltip = (value) => {
  if (value === 0) return '无经验'
  if (value < 1) return `${value * 12}个月`
  return `${value}年`
}


// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 构建技能数据
        const skillData = {
          ...form.value,
          lastUsed: form.value.lastUsed ? 
            new Date(form.value.lastUsed).toISOString().split('T')[0] : null
        }
        
        // 获取当前所有技能，然后更新
        const currentSkills = [...userSkills.value]
        
        if (dialogType.value === 'add') {
          currentSkills.push(skillData)
        } else {
          const index = currentSkills.findIndex(s => s.tagId === form.value.originalTagId)
          if (index > -1) {
            currentSkills[index] = { ...currentSkills[index], ...skillData }
          }
        }

        const updateData = {
          userId: props.userId,
          skills: currentSkills.map(skill => ({
            tagId: skill.tagId,
            proficiencyLevel: skill.proficiencyLevel,
            yearsOfExperience: skill.yearsOfExperience || 0,
            lastUsed: skill.lastUsed,
            isPrimary: skill.isPrimary || 0,
            selfRating: skill.selfRating || 5,
            remark: skill.remark || ''
          }))
        }

        const res = await updateUserSkills(updateData)
        if (res.code === 200) {
          ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
          dialogVisible.value = false
          getUserSkillList()
          emit('skill-updated')
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
  getUserSkillList()
  getAvailableTags()
})

// 暴露刷新方法
defineExpose({
  refresh: getUserSkillList
})
</script>

<style lang="scss" scoped>
.user-skill-manager {
  .skill-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .skill-title {
      display: flex;
      align-items: center;
      gap: 8px;
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #0d47a1;
    }

    .add-skill-btn {
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

  .skill-list {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .skill-item {
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

      .skill-basic {
        flex: 1;
        min-width: 0;

        .skill-name-group {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 4px;

          .skill-color {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            flex-shrink: 0;
          }

          .skill-name {
            font-weight: 600;
            color: #0d47a1;
            font-size: 16px;
          }

          .primary-badge {
            padding: 2px 6px;
            background: #f59e0b;
            color: white;
            border-radius: 10px;
            font-size: 10px;
            font-weight: 500;
          }
        }

        .skill-category {
          color: #1565c0;
          font-size: 13px;
          margin-left: 20px;
        }
      }

      .skill-details {
        display: flex;
        align-items: center;
        gap: 16px;
        margin-right: 16px;

        .skill-level, .skill-experience {
          display: flex;
          align-items: center;
          gap: 4px;
          color: #64748b;
          font-size: 13px;
        }

        .skill-rating {
          display: flex;
          align-items: center;
          gap: 8px;

          .rating-stars {
            display: flex;
            gap: 1px;

            .star {
              color: #e2e8f0;
              font-size: 12px;
              transition: color 0.2s ease;

              &.active {
                color: #f59e0b;
              }
            }
          }

          .rating-text {
            color: #64748b;
            font-size: 12px;
            font-weight: 500;
          }
        }
      }

      .skill-actions {
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

    .add-first-skill {
      padding: 10px 20px;
      background: #0ea5e9;
      color: white;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        background: #0284c7;
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

// 对话框样式
:deep(.skill-dialog) {
  .el-dialog {
    background: rgba(248, 250, 252, 0.95);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(148, 163, 184, 0.2);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    max-width: 1200px;
    min-height: 70vh;
    
    @media (max-width: 768px) {
      width: 95% !important;
      margin: 0 auto;
      min-height: 80vh;
    }
  }
  
  .el-dialog__header {
    border-bottom: 1px solid rgba(148, 163, 184, 0.2);
    padding: 20px 24px 16px;
    
    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: #0d47a1;
    }
  }
  
  .el-dialog__body {
    padding: 24px;
    min-height: 500px;
    
    @media (max-width: 768px) {
      padding: 16px;
    }
  }
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

:deep(.el-slider__runway) {
  background: #f1f5f9;
}

:deep(.el-slider__bar) {
  background: #0ea5e9;
}

:deep(.el-slider__button) {
  border-color: #0ea5e9;
}

:deep(.el-switch.is-checked .el-switch__core) {
  background-color: #0ea5e9;
}

// 步骤表单样式
.skill-form-container {
  .form-step {
    margin-bottom: 24px;
    
    .step-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;
      padding-bottom: 8px;
      border-bottom: 1px solid rgba(148, 163, 184, 0.2);
      
      .step-number {
        width: 28px;
        height: 28px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        background: #e2e8f0;
        color: #64748b;
        font-size: 14px;
        font-weight: 600;
        transition: all 0.3s ease;
      }
      
      .step-title {
        font-size: 16px;
        font-weight: 600;
        color: #475569;
      }
    }
    
    &.active .step-header .step-number {
      background: #1976d2;
      color: white;
    }
    
    &.completed .step-header .step-number {
      background: #10b981;
      color: white;
    }
    
    .step-content {
      padding-left: 40px;
    }
  }
  
  .skill-search {
    margin-bottom: 20px;
  }
  
  .skill-categories {
    max-height: 500px;
    overflow-y: auto;
    
    .category-group {
      margin-bottom: 24px;
      
      .category-title {
        font-size: 14px;
        font-weight: 600;
        color: #0d47a1;
        margin-bottom: 12px;
        padding-left: 8px;
        border-left: 3px solid #1976d2;
      }
      
      .skill-tags-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        gap: 16px;
        
        @media (max-width: 768px) {
          grid-template-columns: 1fr;
          gap: 12px;
        }
        
        .skill-tag-card {
          display: flex;
          align-items: center;
          padding: 12px;
          border: 1px solid rgba(148, 163, 184, 0.25);
          border-radius: 8px;
          background: rgba(248, 250, 252, 0.8);
          cursor: pointer;
          transition: all 0.3s ease;
          position: relative;
          
          &:hover {
            border-color: #1976d2;
            background: rgba(25, 118, 210, 0.05);
            transform: translateY(-1px);
            box-shadow: 0 2px 8px rgba(25, 118, 210, 0.15);
          }
          
          &.selected {
            border-color: #1976d2;
            background: rgba(25, 118, 210, 0.1);
            box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
          }
          
          &.disabled {
            opacity: 0.6;
            cursor: not-allowed;
            background: rgba(148, 163, 184, 0.1);
            
            &:hover {
              transform: none;
              box-shadow: none;
            }
          }
          
          .tag-indicator {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            flex-shrink: 0;
            margin-right: 12px;
          }
          
          .tag-content {
            flex: 1;
            min-width: 0;
            
            .tag-name {
              font-weight: 600;
              color: #0d47a1;
              font-size: 14px;
              margin-bottom: 2px;
            }
            
            .tag-desc {
              color: #64748b;
              font-size: 12px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }
          
          .tag-status {
            position: absolute;
            top: -6px;
            right: -6px;
            padding: 2px 6px;
            background: #ef4444;
            color: white;
            border-radius: 10px;
            font-size: 10px;
            font-weight: 500;
          }
        }
      }
    }
  }
  
  .selected-skill-info {
    margin-bottom: 20px;
    padding: 16px;
    background: rgba(25, 118, 210, 0.05);
    border: 1px solid rgba(25, 118, 210, 0.2);
    border-radius: 8px;
    
    .selected-tag {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .tag-indicator {
        width: 16px;
        height: 16px;
        border-radius: 50%;
        flex-shrink: 0;
      }
      
      .tag-name {
        font-weight: 600;
        color: #0d47a1;
        font-size: 16px;
      }
      
      .tag-category {
        color: #1565c0;
        font-size: 14px;
        opacity: 0.8;
      }
    }
  }
  
  .skill-detail-form {
    .form-row {
      display: flex;
      gap: 20px;
      
      .half-item {
        flex: 1;
      }
      
      .proficiency-item {
        width: 100%;
      }
    }
    
    .proficiency-selector {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;
      
      @media (max-width: 768px) {
        grid-template-columns: 1fr;
        gap: 12px;
      }
      
      .proficiency-option {
        display: flex;
        align-items: center;
        padding: 16px;
        border: 1px solid rgba(148, 163, 184, 0.25);
        border-radius: 10px;
        background: rgba(248, 250, 252, 0.8);
        cursor: pointer;
        transition: all 0.3s ease;
        min-height: 70px;
        
        &:hover {
          border-color: #1976d2;
          background: rgba(25, 118, 210, 0.05);
        }
        
        &.selected {
          border-color: #1976d2;
          background: rgba(25, 118, 210, 0.1);
          box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
        }
        
        .level-icon {
          width: 20px;
          height: 20px;
          margin-right: 12px;
          border-radius: 4px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 10px;
          font-weight: bold;
          color: white;
          
          &.beginner { 
            background: #94a3b8;
            &::before { content: '入'; }
          }
          &.basic { 
            background: #3b82f6;
            &::before { content: '初'; }
          }
          &.intermediate { 
            background: #10b981;
            &::before { content: '中'; }
          }
          &.advanced { 
            background: #f59e0b;
            &::before { content: '高'; }
          }
          &.expert { 
            background: #ef4444;
            &::before { content: '专'; }
          }
        }
        
        .level-text {
          .level-name {
            font-weight: 600;
            color: #0d47a1;
            font-size: 14px;
          }
          
          .level-desc {
            color: #64748b;
            font-size: 12px;
            margin-top: 2px;
          }
        }
      }
    }
    
    .experience-input {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .exp-slider {
        flex: 1;
      }
      
      .exp-display {
        min-width: 50px;
        font-weight: 600;
        color: #0d47a1;
        text-align: center;
      }
    }
    
    .rating-input {
      display: flex;
      align-items: center;
      justify-content: center;
    }
    
    .primary-switch {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .switch-hint {
        color: #64748b;
        font-size: 12px;
      }
    }
  }
  
  .step-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 24px;
    padding-top: 16px;
    border-top: 1px solid rgba(148, 163, 184, 0.2);
  }
}
</style>