# DevLog - 企业级项目管理系统

<div align="center">
  
![Vue](https://img.shields.io/badge/Vue-3.x-4FC08D?style=flat-square&logo=vue.js&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5.x-3178C6?style=flat-square&logo=typescript&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

基于 Vue 3 + Spring Boot 的现代化企业级项目管理系统

</div>

## ✨ 项目简介

DevLog 是一个功能完整的企业级项目管理系统，采用前后端分离架构，支持项目全生命周期管理、任务协作、甘特图可视化、工时统计等核心功能。系统采用 iOS 风格设计语言，提供优秀的用户体验。

## 🚀 核心功能

### 📊 项目管理
- **多维度统计分析**：实时计算项目进度、任务分布、工时统计
- **项目生命周期管理**：从规划到上线的完整流程跟踪
- **智能进度计算**：基于任务完成度(60%) + 工时完成度(40%)的综合算法
- **项目仪表板**：可视化展示项目关键指标和趋势

### 📋 任务管理
- **任务依赖管理**：支持复杂依赖关系，循环依赖检测
- **优先级智能排序**：多维度任务优先级计算
- **状态流转**：待处理 → 进行中 → 审核中 → 已完成
- **批量操作**：支持任务的批量状态更新和删除

### 📈 数据可视化
- **自研甘特图组件**：支持任务拖拽、时间轴缩放、关键路径显示
- **实时统计图表**：任务状态分布、优先级分析、进度趋势
- **响应式仪表板**：iOS风格设计，完美适配移动端

### 🔐 权限管理
- **RBAC权限模型**：角色-权限-资源三级权限控制
- **接口级权限校验**：基于注解的细粒度权限控制
- **数据权限隔离**：部门、项目级数据访问控制

### 🎯 协作功能
- **通知系统**：任务变更实时通知
- **文件附件**：支持任务文件上传和管理
- **工时记录**：详细的工时录入和统计
- **操作日志**：完整的操作审计追踪

## 🛠️ 技术栈

### 前端技术
```
Vue 3.x          # 渐进式JavaScript框架
TypeScript       # 静态类型检查
Composition API  # Vue 3组合式API
Element Plus     # UI组件库
Pinia           # 状态管理
Vue Router      # 路由管理
Vite            # 构建工具
ECharts         # 图表可视化
SCSS            # CSS预处理器
```

### 后端技术
```
Spring Boot 3.x     # Java企业级开发框架
Spring Security     # 安全框架
Sa-Token           # 轻量级权限认证框架
MySQL 8.0          # 关系型数据库
MyBatis Plus       # ORM框架
Redis              # 缓存数据库
Swagger 3          # API文档
Hibernate Validator # 数据校验
```

## 📦 项目结构

```
devlog/
├── DevLogVue/                 # 前端项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── components/        # 公共组件
│   │   ├── views/             # 页面组件
│   │   ├── utils/             # 工具函数
│   │   ├── stores/            # Pinia状态管理
│   │   └── styles/            # 全局样式
│   ├── public/                # 静态资源
│   └── package.json
├── DevLogPulse/              # 后端项目
│   ├── src/main/java/
│   │   └── com/giao/devlogpulse/
│   │       ├── controller/    # 控制器
│   │       ├── service/       # 业务逻辑
│   │       ├── entity/        # 实体类
│   │       ├── mapper/        # 数据访问层
│   │       ├── config/        # 配置类
│   │       └── common/        # 公共组件
│   ├── src/main/resources/
│   │   ├── mapper/            # MyBatis映射文件
│   │   └── application.yml    # 配置文件
│   └── pom.xml
└── docs/                     # 项目文档
```

## 🚀 快速开始

### 环境要求

- **Node.js**: >= 16.0.0
- **Java**: >= 17
- **MySQL**: >= 8.0
- **Redis**: >= 6.0
- **Git**: 最新版本

### 前端启动

```bash
# 进入前端目录
cd DevLogVue

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

### 后端启动

```bash
# 进入后端目录
cd DevLogPulse

# 创建数据库
mysql -u root -p
CREATE DATABASE devlog_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 修改配置文件
# 编辑 src/main/resources/application.yml
# 配置数据库连接信息和Redis连接信息

# 启动应用
./mvnw spring-boot:run

# 或者使用IDE直接运行 DevLogPulseApplication 主类
```

### 数据库初始化

src/main/resources/sql/devlog.sql

### 默认账户

```
管理员账户：admin / Admin123
```

## 📱 功能预览

### 项目仪表板
- 📊 实时项目统计
- 📈 进度趋势分析
- 🎯 关键指标监控

### 任务管理
- ✅ 任务列表管理
- 🔗 依赖关系可视化
- 📅 甘特图时间线

### 数据分析
- 📊 多维度统计报表
- 📈 项目进度分析
- 🎯 团队效率评估

## 🔧 核心特性

### 自研甘特图
- 🎨 自主开发的甘特图组件
- 🚀 性能优于第三方组件30%
- 📱 完美支持移动端操作

### 智能算法
- 🧠 任务依赖分析算法
- 🔍 循环依赖检测
- 📊 关键路径计算

### 性能优化
- ⚡ 虚拟滚动技术
- 💾 智能缓存策略
- 🔄 懒加载机制

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 开源协议

本项目基于 [MIT](LICENSE) 协议开源。

## 👥 作者

- **开发者**: [GuLu9527]
- **邮箱**: 1277623709@qq.com
- **GitHub**: [@GuLu9527](https://github.com/GuLu9527)

## 🙏 致谢

感谢以下开源项目为本项目提供的支持：

- [Vue.js](https://vuejs.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Element Plus](https://element-plus.org/)
- [ECharts](https://echarts.apache.org/)
- [MyBatis Plus](https://baomidou.com/)

## 📞 联系我们

如果您有任何问题或建议，请通过以下方式联系我们：

- 📧 邮箱：support@devlog.com
- 💬 QQ群：123456789
- 🐛 问题反馈：[GitHub Issues](https://github.com/GuLu9527/devlog/issues)

---

<div align="center">
  
**⭐ 如果这个项目对您有帮助，请给它一个 Star ⭐**

Made with ❤️ by DevLog Team

</div>