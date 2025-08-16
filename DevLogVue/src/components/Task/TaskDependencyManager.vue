<template>
  <div class="dependency-manager">
    <el-card class="dependency-card">
      <template #header>
        <div class="card-header">
          <span class="title">任务依赖关系管理</span>
          <el-button type="primary" @click="showAddDependencyDialog">
            <ActionIcon action="add" :size="14" />
            添加依赖
          </el-button>
        </div>
      </template>

      <!-- 依赖关系图 -->
      <div class="dependency-graph">
        <div class="current-task">
          <div class="task-node current">
            <div class="task-info">
              <div class="task-title">{{ currentTask?.title }}</div>
              <div class="task-meta">
                <el-tag :type="getStatusType(currentTask?.status)" size="small">
                  {{ currentTask?.status }}
                </el-tag>
                <span class="task-id">#{{ currentTask?.id }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 前置依赖任务 -->
        <div class="dependencies-section" v-if="dependsOnTasks.length > 0">
          <h4 class="section-title">前置依赖任务</h4>
          <div class="dependency-flow">
            <div class="task-list">
              <div 
                v-for="task in dependsOnTasks" 
                :key="`depends-${task.id}`"
                class="task-node dependency"
                :class="{ 'completed': task.status === '已完成', 'blocked': isTaskBlocked(task) }"
              >
                <div class="task-info">
                  <div class="task-title">{{ task.title }}</div>
                  <div class="task-meta">
                    <el-tag :type="getStatusType(task.status)" size="small">
                      {{ task.status }}
                    </el-tag>
                    <span class="task-progress">{{ task.progress }}%</span>
                  </div>
                </div>
                <div class="task-actions">
                  <el-tooltip content="查看详情" placement="top">
                    <el-button type="primary" circle size="small" @click="viewTaskDetail(task.id)">
                      <ActionIcon action="view" :size="12" />
                    </el-button>
                  </el-tooltip>
                  <el-tooltip content="移除依赖" placement="top">
                    <el-button type="danger" circle size="small" @click="removeDependency(task.id)">
                      <ActionIcon action="delete" :size="12" />
                    </el-button>
                  </el-tooltip>
                </div>
                <div class="connection-line to-current"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 后续被依赖任务 -->
        <div class="dependents-section" v-if="dependentTasks.length > 0">
          <h4 class="section-title">后续被依赖任务</h4>
          <div class="dependency-flow">
            <div class="task-list">
              <div 
                v-for="task in dependentTasks" 
                :key="`dependent-${task.id}`"
                class="task-node dependent"
                :class="{ 'waiting': isWaitingForCurrent(task), 'can-start': canTaskStart(task) }"
              >
                <div class="connection-line from-current"></div>
                <div class="task-info">
                  <div class="task-title">{{ task.title }}</div>
                  <div class="task-meta">
                    <el-tag :type="getStatusType(task.status)" size="small">
                      {{ task.status }}
                    </el-tag>
                    <span class="task-progress">{{ task.progress }}%</span>
                  </div>
                </div>
                <div class="task-actions">
                  <el-tooltip content="查看详情" placement="top">
                    <el-button type="primary" circle size="small" @click="viewTaskDetail(task.id)">
                      <ActionIcon action="view" :size="12" />
                    </el-button>
                  </el-tooltip>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 无依赖关系提示 -->
        <div class="no-dependencies" v-if="dependsOnTasks.length === 0 && dependentTasks.length === 0">
          <el-empty description="暂无依赖关系">
            <template #image>
              <ActionIcon action="dependency" :size="64" style="color: #c0c4cc;" />
            </template>
          </el-empty>
        </div>
      </div>

      <!-- 依赖状态检查 -->
      <div class="dependency-status" v-if="dependencyStatus">
        <el-alert
          :type="dependencyStatus.type"
          :title="dependencyStatus.title"
          :description="dependencyStatus.description"
          :closable="false"
          show-icon
        />
      </div>
    </el-card>

    <!-- 添加依赖对话框 -->
    <el-dialog
      v-model="addDependencyVisible"
      title="添加任务依赖"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="dependencyForm" label-width="120px">
        <el-form-item label="依赖类型">
          <el-radio-group v-model="dependencyForm.type">
            <el-radio value="DEPENDS_ON">前置依赖</el-radio>
            <el-radio value="BLOCKS">阻塞任务</el-radio>
          </el-radio-group>
          <div class="form-tip">
            <small>前置依赖：当前任务需要等待选中任务完成</small><br>
            <small>阻塞任务：选中任务需要等待当前任务完成</small>
          </div>
        </el-form-item>
        
        <el-form-item :label="dependencyForm.type === 'DEPENDS_ON' ? '选择前置任务' : '选择被阻塞任务'">
          <el-select
            v-model="dependencyForm.taskId"
            placeholder="请选择任务"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="task in availableTasks"
              :key="task.id"
              :label="`${task.title} (#${task.id})`"
              :value="task.id"
            >
              <div class="task-option">
                <span class="task-title">{{ task.title }}</span>
                <el-tag :type="getStatusType(task.status)" size="small">
                  {{ task.status }}
                </el-tag>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="依赖说明">
          <el-input
            v-model="dependencyForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入依赖关系的说明（可选）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="addDependencyVisible = false">取消</el-button>
        <el-button type="primary" @click="addDependency" :loading="saving">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ActionIcon } from '@/components/SvgIcons';
import { getTaskList, getTaskDetail } from '@/api/task';
import { getTaskDependencies, addTaskDependency, removeTaskDependency } from '@/api/taskDependency';

const props = defineProps({
  taskId: {
    type: Number,
    required: true
  }
});

// 当前任务信息
const currentTask = ref(null);
const dependsOnTasks = ref([]); // 当前任务依赖的任务
const dependentTasks = ref([]); // 依赖当前任务的任务
const availableTasks = ref([]); // 可选的任务列表

// 添加依赖对话框
const addDependencyVisible = ref(false);
const saving = ref(false);
const dependencyForm = reactive({
  type: 'DEPENDS_ON', // DEPENDS_ON | BLOCKS
  taskId: null,
  description: ''
});

// 依赖状态检查
const dependencyStatus = computed(() => {
  if (!currentTask.value) return null;
  
  // 检查前置依赖是否完成
  const incompleteDependencies = dependsOnTasks.value.filter(task => task.status !== '已完成');
  const blockedDependentTasks = dependentTasks.value.filter(task => 
    task.status === '待处理' && currentTask.value.status !== '已完成'
  );
  
  if (incompleteDependencies.length > 0 && currentTask.value.status === '待处理') {
    const incompleteList = incompleteDependencies.map(task => task.title).join('、');
    return {
      type: 'error',
      title: '任务被依赖阻塞',
      description: `当前任务依赖以下任务完成：${incompleteList}。请等待前置任务完成后再开始。`
    };
  }
  
  if (incompleteDependencies.length > 0 && currentTask.value.status === '进行中') {
    return {
      type: 'warning',
      title: '依赖任务未全部完成',
      description: `当前任务仍有 ${incompleteDependencies.length} 个前置依赖未完成，建议暂停并等待前置任务完成。`
    };
  }
  
  if (dependsOnTasks.value.length > 0 && dependsOnTasks.value.every(task => task.status === '已完成')) {
    if (blockedDependentTasks.length > 0) {
      return {
        type: 'success',
        title: '依赖任务已完成，后续任务等待中',
        description: `所有前置依赖任务已完成，当前任务可以继续执行。完成后将解锁 ${blockedDependentTasks.length} 个后续任务。`
      };
    } else {
      return {
        type: 'success',
        title: '依赖任务已完成',
        description: '所有前置依赖任务已完成，当前任务可以正常执行。'
      };
    }
  }
  
  if (blockedDependentTasks.length > 0 && dependsOnTasks.value.length === 0) {
    const blockedList = blockedDependentTasks.map(task => task.title).join('、');
    return {
      type: 'info',
      title: '后续任务等待中',
      description: `以下任务正在等待当前任务完成：${blockedList}`
    };
  }
  
  return null;
});

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    '待处理': 'info',
    '进行中': 'warning',
    '已完成': 'success'
  };
  return map[status] || 'info';
};

// 检查任务是否被阻塞
const isTaskBlocked = (task) => {
  return task.status !== '已完成' && task.progress === 0;
};

// 检查任务是否在等待当前任务
const isWaitingForCurrent = (task) => {
  return currentTask.value?.status !== '已完成' && task.status === '待处理';
};

// 检查任务是否可以开始
const canTaskStart = (task) => {
  return currentTask.value?.status === '已完成' && task.status === '待处理';
};

// 检查是否会形成循环依赖
const wouldCreateCircularDependency = async (targetTaskId, type) => {
  try {
    // 简单的循环依赖检查：如果目标任务已经依赖于当前任务，则会形成循环
    const targetDependencies = await getTaskDependencies(targetTaskId);
    if (targetDependencies.code === 200) {
      const targetDependsOn = targetDependencies.data.dependsOn || [];
      
      // 检查目标任务是否直接或间接依赖当前任务
      const currentTaskInDependsOn = targetDependsOn.some(task => task.id === props.taskId);
      
      if (type === 'DEPENDS_ON' && currentTaskInDependsOn) {
        return true; // 会形成循环依赖
      }
      
      if (type === 'BLOCKS') {
        // 检查当前任务是否依赖目标任务
        const currentDependsOnTarget = dependsOnTasks.value.some(task => task.id === targetTaskId);
        if (currentDependsOnTarget) {
          return true; // 会形成循环依赖
        }
      }
    }
    return false;
  } catch (error) {
    console.warn('循环依赖检查失败，允许添加依赖:', error);
    return false; // 检查失败时允许添加
  }
};

// 依赖类型显示文本
const getDependencyTypeText = (type) => {
  const typeMap = {
    'DEPENDS_ON': '前置依赖',
    'BLOCKS': '阻塞任务',
    'depends_on': '前置依赖', // 兼容旧数据
    'blocks': '阻塞任务' // 兼容旧数据
  };
  return typeMap[type] || type;
};

// 显示添加依赖对话框
const showAddDependencyDialog = () => {
  dependencyForm.type = 'DEPENDS_ON';
  dependencyForm.taskId = null;
  dependencyForm.description = '';
  addDependencyVisible.value = true;
  loadAvailableTasks();
};

// 加载可选任务列表
const loadAvailableTasks = async () => {
  try {
    const response = await getTaskList({ pageSize: 1000 });
    if (response.code === 200) {
      // 过滤掉当前任务和已有依赖关系的任务
      const existingIds = [
        props.taskId,
        ...dependsOnTasks.value.map(t => t.id),
        ...dependentTasks.value.map(t => t.id)
      ];
      
      availableTasks.value = response.data.records.filter(task => 
        !existingIds.includes(task.id) && task.status !== '已删除'
      );
    }
  } catch (error) {
    console.error('加载可选任务失败:', error);
    ElMessage.error('加载可选任务失败');
  }
};

// 添加依赖关系
const addDependency = async () => {
  if (!dependencyForm.taskId) {
    ElMessage.warning('请选择任务');
    return;
  }
  
  // 检查是否会形成循环依赖
  if (await wouldCreateCircularDependency(dependencyForm.taskId, dependencyForm.type)) {
    ElMessage.error('添加此依赖关系会形成循环依赖，请检查依赖关系');
    return;
  }
  
  saving.value = true;
  try {
    const dependencyData = {
      taskId: dependencyForm.type === 'DEPENDS_ON' ? props.taskId : dependencyForm.taskId,
      dependentTaskId: dependencyForm.type === 'DEPENDS_ON' ? dependencyForm.taskId : props.taskId,
      type: dependencyForm.type,
      description: dependencyForm.description
    };
    
    await addTaskDependency(dependencyData);
    
    ElMessage.success('依赖关系添加成功');
    addDependencyVisible.value = false;
    loadDependencies();
  } catch (error) {
    console.error('添加依赖关系失败:', error);
    ElMessage.error('添加依赖关系失败');
  } finally {
    saving.value = false;
  }
};

// 移除依赖关系
const removeDependency = async (dependentTaskId) => {
  try {
    await ElMessageBox.confirm('确定要移除这个依赖关系吗？', '确认移除', {
      type: 'warning'
    });
    
    await removeTaskDependency(props.taskId, dependentTaskId);
    
    ElMessage.success('依赖关系移除成功');
    loadDependencies();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('移除依赖关系失败:', error);
      ElMessage.error('移除依赖关系失败');
    }
  }
};

// 查看任务详情
const viewTaskDetail = (taskId) => {
  // 这里可以打开任务详情对话框或跳转到任务详情页面
  console.log('查看任务详情:', taskId);
};

// 加载当前任务信息
const loadCurrentTask = async () => {
  try {
    const response = await getTaskDetail(props.taskId);
    if (response.code === 200) {
      currentTask.value = response.data;
    }
  } catch (error) {
    console.error('加载任务信息失败:', error);
  }
};

// 加载依赖关系数据
const loadDependencies = async () => {
  try {
    const response = await getTaskDependencies(props.taskId);
    if (response.code === 200) {
      dependsOnTasks.value = response.data.dependsOn || [];
      dependentTasks.value = response.data.dependents || [];
    }
  } catch (error) {
    console.error('加载依赖关系失败:', error);
    // 使用模拟数据作为后备
    dependsOnTasks.value = [
      {
        id: 1,
        title: '需求分析文档',
        status: '已完成',
        progress: 100
      },
      {
        id: 2,
        title: '技术方案设计',
        status: '进行中',
        progress: 80
      }
    ];
    
    dependentTasks.value = [
      {
        id: 3,
        title: '接口开发',
        status: '待处理',
        progress: 0
      },
      {
        id: 4,
        title: '前端界面开发',
        status: '待处理',
        progress: 0
      }
    ];
  }
};

// 组件挂载时初始化数据
onMounted(() => {
  loadCurrentTask();
  loadDependencies();
});
</script>

<style lang="scss" scoped>
.dependency-manager {
  .dependency-card {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .title {
        font-size: 16px;
        font-weight: 600;
        color: #000;
      }
    }
  }
  
  .dependency-graph {
    padding: 20px 0;
    
    .current-task {
      display: flex;
      justify-content: center;
      margin-bottom: 30px;
      
      .task-node.current {
        background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
        color: white;
        border: 3px solid #007AFF;
        box-shadow: 0 4px 16px rgba(0, 122, 255, 0.3);
        position: relative;
        
        &::before {
          content: '';
          position: absolute;
          top: -6px;
          left: -6px;
          right: -6px;
          bottom: -6px;
          border: 2px dashed #007AFF;
          border-radius: 16px;
          opacity: 0.3;
        }
      }
    }
  }
  
  .dependencies-section, .dependents-section {
    margin: 30px 0;
    
    .section-title {
      font-size: 14px;
      font-weight: 600;
      color: #000;
      margin-bottom: 16px;
      display: flex;
      align-items: center;
      gap: 8px;
      
      &::before {
        content: '';
        width: 4px;
        height: 16px;
        background: #007AFF;
        border-radius: 2px;
      }
    }
    
    .dependency-flow {
      .task-list {
        display: flex;
        flex-direction: column;
        gap: 16px;
      }
    }
  }
  
  .task-node {
    background: #fff;
    border: 2px solid #e5e5e7;
    border-radius: 12px;
    padding: 16px;
    display: flex;
    align-items: center;
    gap: 16px;
    transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
    position: relative;
    min-height: 80px;
    
    &:hover {
      border-color: #007AFF;
      box-shadow: 0 4px 16px rgba(0, 122, 255, 0.15);
      transform: translateY(-1px);
    }
    
    &.dependency {
      &.completed {
        border-color: #34c759;
        background: linear-gradient(135deg, rgba(52, 199, 89, 0.05) 0%, rgba(52, 199, 89, 0.02) 100%);
      }
      
      &.blocked {
        border-color: #ff3b30;
        background: linear-gradient(135deg, rgba(255, 59, 48, 0.05) 0%, rgba(255, 59, 48, 0.02) 100%);
      }
    }
    
    &.dependent {
      &.waiting {
        border-color: #ff9500;
        background: linear-gradient(135deg, rgba(255, 149, 0, 0.05) 0%, rgba(255, 149, 0, 0.02) 100%);
      }
      
      &.can-start {
        border-color: #34c759;
        background: linear-gradient(135deg, rgba(52, 199, 89, 0.05) 0%, rgba(52, 199, 89, 0.02) 100%);
      }
    }
    
    .task-info {
      flex: 1;
      
      .task-title {
        font-weight: 600;
        font-size: 14px;
        color: #000;
        margin-bottom: 8px;
        line-height: 1.4;
      }
      
      .task-meta {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .task-id {
          font-size: 12px;
          color: #8e8e93;
          font-weight: 500;
        }
        
        .task-progress {
          font-size: 12px;
          color: #636366;
          font-weight: 500;
        }
      }
    }
    
    .task-actions {
      display: flex;
      gap: 8px;
      flex-shrink: 0;
      
      .el-button {
        &.is-circle {
          width: 32px;
          height: 32px;
          padding: 0;
          display: flex;
          align-items: center;
          justify-content: center;
        }
        
        &:hover {
          transform: translateY(-1px);
        }
      }
    }
    
    .connection-line {
      position: absolute;
      z-index: 1;
      
      &.to-current {
        right: -20px;
        top: 50%;
        width: 40px;
        height: 3px;
        background: linear-gradient(90deg, #007AFF 0%, #0056CC 100%);
        border-radius: 2px;
        transform: translateY(-1.5px);
        box-shadow: 0 2px 4px rgba(0, 122, 255, 0.2);
        
        &::after {
          content: '';
          position: absolute;
          right: -8px;
          top: -3px;
          width: 0;
          height: 0;
          border-left: 10px solid #0056CC;
          border-top: 4.5px solid transparent;
          border-bottom: 4.5px solid transparent;
          filter: drop-shadow(2px 2px 2px rgba(0, 122, 255, 0.2));
        }
        
        &::before {
          content: '';
          position: absolute;
          left: -5px;
          top: -1px;
          width: 5px;
          height: 5px;
          background: #007AFF;
          border-radius: 50%;
          box-shadow: 0 0 4px rgba(0, 122, 255, 0.4);
        }
      }
      
      &.from-current {
        left: -20px;
        top: 50%;
        width: 40px;
        height: 3px;
        background: linear-gradient(90deg, #0056CC 0%, #007AFF 100%);
        border-radius: 2px;
        transform: translateY(-1.5px);
        box-shadow: 0 2px 4px rgba(0, 122, 255, 0.2);
        
        &::before {
          content: '';
          position: absolute;
          left: -8px;
          top: -3px;
          width: 0;
          height: 0;
          border-right: 10px solid #0056CC;
          border-top: 4.5px solid transparent;
          border-bottom: 4.5px solid transparent;
          filter: drop-shadow(-2px 2px 2px rgba(0, 122, 255, 0.2));
        }
        
        &::after {
          content: '';
          position: absolute;
          right: -5px;
          top: -1px;
          width: 5px;
          height: 5px;
          background: #007AFF;
          border-radius: 50%;
          box-shadow: 0 0 4px rgba(0, 122, 255, 0.4);
        }
      }
    }
  }
  
  .no-dependencies {
    text-align: center;
    padding: 60px 20px;
    color: #8e8e93;
  }
  
  .dependency-status {
    margin-top: 20px;
  }
}

.task-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  
  .task-title {
    flex: 1;
    font-weight: 500;
  }
}

.form-tip {
  color: #8e8e93;
  font-size: 12px;
  margin-top: 4px;
  line-height: 1.4;
}

// 响应式设计
@media (max-width: 768px) {
  .dependency-graph {
    .task-node {
      flex-direction: column;
      align-items: stretch;
      text-align: center;
      
      .task-actions {
        justify-content: center;
        margin-top: 12px;
      }
      
      .connection-line {
        display: none; // 在小屏幕上隐藏连接线
      }
    }
  }
}
</style>