<template>
  <div class="task-template-manager">
    <el-card class="template-card">
      <template #header>
        <div class="card-header">
          <span class="title">任务模板管理</span>
          <div class="header-actions">
            <el-button type="success" @click="showCreateTemplate">
              <ActionIcon action="add" :size="14" />
              创建模板
            </el-button>
            <el-button type="primary" @click="showApplyTemplate">
              <ActionIcon action="template" :size="14" />
              应用模板
            </el-button>
          </div>
        </div>
      </template>

      <!-- 模板分类标签 -->
      <div class="template-categories">
        <el-tag
          v-for="category in categories"
          :key="category.value"
          :type="selectedCategory === category.value ? 'primary' : 'info'"
          :effect="selectedCategory === category.value ? 'dark' : 'plain'"
          class="category-tag"
          @click="filterByCategory(category.value)"
        >
          <ActionIcon :action="category.icon" :size="12" />
          {{ category.label }}
        </el-tag>
      </div>

      <!-- 模板列表 -->
      <div class="template-grid" v-loading="loading">
        <div
          v-for="template in filteredTemplates"
          :key="template.id"
          class="template-item"
          :class="{ selected: selectedTemplate?.id === template.id }"
          @click="selectTemplate(template)"
        >
          <div class="template-header">
            <div class="template-icon">
              <ActionIcon :action="getCategoryIcon(template.category)" :size="20" />
            </div>
            <div class="template-meta">
              <el-dropdown trigger="click" @command="handleTemplateAction">
                <el-button type="text" class="more-btn">
                  <ActionIcon action="more" :size="16" />
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :command="`edit:${template.id}`">
                      <ActionIcon action="edit" :size="12" />
                      编辑模板
                    </el-dropdown-item>
                    <el-dropdown-item :command="`clone:${template.id}`">
                      <ActionIcon action="copy" :size="12" />
                      克隆模板
                    </el-dropdown-item>
                    <el-dropdown-item :command="`export:${template.id}`">
                      <ActionIcon action="download" :size="12" />
                      导出模板
                    </el-dropdown-item>
                    <el-dropdown-item :command="`delete:${template.id}`" divided>
                      <ActionIcon action="delete" :size="12" />
                      删除模板
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
          
          <div class="template-content">
            <h3 class="template-title">{{ template.name }}</h3>
            <p class="template-description">{{ template.description }}</p>
            
            <div class="template-info">
              <div class="info-item">
                <ActionIcon action="category" :size="12" />
                <span>{{ getCategoryLabel(template.category) }}</span>
              </div>
              <div class="info-item">
                <ActionIcon action="task" :size="12" />
                <span>{{ template.tasksCount || 0 }} 个任务</span>
              </div>
              <div class="info-item">
                <ActionIcon action="time" :size="12" />
                <span>{{ template.estimatedHours || 0 }}小时</span>
              </div>
            </div>

            <div class="template-tags">
              <el-tag
                v-for="tag in template.tags"
                :key="tag"
                size="small"
                class="template-tag"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>

          <div class="template-footer">
            <div class="usage-stats">
              <span class="usage-count">已使用 {{ template.usageCount || 0 }} 次</span>
              <span class="last-used">{{ formatTime(template.lastUsed) }}</span>
            </div>
            <div class="template-actions">
              <el-button type="primary" size="small" @click.stop="applyTemplate(template)">
                应用模板
              </el-button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="filteredTemplates.length === 0" class="empty-templates">
          <el-empty description="暂无模板">
            <template #image>
              <ActionIcon action="template" :size="80" style="color: #c0c4cc;" />
            </template>
            <el-button type="primary" @click="showCreateTemplate">创建第一个模板</el-button>
          </el-empty>
        </div>
      </div>
    </el-card>

    <!-- 创建/编辑模板对话框 -->
    <el-dialog
      v-model="templateDialogVisible"
      :title="templateDialogTitle"
      width="900px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="templateFormRef"
        :model="templateForm"
        :rules="templateRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="模板名称" prop="name">
              <el-input v-model="templateForm.name" placeholder="请输入模板名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模板分类" prop="category">
              <el-select v-model="templateForm.category" placeholder="请选择分类" style="width: 100%">
                <el-option
                  v-for="category in categories.slice(1)"
                  :key="category.value"
                  :label="category.label"
                  :value="category.value"
                >
                  <ActionIcon :action="category.icon" :size="12" />
                  {{ category.label }}
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="模板描述" prop="description">
          <el-input
            v-model="templateForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入模板描述"
          />
        </el-form-item>

        <el-form-item label="标签">
          <el-tag
            v-for="tag in templateForm.tags"
            :key="tag"
            closable
            @close="removeTag(tag)"
            class="tag-item"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-if="inputVisible"
            ref="InputRef"
            v-model="inputValue"
            class="tag-input"
            size="small"
            @keyup.enter="handleInputConfirm"
            @blur="handleInputConfirm"
          />
          <el-button v-else class="button-new-tag" size="small" @click="showInput">
            + 添加标签
          </el-button>
        </el-form-item>

        <el-form-item label="任务列表">
          <div class="task-template-list">
            <div class="task-list-header">
              <span>任务模板配置</span>
              <el-button type="primary" size="small" @click="addTaskTemplate">
                <ActionIcon action="add" :size="12" />
                添加任务
              </el-button>
            </div>
            
            <div
              v-for="(task, index) in templateForm.tasks"
              :key="`task-${index}`"
              class="task-template-item"
            >
              <div class="task-order">{{ index + 1 }}</div>
              <div class="task-content">
                <el-row :gutter="12">
                  <el-col :span="8">
                    <el-input
                      v-model="task.title"
                      placeholder="任务标题"
                      size="small"
                    />
                  </el-col>
                  <el-col :span="4">
                    <el-select
                      v-model="task.priority"
                      placeholder="优先级"
                      size="small"
                      style="width: 100%"
                    >
                      <el-option label="高" value="高" />
                      <el-option label="中" value="中" />
                      <el-option label="低" value="低" />
                    </el-select>
                  </el-col>
                  <el-col :span="4">
                    <el-input-number
                      v-model="task.estimatedHours"
                      placeholder="预计工时"
                      size="small"
                      :min="0"
                      :max="1000"
                      style="width: 100%"
                    />
                  </el-col>
                  <el-col :span="6">
                    <el-input
                      v-model="task.description"
                      placeholder="任务描述"
                      size="small"
                    />
                  </el-col>
                  <el-col :span="2">
                    <el-button
                      type="danger"
                      size="small"
                      circle
                      @click="removeTaskTemplate(index)"
                    >
                      <ActionIcon action="delete" :size="12" />
                    </el-button>
                  </el-col>
                </el-row>
              </div>
            </div>

            <div v-if="templateForm.tasks.length === 0" class="no-tasks">
              <el-empty description="暂无任务模板" :image-size="60">
                <el-button type="primary" size="small" @click="addTaskTemplate">
                  添加第一个任务
                </el-button>
              </el-empty>
            </div>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="templateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveTemplate" :loading="saving">
            {{ templateForm.id ? '更新模板' : '创建模板' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 应用模板对话框 -->
    <el-dialog
      v-model="applyDialogVisible"
      title="应用任务模板"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="selectedTemplate" class="apply-template-content">
        <div class="template-preview">
          <h3>{{ selectedTemplate.name }}</h3>
          <p>{{ selectedTemplate.description }}</p>
          
          <div class="apply-options">
            <el-form :model="applyForm" label-width="120px">
              <el-form-item label="应用到项目">
                <el-select v-model="applyForm.projectId" placeholder="请选择项目" style="width: 100%">
                  <el-option
                    v-for="project in projects"
                    :key="project.id"
                    :label="project.name"
                    :value="project.id"
                  />
                </el-select>
              </el-form-item>
              
              <el-form-item label="任务负责人">
                <UserSelector 
                  v-model="applyForm.assigneeId" 
                  :project-id="applyForm.projectId"
                  placeholder="请选择负责人"
                  @change="handleAssigneeChange"
                />
              </el-form-item>
              
              <el-form-item label="开始日期">
                <el-date-picker
                  v-model="applyForm.startDate"
                  type="date"
                  placeholder="请选择开始日期"
                  style="width: 100%"
                />
              </el-form-item>
              
              <el-form-item label="任务前缀">
                <el-input v-model="applyForm.taskPrefix" placeholder="任务标题前缀（可选）" />
              </el-form-item>
            </el-form>
          </div>

          <div class="tasks-preview">
            <h4>将创建以下任务：</h4>
            <div class="preview-list">
              <div
                v-for="(task, index) in selectedTemplate.tasks"
                :key="index"
                class="preview-task"
              >
                <div class="task-order">{{ index + 1 }}</div>
                <div class="task-info">
                  <div class="task-title">
                    {{ applyForm.taskPrefix ? `${applyForm.taskPrefix}${task.title}` : task.title }}
                  </div>
                  <div class="task-meta">
                    <el-tag :type="getPriorityType(task.priority)" size="small">
                      {{ task.priority }}优先级
                    </el-tag>
                    <span class="estimated-hours">预计 {{ task.estimatedHours }}h</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="applyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmApplyTemplate" :loading="applying">
            应用模板
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ActionIcon } from '@/components/SvgIcons';
import UserSelector from '@/components/Common/UserSelector.vue';
import { getProjectList } from '@/api/project';
import { 
  getTaskTemplateList, 
  getTaskTemplateById, 
  createTaskTemplate, 
  updateTaskTemplate, 
  deleteTaskTemplate, 
  applyTaskTemplate, 
  cloneTaskTemplate,
  getTaskTemplatesByCategory,
  getPopularTaskTemplates
} from '@/api/taskTemplate';

const emit = defineEmits(['template-applied']);

// 响应式数据
const loading = ref(false);
const saving = ref(false);
const applying = ref(false);
const selectedCategory = ref('all');
const selectedTemplate = ref(null);
const templateDialogVisible = ref(false);
const templateDialogTitle = ref('');
const applyDialogVisible = ref(false);

// 标签输入
const inputVisible = ref(false);
const inputValue = ref('');

// 数据
const templates = ref([]);
const projects = ref([]);
const selectedAssignee = ref(null);

// 模板分类
const categories = [
  { value: 'all', label: '全部', icon: 'all' },
  { value: 'development', label: '开发类', icon: 'code' },
  { value: 'design', label: '设计类', icon: 'design' },
  { value: 'testing', label: '测试类', icon: 'test' },
  { value: 'deployment', label: '部署类', icon: 'deploy' },
  { value: 'meeting', label: '会议类', icon: 'meeting' },
  { value: 'research', label: '调研类', icon: 'research' },
  { value: 'maintenance', label: '维护类', icon: 'maintenance' }
];

// 表单数据
const templateFormRef = ref(null);
const templateForm = reactive({
  id: null,
  name: '',
  description: '',
  category: '',
  tags: [],
  tasks: []
});

// 应用表单
const applyForm = reactive({
  projectId: '',
  assigneeId: '',
  startDate: '',
  taskPrefix: ''
});

// 表单验证规则
const templateRules = {
  name: [
    { required: true, message: '请输入模板名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择模板分类', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入模板描述', trigger: 'blur' }
  ]
};

// 过滤后的模板
const filteredTemplates = computed(() => {
  if (selectedCategory.value === 'all') {
    return templates.value;
  }
  return templates.value.filter(template => template.category === selectedCategory.value);
});

// 获取分类图标
const getCategoryIcon = (category) => {
  const categoryItem = categories.find(c => c.value === category);
  return categoryItem ? categoryItem.icon : 'template';
};

// 获取分类标签
const getCategoryLabel = (category) => {
  const categoryItem = categories.find(c => c.value === category);
  return categoryItem ? categoryItem.label : '未知分类';
};

// 获取优先级类型
const getPriorityType = (priority) => {
  const map = {
    '高': 'danger',
    '中': 'warning', 
    '低': 'info'
  };
  return map[priority] || 'info';
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '从未使用';
  const now = new Date();
  const target = new Date(time);
  const diff = now - target;
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (days === 0) return '今天使用';
  if (days === 1) return '昨天使用';
  if (days < 7) return `${days}天前使用`;
  if (days < 30) return `${Math.floor(days / 7)}周前使用`;
  return `${Math.floor(days / 30)}个月前使用`;
};

// 按分类筛选
const filterByCategory = (category) => {
  selectedCategory.value = category;
};

// 选择模板
const selectTemplate = (template) => {
  selectedTemplate.value = selectedTemplate.value?.id === template.id ? null : template;
};

// 显示创建模板对话框
const showCreateTemplate = () => {
  templateDialogTitle.value = '创建任务模板';
  resetTemplateForm();
  templateDialogVisible.value = true;
};

// 显示应用模板对话框
const showApplyTemplate = () => {
  if (!selectedTemplate.value) {
    ElMessage.warning('请先选择一个模板');
    return;
  }
  resetApplyForm();
  applyDialogVisible.value = true;
};

// 重置模板表单
const resetTemplateForm = () => {
  templateForm.id = null;
  templateForm.name = '';
  templateForm.description = '';
  templateForm.category = '';
  templateForm.tags = [];
  templateForm.tasks = [];
};

// 重置应用表单
const resetApplyForm = () => {
  applyForm.projectId = '';
  applyForm.assigneeId = '';
  applyForm.startDate = '';
  applyForm.taskPrefix = '';
  selectedAssignee.value = null;
};

// 处理模板操作
const handleTemplateAction = (command) => {
  const [action, templateId] = command.split(':');
  const template = templates.value.find(t => t.id == templateId);
  
  switch (action) {
    case 'edit':
      editTemplate(template);
      break;
    case 'clone':
      cloneTemplate(template);
      break;
    case 'export':
      exportTemplate(template);
      break;
    case 'delete':
      deleteTemplate(template);
      break;
  }
};

// 编辑模板
const editTemplate = (template) => {
  templateDialogTitle.value = '编辑任务模板';
  Object.assign(templateForm, template);
  templateDialogVisible.value = true;
};

// 克隆模板
const cloneTemplate = (template) => {
  templateDialogTitle.value = '克隆任务模板';
  Object.assign(templateForm, {
    ...template,
    id: null,
    name: `${template.name} - 副本`,
    usageCount: 0,
    lastUsed: null
  });
  templateDialogVisible.value = true;
};

// 导出模板
const exportTemplate = (template) => {
  const dataStr = JSON.stringify(template, null, 2);
  const dataBlob = new Blob([dataStr], { type: 'application/json' });
  const url = URL.createObjectURL(dataBlob);
  const link = document.createElement('a');
  link.href = url;
  link.download = `${template.name}-模板.json`;
  link.click();
  URL.revokeObjectURL(url);
  ElMessage.success('模板导出成功');
};

// 删除模板
const deleteTemplate = async (template) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除模板"${template.name}"吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    // 这里调用删除API
    templates.value = templates.value.filter(t => t.id !== template.id);
    ElMessage.success('模板删除成功');
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除模板失败:', error);
      ElMessage.error('删除模板失败');
    }
  }
};

// 添加任务模板
const addTaskTemplate = () => {
  templateForm.tasks.push({
    title: '',
    description: '',
    priority: '中',
    estimatedHours: 8
  });
};

// 删除任务模板
const removeTaskTemplate = (index) => {
  templateForm.tasks.splice(index, 1);
};

// 添加标签
const showInput = () => {
  inputVisible.value = true;
  nextTick(() => {
    document.querySelector('.tag-input input')?.focus();
  });
};

const handleInputConfirm = () => {
  if (inputValue.value && !templateForm.tags.includes(inputValue.value)) {
    templateForm.tags.push(inputValue.value);
  }
  inputVisible.value = false;
  inputValue.value = '';
};

const removeTag = (tag) => {
  templateForm.tags = templateForm.tags.filter(t => t !== tag);
};

// 保存模板
const saveTemplate = async () => {
  try {
    await templateFormRef.value.validate();
    
    if (templateForm.tasks.length === 0) {
      ElMessage.warning('请至少添加一个任务');
      return;
    }
    
    saving.value = true;
    
    // 这里调用保存API
    const templateData = { ...templateForm };
    templateData.estimatedHours = templateData.tasks.reduce((total, task) => total + task.estimatedHours, 0);
    templateData.tasksCount = templateData.tasks.length;
    templateData.createdAt = new Date().toISOString();
    templateData.usageCount = 0;
    
    if (templateForm.id) {
      // 更新模板
      const index = templates.value.findIndex(t => t.id === templateForm.id);
      if (index > -1) {
        templates.value[index] = { ...templateData };
      }
      ElMessage.success('模板更新成功');
    } else {
      // 创建模板
      templateData.id = Date.now();
      templates.value.push(templateData);
      ElMessage.success('模板创建成功');
    }
    
    templateDialogVisible.value = false;
  } catch (error) {
    console.error('保存模板失败:', error);
    ElMessage.error('保存模板失败');
  } finally {
    saving.value = false;
  }
};

// 应用模板
const applyTemplate = (template) => {
  selectedTemplate.value = template;
  showApplyTemplate();
};

// 处理负责人选择变化
const handleAssigneeChange = (user) => {
  selectedAssignee.value = user;
};

// 确认应用模板
const confirmApplyTemplate = async () => {
  if (!applyForm.projectId) {
    ElMessage.warning('请选择项目');
    return;
  }
  
  if (!applyForm.assigneeId) {
    ElMessage.warning('请选择负责人');
    return;
  }
  
  applying.value = true;
  try {
    // 这里调用应用模板的API
    const tasksToCreate = selectedTemplate.value.tasks.map((task, index) => ({
      ...task,
      title: applyForm.taskPrefix ? `${applyForm.taskPrefix}${task.title}` : task.title,
      projectId: applyForm.projectId,
      assigneeId: applyForm.assigneeId,
      startTime: applyForm.startDate,
      status: '待处理',
      progress: 0,
      order: index + 1
    }));
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    // 更新使用统计
    selectedTemplate.value.usageCount = (selectedTemplate.value.usageCount || 0) + 1;
    selectedTemplate.value.lastUsed = new Date().toISOString();
    
    const assigneeName = selectedAssignee.value?.realName || selectedAssignee.value?.username || '选中用户';
    ElMessage.success(`成功创建 ${tasksToCreate.length} 个任务，负责人：${assigneeName}`);
    applyDialogVisible.value = false;
    emit('template-applied', { template: selectedTemplate.value, tasks: tasksToCreate, assignee: selectedAssignee.value });
  } catch (error) {
    console.error('应用模板失败:', error);
    ElMessage.error('应用模板失败');
  } finally {
    applying.value = false;
  }
};

// 加载项目数据
const loadProjects = async () => {
  try {
    const response = await getProjectList({
      pageNum: 1,
      pageSize: 100
    });
    if (response.code === 200) {
      projects.value = response.data?.records || response.data || [];
    }
  } catch (error) {
    console.error('加载项目列表失败:', error);
    // 使用模拟数据作为后备
    projects.value = [];
  }
};

// 加载模板数据
const loadTemplates = async () => {
  loading.value = true;
  try {
    const response = await getTaskTemplateList({
      pageNum: 1,
      pageSize: 100,
      category: selectedCategory.value === 'all' ? undefined : selectedCategory.value
    });
    
    if (response.code === 200) {
      templates.value = response.data?.records || [];
    } else {
      throw new Error(response.message || '获取模板列表失败');
    }
  } catch (error) {
    console.error('加载模板失败:', error);
    ElMessage.error('加载模板失败: ' + (error.response?.data?.message || error.message || '未知错误'));
    
    // 如果API调用失败，使用模拟数据作为后备
    templates.value = [
      {
        id: 1,
        name: 'Web开发标准流程',
        description: '包含需求分析、设计、开发、测试、部署的完整Web开发流程',
        category: 'development',
        tags: ['前端', '后端', '全栈'],
        tasksCount: 8,
        estimatedHours: 120,
        usageCount: 15,
        lastUsed: new Date(Date.now() - 86400000).toISOString(),
        tasks: [
          { title: '需求分析', description: '分析项目需求', priority: '高', estimatedHours: 16 },
          { title: 'UI/UX设计', description: '界面设计', priority: '高', estimatedHours: 24 },
          { title: '前端开发', description: '前端页面开发', priority: '中', estimatedHours: 40 },
          { title: '后端开发', description: '后端接口开发', priority: '中', estimatedHours: 32 }
        ]
      },
      {
        id: 2,
        name: '移动应用开发',
        description: 'iOS和Android移动应用开发标准流程',
        category: 'development',
        tags: ['移动端', 'iOS', 'Android'],
        tasksCount: 6,
        estimatedHours: 80,
        usageCount: 8,
        lastUsed: new Date(Date.now() - 172800000).toISOString(),
        tasks: [
          { title: '产品规划', description: '确定产品功能和设计', priority: '高', estimatedHours: 16 },
          { title: '原型设计', description: '制作应用原型', priority: '高', estimatedHours: 12 },
          { title: 'UI设计', description: '界面视觉设计', priority: '中', estimatedHours: 20 },
          { title: '前端开发', description: '移动端开发', priority: '中', estimatedHours: 32 }
        ]
      }
    ];
  } finally {
    loading.value = false;
  }
};

// 组件挂载
onMounted(() => {
  loadTemplates();
  loadProjects();
});
</script>

<style lang="scss" scoped>
.task-template-manager {
  .template-card {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .title {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
      }
      
      .header-actions {
        display: flex;
        gap: 12px;
      }
    }
  }
  
  .template-categories {
    display: flex;
    gap: 8px;
    margin-bottom: 24px;
    flex-wrap: wrap;
    
    .category-tag {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 16px;
      cursor: pointer;
      border-radius: 20px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-1px);
      }
    }
  }
  
  .template-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 20px;
    
    .template-item {
      background: #fff;
      border: 2px solid #e4e7ed;
      border-radius: 12px;
      padding: 20px;
      cursor: pointer;
      transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
      position: relative;
      
      &:hover {
        border-color: #409eff;
        transform: translateY(-4px);
        box-shadow: 0 8px 25px rgba(64, 158, 255, 0.15);
      }
      
      &.selected {
        border-color: #409eff;
        background: linear-gradient(135deg, rgba(64, 158, 255, 0.05) 0%, rgba(64, 158, 255, 0.02) 100%);
        
        &::before {
          content: '';
          position: absolute;
          top: -2px;
          left: -2px;
          right: -2px;
          bottom: -2px;
          border: 2px solid #409eff;
          border-radius: 14px;
          opacity: 0.3;
        }
      }
      
      .template-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;
        
        .template-icon {
          width: 40px;
          height: 40px;
          border-radius: 10px;
          background: linear-gradient(135deg, #409eff 0%, #2e7ce0 100%);
          color: white;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
        }
        
        .template-meta {
          .more-btn {
            color: #909399;
            padding: 4px;
            
            &:hover {
              color: #409eff;
            }
          }
        }
      }
      
      .template-content {
        .template-title {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          margin: 0 0 8px 0;
          line-height: 1.4;
        }
        
        .template-description {
          color: #606266;
          font-size: 14px;
          line-height: 1.6;
          margin: 0 0 16px 0;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
        
        .template-info {
          display: flex;
          gap: 16px;
          margin-bottom: 12px;
          
          .info-item {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 12px;
            color: #909399;
          }
        }
        
        .template-tags {
          display: flex;
          gap: 6px;
          flex-wrap: wrap;
          margin-bottom: 16px;
          
          .template-tag {
            background: rgba(64, 158, 255, 0.1);
            color: #409eff;
            border: none;
          }
        }
      }
      
      .template-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 16px;
        border-top: 1px solid #f0f2f5;
        
        .usage-stats {
          display: flex;
          flex-direction: column;
          gap: 2px;
          
          .usage-count {
            font-size: 12px;
            color: #606266;
            font-weight: 500;
          }
          
          .last-used {
            font-size: 11px;
            color: #909399;
          }
        }
        
        .template-actions {
          .el-button {
            border-radius: 8px;
          }
        }
      }
    }
    
    .empty-templates {
      grid-column: 1 / -1;
      text-align: center;
      padding: 60px 20px;
    }
  }
}

// 对话框样式
.task-template-list {
  .task-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 1px solid #e4e7ed;
    
    span {
      font-weight: 600;
      color: #303133;
    }
  }
  
  .task-template-item {
    display: flex;
    gap: 12px;
    margin-bottom: 12px;
    padding: 12px;
    background: #f8f9fa;
    border-radius: 8px;
    
    .task-order {
      width: 24px;
      height: 24px;
      border-radius: 50%;
      background: #409eff;
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      font-weight: 600;
      flex-shrink: 0;
    }
    
    .task-content {
      flex: 1;
    }
  }
  
  .no-tasks {
    text-align: center;
    padding: 40px 0;
    background: #f8f9fa;
    border-radius: 8px;
  }
}

.apply-template-content {
  .template-preview {
    h3 {
      margin: 0 0 8px 0;
      color: #303133;
    }
    
    p {
      margin: 0 0 24px 0;
      color: #606266;
      line-height: 1.6;
    }
  }
  
  .apply-options {
    margin-bottom: 24px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;
  }
  
  .tasks-preview {
    h4 {
      margin: 0 0 16px 0;
      color: #303133;
    }
    
    .preview-list {
      max-height: 200px;
      overflow-y: auto;
      
      .preview-task {
        display: flex;
        gap: 12px;
        padding: 8px 0;
        border-bottom: 1px solid #f0f2f5;
        
        &:last-child {
          border-bottom: none;
        }
        
        .task-order {
          width: 20px;
          height: 20px;
          border-radius: 50%;
          background: #e4e7ed;
          color: #606266;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 11px;
          font-weight: 600;
          flex-shrink: 0;
        }
        
        .task-info {
          flex: 1;
          
          .task-title {
            font-weight: 500;
            color: #303133;
            margin-bottom: 4px;
          }
          
          .task-meta {
            display: flex;
            gap: 8px;
            align-items: center;
            
            .estimated-hours {
              font-size: 12px;
              color: #909399;
            }
          }
        }
      }
    }
  }
}

.tag-item {
  margin-right: 8px;
  margin-bottom: 8px;
}

.tag-input {
  width: 90px;
  margin-right: 8px;
  vertical-align: bottom;
}

.button-new-tag {
  height: 24px;
  line-height: 22px;
  padding: 0 8px;
  border-style: dashed;
}

// 响应式设计
@media (max-width: 1200px) {
  .task-template-manager {
    .template-grid {
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 16px;
    }
  }
}

@media (max-width: 768px) {
  .task-template-manager {
    .template-card .card-header {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;
      
      .header-actions {
        justify-content: space-between;
      }
    }
    
    .template-categories {
      gap: 6px;
      
      .category-tag {
        padding: 6px 12px;
        font-size: 12px;
      }
    }
    
    .template-grid {
      grid-template-columns: 1fr;
      
      .template-item {
        padding: 16px;
      }
    }
  }
}
</style>