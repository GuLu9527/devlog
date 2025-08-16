/*
 Navicat Premium Dump SQL

 Source Server         : giao
 Source Server Type    : MySQL
 Source Server Version : 80030 (8.0.30)
 Source Host           : localhost:3306
 Source Schema         : devlog

 Target Server Type    : MySQL
 Target Server Version : 80030 (8.0.30)
 File Encoding         : 65001

 Date: 16/08/2025 15:12:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_department
-- ----------------------------
DROP TABLE IF EXISTS `t_department`;
CREATE TABLE `t_department`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父部门ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(1启用 0禁用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `parent_id`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `t_department_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `t_department` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_department
-- ----------------------------
INSERT INTO `t_department` VALUES (1, '技术部', NULL, 1, '2025-05-22 16:05:47', '2025-05-24 17:39:41');
INSERT INTO `t_department` VALUES (2, '产品部', NULL, 1, '2025-05-22 16:05:47', '2025-05-22 16:05:47');
INSERT INTO `t_department` VALUES (3, '前端开发组', 1, 1, '2025-05-22 16:05:47', '2025-05-22 16:05:47');
INSERT INTO `t_department` VALUES (4, '后端开发组', 1, 1, '2025-05-22 16:05:47', '2025-05-22 16:05:47');
INSERT INTO `t_department` VALUES (5, '测试组', 1, 1, '2025-05-22 16:05:47', '2025-05-22 16:05:47');
INSERT INTO `t_department` VALUES (6, '产品设计组', 2, 1, '2025-05-22 16:05:47', '2025-05-22 16:05:47');
INSERT INTO `t_department` VALUES (8, '测试', NULL, 1, '2025-05-24 14:37:21', '2025-05-24 17:39:39');

-- ----------------------------
-- Table structure for t_group
-- ----------------------------
DROP TABLE IF EXISTS `t_group`;
CREATE TABLE `t_group`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '小组ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '小组名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '小组描述',
  `department_id` bigint NOT NULL COMMENT '所属部门ID',
  `leader_id` bigint NULL DEFAULT NULL COMMENT '组长用户ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(1启用 0禁用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_group_name_department`(`name` ASC, `department_id` ASC) USING BTREE,
  INDEX `idx_department`(`department_id` ASC) USING BTREE,
  INDEX `idx_leader`(`leader_id` ASC) USING BTREE,
  CONSTRAINT `t_group_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `t_department` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `t_group_ibfk_2` FOREIGN KEY (`leader_id`) REFERENCES `t_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '小组表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_group
-- ----------------------------
INSERT INTO `t_group` VALUES (1, '前端开发欻欻欻小组', '前端开发大大滴', 3, 10, 1, '2025-05-25 14:55:03', '2025-05-25 15:13:18');
INSERT INTO `t_group` VALUES (2, '后端开发小组', '进行大部分的后端开发', 4, 10, 1, '2025-05-25 15:04:39', '2025-05-25 15:04:39');
INSERT INTO `t_group` VALUES (3, '测试', '测试', 8, 3, 1, '2025-05-25 15:13:55', '2025-05-25 15:13:55');
INSERT INTO `t_group` VALUES (4, '地推小组', '负责地推', 8, 10, 1, '2025-05-25 15:20:51', '2025-05-25 15:20:51');
INSERT INTO `t_group` VALUES (5, '实习生小组', '实习', 3, 2, 1, '2025-05-25 15:21:46', '2025-05-25 15:21:46');

-- ----------------------------
-- Table structure for t_log_template
-- ----------------------------
DROP TABLE IF EXISTS `t_log_template`;
CREATE TABLE `t_log_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板内容（JSON格式）',
  `department_id` bigint NULL DEFAULT NULL COMMENT '适用部门ID',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认模板',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `department_id`(`department_id` ASC) USING BTREE,
  CONSTRAINT `t_log_template_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `t_department` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '日志模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_log_template
-- ----------------------------
INSERT INTO `t_log_template` VALUES (1, '开发日志模板', '{\"work\": \"今日完成的工作\", \"problem\": \"遇到的问题\", \"plan\": \"明日计划\"}', 1, 1, '2025-05-22 16:05:47', '2025-05-22 16:05:47');
INSERT INTO `t_log_template` VALUES (2, '测试日志模板', '{\"testCases\": \"执行的测试用例\", \"bugs\": \"发现的缺陷\", \"notes\": \"其他说明\"}', 5, 1, '2025-05-22 16:05:47', '2025-05-22 16:05:47');

-- ----------------------------
-- Table structure for t_notification
-- ----------------------------
DROP TABLE IF EXISTS `t_notification`;
CREATE TABLE `t_notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '通知内容',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知类型(TASK_ASSIGN,TASK_DEADLINE,WORKLOG_REVIEW,PROJECT_UPDATE,SYSTEM_MESSAGE)',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '普通' COMMENT '通知级别(普通,重要,紧急)',
  `sender_id` bigint NULL DEFAULT NULL COMMENT '发送人ID(系统消息为NULL)',
  `receiver_id` bigint NOT NULL COMMENT '接收人ID',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID(任务ID、日志ID等)',
  `related_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联业务类型(task、worklog、project等)',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读(0未读 1已读)',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_receiver_read`(`receiver_id` ASC, `is_read` ASC) USING BTREE,
  INDEX `idx_related`(`related_type` ASC, `related_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `fk_notification_sender`(`sender_id` ASC) USING BTREE,
  INDEX `idx_type_level`(`type` ASC, `level` ASC) USING BTREE,
  CONSTRAINT `fk_notification_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_notification_sender` FOREIGN KEY (`sender_id`) REFERENCES `t_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_notification
-- ----------------------------
INSERT INTO `t_notification` VALUES (1, '测试通知', '这是一条测试通知消息，用于验证通知系统是否正常工作。', 'SYSTEM_MESSAGE', '普通', NULL, 1, NULL, NULL, 1, 1, '2025-07-29 16:23:31', '2025-07-29 16:23:27', '2025-07-29 16:23:34');
INSERT INTO `t_notification` VALUES (2, '测试通知', '这是一条测试通知消息，用于验证通知系统是否正常工作。', 'SYSTEM_MESSAGE', '普通', NULL, 1, NULL, NULL, 0, 1, NULL, '2025-07-29 16:24:24', '2025-07-29 16:25:57');
INSERT INTO `t_notification` VALUES (3, '测试通知', '这是一条测试通知消息，用于验证通知系统是否正常工作。', 'SYSTEM_MESSAGE', '普通', NULL, 3, NULL, NULL, 0, 0, NULL, '2025-07-29 16:24:24', '2025-07-29 16:24:24');
INSERT INTO `t_notification` VALUES (4, '测试通知', '这是一条测试通知消息，用于验证通知系统是否正常工作。', 'SYSTEM_MESSAGE', '普通', NULL, 2, NULL, NULL, 0, 0, NULL, '2025-07-29 16:24:24', '2025-07-29 16:24:24');
INSERT INTO `t_notification` VALUES (5, '任务即将到期：扫地', '您的任务即将到期：\n任务标题：扫地\n截止时间：2025-07-30T16:00\n请尽快完成。', 'TASK_DEADLINE', '普通', NULL, 1, 15, 'task', 0, 1, NULL, '2025-07-29 17:00:00', '2025-07-29 17:07:37');
INSERT INTO `t_notification` VALUES (6, '你们都干啥呢？', '我看看你', 'PROJECT_UPDATE', '紧急', NULL, 12, NULL, 'worklog', 0, 1, NULL, '2025-07-29 17:34:38', '2025-08-05 20:28:14');
INSERT INTO `t_notification` VALUES (7, '213123', '21312312', 'SYSTEM_MESSAGE', '普通', NULL, 12, NULL, NULL, 0, 1, NULL, '2025-07-29 17:54:00', '2025-08-03 21:26:45');
INSERT INTO `t_notification` VALUES (8, '213123', '21312312', 'SYSTEM_MESSAGE', '普通', NULL, 11, NULL, NULL, 0, 0, NULL, '2025-07-29 17:54:00', '2025-07-29 17:54:00');
INSERT INTO `t_notification` VALUES (9, '213123', '21312312', 'SYSTEM_MESSAGE', '普通', NULL, 10, NULL, NULL, 0, 0, NULL, '2025-07-29 17:54:00', '2025-07-29 17:54:00');
INSERT INTO `t_notification` VALUES (10, '213123', '21312312', 'SYSTEM_MESSAGE', '普通', NULL, 1, NULL, NULL, 0, 1, NULL, '2025-07-29 17:54:00', '2025-07-29 17:54:07');
INSERT INTO `t_notification` VALUES (11, '213123', '21312312', 'SYSTEM_MESSAGE', '普通', NULL, 2, NULL, NULL, 0, 0, NULL, '2025-07-29 17:54:00', '2025-07-29 17:54:00');
INSERT INTO `t_notification` VALUES (12, '213123', '21312312', 'SYSTEM_MESSAGE', '普通', NULL, 3, NULL, NULL, 0, 0, NULL, '2025-07-29 17:54:00', '2025-07-29 17:54:00');
INSERT INTO `t_notification` VALUES (13, '213123', '21312312', 'SYSTEM_MESSAGE', '普通', NULL, 4, NULL, NULL, 0, 0, NULL, '2025-07-29 17:54:00', '2025-07-29 17:54:00');
INSERT INTO `t_notification` VALUES (15, '213123', '21312312', 'SYSTEM_MESSAGE', '普通', NULL, 6, NULL, NULL, 0, 0, NULL, '2025-07-29 17:54:00', '2025-07-29 17:54:00');
INSERT INTO `t_notification` VALUES (16, '213123', '21312312', 'SYSTEM_MESSAGE', '普通', NULL, 7, NULL, NULL, 0, 0, NULL, '2025-07-29 17:54:00', '2025-07-29 17:54:00');
INSERT INTO `t_notification` VALUES (18, '任务即将到期：扫地', '您的任务即将到期：\n任务标题：扫地\n截止时间：2025-07-30T16:00\n请尽快完成。', 'TASK_DEADLINE', '普通', NULL, 1, 15, 'task', 1, 1, '2025-07-29 18:08:20', '2025-07-29 18:00:00', '2025-07-29 18:08:41');
INSERT INTO `t_notification` VALUES (19, 'ceshi ', '213213', 'SYSTEM_MESSAGE', '普通', NULL, 14, NULL, NULL, 0, 0, NULL, '2025-08-05 19:01:09', '2025-08-05 19:01:09');
INSERT INTO `t_notification` VALUES (20, 'ceshi ', '213213', 'SYSTEM_MESSAGE', '普通', NULL, 13, NULL, NULL, 0, 0, NULL, '2025-08-05 19:01:09', '2025-08-05 19:01:09');
INSERT INTO `t_notification` VALUES (21, 'ceshi ', '213213', 'SYSTEM_MESSAGE', '普通', NULL, 12, NULL, NULL, 0, 1, NULL, '2025-08-05 19:01:09', '2025-08-05 20:28:12');
INSERT INTO `t_notification` VALUES (22, 'ceshi ', '213213', 'SYSTEM_MESSAGE', '普通', NULL, 11, NULL, NULL, 0, 0, NULL, '2025-08-05 19:01:09', '2025-08-05 19:01:09');
INSERT INTO `t_notification` VALUES (23, 'ceshi ', '213213', 'SYSTEM_MESSAGE', '普通', NULL, 10, NULL, NULL, 0, 0, NULL, '2025-08-05 19:01:09', '2025-08-05 19:01:09');
INSERT INTO `t_notification` VALUES (24, 'ceshi ', '213213', 'SYSTEM_MESSAGE', '普通', NULL, 1, NULL, NULL, 1, 1, '2025-08-05 19:01:14', '2025-08-05 19:01:09', '2025-08-05 19:01:17');
INSERT INTO `t_notification` VALUES (25, 'ceshi ', '213213', 'SYSTEM_MESSAGE', '普通', NULL, 2, NULL, NULL, 0, 0, NULL, '2025-08-05 19:01:09', '2025-08-05 19:01:09');
INSERT INTO `t_notification` VALUES (26, 'ceshi ', '213213', 'SYSTEM_MESSAGE', '普通', NULL, 3, NULL, NULL, 0, 0, NULL, '2025-08-05 19:01:09', '2025-08-05 19:01:09');
INSERT INTO `t_notification` VALUES (27, 'ceshi ', '213213', 'SYSTEM_MESSAGE', '普通', NULL, 4, NULL, NULL, 0, 0, NULL, '2025-08-05 19:01:09', '2025-08-05 19:01:09');
INSERT INTO `t_notification` VALUES (29, 'ceshi ', '213213', 'SYSTEM_MESSAGE', '普通', NULL, 6, NULL, NULL, 0, 0, NULL, '2025-08-05 19:01:09', '2025-08-05 19:01:09');
INSERT INTO `t_notification` VALUES (30, 'ceshi ', '213213', 'SYSTEM_MESSAGE', '普通', NULL, 7, NULL, NULL, 0, 0, NULL, '2025-08-05 19:01:09', '2025-08-05 19:01:09');

-- ----------------------------
-- Table structure for t_notification_setting
-- ----------------------------
DROP TABLE IF EXISTS `t_notification_setting`;
CREATE TABLE `t_notification_setting`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设置ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `task_assign` tinyint NULL DEFAULT 1 COMMENT '任务分配通知(0关闭 1开启)',
  `task_deadline` tinyint NULL DEFAULT 1 COMMENT '任务截止提醒(0关闭 1开启)',
  `worklog_review` tinyint NULL DEFAULT 1 COMMENT '日志审核通知(0关闭 1开启)',
  `project_update` tinyint NULL DEFAULT 1 COMMENT '项目更新通知(0关闭 1开启)',
  `system_message` tinyint NULL DEFAULT 1 COMMENT '系统消息通知(0关闭 1开启)',
  `email_notify` tinyint NULL DEFAULT 0 COMMENT '邮件通知(0关闭 1开启)',
  `deadline_hours` int NULL DEFAULT 24 COMMENT '截止提醒提前小时数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_setting_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_notification_setting
-- ----------------------------
INSERT INTO `t_notification_setting` VALUES (1, 1, 1, 1, 1, 1, 1, 0, 24, '2025-07-28 21:12:42', '2025-07-28 21:12:42');
INSERT INTO `t_notification_setting` VALUES (2, 2, 1, 1, 1, 1, 1, 0, 24, '2025-07-28 21:12:42', '2025-07-28 21:12:42');
INSERT INTO `t_notification_setting` VALUES (3, 3, 1, 1, 1, 1, 1, 0, 24, '2025-07-28 21:12:42', '2025-07-28 21:12:42');
INSERT INTO `t_notification_setting` VALUES (4, 4, 1, 1, 1, 1, 1, 0, 24, '2025-07-28 21:12:42', '2025-07-28 21:12:42');
INSERT INTO `t_notification_setting` VALUES (6, 6, 1, 1, 1, 1, 1, 0, 24, '2025-07-28 21:12:42', '2025-07-28 21:12:42');
INSERT INTO `t_notification_setting` VALUES (7, 7, 1, 1, 1, 1, 1, 0, 24, '2025-07-28 21:12:42', '2025-07-28 21:12:42');
INSERT INTO `t_notification_setting` VALUES (9, 10, 1, 1, 1, 1, 1, 0, 24, '2025-07-28 21:12:42', '2025-07-28 21:12:42');
INSERT INTO `t_notification_setting` VALUES (10, 11, 1, 1, 1, 1, 1, 0, 24, '2025-07-28 21:12:42', '2025-07-28 21:12:42');
INSERT INTO `t_notification_setting` VALUES (11, 12, 1, 1, 1, 1, 1, 0, 24, '2025-07-28 21:12:42', '2025-07-28 21:12:42');

-- ----------------------------
-- Table structure for t_notification_template
-- ----------------------------
DROP TABLE IF EXISTS `t_notification_template`;
CREATE TABLE `t_notification_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知类型(TASK_ASSIGN,TASK_DEADLINE,WORKLOG_REVIEW,PROJECT_UPDATE,SYSTEM_MESSAGE)',
  `title_template` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题模板',
  `content_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容模板',
  `variables` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板变量说明(JSON格式)',
  `is_active` tinyint NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_notification_template
-- ----------------------------
INSERT INTO `t_notification_template` VALUES (1, 'TASK_ASSIGN', '新任务分配：{taskTitle}', '您有一个新的任务被分配：\n任务标题：{taskTitle}\n分配人：{assignerName}\n截止时间：{deadline}\n请及时处理。', '{\"taskTitle\":\"任务标题\",\"assignerName\":\"分配人姓名\",\"deadline\":\"截止时间\"}', 1, '2025-07-29 16:22:10', '2025-07-29 16:22:10');
INSERT INTO `t_notification_template` VALUES (2, 'TASK_DEADLINE', '任务即将到期：{taskTitle}', '您的任务即将到期：\n任务标题：{taskTitle}\n截止时间：{deadline}\n请尽快完成。', '{\"taskTitle\":\"任务标题\",\"deadline\":\"截止时间\"}', 1, '2025-07-29 16:22:10', '2025-07-29 16:22:10');
INSERT INTO `t_notification_template` VALUES (3, 'WORKLOG_REVIEW', '工作日志待审核', '您有新的工作日志需要审核：\n提交人：{submitterName}\n日志日期：{logDate}\n工作内容：{content}', '{\"submitterName\":\"提交人姓名\",\"logDate\":\"日志日期\",\"content\":\"工作内容\"}', 1, '2025-07-29 16:22:10', '2025-07-29 16:22:10');
INSERT INTO `t_notification_template` VALUES (4, 'PROJECT_UPDATE', '项目状态更新：{projectName}', '项目状态已更新：\n项目名称：{projectName}\n新状态：{newStatus}\n更新人：{updaterName}', '{\"projectName\":\"项目名称\",\"newStatus\":\"新状态\",\"updaterName\":\"更新人姓名\"}', 1, '2025-07-29 16:22:10', '2025-07-29 16:22:10');
INSERT INTO `t_notification_template` VALUES (5, 'SYSTEM_MESSAGE', '系统消息：{title}', '{content}', '{\"title\":\"消息标题\",\"content\":\"消息内容\"}', 1, '2025-07-29 16:22:10', '2025-07-29 16:22:10');

-- ----------------------------
-- Table structure for t_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `t_operation_log`;
CREATE TABLE `t_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NOT NULL COMMENT '操作人ID',
  `group_id` bigint NOT NULL COMMENT '操作所属组ID',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作模块',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作描述',
  `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法',
  `params` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求参数',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `status` tinyint NULL DEFAULT NULL COMMENT '操作状态(1成功 0失败)',
  `error_msg` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误消息',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  `execute_time` bigint NULL DEFAULT NULL COMMENT '执行时长(毫秒)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_group_user`(`group_id` ASC, `user_id` ASC) USING BTREE,
  CONSTRAINT `t_operation_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_operation_log_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `t_department` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_operation_log
-- ----------------------------
INSERT INTO `t_operation_log` VALUES (1, 1, 1, '用户管理', '创建', '创建了用户user1', 'POST', '{\"username\":\"user1\",\"role_id\":3}', '127.0.0.1', 1, NULL, '2025-05-21 10:00:00', 200);
INSERT INTO `t_operation_log` VALUES (2, 2, 3, '任务管理', '分配', '将任务\"首页布局设计\"分配给user1', 'PUT', '{\"task_id\":1,\"assignee_id\":4}', '127.0.0.1', 1, NULL, '2025-05-21 11:30:00', 150);
INSERT INTO `t_operation_log` VALUES (3, 4, 3, '日志管理', '提交', '提交了2025-05-20的工作日志', 'POST', '{\"task_id\":1,\"content\":\"...\"}', '127.0.0.1', 1, NULL, '2025-05-21 17:00:00', 300);

-- ----------------------------
-- Table structure for t_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限编码',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接口URL',
  `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 155 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_permission
-- ----------------------------
INSERT INTO `t_permission` VALUES (1, '查看用户列表', 'user:list', '/user/list', 'GET', '查看用户列表', '2025-05-21 19:52:44');
INSERT INTO `t_permission` VALUES (2, '创建用户', 'user:create', '/user', 'POST', '创建新用户', '2025-05-21 19:52:44');
INSERT INTO `t_permission` VALUES (3, '编辑用户', 'user:update', '/user/*', 'PUT', '编辑用户信息', '2025-05-21 19:52:44');
INSERT INTO `t_permission` VALUES (4, '删除用户', 'user:delete', '/user/*', 'DELETE', '删除用户', '2025-05-21 19:52:44');
INSERT INTO `t_permission` VALUES (5, '重置密码', 'user:reset', '/user/*/reset', 'POST', '重置用户密码', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (10, '查看角色列表', 'role:list', '/role/list', 'GET', '查看角色列表', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (11, '创建角色', 'role:create', '/role', 'POST', '创建新角色', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (12, '编辑角色', 'role:update', '/role/*', 'PUT', '编辑角色信息', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (13, '删除角色', 'role:delete', '/role/*', 'DELETE', '删除角色', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (14, '分配权限', 'role:permission', '/role/*/permission', 'POST', '为角色分配权限', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (20, '查看权限列表', 'permission:list', '/permission/list', 'GET', '查看权限列表', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (21, '创建权限', 'permission:create', '/permission', 'POST', '创建新权限', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (22, '编辑权限', 'permission:update', '/permission/*', 'PUT', '编辑权限信息', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (23, '删除权限', 'permission:delete', '/permission/*', 'DELETE', '删除权限', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (30, '查看部门列表', 'department:list', '/department/list', 'GET', '查看部门列表', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (31, '创建部门', 'department:create', '/department', 'POST', '创建新部门', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (32, '编辑部门', 'department:update', '/department/*', 'PUT', '编辑部门信息', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (33, '删除部门', 'department:delete', '/department/*', 'DELETE', '删除部门', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (40, '查看小组列表', 'group:list', '/group/list', 'GET', '查看小组列表', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (41, '创建小组', 'group:create', '/group', 'POST', '创建新小组', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (42, '编辑小组', 'group:update', '/group/*', 'PUT', '编辑小组信息', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (43, '删除小组', 'group:delete', '/group/*', 'DELETE', '删除小组', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (50, '查看项目列表', 'project:list', '/project/list', 'GET', '查看项目列表', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (51, '创建项目', 'project:create', '/project', 'POST', '创建新项目', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (52, '编辑项目', 'project:update', '/project/*', 'PUT', '编辑项目信息', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (53, '删除项目', 'project:delete', '/project/*', 'DELETE', '删除项目', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (60, '查看任务列表', 'task:list', '/task/list', 'GET', '查看任务列表', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (61, '创建任务', 'task:create', '/task', 'POST', '创建新任务', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (62, '编辑任务', 'task:update', '/task/*', 'PUT', '编辑任务信息', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (63, '删除任务', 'task:delete', '/task/*', 'DELETE', '删除任务', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (64, '分配任务', 'task:assign', '/task/*/assign', 'POST', '分配任务给用户', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (65, '完成任务', 'task:complete', '/task/*/complete', 'POST', '标记任务完成', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (70, '查看工作日志', 'worklog:list', '/worklog/list', 'GET', '查看工作日志列表', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (71, '创建工作日志', 'worklog:create', '/worklog', 'POST', '创建工作日志', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (72, '编辑工作日志', 'worklog:update', '/worklog/*', 'PUT', '编辑工作日志', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (73, '删除工作日志', 'worklog:delete', '/worklog/*', 'DELETE', '删除工作日志', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (74, '审核工作日志', 'worklog:review', '/worklog/*/review', 'POST', '审核工作日志', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (80, '查看个人统计', 'statistics:personal', '/statistics/personal', 'GET', '查看个人统计数据', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (81, '查看小组统计', 'statistics:group', '/statistics/group', 'GET', '查看小组统计数据', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (82, '查看部门统计', 'statistics:department', '/statistics/department', 'GET', '查看部门统计数据', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (83, '查看全局统计', 'statistics:global', '/statistics/global', 'GET', '查看全局统计数据', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (90, '查看通知', 'notification:list', '/notification/list', 'GET', '查看通知列表', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (91, '发送通知', 'notification:send', '/notification', 'POST', '发送系统通知', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (92, '批量通知', 'notification:batch', '/notification/batch', 'POST', '批量发送通知', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (100, '查看操作日志', 'log:operation', '/log/operation', 'GET', '查看操作日志', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (101, '系统配置', 'system:config', '/system/config', 'GET,POST', '系统配置管理', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (102, '数据备份', 'system:backup', '/system/backup', 'POST', '数据备份', '2025-08-01 19:54:51');
INSERT INTO `t_permission` VALUES (103, '通知读取', 'notification:read', '/notifications/unread-count', 'GET', '通知读取', '2025-08-01 21:56:24');
INSERT INTO `t_permission` VALUES (104, '通知更新', 'notification:update', '/notifications/*', 'PUT', '通知更新', '2025-08-01 21:56:24');
INSERT INTO `t_permission` VALUES (105, '用户读取', 'user:read', '/users', 'GET', '用户读取', '2025-08-01 22:22:59');
INSERT INTO `t_permission` VALUES (106, '标记通知已读', 'notification:mark-read', '/notifications/mark-read', 'PUT', '标记通知已读', '2025-08-01 22:23:10');
INSERT INTO `t_permission` VALUES (107, '标记所有通知已读', 'notification:mark-all-read', '/notifications/mark-all-read', 'PUT', '标记所有通知已读', '2025-08-01 22:23:10');
INSERT INTO `t_permission` VALUES (108, '删除通知', 'notification:delete', '/notifications/delete', 'DELETE', '删除通知', '2025-08-01 22:23:10');
INSERT INTO `t_permission` VALUES (109, '批量发送通知', 'notification:batch-send', '/notifications/batch-send', 'POST', '批量发送通知', '2025-08-01 22:23:10');
INSERT INTO `t_permission` VALUES (110, '查看技能列表', 'skilltag:list', '/skill-tags', 'GET', NULL, '2025-08-06 12:48:29');
INSERT INTO `t_permission` VALUES (113, '创建技能标签', 'skilltag:create', '/skill-tags', 'POST', NULL, '2025-08-06 12:51:18');
INSERT INTO `t_permission` VALUES (114, '更新技能标签', 'skilltag:update', '/skill-tags', 'PUT', NULL, '2025-08-06 12:51:47');
INSERT INTO `t_permission` VALUES (115, '删除技能标签', 'skilltag:delete', '/skill-tags', 'DELETE', NULL, '2025-08-06 12:52:04');
INSERT INTO `t_permission` VALUES (116, '读取技能详情', 'skilltag:read', '/skill-tag', 'GET', NULL, '2025-08-06 13:12:35');
INSERT INTO `t_permission` VALUES (126, '查看所有项目', 'project:list:all', '/projects/all', 'GET', '获取所有项目（不分页）', '2025-08-06 22:10:57');
INSERT INTO `t_permission` VALUES (127, '查看项目详情', 'project:view', '/projects/{id}', 'GET', '查看项目详细信息', '2025-08-06 22:10:57');
INSERT INTO `t_permission` VALUES (128, '查看项目统计', 'project:stats', '/projects/{id}/stats', 'GET', '查看项目任务统计信息', '2025-08-06 22:10:57');
INSERT INTO `t_permission` VALUES (129, '查看项目进度趋势', 'project:trend', '/projects/{id}/progress-trend', 'GET', '查看项目进度趋势', '2025-08-06 22:10:57');
INSERT INTO `t_permission` VALUES (130, '刷新项目进度', 'project:refresh', '/projects/{id}/refresh-progress', 'POST', '手动刷新项目进度', '2025-08-06 22:10:57');
INSERT INTO `t_permission` VALUES (135, '获取所有任务', 'task:list:all', '/tasks/all', 'GET', '获取所有任务（不分页）', '2025-08-06 22:10:57');
INSERT INTO `t_permission` VALUES (136, '批量重新计算任务进度', 'task:batch:recalculate', '/tasks/batch/recalculate-progress', 'POST', '批量重新计算任务进度', '2025-08-06 22:10:57');
INSERT INTO `t_permission` VALUES (137, '重新计算单个任务进度', 'task:recalculate', '/tasks/{id}/recalculate-progress', 'POST', '重新计算单个任务进度', '2025-08-06 22:10:57');
INSERT INTO `t_permission` VALUES (138, '批量发送通知', 'notification:batch:send', '/notifications/batch-send', 'POST', '批量发送通知权限', '2025-08-06 22:10:57');
INSERT INTO `t_permission` VALUES (140, '批量操作工作日志', 'worklog:batch', '/worklogs/batch/*', 'POST', '工作日志批量操作权限', '2025-08-06 22:10:57');
INSERT INTO `t_permission` VALUES (141, '任务依赖查看', 'task:dependency:view', NULL, NULL, '查看任务依赖关系权限', '2025-08-07 21:26:10');
INSERT INTO `t_permission` VALUES (142, '任务依赖创建', 'task:dependency:create', NULL, NULL, '创建任务依赖关系权限', '2025-08-07 21:26:10');
INSERT INTO `t_permission` VALUES (143, '任务依赖删除', 'task:dependency:delete', NULL, NULL, '删除任务依赖关系权限', '2025-08-07 21:26:10');
INSERT INTO `t_permission` VALUES (144, '任务模板列表', 'task:template:list', NULL, NULL, '查看任务模板列表权限', '2025-08-07 21:26:10');
INSERT INTO `t_permission` VALUES (145, '任务模板查看', 'task:template:view', NULL, NULL, '查看任务模板详情权限', '2025-08-07 21:26:10');
INSERT INTO `t_permission` VALUES (146, '任务模板创建', 'task:template:create', NULL, NULL, '创建任务模板权限', '2025-08-07 21:26:10');
INSERT INTO `t_permission` VALUES (147, '任务模板更新', 'task:template:update', NULL, NULL, '更新任务模板权限', '2025-08-07 21:26:10');
INSERT INTO `t_permission` VALUES (148, '任务模板删除', 'task:template:delete', NULL, NULL, '删除任务模板权限', '2025-08-07 21:26:10');
INSERT INTO `t_permission` VALUES (149, '任务模板应用', 'task:template:apply', NULL, NULL, '应用任务模板权限', '2025-08-07 21:26:10');
INSERT INTO `t_permission` VALUES (150, '工时记录查看', 'work:hour:view', NULL, NULL, '查看工时记录权限', '2025-08-07 21:26:10');
INSERT INTO `t_permission` VALUES (151, '工时记录创建', 'work:hour:create', NULL, NULL, '创建工时记录权限', '2025-08-07 21:26:10');
INSERT INTO `t_permission` VALUES (152, '工时记录更新', 'work:hour:update', NULL, NULL, '更新工时记录权限', '2025-08-07 21:26:10');
INSERT INTO `t_permission` VALUES (153, '工时记录删除', 'work:hour:delete', NULL, NULL, '删除工时记录权限', '2025-08-07 21:26:10');
INSERT INTO `t_permission` VALUES (154, '工时记录审核', 'work:hour:review', NULL, NULL, '审核工时记录权限', '2025-08-07 21:26:10');

-- ----------------------------
-- Table structure for t_project
-- ----------------------------
DROP TABLE IF EXISTS `t_project`;
CREATE TABLE `t_project`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '项目描述',
  `status` enum('规划中','开发中','测试中','已上线','维护中','已关闭') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '规划中',
  `manager_id` bigint NULL DEFAULT NULL COMMENT '项目经理ID',
  `department_id` bigint NULL DEFAULT NULL COMMENT '负责部门ID',
  `start_time` date NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` date NULL DEFAULT NULL COMMENT '结束时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除(1是 0否)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `manager_id`(`manager_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `t_project_ibfk_1` FOREIGN KEY (`manager_id`) REFERENCES `t_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_project
-- ----------------------------
INSERT INTO `t_project` VALUES (1, '企业管理系统', '公司内部使用的综合管理系统', '维护中', 11, 1, '2025-05-31', '2025-05-31', 0, '2025-05-22 16:05:47', '2025-05-24 19:50:01');
INSERT INTO `t_project` VALUES (2, '移动端应用', '公司产品的移动端实现', '规划中', 3, 1, NULL, NULL, 1, '2025-05-22 16:05:47', '2025-05-24 18:36:48');
INSERT INTO `t_project` VALUES (3, '企业管理系统前端', '开发前端', '开发中', 11, 1, '2025-05-24', '2025-05-31', 0, '2025-05-24 19:51:54', '2025-05-24 19:51:54');

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称(管理员/组长/组员)',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES (1, 'ADMIN', '系统管理员', '2025-05-21 19:52:44', '2025-08-01 19:54:51');
INSERT INTO `t_role` VALUES (2, 'MANAGER', '部门经理', '2025-05-21 19:52:44', '2025-08-01 19:54:51');
INSERT INTO `t_role` VALUES (3, 'LEADER', '项目组长', '2025-05-21 19:52:44', '2025-08-01 19:54:51');
INSERT INTO `t_role` VALUES (4, 'MEMBER', '普通成员', '2025-08-01 19:54:51', '2025-08-01 19:54:51');
INSERT INTO `t_role` VALUES (8, '项目经理', '管理多个项目', '2025-05-24 19:09:00', '2025-05-24 19:09:00');

-- ----------------------------
-- Table structure for t_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_role_permission`;
CREATE TABLE `t_role_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_role_permission`(`role_id` ASC, `permission_id` ASC) USING BTREE,
  INDEX `permission_id`(`permission_id` ASC) USING BTREE,
  CONSTRAINT `t_role_permission_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `t_role_permission_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `t_permission` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 791 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_role_permission
-- ----------------------------
INSERT INTO `t_role_permission` VALUES (517, 2, 1, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (518, 2, 2, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (519, 2, 3, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (520, 2, 5, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (521, 2, 105, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (522, 2, 10, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (523, 2, 20, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (524, 2, 30, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (525, 2, 31, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (526, 2, 32, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (527, 2, 33, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (528, 2, 40, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (529, 2, 41, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (530, 2, 42, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (531, 2, 43, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (532, 2, 50, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (533, 2, 51, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (534, 2, 52, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (535, 2, 53, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (536, 2, 60, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (537, 2, 61, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (538, 2, 62, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (539, 2, 63, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (540, 2, 64, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (541, 2, 70, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (542, 2, 74, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (543, 2, 80, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (544, 2, 81, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (545, 2, 82, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (546, 2, 90, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (547, 2, 91, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (548, 2, 103, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (549, 2, 104, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (550, 2, 106, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (551, 2, 107, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (552, 2, 108, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (553, 2, 109, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (554, 2, 100, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (555, 3, 1, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (556, 3, 105, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (557, 3, 10, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (558, 3, 40, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (559, 3, 42, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (560, 3, 50, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (561, 3, 52, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (562, 3, 60, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (563, 3, 61, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (564, 3, 62, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (565, 3, 64, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (566, 3, 65, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (567, 3, 70, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (568, 3, 71, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (569, 3, 72, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (570, 3, 74, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (571, 3, 80, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (572, 3, 81, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (573, 3, 90, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (574, 3, 91, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (575, 3, 103, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (576, 3, 104, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (577, 3, 106, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (578, 3, 107, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (579, 3, 108, '2025-08-05 19:40:47');
INSERT INTO `t_role_permission` VALUES (606, 4, 60, '2025-08-05 21:25:47');
INSERT INTO `t_role_permission` VALUES (607, 4, 65, '2025-08-05 21:25:47');
INSERT INTO `t_role_permission` VALUES (608, 4, 70, '2025-08-05 21:25:47');
INSERT INTO `t_role_permission` VALUES (609, 4, 71, '2025-08-05 21:25:47');
INSERT INTO `t_role_permission` VALUES (610, 4, 72, '2025-08-05 21:25:47');
INSERT INTO `t_role_permission` VALUES (611, 4, 80, '2025-08-05 21:25:47');
INSERT INTO `t_role_permission` VALUES (612, 4, 90, '2025-08-05 21:25:47');
INSERT INTO `t_role_permission` VALUES (613, 4, 103, '2025-08-05 21:25:47');
INSERT INTO `t_role_permission` VALUES (614, 4, 104, '2025-08-05 21:25:47');
INSERT INTO `t_role_permission` VALUES (615, 4, 106, '2025-08-05 21:25:47');
INSERT INTO `t_role_permission` VALUES (616, 4, 107, '2025-08-05 21:25:47');
INSERT INTO `t_role_permission` VALUES (675, 1, 1, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (676, 1, 2, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (677, 1, 3, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (678, 1, 4, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (679, 1, 5, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (680, 1, 10, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (681, 1, 11, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (682, 1, 12, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (683, 1, 13, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (684, 1, 14, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (685, 1, 20, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (686, 1, 21, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (687, 1, 22, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (688, 1, 23, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (689, 1, 30, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (690, 1, 31, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (691, 1, 32, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (692, 1, 33, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (693, 1, 40, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (694, 1, 41, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (695, 1, 42, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (696, 1, 43, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (697, 1, 50, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (698, 1, 51, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (699, 1, 52, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (700, 1, 53, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (701, 1, 60, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (702, 1, 61, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (703, 1, 62, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (704, 1, 63, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (705, 1, 64, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (706, 1, 65, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (707, 1, 70, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (708, 1, 71, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (709, 1, 72, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (710, 1, 73, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (711, 1, 74, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (712, 1, 80, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (713, 1, 81, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (714, 1, 82, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (715, 1, 83, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (716, 1, 90, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (717, 1, 91, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (718, 1, 92, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (719, 1, 100, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (720, 1, 101, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (721, 1, 102, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (722, 1, 103, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (723, 1, 104, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (724, 1, 105, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (725, 1, 106, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (726, 1, 107, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (727, 1, 108, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (728, 1, 109, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (729, 1, 110, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (730, 1, 113, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (731, 1, 114, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (732, 1, 115, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (733, 1, 116, '2025-08-06 13:12:50');
INSERT INTO `t_role_permission` VALUES (734, 1, 138, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (735, 1, 126, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (736, 1, 130, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (737, 1, 128, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (738, 1, 129, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (739, 1, 127, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (740, 1, 136, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (741, 1, 135, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (742, 1, 137, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (743, 1, 140, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (749, 2, 126, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (750, 2, 128, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (751, 2, 129, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (752, 2, 127, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (753, 2, 135, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (754, 2, 137, '2025-08-06 22:10:57');
INSERT INTO `t_role_permission` VALUES (755, 1, 142, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (756, 1, 143, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (757, 1, 141, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (758, 1, 149, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (759, 1, 146, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (760, 1, 148, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (761, 1, 144, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (762, 1, 147, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (763, 1, 145, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (764, 1, 151, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (765, 1, 153, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (766, 1, 154, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (767, 1, 152, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (768, 1, 150, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (770, 2, 142, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (771, 2, 143, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (772, 2, 141, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (773, 2, 149, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (774, 2, 144, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (775, 2, 145, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (776, 2, 151, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (777, 2, 154, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (778, 2, 152, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (779, 2, 150, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (785, 3, 141, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (786, 3, 144, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (787, 3, 145, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (788, 3, 151, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (789, 3, 152, '2025-08-07 21:26:10');
INSERT INTO `t_role_permission` VALUES (790, 3, 150, '2025-08-07 21:26:10');

-- ----------------------------
-- Table structure for t_skill_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_skill_tag`;
CREATE TABLE `t_skill_tag`  (
  `tag_id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签描述',
  `color` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#3B82F6' COMMENT '标签颜色（十六进制）',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签分类（前端/后端/数据库/工具等）',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序权重',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态（1启用 0禁用）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者ID',
  PRIMARY KEY (`tag_id`) USING BTREE,
  UNIQUE INDEX `tag_name`(`tag_name` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '技术栈标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_skill_tag
-- ----------------------------
INSERT INTO `t_skill_tag` VALUES (1, 'Vue.js', 'Vue.js前端框架', '#4FC08D', '前端', 1, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (2, 'React', 'React前端框架', '#61DAFB', '前端', 2, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (3, 'Angular', 'Angular前端框架', '#DD0031', '前端', 3, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (4, 'JavaScript', 'JavaScript编程语言', '#F7DF1E', '前端', 4, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (5, 'TypeScript', 'TypeScript编程语言', '#3178C6', '前端', 5, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (6, 'HTML5', 'HTML5标记语言', '#E34F26', '前端', 6, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (7, 'CSS3', 'CSS3样式表', '#1572B6', '前端', 7, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (8, 'Sass/SCSS', 'CSS预处理器', '#CF649A', '前端', 8, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (9, 'Less', 'CSS预处理器', '#1D365D', '前端', 9, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (10, 'Webpack', '模块打包工具', '#8DD6F9', '前端', 10, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (11, 'Java', 'Java编程语言', '#ED8B00', '后端', 1, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (12, 'Spring Boot', 'Spring Boot框架', '#6DB33F', '后端', 2, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (13, 'Spring Cloud', 'Spring Cloud微服务', '#6DB33F', '后端', 3, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (14, 'MyBatis', 'MyBatis ORM框架', '#000000', '后端', 4, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (15, 'Node.js', 'Node.js运行时', '#339933', '后端', 5, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (16, 'Python', 'Python编程语言', '#3776AB', '后端', 6, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (17, 'Django', 'Django Web框架', '#092E20', '后端', 7, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (18, 'Flask', 'Flask微框架', '#000000', '后端', 8, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (19, 'Go', 'Go编程语言', '#00ADD8', '后端', 9, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (20, 'C#', 'C#编程语言', '#239120', '后端', 10, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (21, 'MySQL', 'MySQL关系型数据库', '#4479A1', '数据库', 1, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (22, 'PostgreSQL', 'PostgreSQL数据库', '#336791', '数据库', 2, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (23, 'MongoDB', 'MongoDB文档数据库', '#47A248', '数据库', 3, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (24, 'Redis', 'Redis内存数据库', '#DC382D', '数据库', 4, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (25, 'Oracle', 'Oracle数据库', '#F80000', '数据库', 5, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (26, 'SQL Server', 'SQL Server数据库', '#CC2927', '数据库', 6, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (27, 'Docker', '容器化技术', '#2496ED', '工具', 1, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (28, 'Kubernetes', '容器编排', '#326CE5', '工具', 2, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (29, 'Git', '版本控制系统', '#F05032', '工具', 3, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (30, 'Jenkins', 'CI/CD工具', '#D24939', '工具', 4, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (31, 'Linux', 'Linux操作系统', '#FCC624', '工具', 5, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (32, 'AWS', '亚马逊云服务', '#FF9900', '工具', 6, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (33, '阿里云', '阿里云服务', '#FF6A00', '工具', 7, 1, '2025-07-30 21:48:29', '2025-07-30 21:48:29', NULL, NULL);
INSERT INTO `t_skill_tag` VALUES (34, '数据分析', '可以分析数据', '#0ea5e9', '数据库', 1, 1, '2025-07-31 21:29:07', '2025-07-31 21:29:07', 1, NULL);

-- ----------------------------
-- Table structure for t_task
-- ----------------------------
DROP TABLE IF EXISTS `t_task`;
CREATE TABLE `t_task`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任务描述',
  `status` enum('待处理','进行中','审核中','已完成','已取消') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待处理',
  `priority` enum('高','中','低') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '中',
  `project_id` bigint NULL DEFAULT NULL COMMENT '所属项目ID',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `assignee_id` bigint NULL DEFAULT NULL COMMENT '负责人ID',
  `department_id` bigint NOT NULL COMMENT '所属部门ID',
  `group_id` bigint NULL DEFAULT NULL COMMENT '所属组ID',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `deadline` datetime NULL DEFAULT NULL COMMENT '截止时间',
  `estimated_hours` decimal(5, 2) NULL DEFAULT NULL COMMENT '预估工时(小时)',
  `actual_hours` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '实际工时(小时)',
  `progress` tinyint NULL DEFAULT 0 COMMENT '进度(0-100)',
  `depend_task_id` bigint NULL DEFAULT NULL COMMENT '依赖任务ID',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除(1是 0否)',
  `delete_status` tinyint NULL DEFAULT 0 COMMENT '删除状态(0=可删除,1=不可删除)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `creator_id`(`creator_id` ASC) USING BTREE,
  INDEX `idx_group_status`(`group_id` ASC, `status` ASC) USING BTREE,
  INDEX `idx_assignee`(`assignee_id` ASC) USING BTREE,
  CONSTRAINT `t_task_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `t_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_task_ibfk_2` FOREIGN KEY (`assignee_id`) REFERENCES `t_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `t_task_ibfk_3` FOREIGN KEY (`group_id`) REFERENCES `t_department` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_task
-- ----------------------------
INSERT INTO `t_task` VALUES (1, '首页布局设计', '完成管理系统首页UI设计', '进行中', '高', 1, 2, 4, 1, NULL, '2025-05-01 00:00:00', '2025-05-10 00:00:00', 16.00, 0.00, 30, NULL, 0, 0, '2025-05-22 16:05:47', '2025-08-06 21:15:49');
INSERT INTO `t_task` VALUES (2, '用户认证模块', '实现登录、注册和权限控制', '进行中', '高', 1, 3, 6, 1, NULL, '2025-05-01 00:00:00', '2025-05-15 00:00:00', 24.00, 0.00, 0, NULL, 0, 0, '2025-05-22 16:05:47', '2025-05-24 17:37:51');
INSERT INTO `t_task` VALUES (3, '数据可视化', '完成系统数据统计图表', '待处理', '中', 1, 2, 1, 1, 1, '2025-05-10 00:00:00', '2025-05-20 00:00:00', 20.00, 0.00, 0, NULL, 0, 0, '2025-05-22 16:05:47', '2025-05-25 15:30:38');
INSERT INTO `t_task` VALUES (4, 'API接口开发', '提供业务所需的RESTful API', '进行中', '高', 1, 3, 7, 1, NULL, '2025-05-01 00:00:00', '2025-05-15 00:00:00', 32.00, 0.00, 0, NULL, 0, 0, '2025-05-22 16:05:47', '2025-05-24 17:37:51');
INSERT INTO `t_task` VALUES (5, '测试任务', '这是一个测试任务描述', '进行中', '中', NULL, 1, 1, 1, NULL, NULL, NULL, NULL, 0.00, 0, NULL, 1, 0, '2025-05-22 18:25:30', '2025-05-24 17:37:52');
INSERT INTO `t_task` VALUES (7, '绿最后在抄', '这何族看达。育参步确质。色属任多目价眼。越完面众条证真商几集。证学重要广点。住重周始务表系。志全得想儿制现电。果思前在命知音流。', '已完成', '高', 1, 1, 1, 1, 1, '2025-05-23 16:31:11', '2025-05-23 16:31:11', 43.00, 82.00, 100, NULL, 0, 0, '2025-05-23 17:18:52', '2025-08-07 20:37:40');
INSERT INTO `t_task` VALUES (8, '测试一下', NULL, '待处理', '中', NULL, 1, NULL, 1, NULL, NULL, NULL, NULL, 0.00, 0, NULL, 1, 0, '2025-05-23 17:23:46', '2025-05-24 17:37:53');
INSERT INTO `t_task` VALUES (10, '2132131', '21313123', '待处理', '中', NULL, 1, 1, 1, 1, '2025-05-24 05:11:04', '2025-05-24 05:11:05', 0.00, 0.00, 40, NULL, 1, 0, '2025-05-24 13:11:08', '2025-05-31 15:10:09');
INSERT INTO `t_task` VALUES (11, '测试', '12312312', '待处理', '高', NULL, 1, 1, 1, NULL, '2025-05-24 06:17:59', '2025-05-24 06:18:00', 0.00, 0.00, 80, NULL, 1, 0, '2025-05-24 14:18:02', '2025-05-31 15:06:03');
INSERT INTO `t_task` VALUES (12, 'ceshi', 'weqewqe', '待处理', '低', NULL, 1, 1, 1, 1, '2025-05-29 00:19:33', '2025-05-29 00:19:35', 0.00, 0.00, 30, NULL, 1, 0, '2025-05-29 08:19:38', '2025-05-29 08:23:24');
INSERT INTO `t_task` VALUES (13, '测试', '12345', '已完成', '高', NULL, 1, 1, 1, 1, '2025-05-31 07:08:02', '2025-05-31 07:08:05', 0.00, 0.00, 80, NULL, 1, 0, '2025-05-31 15:08:10', '2025-05-31 15:08:40');
INSERT INTO `t_task` VALUES (14, '丛宇欣的任务', '213124124', '已完成', '高', NULL, 1, 1, 1, 1, '2025-05-31 07:10:37', '2025-05-31 07:10:35', 0.00, 0.00, 0, NULL, 1, 0, '2025-05-31 15:10:49', '2025-05-31 15:11:31');
INSERT INTO `t_task` VALUES (15, '扫地', '用拖把扫地', '待处理', '高', NULL, 1, 1, 1, 1, '2025-07-28 11:36:36', '2025-07-30 16:00:00', 0.00, 0.00, 0, NULL, 1, 0, '2025-07-28 19:36:49', '2025-07-29 18:08:25');

-- ----------------------------
-- Table structure for t_task_attachment
-- ----------------------------
DROP TABLE IF EXISTS `t_task_attachment`;
CREATE TABLE `t_task_attachment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件类型',
  `oss_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'OSS文件路径',
  `uploader_id` bigint NOT NULL COMMENT '上传人ID',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(1是 0否)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_uploader_id`(`uploader_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务附件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_task_attachment
-- ----------------------------
INSERT INTO `t_task_attachment` VALUES (1, 11, '文玩核桃展示.png', 1920928, '.png', 'task/11/images/69fb92b4-ffb2-4ba1-9883-ba972056c401.png', 1, 1, '2025-05-31 14:57:08', '2025-05-31 15:04:53');
INSERT INTO `t_task_attachment` VALUES (2, 11, '文玩核桃展示.png', 1920928, '.png', 'task/11/images/b9261cf9-3a51-4464-a9fd-657cf55873e1.png', 1, 1, '2025-05-31 15:05:07', '2025-05-31 15:06:00');
INSERT INTO `t_task_attachment` VALUES (3, 13, '文玩核桃展示.png', 1920928, '.png', 'task/13/images/11a33017-1395-4531-8348-f9eb37358f69.png', 1, 0, '2025-05-31 15:08:20', '2025-05-31 15:08:20');
INSERT INTO `t_task_attachment` VALUES (4, 10, '文玩核桃展示.png', 1920928, '.png', 'task/10/images/4a22d2ac-9894-4d79-aed5-bd6c28ae5d76.png', 1, 1, '2025-05-31 15:09:51', '2025-05-31 15:10:47');
INSERT INTO `t_task_attachment` VALUES (5, 14, '文玩核桃展示.png', 1920928, '.png', 'task/14/images/5de0e2b4-1cb4-4568-b8f1-f42801ab97a8.png', 1, 1, '2025-05-31 15:10:57', '2025-05-31 15:11:30');
INSERT INTO `t_task_attachment` VALUES (6, 7, '文玩核桃展示.png', 1920928, '.png', 'task/7/images/ab35db06-76d7-4605-a922-cd50e849fe56.png', 1, 1, '2025-06-01 20:24:07', '2025-06-01 20:29:21');
INSERT INTO `t_task_attachment` VALUES (7, 7, 'Vmware16注册码.txt', 95, '.txt', 'task/7/files/43952975-03e5-422a-8886-59fb170b0331.txt', 1, 1, '2025-06-01 20:29:15', '2025-06-01 20:29:20');
INSERT INTO `t_task_attachment` VALUES (8, 7, '文玩核桃展示.png', 1920928, '.png', 'task/7/images/e611f687-7d72-4dd1-9fdd-d1d63a6bcef5.png', 1, 1, '2025-06-01 20:32:36', '2025-06-01 20:33:09');
INSERT INTO `t_task_attachment` VALUES (9, 7, '文玩核桃展示.png', 1920928, '.png', 'task/7/images/db8ea0e1-be19-4b78-8c7b-852eaf5a89a4.png', 1, 0, '2025-06-04 16:59:22', '2025-06-04 16:59:22');

-- ----------------------------
-- Table structure for t_task_dependency
-- ----------------------------
DROP TABLE IF EXISTS `t_task_dependency`;
CREATE TABLE `t_task_dependency`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint NOT NULL COMMENT '任务ID（被依赖的任务）',
  `dependent_task_id` bigint NOT NULL COMMENT '依赖任务ID（依赖的任务）',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DEPENDS_ON' COMMENT '依赖类型：BLOCKS-阻塞关系，DEPENDS_ON-依赖关系',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '依赖描述',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_task_dependency`(`task_id` ASC, `dependent_task_id` ASC, `is_deleted` ASC) USING BTREE,
  INDEX `idx_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_dependent_task_id`(`dependent_task_id` ASC) USING BTREE,
  INDEX `idx_creator_id`(`creator_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '任务依赖关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_task_dependency
-- ----------------------------
INSERT INTO `t_task_dependency` VALUES (1, 7, 1, 'DEPENDS_ON', '123', 1, '2025-08-07 22:01:01', '2025-08-07 22:01:01', 0);
INSERT INTO `t_task_dependency` VALUES (2, 7, 4, 'BLOCKS', '213221', 1, '2025-08-07 22:03:44', '2025-08-07 22:03:44', 0);

-- ----------------------------
-- Table structure for t_task_requirement
-- ----------------------------
DROP TABLE IF EXISTS `t_task_requirement`;
CREATE TABLE `t_task_requirement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `required_tag_id` bigint NOT NULL COMMENT '所需标签ID',
  `minimum_proficiency` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '最低熟练程度要求',
  `importance` tinyint NULL DEFAULT 3 COMMENT '重要程度（1-5级，5最重要）',
  `is_required` tinyint NULL DEFAULT 1 COMMENT '是否必需（1必需 0可选）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注说明',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_task_tag`(`task_id` ASC, `required_tag_id` ASC) USING BTREE,
  INDEX `idx_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_required_tag_id`(`required_tag_id` ASC) USING BTREE,
  INDEX `idx_importance`(`importance` ASC) USING BTREE,
  INDEX `idx_is_required`(`is_required` ASC) USING BTREE,
  CONSTRAINT `t_task_requirement_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `t_task` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `t_task_requirement_ibfk_2` FOREIGN KEY (`required_tag_id`) REFERENCES `t_skill_tag` (`tag_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '任务技能需求关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_task_requirement
-- ----------------------------

-- ----------------------------
-- Table structure for t_task_template
-- ----------------------------
DROP TABLE IF EXISTS `t_task_template`;
CREATE TABLE `t_task_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '模板描述',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板分类',
  `tags` json NULL COMMENT '模板标签，JSON数组格式',
  `tasks` json NULL COMMENT '任务配置，JSON格式',
  `tasks_count` int NOT NULL DEFAULT 0 COMMENT '任务数量',
  `estimated_hours` decimal(8, 2) NOT NULL DEFAULT 0.00 COMMENT '预计总工时',
  `usage_count` int NOT NULL DEFAULT 0 COMMENT '使用次数',
  `last_used` datetime NULL DEFAULT NULL COMMENT '最后使用时间',
  `creator_id` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_creator_id`(`creator_id` ASC) USING BTREE,
  INDEX `idx_usage_count`(`usage_count` DESC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '任务模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_task_template
-- ----------------------------
INSERT INTO `t_task_template` VALUES (1, 'Web开发标准流程', '包含需求分析、设计、开发、测试、部署的完整Web开发流程模板', 'development', '[\"前端\", \"后端\", \"全栈\"]', '[{\"order\": 1, \"title\": \"需求分析\", \"priority\": \"高\", \"description\": \"分析项目需求和功能点\", \"estimatedHours\": 16}, {\"order\": 2, \"title\": \"UI/UX设计\", \"priority\": \"高\", \"description\": \"用户界面和交互设计\", \"estimatedHours\": 24}, {\"order\": 3, \"title\": \"前端开发\", \"priority\": \"中\", \"description\": \"前端页面和组件开发\", \"estimatedHours\": 40}, {\"order\": 4, \"title\": \"后端开发\", \"priority\": \"中\", \"description\": \"后端接口和业务逻辑开发\", \"estimatedHours\": 32}, {\"order\": 5, \"title\": \"数据库设计\", \"priority\": \"中\", \"description\": \"数据库结构设计和优化\", \"estimatedHours\": 12}, {\"order\": 6, \"title\": \"集成测试\", \"priority\": \"中\", \"description\": \"前后端集成测试\", \"estimatedHours\": 16}, {\"order\": 7, \"title\": \"部署上线\", \"priority\": \"低\", \"description\": \"项目部署和上线配置\", \"estimatedHours\": 8}, {\"order\": 8, \"title\": \"文档编写\", \"priority\": \"低\", \"description\": \"技术文档和用户手册\", \"estimatedHours\": 12}]', 8, 160.00, 15, '2025-08-07 16:30:00', 1, '2025-08-01 10:00:00', '2025-08-07 16:30:00', 0);
INSERT INTO `t_task_template` VALUES (2, '移动应用开发', 'iOS和Android移动应用开发标准流程模板', 'development', '[\"移动端\", \"iOS\", \"Android\"]', '[{\"order\": 1, \"title\": \"产品规划\", \"priority\": \"高\", \"description\": \"确定产品功能和核心特性\", \"estimatedHours\": 16}, {\"order\": 2, \"title\": \"原型设计\", \"priority\": \"高\", \"description\": \"制作应用交互原型\", \"estimatedHours\": 12}, {\"order\": 3, \"title\": \"UI设计\", \"priority\": \"高\", \"description\": \"移动端界面视觉设计\", \"estimatedHours\": 20}, {\"order\": 4, \"title\": \"前端开发\", \"priority\": \"中\", \"description\": \"移动端前端开发\", \"estimatedHours\": 32}, {\"order\": 5, \"title\": \"后端API\", \"priority\": \"中\", \"description\": \"移动端API接口开发\", \"estimatedHours\": 24}, {\"order\": 6, \"title\": \"测试调试\", \"priority\": \"中\", \"description\": \"功能测试和兼容性测试\", \"estimatedHours\": 16}]', 6, 120.00, 8, '2025-08-05 14:20:00', 1, '2025-07-15 09:00:00', '2025-08-05 14:20:00', 0);
INSERT INTO `t_task_template` VALUES (3, '系统测试流程', '完整的系统测试流程，包含单元测试、集成测试、性能测试', 'testing', '[\"测试\", \"质量保证\", \"自动化\"]', '[{\"order\": 1, \"title\": \"测试计划\", \"priority\": \"高\", \"description\": \"制定详细的测试计划和策略\", \"estimatedHours\": 8}, {\"order\": 2, \"title\": \"单元测试\", \"priority\": \"高\", \"description\": \"编写和执行单元测试\", \"estimatedHours\": 16}, {\"order\": 3, \"title\": \"集成测试\", \"priority\": \"中\", \"description\": \"模块间集成测试\", \"estimatedHours\": 12}, {\"order\": 4, \"title\": \"性能测试\", \"priority\": \"中\", \"description\": \"系统性能和压力测试\", \"estimatedHours\": 16}, {\"order\": 5, \"title\": \"安全测试\", \"priority\": \"中\", \"description\": \"系统安全性测试\", \"estimatedHours\": 8}, {\"order\": 6, \"title\": \"用户体验测试\", \"priority\": \"低\", \"description\": \"用户界面和体验测试\", \"estimatedHours\": 8}]', 6, 68.00, 12, '2025-08-06 11:45:00', 1, '2025-07-20 14:30:00', '2025-08-06 11:45:00', 0);
INSERT INTO `t_task_template` VALUES (4, '产品设计流程', '产品设计的完整流程，从用户调研到最终设计交付', 'design', '[\"设计\", \"用户体验\", \"原型\"]', '[{\"order\": 1, \"title\": \"用户调研\", \"priority\": \"高\", \"description\": \"目标用户群体调研分析\", \"estimatedHours\": 12}, {\"order\": 2, \"title\": \"竞品分析\", \"priority\": \"高\", \"description\": \"竞争对手产品分析\", \"estimatedHours\": 8}, {\"order\": 3, \"title\": \"信息架构\", \"priority\": \"高\", \"description\": \"产品信息架构设计\", \"estimatedHours\": 10}, {\"order\": 4, \"title\": \"交互设计\", \"priority\": \"中\", \"description\": \"用户交互流程设计\", \"estimatedHours\": 16}, {\"order\": 5, \"title\": \"视觉设计\", \"priority\": \"中\", \"description\": \"界面视觉风格设计\", \"estimatedHours\": 20}, {\"order\": 6, \"title\": \"设计规范\", \"priority\": \"低\", \"description\": \"制定设计系统和规范\", \"estimatedHours\": 12}]', 6, 78.00, 6, '2025-08-03 09:15:00', 2, '2025-07-25 16:00:00', '2025-08-03 09:15:00', 0);
INSERT INTO `t_task_template` VALUES (5, '项目部署流程', '标准的项目部署和运维流程模板', 'deployment', '[\"部署\", \"运维\", \"监控\"]', '[{\"order\": 1, \"title\": \"环境准备\", \"priority\": \"高\", \"description\": \"服务器环境配置\", \"estimatedHours\": 6}, {\"order\": 2, \"title\": \"代码构建\", \"priority\": \"高\", \"description\": \"项目代码编译打包\", \"estimatedHours\": 4}, {\"order\": 3, \"title\": \"数据库迁移\", \"priority\": \"高\", \"description\": \"数据库结构和数据迁移\", \"estimatedHours\": 8}, {\"order\": 4, \"title\": \"服务部署\", \"priority\": \"中\", \"description\": \"应用服务部署配置\", \"estimatedHours\": 6}, {\"order\": 5, \"title\": \"负载均衡\", \"priority\": \"中\", \"description\": \"负载均衡配置\", \"estimatedHours\": 4}, {\"order\": 6, \"title\": \"监控配置\", \"priority\": \"中\", \"description\": \"系统监控和报警配置\", \"estimatedHours\": 6}, {\"order\": 7, \"title\": \"备份策略\", \"priority\": \"低\", \"description\": \"数据备份策略制定\", \"estimatedHours\": 4}]', 7, 38.00, 4, '2025-08-04 13:30:00', 1, '2025-07-28 11:20:00', '2025-08-04 13:30:00', 0);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码(BCrypt哈希)',
  `salt` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码盐值',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `department_id` bigint NULL DEFAULT NULL COMMENT '所属部门ID',
  `group_id` bigint NULL DEFAULT NULL COMMENT '所属组ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(1启用 0禁用)',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_department`(`department_id` ASC) USING BTREE,
  INDEX `idx_group`(`group_id` ASC) USING BTREE,
  CONSTRAINT `t_user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_user_ibfk_2` FOREIGN KEY (`department_id`) REFERENCES `t_department` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `t_user_ibfk_3` FOREIGN KEY (`group_id`) REFERENCES `t_department` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, 'admin', '$2a$10$v4s2tMrcua50Y9sbbeM1MusGtIA9k/0z5x46e6Zwl9EeGjv.8s9xa', NULL, '管理员', 'admin@example.com', '13800138000', NULL, 1, 1, 1, 1, '2025-08-14 20:08:38', '2025-05-22 16:05:47', '2025-08-14 20:08:38');
INSERT INTO `t_user` VALUES (2, 'leader1', '$2a$10$Z6H4q3pW2UjvBd1tZ5pL1eYJQwJQwJQwJQwJQwJQwJQwJQw', NULL, '前端组长', 'leader1@example.com', '13800138001', NULL, 2, 1, 3, 1, NULL, '2025-05-22 16:05:47', '2025-05-22 16:05:47');
INSERT INTO `t_user` VALUES (3, 'leader2', '$2a$10$Z6H4q3pW2UjvBd1tZ5pL1eYJQwJQwJQwJQwJQwJQwJQwJQw', NULL, '后端组长', 'leader2@example.com', '13800138002', NULL, 2, 1, 4, 1, NULL, '2025-05-22 16:05:47', '2025-05-22 16:05:47');
INSERT INTO `t_user` VALUES (4, 'user1', '$2a$10$Z6H4q3pW2UjvBd1tZ5pL1eYJQwJQwJQwJQwJQwJQwJQwJQw', NULL, '前端开发1', 'user1@example.com', '13800138003', NULL, 4, 1, 3, 1, NULL, '2025-05-22 16:05:47', '2025-08-01 19:54:51');
INSERT INTO `t_user` VALUES (6, 'user3', '$2a$10$Z6H4q3pW2UjvBd1tZ5pL1eYJQwJQwJQwJQwJQwJQwJQwJQw', NULL, '后端开发1', 'user3@example.com', '13800138005', NULL, 4, 1, 4, 1, NULL, '2025-05-22 16:05:47', '2025-08-01 19:54:51');
INSERT INTO `t_user` VALUES (7, 'user4', '$2a$10$Z6H4q3pW2UjvBd1tZ5pL1eYJQwJQwJQwJQwJQwJQwJQwJQw', NULL, '后端开发2', 'user4@example.com', '13800138006', NULL, 4, 1, 4, 1, NULL, '2025-05-22 16:05:47', '2025-08-01 19:54:51');
INSERT INTO `t_user` VALUES (10, 'xiaocong1', 'fbe6ba2d8057d2b18a68f17b35ddf41c', NULL, '丛宇欣', '1277623709@qq.com', '18846586137', NULL, 2, 8, NULL, 1, NULL, '2025-05-23 15:47:24', '2025-05-25 15:00:12');
INSERT INTO `t_user` VALUES (11, 'jingli', 'e10adc3949ba59abbe56e057f20f883e', NULL, '高敬礼', 'jingli@qq.com', '15046049913', NULL, 8, 1, 1, 1, NULL, '2025-05-24 19:15:37', '2025-05-25 15:27:34');
INSERT INTO `t_user` VALUES (12, 'Giao123', '$2a$10$NrT1J7QHKEk4f7gunk1n5.Slrfp.ES0bA7hTqQ96KultgJb6PQCTS', NULL, '阿giao', '789456123@qq.com', '15044444444', NULL, 4, 1, NULL, 1, '2025-08-05 21:27:14', '2025-07-28 18:30:44', '2025-08-05 21:27:14');
INSERT INTO `t_user` VALUES (13, 'GuLu9527', '$2a$10$GToZiLzRkKccK0NrSmNkhuxmoN/2o0AMg7F/tEi7QqLzXwAU/.PC6', NULL, '啊giao', 'test123456@qq.com', '15000000000', NULL, 3, 2, NULL, 1, '2025-08-01 20:06:40', '2025-08-01 19:59:04', '2025-08-01 20:06:40');
INSERT INTO `t_user` VALUES (14, 'superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIy9TgfpK9pnhKJnEgqVKHNE.2', NULL, '超级管理员', 'superadmin@devlog.com', NULL, NULL, 1, NULL, NULL, 1, NULL, '2025-08-01 20:09:56', '2025-08-01 20:09:56');

-- ----------------------------
-- Table structure for t_user_skill
-- ----------------------------
DROP TABLE IF EXISTS `t_user_skill`;
CREATE TABLE `t_user_skill`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  `proficiency_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '熟练程度（初级/中级/高级/专家）',
  `years_of_experience` int NULL DEFAULT 0 COMMENT '使用年限',
  `last_used` date NULL DEFAULT NULL COMMENT '最后使用时间',
  `is_primary` tinyint NULL DEFAULT 0 COMMENT '是否主要技能（1是 0否）',
  `self_rating` tinyint NULL DEFAULT 5 COMMENT '自评分数（1-10分）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_tag`(`user_id` ASC, `tag_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_tag_id`(`tag_id` ASC) USING BTREE,
  INDEX `idx_proficiency`(`proficiency_level` ASC) USING BTREE,
  INDEX `idx_is_primary`(`is_primary` ASC) USING BTREE,
  CONSTRAINT `t_user_skill_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `t_user_skill_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `t_skill_tag` (`tag_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户技能关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_skill
-- ----------------------------
INSERT INTO `t_user_skill` VALUES (1, 12, 1, '专家', 0, NULL, 1, 5, '测试', '2025-07-31 21:21:21', '2025-07-31 21:21:21');

-- ----------------------------
-- Table structure for t_work_hour
-- ----------------------------
DROP TABLE IF EXISTS `t_work_hour`;
CREATE TABLE `t_work_hour`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `work_date` date NOT NULL COMMENT '工作日期',
  `hours` decimal(4, 2) NOT NULL COMMENT '工时数',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DEVELOPMENT' COMMENT '工时类型：DEVELOPMENT-开发，TESTING-测试，MEETING-会议，RESEARCH-调研，OTHER-其他',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工时描述',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已通过，REJECTED-已驳回',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `review_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_task_date`(`user_id` ASC, `task_id` ASC, `work_date` ASC, `is_deleted` ASC) USING BTREE,
  INDEX `idx_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_project_id`(`project_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_work_date`(`work_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_reviewer_id`(`reviewer_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工时记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_work_hour
-- ----------------------------
INSERT INTO `t_work_hour` VALUES (1, 7, 1, 1, '2025-08-08', 1.00, 'DEVELOPMENT', '12323431321', NULL, NULL, 'APPROVED', 1, '2025-08-08 19:42:02', NULL, '2025-08-08 19:41:28', '2025-08-08 19:42:02', 0);
INSERT INTO `t_work_hour` VALUES (2, 2, 1, 1, '2025-08-08', 24.00, 'DEVELOPMENT', '12312', NULL, NULL, 'PENDING', NULL, NULL, NULL, '2025-08-08 20:14:18', '2025-08-08 20:14:18', 1);

-- ----------------------------
-- Table structure for t_work_log
-- ----------------------------
DROP TABLE IF EXISTS `t_work_log`;
CREATE TABLE `t_work_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `task_id` bigint NOT NULL COMMENT '关联任务ID',
  `user_id` bigint NOT NULL COMMENT '提交人ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '日志内容',
  `hours` decimal(6, 2) NOT NULL COMMENT '工时(小时)',
  `log_date` date NOT NULL COMMENT '日志日期',
  `review_status` enum('待审核','已通过','已拒绝') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '待审核',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `review_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核意见',
  `review_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `group_id` bigint NOT NULL COMMENT '所属组ID',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除(1是 0否)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `task_id`(`task_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_group_user_date`(`group_id` ASC, `user_id` ASC, `log_date` ASC) USING BTREE,
  CONSTRAINT `t_work_log_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `t_task` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_work_log_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `t_work_log_ibfk_3` FOREIGN KEY (`group_id`) REFERENCES `t_department` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '工作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_work_log
-- ----------------------------
INSERT INTO `t_work_log` VALUES (1, 1, 4, '{\"work\": \"完成了首页顶部导航栏和侧边菜单的开发\", \"problem\": \"导航栏在移动端显示有问题\", \"plan\": \"修复移动端显示问题，开始开发数据卡片\"}', 8.00, '2025-05-20', '已通过', 2, NULL, NULL, 3, 0, '2025-05-22 16:05:47', '2025-05-22 16:05:47');
INSERT INTO `t_work_log` VALUES (2, 2, 6, '{\"work\": \"完成了用户登录和JWT认证功能\", \"problem\": \"密码加密算法选择有争议\", \"plan\": \"实现注册功能，编写单元测试\"}', 8.00, '2025-05-20', '已通过', 3, NULL, NULL, 4, 0, '2025-05-22 16:05:47', '2025-05-22 16:05:47');
INSERT INTO `t_work_log` VALUES (3, 4, 7, '{\"work\": \"完成了用户管理相关API的开发\", \"problem\": \"权限校验逻辑需要优化\", \"plan\": \"优化权限校验，开发角色管理API\"}', 8.00, '2025-05-20', '已通过', 1, '没意见', '2025-08-06 19:45:53', 4, 0, '2025-05-22 16:05:47', '2025-08-06 19:45:53');

SET FOREIGN_KEY_CHECKS = 1;
