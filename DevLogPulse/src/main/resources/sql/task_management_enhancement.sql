-- 任务依赖关系表
CREATE TABLE IF NOT EXISTS `t_task_dependency` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID（被依赖的任务）',
    `dependent_task_id` BIGINT NOT NULL COMMENT '依赖任务ID（依赖的任务）',
    `type` VARCHAR(20) NOT NULL DEFAULT 'DEPENDS_ON' COMMENT '依赖类型：BLOCKS-阻塞关系，DEPENDS_ON-依赖关系',
    `description` VARCHAR(500) COMMENT '依赖描述',
    `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_dependency` (`task_id`, `dependent_task_id`, `is_deleted`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_dependent_task_id` (`dependent_task_id`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务依赖关系表';

-- 任务模板表
CREATE TABLE IF NOT EXISTS `t_task_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '模板名称',
    `description` TEXT COMMENT '模板描述',
    `category` VARCHAR(50) NOT NULL COMMENT '模板分类',
    `tags` JSON COMMENT '模板标签，JSON数组格式',
    `tasks` JSON COMMENT '任务配置，JSON格式',
    `tasks_count` INT NOT NULL DEFAULT 0 COMMENT '任务数量',
    `estimated_hours` DECIMAL(8,2) NOT NULL DEFAULT 0.00 COMMENT '预计总工时',
    `usage_count` INT NOT NULL DEFAULT 0 COMMENT '使用次数',
    `last_used` DATETIME COMMENT '最后使用时间',
    `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_category` (`category`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_usage_count` (`usage_count` DESC),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务模板表';

-- 工时记录表
CREATE TABLE IF NOT EXISTS `t_work_hour` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `project_id` BIGINT NOT NULL COMMENT '项目ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `work_date` DATE NOT NULL COMMENT '工作日期',
    `hours` DECIMAL(4,2) NOT NULL COMMENT '工时数',
    `type` VARCHAR(20) NOT NULL DEFAULT 'DEVELOPMENT' COMMENT '工时类型：DEVELOPMENT-开发，TESTING-测试，MEETING-会议，RESEARCH-调研，OTHER-其他',
    `description` VARCHAR(500) COMMENT '工时描述',
    `start_time` DATETIME COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已通过，REJECTED-已驳回',
    `reviewer_id` BIGINT COMMENT '审核人ID',
    `review_time` DATETIME COMMENT '审核时间',
    `review_note` VARCHAR(500) COMMENT '审核备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_task_date` (`user_id`, `task_id`, `work_date`, `is_deleted`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_work_date` (`work_date`),
    KEY `idx_status` (`status`),
    KEY `idx_reviewer_id` (`reviewer_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工时记录表';

-- 添加新权限
INSERT IGNORE INTO `t_permission` (`name`, `code`, `description`, `create_time`) VALUES
('任务依赖查看', 'task:dependency:view', '查看任务依赖关系权限', NOW()),
('任务依赖创建', 'task:dependency:create', '创建任务依赖关系权限', NOW()),
('任务依赖删除', 'task:dependency:delete', '删除任务依赖关系权限', NOW()),
('任务模板列表', 'task:template:list', '查看任务模板列表权限', NOW()),
('任务模板查看', 'task:template:view', '查看任务模板详情权限', NOW()),
('任务模板创建', 'task:template:create', '创建任务模板权限', NOW()),
('任务模板更新', 'task:template:update', '更新任务模板权限', NOW()),
('任务模板删除', 'task:template:delete', '删除任务模板权限', NOW()),
('任务模板应用', 'task:template:apply', '应用任务模板权限', NOW()),
('工时记录查看', 'work:hour:view', '查看工时记录权限', NOW()),
('工时记录创建', 'work:hour:create', '创建工时记录权限', NOW()),
('工时记录更新', 'work:hour:update', '更新工时记录权限', NOW()),
('工时记录删除', 'work:hour:delete', '删除工时记录权限', NOW()),
('工时记录审核', 'work:hour:review', '审核工时记录权限', NOW());

-- 为管理员角色添加所有新权限
INSERT IGNORE INTO `t_role_permission` (`role_id`, `permission_id`)
SELECT 1, p.id FROM `t_permission` p WHERE p.code IN (
    'task:dependency:view', 'task:dependency:create', 'task:dependency:delete',
    'task:template:list', 'task:template:view', 'task:template:create', 
    'task:template:update', 'task:template:delete', 'task:template:apply',
    'work:hour:view', 'work:hour:create', 'work:hour:update', 
    'work:hour:delete', 'work:hour:review'
);

-- 为组长角色添加相关权限
INSERT IGNORE INTO `t_role_permission` (`role_id`, `permission_id`)
SELECT 2, p.id FROM `t_permission` p WHERE p.code IN (
    'task:dependency:view', 'task:dependency:create', 'task:dependency:delete',
    'task:template:list', 'task:template:view', 'task:template:apply',
    'work:hour:view', 'work:hour:create', 'work:hour:update', 'work:hour:review'
);

-- 为普通用户角色添加基础权限
INSERT IGNORE INTO `t_role_permission` (`role_id`, `permission_id`)
SELECT 3, p.id FROM `t_permission` p WHERE p.code IN (
    'task:dependency:view', 'task:template:list', 'task:template:view',
    'work:hour:view', 'work:hour:create', 'work:hour:update'
);