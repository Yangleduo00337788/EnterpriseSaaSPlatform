<div align="center">

# FlowX 流擎

**企业级审批协同与流程自动化平台**

<img src="docs/images/logo.png" width="120" height="120" alt="FlowX Logo" style="border-radius: 24px; box-shadow: 0 4px 16px rgba(0,0,0,0.1);" />



<br />

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3-blue?logo=vuedotjs&logoColor=white)](https://vuejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.4-blue?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7-red?logo=redis&logoColor=white)](https://redis.io/)
[![License](https://img.shields.io/badge/License-Proprietary-blue)](#license)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker&logoColor=white)](docker/)

<br />

[快速开始](#-快速开始) · [功能特性](#-功能特性) · [系统架构](#-系统架构) · [项目结构](#-项目结构) · [开发指南](#-开发指南) · [部署文档](#-部署方案)

</div>

---

## 📖 项目简介

**FlowX 流擎** 是一款面向中大型企业的审批协同与流程自动化 SaaS 平台。基于 **模块化单体架构（Modular Monolith）** 设计，集成了 Flowable 工作流引擎、AI 智能助手、多租户体系等核心能力，支持 10 万+ 用户、1000+ 企业租户的高并发场景。

### 核心价值

| 价值维度 | 说明 |
|---------|------|
| 🏢 **多租户 SaaS** | 数据库共享 + 字段级租户隔离，支持租户套餐、容量管理 |
| ⚙️ **流程自动化** | 基于 Flowable 7.x 的 BPMN 2.0 工作流引擎，支持会签/或签/加签/转办 |
| 🤖 **AI 智能** | 统一 AIProvider 抽象，接入 DeepSeek / OpenAI / Claude / Gemini / 通义千问 |
| 🔐 **企业级安全** | JWT + RBAC + 数据权限 + 接口限流 + 操作审计 |
| 📊 **数据洞察** | 内置审批、员工、组织、租户、运营多维度报表 |

---

## ✨ 功能特性

<details>
<summary><b>👤 用户中心</b></summary>

- 用户注册 / 手机号登录 / 邮箱登录
- JWT 认证 + 多端登录 + 设备管理
- 密码找回 / 验证码 / 登录风控
</details>

<details>
<summary><b>🔐 RBAC 权限体系</b></summary>

- 用户 / 角色 / 菜单 / 按钮权限 / 数据权限
- 组织架构 / 部门管理 / 岗位管理
- 五级数据权限：全部 / 本部门 / 本部门及以下 / 仅本人 / 自定义
</details>

<details>
<summary><b>🏢 多租户体系</b></summary>

- 租户管理 / 租户套餐 / 租户管理员
- 租户资源统计 / 容量管理
- 数据库共享 + tenant_id 字段隔离
</details>

<details>
<summary><b>✅ 审批中心</b></summary>

- 请假 / 报销 / 采购 / 合同 / 自定义审批
- 会签 / 或签 / 加签 / 转办 / 抄送
- 撤回 / 催办 / 审批意见 / 流程追踪
</details>

<details>
<summary><b>🤖 AI 助手</b></summary>

- AI 审批建议 / AI 流程生成
- AI 知识问答 / AI 报表分析 / AI 数据洞察
- 支持：OpenAI · DeepSeek · 通义千问 · Claude · Gemini
</details>

<details>
<summary><b>💬 消息中心</b></summary>

- 站内信 / 邮件通知 / 短信通知
- 企业微信通知 / 钉钉通知
- Kafka 异步消息队列
</details>

<details>
<summary><b>📁 文件中心</b></summary>

- 文件上传 / 下载 / 预览 / 分类
- 版本管理 / 权限控制
- MinIO 对象存储
</details>

<details>
<summary><b>📊 数据报表</b></summary>

- 审批统计 / 员工统计 / 组织统计
- 租户统计 / 运营统计
- ECharts 可视化图表
</details>

---

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         客户端层                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                  │
│   │  浏览器   │    │  移动端   │    │  API     │                  │
│   │ (Vue 3)  │    │ (Future) │    │  调用方   │                  │
│   └────┬─────┘    └────┬─────┘    └────┬─────┘                  │
└────────┼──────────────┼──────────────┼───────────────────────────┘
         │              │              │
         ▼              ▼              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Nginx 反向代理 (80/443)                       │
│          静态资源 · API 转发 · SSL/TLS · 限流                     │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│              FlowX 应用 (Spring Boot 3.5 · Port 8080)           │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐            │   │
│  │  │ 认证   │ │ 用户   │ │ 系统   │ │ 流程   │            │   │
│  │  │ 中心   │ │ 中心   │ │ 管理   │ │ 引擎   │            │   │
│  │  └────────┘ └────────┘ └────────┘ └────────┘            │   │
│  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐            │   │
│  │  │ 审批   │ │ 消息   │ │ 文件   │ │  AI    │            │   │
│  │  │ 中心   │ │ 中心   │ │ 中心   │ │ 助手   │            │   │
│  │  └────────┘ └────────┘ └────────┘ └────────┘            │   │
│  │  ┌────────┐ ┌────────┐ ┌──────────────────┐             │   │
│  │  │ 报表   │ │ 管理   │ │    基础设施       │             │   │
│  │  │ 中心   │ │ 后台   │ │ Redis·Kafka·MinIO │             │   │
│  │  └────────┘ └────────┘ └──────────────────┘             │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────────────────┘
          ┌──────────────┼──────────────┐
          ▼              ▼              ▼
   ┌────────────┐ ┌────────────┐ ┌────────────┐
   │ MySQL 8.4  │ │  Redis 7   │ │   Kafka    │
   │  (3306)    │ │  (6379)    │ │  (9092)    │
   └────────────┘ └────────────┘ └────────────┘
          │
          ▼
   ┌────────────┐
   │   MinIO    │
   │  (9000)    │
   └────────────┘
```

### 模块依赖关系

```
flowx-admin (应用入口，聚合所有模块)
  ├── flowx-auth          (认证中心)
  ├── flowx-user          (用户中心)
  ├── flowx-system        (系统管理)
  ├── flowx-workflow      (流程引擎)
  ├── flowx-approval      (审批中心)
  ├── flowx-message       (消息中心)
  ├── flowx-file          (文件中心)
  ├── flowx-ai            (AI 助手)
  ├── flowx-report        (数据报表)
  └── flowx-infrastructure(基础设施)
        └── flowx-common  (公共模块，无外部依赖)
```

---

## 🛠️ 技术栈

| 层级 | 技术选型 | 版本 |
|------|---------|------|
| **语言** | Java | 21 LTS |
| **框架** | Spring Boot | 3.5+ |
| **安全** | Spring Security + JWT | 6.x |
| **ORM** | MyBatis-Flex | 1.9+ |
| **数据库** | MySQL | 8.4 |
| **缓存** | Redis + Redisson | 7.x |
| **消息队列** | Apache Kafka (KRaft) | 3.8+ |
| **对象存储** | MinIO | Latest |
| **工作流** | Flowable | 7.1+ |
| **定时任务** | Quartz | - |
| **前端框架** | Vue 3 + TypeScript | 3.5+ |
| **构建工具** | Vite | 5.x |
| **UI 组件** | Naive UI | 2.39+ |
| **CSS 引擎** | UnoCSS | 0.62+ |
| **图表** | ECharts | 5.5+ |
| **API 文档** | SpringDoc + Knife4j | 2.6+ |
| **容器化** | Docker + Docker Compose | - |
| **CI/CD** | Jenkins / GitHub Actions | - |

---

## 📁 项目结构

```
flowx-platform/
│
├── flowx-common/                # 公共模块：基础实体、统一响应、异常、工具类
│   └── src/
│
├── flowx-infrastructure/        # 基础设施：Redis、Kafka、MinIO、MyBatis 配置
│   └── src/
│
├── flowx-auth/                  # 认证中心：JWT、登录注册、验证码、安全配置
│   └── src/
│
├── flowx-user/                  # 用户中心：用户、角色、菜单、部门、岗位
│   └── src/
│
├── flowx-system/                # 系统管理：租户、字典、配置、日志
│   └── src/
│
├── flowx-workflow/              # 流程引擎：Flowable 集成、流程定义与实例
│   └── src/
│
├── flowx-approval/              # 审批中心：审批类型、审批实例、审批监听
│   └── src/
│
├── flowx-message/               # 消息中心：站内信、邮件、短信、企微、钉钉
│   └── src/
│
├── flowx-file/                  # 文件中心：MinIO 文件上传下载、预览
│   └── src/
│
├── flowx-ai/                    # AI 助手：多模型 Provider、对话、Prompt 模板
│   └── src/
│
├── flowx-report/                # 数据报表：审批、员工、组织、租户、运营统计
│   └── src/
│
├── flowx-admin/                 # 管理后台：应用入口、全局异常、API 文档配置
│   └── src/
│
├── flowx-ui/                    # 前端项目：Vue 3 + TypeScript + Naive UI
│   ├── src/
│   │   ├── api/                 # API 接口封装
│   │   ├── layouts/             # 布局组件
│   │   ├── router/              # 路由配置
│   │   ├── stores/              # Pinia 状态管理
│   │   ├── utils/               # 工具函数
│   │   └── views/               # 页面视图
│   └── package.json
│
├── sql/                         # 数据库初始化脚本（28 张表 + 种子数据）
│   └── flowx_init.sql
│
├── docker/                      # Docker 与 Nginx 配置
│   ├── docker-compose.yml
│   └── nginx.conf
│
├── docs/                        # 项目文档
│   ├── images/logo.png
│   └── deployment.md
│
├── Dockerfile                   # 多阶段 Docker 构建
├── Jenkinsfile                  # Jenkins CI/CD 流水线
├── .github/workflows/ci.yml    # GitHub Actions CI
├── pom.xml                      # Maven 父 POM
└── README.md                    # 项目说明
```

---

## 🚀 快速开始

### 环境要求

| 工具 | 版本要求 |
|------|---------|
| JDK | 21+ |
| Maven | 3.9+ |
| Node.js | 18+ |
| Docker | 24+ |
| Docker Compose | 2.20+ |

### 方式一：Docker Compose 一键启动

```bash
# 1. 克隆项目
git clone https://github.com/flowx/flowx-platform.git
cd flowx-platform

# 2. 启动所有服务
cd docker
docker-compose up -d

# 3. 查看日志
docker-compose logs -f flowx-app

# 4. 访问系统
#    前端页面：http://localhost
#    API 文档：http://localhost:8080/doc.html
#    默认账号：admin / admin123
```

### 方式二：本地开发

```bash
# 1. 启动基础设施
cd docker
docker-compose up -d mysql redis kafka minio

# 2. 初始化数据库
mysql -u root -p < sql/flowx_init.sql

# 3. 构建后端
cd ..
mvn clean package -DskipTests

# 4. 启动后端（IDE 中运行 FlowxAdminApplication）
java -jar flowx-admin/target/flowx-admin-1.0.0-SNAPSHOT.jar

# 5. 启动前端
cd flowx-ui
npm install
npm run dev
```

### 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| **FlowX 前端** | http://localhost:5173 | 开发模式前端 |
| **FlowX 后端** | http://localhost:8080 | API 服务 |
| **API 文档** | http://localhost:8080/doc.html | Knife4j 接口文档 |
| **MinIO 控制台** | http://localhost:9001 | minioadmin / minioadmin |

---

## 💻 开发指南

### 后端开发

```bash
# 导入 IntelliJ IDEA
# 主入口类：com.flowx.admin.FlowxAdminApplication
# 端口：8080
# API 前缀：/api/
```

### 前端开发

```bash
cd flowx-ui
npm install
npm run dev     # 启动开发服务器
npm run build   # 构建生产版本
```

### API 规范

统一响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

分页响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [],
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 提交规范

遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

| 前缀 | 说明 |
|------|------|
| `feat:` | 新功能 |
| `fix:` | Bug 修复 |
| `docs:` | 文档更新 |
| `style:` | 代码格式（不影响逻辑） |
| `refactor:` | 代码重构 |
| `test:` | 测试相关 |
| `chore:` | 构建/工具变更 |

---

## 📦 部署方案

详细的生产环境部署指南请参阅 [docs/deployment.md](docs/deployment.md)。

### 部署架构

```
                    ┌─────────────┐
                    │   Nginx     │
                    │  (80/443)   │
                    └──────┬──────┘
                           │
                    ┌──────┴──────┐
                    │  FlowX App  │
                    │  (8080)     │
                    └──────┬──────┘
              ┌────────────┼────────────┐
              ▼            ▼            ▼
       ┌──────────┐ ┌──────────┐ ┌──────────┐
       │  MySQL   │ │  Redis   │ │  Kafka   │
       │  主从    │ │  哨兵    │ │  集群    │
       └──────────┘ └──────────┘ └──────────┘
```

---

## 📄 License

Copyright © 2026 FlowX Team. All rights reserved.

本项目为商业软件，未经授权禁止复制、修改、分发或使用。

---

<div align="center">

**FlowX 流擎** — 让审批更高效，让协作更智能

</div>
