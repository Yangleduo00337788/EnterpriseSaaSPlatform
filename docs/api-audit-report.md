# FlowCloud 后端接口审计报告

## 1. 审计结论

- 审计时间: 2026-06-07
- 审计范围: `flowcloud-system`、`flowcloud-approval`、`flowcloud-notification`、`flowcloud-report`
- 后端接口总数: 72
- 实际冒烟测试请求数: 78
- 实际可用接口: 78/78
- 前端已使用的后端接口: 65
- 前端未使用的后端接口: 7

结论:

1. 当前前端所需接口未发现缺失。
2. 后端存在少量未被当前前端调用的接口，属于“可能冗余接口”。
3. 当前 `README.md` 中的接口文档已与实际实现保持一致，历史不一致项已修正。

## 2. 审计方法与统计口径

### 2.1 测试方法

- 源码扫描: 扫描后端 Controller 中的 `@RequestMapping`、`@GetMapping`、`@PostMapping`、`@PutMapping`、`@DeleteMapping`，汇总真实接口清单。
- 前端比对: 扫描 `flowcloud-web/src/api` 中的 `request.get/post/put/delete` 与 `fetch('/api/...')` 调用，归一化后与后端接口逐项匹配。
- 文档复核: 对比 `README.md` 中 API 表格与当前后端实现，检查请求方法与路径是否一致。
- 冒烟验证: 以实际可运行环境发送接口请求，验证接口是否可达、权限链路是否正常、响应结构是否符合预期。

### 2.2 统计口径

- 后端接口总数: 以四个审计模块 Controller 中声明的 HTTP 接口为准，同一路径不同请求方法分别计数。
- 前端已使用接口: 以前端 API 封装层存在明确调用，且能与后端唯一匹配的接口为准。
- 前端未使用接口: 以后端存在实现、但前端 API 封装层未发现对应调用的接口为准。
- 当前无缺失接口: 以前端 API 调用层中的接口在后端均能找到实现为判定标准。

### 2.3 自动化校验说明

- 本次审计已使用脚本对后端接口和前端 API 调用进行归一化比对。
- 路径归一化规则包括: 去除 `/api` 前缀差异、统一路径参数占位形式、消除尾部斜杠差异。
- 自动化结果显示: 后端接口 72 个，前端命中 65 个，前端缺失 0 个，后端未使用 7 个。
- 后续建议将该脚本沉淀为固定审计步骤，作为版本发布前的接口治理检查项。

## 3. README 状态复核

当前代码复核结果:

- `README.md` 当前已正确写为 `GET /api/approval/instances`
- `README.md` 当前已正确写为 `PUT /api/approval/instances/{id}/cancel`
- 本次复核未发现 `README.md` 与当前后端实现存在接口路径或请求方法不一致问题

## 4. 当前无缺失接口

以下前端 API 调用在后端均已找到对应实现，并已完成真实请求验证:

- 认证: 登录、注册、当前用户
- 租户: 当前租户详情、更新当前租户
- 用户: 列表、选项、新增、修改、删除、状态切换、导入、导出
- 角色: 列表、选项、详情、新增、修改、删除
- 权限: 权限树
- 部门: 部门树、新增、修改、删除
- 字典: 列表、详情、创建、更新、删除
- 岗位: 列表、创建、更新、删除
- 消息模板: 列表、创建、更新、删除
- 消息中心: 列表、未读数、单条已读、批量已读、全部已读
- 审批模板: 列表、管理列表、详情、创建、更新、发布、停用、版本历史、删除
- 审批实例: 我的申请、全部审批、详情、提交、撤销
- 审批任务: 待办、已办、完成、催办
- 附件: 上传、列表
- 报表: 仪表盘、分析
- 审计日志: 分页查询

## 5. 可能冗余接口

以下接口在当前后端中存在，但前端 API 调用层未发现对应使用方:

### 5.1 用户模块

- `GET /api/system/users/{id}`
- `PUT /api/system/users/{id}/reset-password`

说明:

- 当前前端用户页使用列表数据直接编辑，未单独拉取用户详情。
- 当前前端也未提供“管理员重置密码”入口。

### 5.2 字典模块

- `GET /api/system/dicts/code/{dictCode}`

说明:

- 当前前端字典页走的是列表与详情接口，未按编码加载字典项。
- 该接口适合作为表单下拉、通用配置中心或移动端复用接口保留。

### 5.3 消息模板模块

- `GET /api/system/message-templates/{id}`

说明:

- 当前前端消息模板页未单独请求详情，通常依赖列表行数据回填。

### 5.4 岗位模块

- `GET /api/system/positions/user/{userId}`
- `PUT /api/system/positions/user/{userId}`

说明:

- 当前前端只做岗位基础 CRUD，未做“用户-岗位绑定”管理。

### 5.5 附件模块

- `DELETE /api/attachments/{id}`

说明:

- 当前前端只有上传和查询，没有显式删除附件操作。

## 6. 后端接口全量清单

### 6.1 认证模块

- `POST /api/auth/register` - 已使用
- `POST /api/auth/login` - 已使用
- `GET /api/auth/me` - 已使用

### 6.2 租户模块

- `GET /api/system/tenant/current` - 已使用
- `PUT /api/system/tenant/current` - 已使用

### 6.3 用户模块

- `GET /api/system/users/options` - 已使用
- `GET /api/system/users` - 已使用
- `GET /api/system/users/{id}` - 未使用
- `POST /api/system/users` - 已使用
- `PUT /api/system/users/{id}` - 已使用
- `DELETE /api/system/users/{id}` - 已使用
- `PUT /api/system/users/{id}/reset-password` - 未使用
- `PUT /api/system/users/{id}/status` - 已使用
- `GET /api/system/users/export` - 已使用
- `POST /api/system/users/import` - 已使用

### 6.4 角色模块

- `GET /api/system/roles/options` - 已使用
- `GET /api/system/roles` - 已使用
- `GET /api/system/roles/{id}` - 已使用
- `POST /api/system/roles` - 已使用
- `PUT /api/system/roles/{id}` - 已使用
- `DELETE /api/system/roles/{id}` - 已使用

### 6.5 权限模块

- `GET /api/system/permissions/tree` - 已使用

### 6.6 部门模块

- `GET /api/system/depts` - 已使用
- `POST /api/system/depts` - 已使用
- `PUT /api/system/depts/{id}` - 已使用
- `DELETE /api/system/depts/{id}` - 已使用

### 6.7 字典模块

- `GET /api/system/dicts` - 已使用
- `GET /api/system/dicts/code/{dictCode}` - 未使用
- `GET /api/system/dicts/{id}` - 已使用
- `POST /api/system/dicts` - 已使用
- `PUT /api/system/dicts/{id}` - 已使用
- `DELETE /api/system/dicts/{id}` - 已使用

### 6.8 岗位模块

- `GET /api/system/positions` - 已使用
- `POST /api/system/positions` - 已使用
- `PUT /api/system/positions/{id}` - 已使用
- `DELETE /api/system/positions/{id}` - 已使用
- `GET /api/system/positions/user/{userId}` - 未使用
- `PUT /api/system/positions/user/{userId}` - 未使用

### 6.9 审计日志模块

- `GET /api/system/audit-logs` - 已使用

### 6.10 消息模板模块

- `GET /api/system/message-templates` - 已使用
- `GET /api/system/message-templates/{id}` - 未使用
- `POST /api/system/message-templates` - 已使用
- `PUT /api/system/message-templates/{id}` - 已使用
- `DELETE /api/system/message-templates/{id}` - 已使用

### 6.11 消息中心模块

- `GET /api/messages` - 已使用
- `GET /api/messages/unread-count` - 已使用
- `PUT /api/messages/{id}/read` - 已使用
- `PUT /api/messages/read-all` - 已使用
- `PUT /api/messages/batch-read` - 已使用

### 6.12 审批模板模块

- `GET /api/approval/templates` - 已使用
- `GET /api/approval/templates/all` - 已使用
- `GET /api/approval/templates/{id}` - 已使用
- `POST /api/approval/templates` - 已使用
- `PUT /api/approval/templates/{id}` - 已使用
- `POST /api/approval/templates/{id}/publish` - 已使用
- `POST /api/approval/templates/{id}/disable` - 已使用
- `GET /api/approval/templates/{id}/versions` - 已使用
- `DELETE /api/approval/templates/{id}` - 已使用

### 6.13 审批实例模块

- `GET /api/approval/instances/my` - 已使用
- `GET /api/approval/instances` - 已使用
- `GET /api/approval/instances/{id}` - 已使用
- `POST /api/approval/instances` - 已使用
- `PUT /api/approval/instances/{id}/cancel` - 已使用

### 6.14 审批任务模块

- `GET /api/approval/tasks/pending` - 已使用
- `GET /api/approval/tasks/handled` - 已使用
- `POST /api/approval/tasks/complete` - 已使用
- `POST /api/approval/tasks/{id}/remind` - 已使用

### 6.15 附件模块

- `POST /api/attachments/upload` - 已使用
- `GET /api/attachments` - 已使用
- `DELETE /api/attachments/{id}` - 未使用

### 6.16 报表模块

- `GET /api/report/dashboard` - 已使用
- `GET /api/report/analytics` - 已使用

## 7. 建议动作

### 7.1 必做

- 维护本报告中的统计口径与源码一致，避免接口总数和前端使用数出现偏差。
- 将本报告作为接口治理基线，避免继续新增“前端无入口、文档无说明”的接口。

### 7.2 分级治理建议

#### A. 建议保留

| 接口 | 建议 | 原因 |
|------|------|------|
| `GET /api/system/dicts/code/{dictCode}` | 保留 | 适合表单下拉、配置中心、移动端按编码取值场景，复用价值高 |
| `GET /api/system/users/{id}` | 保留 | 当前用户页虽然未单独拉详情，但后续一旦表单复杂化、增加抽屉详情页，会直接需要 |
| `GET /api/system/message-templates/{id}` | 保留 | 当前页面依赖列表行回填，但单独详情接口对抽屉查看、只读模式、权限隔离更稳妥 |

处理建议:

- 在 OpenAPI 或接口注释中标注“预留给详情页/通用表单场景”。

#### B. 建议补前端入口

| 接口 | 建议 | 原因 |
|------|------|------|
| `PUT /api/system/users/{id}/reset-password` | 补前端入口 | 管理后台通常应具备管理员重置密码能力，该接口具备明确业务价值 |
| `GET /api/system/positions/user/{userId}` | 补前端入口 | 当前已存在“岗位”能力，但缺少“用户-岗位绑定”界面，能力不完整 |
| `PUT /api/system/positions/user/{userId}` | 补前端入口 | 与上一个接口成对出现，适合在用户管理或岗位管理中补一个绑定弹窗 |
| `DELETE /api/attachments/{id}` | 补前端入口 | 当前详情页已展示附件，若后续支持草稿编辑、重新上传、撤回调整，删除能力会直接用到 |

处理建议:

- 用户管理页补“重置密码”操作按钮。
- 用户页或岗位页补“分配岗位”弹窗。
- 附件上传区域补“删除附件”能力，至少在草稿态和申请未提交时允许删除。

#### C. 暂不建议删除

| 接口 | 结论 | 原因 |
|------|------|------|
| 上述 7 个接口全部 | 暂不建议直接删除 | 数量少、维护成本低，且多数具备明显扩展价值；当前更像“未接入完成”，不是“纯废弃接口” |

删除前置条件:

- 连续两个版本无调用计划。
- Swagger 或接口文档中无保留说明。
- 产品确认未来不会补入口。

### 7.3 可选优化

- 为“前端未使用接口”增加 OpenAPI 标签或备注，例如“预留接口”“管理端二期”“移动端复用”。
- 给导出类接口增加统一测试脚本适配，避免二进制下载被错误标记为失败。
- 在接口审计流程中增加一条规则: 新增接口时必须同步标注“调用方页面”或“预留用途”。
