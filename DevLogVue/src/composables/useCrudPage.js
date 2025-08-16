/**
 * CRUD页面通用组合式函数
 * 提供标准的增删改查功能模板
 */

import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { usePermissionCheck } from './usePermissionCheck'

export function useCrudPage(options = {}) {
  const {
    module = '', // 模块名称，用于权限检查
    apiMethods = {}, // API方法对象 { list, create, update, delete }
    defaultForm = {}, // 默认表单数据
    formRules = {}, // 表单验证规则
    pageSize = 10 // 默认分页大小
  } = options

  const { permissions } = usePermissionCheck()

  // 列表相关状态
  const loading = ref(false)
  const tableData = ref([])
  const currentPage = ref(1)
  const pageSizeRef = ref(pageSize)
  const total = ref(0)
  const searchForm = reactive({})

  // 对话框相关状态
  const dialogVisible = ref(false)
  const dialogType = ref('add') // 'add' | 'edit'
  const formRef = ref(null)
  const form = reactive({ ...defaultForm })

  /**
   * 获取列表数据
   * @param {Object} params 额外查询参数
   */
  const getList = async (params = {}) => {
    if (!apiMethods.list) {
      console.warn('未提供 list API 方法')
      return
    }

    loading.value = true
    try {
      const queryParams = {
        pageNum: currentPage.value,
        pageSize: pageSizeRef.value,
        ...searchForm,
        ...params
      }
      
      const res = await apiMethods.list(queryParams)
      
      // 适配不同的响应格式
      if (res.data) {
        if (res.data.records) {
          // 分页格式
          tableData.value = res.data.records
          total.value = res.data.total
        } else if (Array.isArray(res.data)) {
          // 数组格式
          tableData.value = res.data
          total.value = res.data.length
        } else {
          tableData.value = []
          total.value = 0
        }
      }
    } catch (error) {
      console.error('获取列表失败:', error)
      ElMessage.error('获取数据失败')
    } finally {
      loading.value = false
    }
  }

  /**
   * 处理搜索
   */
  const handleSearch = () => {
    currentPage.value = 1
    getList()
  }

  /**
   * 重置搜索
   */
  const handleResetSearch = () => {
    Object.keys(searchForm).forEach(key => {
      searchForm[key] = ''
    })
    handleSearch()
  }

  /**
   * 处理新增
   */
  const handleAdd = () => {
    if (module && !permissions.canCreate(module)) return
    
    dialogType.value = 'add'
    Object.assign(form, defaultForm)
    dialogVisible.value = true
  }

  /**
   * 处理编辑
   * @param {Object} row 行数据
   */
  const handleEdit = (row) => {
    if (module && !permissions.canUpdate(module)) return
    
    dialogType.value = 'edit'
    Object.assign(form, row)
    dialogVisible.value = true
  }

  /**
   * 处理删除
   * @param {Object} row 行数据
   * @param {string} idField ID字段名，默认为 'id'
   * @param {string} nameField 名称字段，用于确认提示
   */
  const handleDelete = async (row, idField = 'id', nameField = 'name') => {
    if (module && !permissions.canDelete(module)) return
    
    if (!apiMethods.delete) {
      console.warn('未提供 delete API 方法')
      return
    }

    const name = nameField ? row[nameField] : '该项'
    
    try {
      await ElMessageBox.confirm(
        `确认删除 "${name}" 吗？`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      await apiMethods.delete(row[idField])
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    }
  }

  /**
   * 处理批量删除
   * @param {Array} selection 选中的行
   * @param {string} idField ID字段名，默认为 'id'
   */
  const handleBatchDelete = async (selection, idField = 'id') => {
    if (module && !permissions.canBatchDelete(module)) return
    
    if (!selection || selection.length === 0) {
      ElMessage.warning('请选择要删除的项')
      return
    }

    if (!apiMethods.batchDelete) {
      console.warn('未提供 batchDelete API 方法')
      return
    }

    try {
      await ElMessageBox.confirm(
        `确认删除选中的 ${selection.length} 项吗？`,
        '批量删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      const ids = selection.map(item => item[idField])
      await apiMethods.batchDelete(ids)
      ElMessage.success('批量删除成功')
      getList()
    } catch (error) {
      if (error !== 'cancel') {
        console.error('批量删除失败:', error)
        ElMessage.error('批量删除失败')
      }
    }
  }

  /**
   * 处理表单提交
   */
  const handleSubmit = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()
      
      if (dialogType.value === 'add') {
        if (!apiMethods.create) {
          console.warn('未提供 create API 方法')
          return
        }
        await apiMethods.create(form)
        ElMessage.success('新增成功')
      } else {
        if (!apiMethods.update) {
          console.warn('未提供 update API 方法')
          return
        }
        await apiMethods.update(form.id, form)
        ElMessage.success('更新成功')
      }
      
      dialogVisible.value = false
      getList()
    } catch (error) {
      console.error('操作失败:', error)
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }

  /**
   * 处理对话框取消
   */
  const handleCancel = () => {
    dialogVisible.value = false
    if (formRef.value) {
      formRef.value.resetFields()
    }
  }

  /**
   * 处理分页大小变化
   */
  const handleSizeChange = (val) => {
    pageSizeRef.value = val
    currentPage.value = 1
    getList()
  }

  /**
   * 处理页码变化
   */
  const handleCurrentChange = (val) => {
    currentPage.value = val
    getList()
  }

  /**
   * 处理状态切换
   * @param {Object} row 行数据
   * @param {string} field 状态字段名
   * @param {string} idField ID字段名
   */
  const handleStatusChange = async (row, field = 'status', idField = 'id') => {
    if (!apiMethods.updateStatus) {
      console.warn('未提供 updateStatus API 方法')
      return
    }

    try {
      await apiMethods.updateStatus(row[idField], { [field]: row[field] })
      ElMessage.success('状态更新成功')
    } catch (error) {
      console.error('状态更新失败:', error)
      ElMessage.error('状态更新失败')
      // 恢复原状态
      row[field] = !row[field]
    }
  }

  return {
    // 状态
    loading,
    tableData,
    currentPage,
    pageSize: pageSizeRef,
    total,
    searchForm,
    dialogVisible,
    dialogType,
    formRef,
    form,
    
    // 方法
    getList,
    handleSearch,
    handleResetSearch,
    handleAdd,
    handleEdit,
    handleDelete,
    handleBatchDelete,
    handleSubmit,
    handleCancel,
    handleSizeChange,
    handleCurrentChange,
    handleStatusChange,
    
    // 权限检查
    permissions
  }
}