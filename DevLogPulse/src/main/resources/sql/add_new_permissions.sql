-- 新增权限配置脚本
-- 用于配置新添加的接口权限

-- 检查并添加项目管理权限（避免重复）
INSERT IGNORE INTO t_permission (name, code, url, method, description, create_time) VALUES
('查看项目列表', 'project:list', '/projects', 'GET', '查看项目列表（分页）', NOW()),
('查看所有项目', 'project:list:all', '/projects/all', 'GET', '获取所有项目（不分页）', NOW()),
('查看项目详情', 'project:view', '/projects/{id}', 'GET', '查看项目详细信息', NOW()),
('创建项目', 'project:create', '/projects', 'POST', '创建新项目', NOW()),
('更新项目', 'project:update', '/projects/{id}', 'PUT', '更新项目信息', NOW()),
('删除项目', 'project:delete', '/projects/{id}', 'DELETE', '删除项目', NOW()),
('查看项目统计', 'project:stats', '/projects/{id}/stats', 'GET', '查看项目任务统计信息', NOW()),
('查看项目进度趋势', 'project:trend', '/projects/{id}/progress-trend', 'GET', '查看项目进度趋势', NOW()),
('刷新项目进度', 'project:refresh', '/projects/{id}/refresh-progress', 'POST', '手动刷新项目进度', NOW());

-- 2. 任务管理新增权限
INSERT IGNORE INTO t_permission (name, code, url, method, description, create_time) VALUES
('获取所有任务', 'task:list:all', '/tasks/all', 'GET', '获取所有任务（不分页）', NOW()),
('批量重新计算任务进度', 'task:batch:recalculate', '/tasks/batch/recalculate-progress', 'POST', '批量重新计算任务进度', NOW()),
('重新计算单个任务进度', 'task:recalculate', '/tasks/{id}/recalculate-progress', 'POST', '重新计算单个任务进度', NOW());

-- 3. 通知管理权限补充  
INSERT IGNORE INTO t_permission (name, code, url, method, description, create_time) VALUES
('发送通知', 'notification:send', '/notifications/send', 'POST', '发送通知权限', NOW()),
('批量发送通知', 'notification:batch:send', '/notifications/batch-send', 'POST', '批量发送通知权限', NOW());

-- 4. 工作日志权限补充（如果需要）
INSERT IGNORE INTO t_permission (name, code, url, method, description, create_time) VALUES
('批量操作工作日志', 'worklog:batch', '/worklogs/batch/*', 'POST', '工作日志批量操作权限', NOW());

-- 5. 为管理员角色添加新权限（假设管理员角色ID为1）
INSERT IGNORE INTO t_role_permission (role_id, permission_id, create_time)
SELECT 1, p.id, NOW() 
FROM t_permission p 
WHERE p.code IN (
    'project:list', 'project:list:all', 'project:view', 'project:create', 'project:update', 'project:delete',
    'project:stats', 'project:trend', 'project:refresh',
    'task:list:all', 'task:batch:recalculate', 'task:recalculate',
    'notification:send', 'notification:batch:send', 'worklog:batch'
);

-- 6. 为组长角色添加相关权限（假设组长角色ID为2）
INSERT IGNORE INTO t_role_permission (role_id, permission_id, create_time)
SELECT 2, p.id, NOW() 
FROM t_permission p 
WHERE p.code IN (
    'project:list', 'project:list:all', 'project:view', 'project:stats', 'project:trend',
    'task:list:all', 'task:recalculate',
    'notification:send'
);

-- 验证权限是否添加成功
SELECT 
    p.name as permission_name,
    p.code as permission_code,
    p.url,
    p.method,
    r.name as role_name
FROM t_permission p
LEFT JOIN t_role_permission rp ON p.id = rp.permission_id
LEFT JOIN t_role r ON rp.role_id = r.id
WHERE p.create_time >= CURDATE()
ORDER BY p.id, r.id;