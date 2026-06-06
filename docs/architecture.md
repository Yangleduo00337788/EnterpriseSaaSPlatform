# 审流云架构设计

## 一、整体架构

采用**模块化单体**架构，各模块独立 jar 包，通过 `flowcloud-admin` 统一启动，后续可按模块拆分为微服务。

```
┌─────────────────────────────────────────────────────────┐
│                    flowcloud-web (React)                 │
│              Semi Design + Redux Toolkit                 │
└────────────────────────┬────────────────────────────────┘
                         │ HTTP / JWT
┌────────────────────────▼────────────────────────────────┐
│                   flowcloud-admin                        │
│         Spring Boot 3 + AuthInterceptor + Swagger        │
├──────────┬──────────┬──────────────┬─────────────────────┤
│ system   │ approval │ notification │ report              │
├──────────┴──────────┴──────────────┴─────────────────────┤
│                   flowcloud-common                       │
│     Result / JWT / TenantContext / MyBatis-Flex          │
└────────────────────────┬────────────────────────────────┘
                         │
              ┌──────────┴──────────┐
              │ MySQL    │   Redis   │
              └──────────┴───────────┘
```

## 二、多租户隔离

- 数据库层：`tenant_id` 字段 + MyBatis-Flex `TenantFactory` 自动注入
- 请求层：`AuthInterceptor` 解析 JWT，设置 `TenantContext`
- 租户注册时自动初始化：根部门、默认角色（管理员/审批人/员工）

## 三、数据库表设计

| 表名 | 说明 |
|------|------|
| sys_tenant | 租户（企业） |
| sys_dept | 部门 |
| sys_user | 用户 |
| sys_role | 角色 |
| sys_user_role | 用户角色关联 |
| sys_permission | 权限 |
| sys_role_permission | 角色权限关联 |
| approval_template | 审批模板 |
| approval_instance | 审批实例 |
| approval_task | 审批任务 |
| approval_record | 审批记录 |
| sys_message | 站内消息 |

## 四、审批流程引擎

1. **模板配置**：`flow_config` JSON 存储多级节点，每节点指定审批人列表
2. **提交审批**：创建实例 → 生成第一节点任务 → 记录发起日志
3. **任务处理**：通过 → 检查同节点是否全部完成 → 流转下一节点或结束；驳回 → 终止流程
4. **状态机**：draft → pending → approved/rejected/cancelled

## 五、前端页面结构

```
src/
├── api/          # auth, user, approval, report
├── layouts/      # MainLayout 侧边栏布局
├── pages/
│   ├── login/    # 登录
│   ├── register/ # 企业注册
│   ├── dashboard/# 工作台
│   ├── approval/ # 待办/我的申请/发起/详情/全部
│   ├── template/ # 流程模板管理
│   └── system/   # 员工管理
├── router/       # 路由 + 鉴权
├── store/        # Redux auth slice
└── types/        # TypeScript 类型
```

## 六、核心 API 清单

### 认证
- `POST /api/auth/register` - 企业注册
- `POST /api/auth/login` - 登录
- `GET /api/auth/me` - 当前用户

### 系统
- `GET/POST/PUT/DELETE /api/system/users` - 员工 CRUD

### 审批
- `GET/POST/PUT/DELETE /api/approval/templates` - 模板管理
- `POST /api/approval/instances` - 提交审批
- `GET /api/approval/instances/my` - 我的申请
- `GET /api/approval/tasks/pending` - 待办
- `POST /api/approval/tasks/complete` - 处理任务

### 报表
- `GET /api/report/dashboard` - 仪表盘统计

### 消息
- `GET /api/messages` - 消息列表
- `GET /api/messages/unread-count` - 未读数

## 七、扩展规划

- **消息队列**：RabbitMQ 异步发送审批通知
- **OAuth2**：对接企业微信/飞书 SSO
- **AI 模块**：审批建议、流程优化推荐
- **拖拽流程编辑器**：可视化配置审批节点
- **报表导出**：EasyExcel / PDF 导出
