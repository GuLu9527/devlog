/**
 * 权限调试工具
 * 用于开发和调试权限相关问题
 */

export class PermissionDebugger {
  constructor() {
    this.isEnabled = process.env.NODE_ENV === 'development'
    this.logs = []
  }

  /**
   * 记录权限检查日志
   * @param {string} type 日志类型
   * @param {string} message 日志消息
   * @param {Object} data 额外数据
   */
  log(type, message, data = {}) {
    if (!this.isEnabled) return

    const logEntry = {
      timestamp: new Date().toISOString(),
      type,
      message,
      data
    }

    this.logs.push(logEntry)
    console.debug(`[权限调试] ${type}: ${message}`, data)

    // 保持日志数量在合理范围内
    if (this.logs.length > 100) {
      this.logs = this.logs.slice(-50)
    }
  }

  /**
   * 分析菜单权限配置
   * @param {Array} menuConfig 菜单配置
   * @param {Array} userPermissions 用户权限
   * @param {string} userRole 用户角色
   */
  analyzeMenuPermissions(menuConfig, userPermissions, userRole) {
    if (!this.isEnabled) return

    this.log('MENU_ANALYSIS', '开始分析菜单权限配置', {
      menuCount: menuConfig.length,
      userPermissions,
      userRole
    })

    const analysis = {
      totalMenus: 0,
      accessibleMenus: 0,
      deniedMenus: 0,
      details: []
    }

    const analyzeMenu = (menu, level = 0) => {
      analysis.totalMenus++
      
      const hasAccess = this.checkMenuAccess(menu, userPermissions)
      if (hasAccess) {
        analysis.accessibleMenus++
      } else {
        analysis.deniedMenus++
      }

      const detail = {
        level,
        key: menu.key,
        title: menu.title,
        permission: menu.permission,
        requireAllPermissions: menu.requireAllPermissions,
        adminOnly: menu.adminOnly,
        hasAccess,
        reason: hasAccess ? '有权限' : this.getAccessDeniedReason(menu, userPermissions, userRole)
      }

      analysis.details.push(detail)

      if (menu.children) {
        menu.children.forEach(child => analyzeMenu(child, level + 1))
      }
    }

    menuConfig.forEach(menu => analyzeMenu(menu))

    this.log('MENU_ANALYSIS_RESULT', '菜单权限分析完成', analysis)
    return analysis
  }

  /**
   * 检查菜单访问权限
   * @param {Object} menu 菜单项
   * @param {Array} userPermissions 用户权限
   * @returns {boolean} 是否有访问权限
   */
  checkMenuAccess(menu, userPermissions) {
    if (!menu.permission) return true

    if (Array.isArray(menu.permission)) {
      if (menu.requireAllPermissions) {
        return menu.permission.every(p => userPermissions.includes(p))
      } else {
        return menu.permission.some(p => userPermissions.includes(p))
      }
    }

    return userPermissions.includes(menu.permission)
  }

  /**
   * 获取访问被拒绝的原因
   * @param {Object} menu 菜单项
   * @param {Array} userPermissions 用户权限
   * @param {string} userRole 用户角色
   * @returns {string} 拒绝原因
   */
  getAccessDeniedReason(menu, userPermissions, userRole) {
    if (menu.adminOnly && userRole !== 'ADMIN' && userRole !== '管理员') {
      return '仅管理员可访问'
    }

    if (!menu.permission) {
      return '无权限要求但被拒绝（可能是其他原因）'
    }

    if (Array.isArray(menu.permission)) {
      const hasAny = menu.permission.some(p => userPermissions.includes(p))
      const hasAll = menu.permission.every(p => userPermissions.includes(p))

      if (menu.requireAllPermissions) {
        if (!hasAll) {
          const missing = menu.permission.filter(p => !userPermissions.includes(p))
          return `缺少必需权限: ${missing.join(', ')}`
        }
      } else {
        if (!hasAny) {
          return `缺少任一权限: ${menu.permission.join(', ')}`
        }
      }
    } else {
      if (!userPermissions.includes(menu.permission)) {
        return `缺少权限: ${menu.permission}`
      }
    }

    return '未知原因'
  }

  /**
   * 验证MEMBER角色权限配置
   * @param {Array} userPermissions MEMBER用户的权限列表
   */
  validateMemberPermissions(userPermissions) {
    if (!this.isEnabled) return

    this.log('MEMBER_VALIDATION', '开始验证MEMBER角色权限', { userPermissions })

    const expectedPermissions = [
      'project:list',
      'task:list',
      'worklog:list',
      'notification:list',
      'statistics:personal'
    ]

    const unexpectedPermissions = [
      'user:list', 'user:create', 'user:update', 'user:delete',
      'role:list', 'role:create', 'role:update', 'role:delete',
      'permission:list', 'permission:create', 'permission:update', 'permission:delete',
      'department:list', 'department:create', 'department:update', 'department:delete',
      'group:list', 'group:create', 'group:update', 'group:delete'
    ]

    const validation = {
      hasExpected: expectedPermissions.filter(p => userPermissions.includes(p)),
      missingExpected: expectedPermissions.filter(p => !userPermissions.includes(p)),
      hasUnexpected: unexpectedPermissions.filter(p => userPermissions.includes(p)),
      isValid: true,
      issues: []
    }

    if (validation.missingExpected.length > 0) {
      validation.isValid = false
      validation.issues.push(`缺少基本权限: ${validation.missingExpected.join(', ')}`)
    }

    if (validation.hasUnexpected.length > 0) {
      validation.isValid = false
      validation.issues.push(`意外的管理权限: ${validation.hasUnexpected.join(', ')}`)
    }

    this.log('MEMBER_VALIDATION_RESULT', 'MEMBER角色权限验证完成', validation)
    return validation
  }

  /**
   * 生成权限报告
   * @returns {Object} 权限报告
   */
  generateReport() {
    if (!this.isEnabled) return null

    const report = {
      timestamp: new Date().toISOString(),
      logCount: this.logs.length,
      logs: [...this.logs],
      summary: {
        errors: this.logs.filter(log => log.type.includes('ERROR')).length,
        warnings: this.logs.filter(log => log.type.includes('WARNING')).length,
        info: this.logs.filter(log => log.type.includes('INFO')).length
      }
    }

    return report
  }

  /**
   * 清空日志
   */
  clearLogs() {
    this.logs = []
    if (this.isEnabled) {
      console.debug('[权限调试] 日志已清空')
    }
  }

  /**
   * 导出调试信息到控制台
   */
  exportToConsole() {
    if (!this.isEnabled) return

    console.group('权限系统调试报告')
    console.table(this.logs.map(log => ({
      时间: log.timestamp,
      类型: log.type,
      消息: log.message
    })))
    console.groupEnd()
  }
}

// 创建全局实例
export const permissionDebugger = new PermissionDebugger()

// 添加到window对象以便在控制台中使用
if (typeof window !== 'undefined' && process.env.NODE_ENV === 'development') {
  window.permissionDebugger = permissionDebugger
}