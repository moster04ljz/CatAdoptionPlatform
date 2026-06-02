# 流浪猫领养平台

一个基于 Spring Boot 3 + Vue 3 的流浪猫领养与交流平台。平台支持流浪猫信息展示、用户领养申请、猫咪发布审核、用户私信沟通，以及猫咪用品商城等功能，适合作为 Java Web 全栈项目学习和课程项目展示。

## 项目功能

- 用户注册、登录与 JWT 身份认证
- 首页猫咪推荐、领养猫列表、市场猫列表
- 猫咪详情查看、评论互动、领养申请
- 用户发布猫咪信息，管理员审核发布内容
- 我的领养申请、个人信息管理
- 私信聊天与会话列表，支持围绕猫咪发起沟通
- 猫咪用品商城、商品详情展示
- 管理员后台：猫咪管理、领养审核、发布审核
- 图片上传与本地静态资源访问

## 技术栈

### 后端

- Java 17
- Spring Boot 3.2.0
- Spring Web
- MyBatis
- MySQL 8.x
- JWT
- Lombok

### 前端

- Vue 3
- Vite 5
- Vue Router 4
- Element Plus
- Axios

## 项目结构

```text
CatAdoptionPlatform/
├─ backend/                 # Spring Boot 后端
│  ├─ src/main/java/com/catadoption/
│  │  ├─ common/            # 通用返回、JWT、拦截器、跨域配置
│  │  ├─ controller/        # 接口控制层
│  │  ├─ dao/               # MyBatis 数据访问层
│  │  ├─ entity/            # 实体类
│  │  └─ service/           # 业务逻辑层
│  └─ src/main/resources/
│     └─ application.yml    # 后端配置
├─ frontend/                # Vue 前端
│  ├─ src/
│  │  ├─ api/               # Axios 接口封装
│  │  ├─ router/            # 路由配置
│  │  └─ views/             # 页面视图
│  └─ vite.config.js        # Vite 配置与代理
├─ uploads/                 # 本地上传文件目录
├─ database.sql             # 主数据库初始化脚本
├─ database-products.sql    # 商品相关数据脚本
├─ database-update.sql      # 数据库更新脚本
└─ migration.sql            # 迁移脚本
```

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.x

## 快速开始

### 1. 初始化数据库

先创建数据库并导入主脚本：

```sql
CREATE DATABASE cat_adoption DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

```bash
mysql -u root -p cat_adoption < database.sql
```

如需商品演示数据，可继续导入：

```bash
mysql -u root -p cat_adoption < database-products.sql
```

### 2. 配置后端数据库连接

后端配置文件位于：

```text
backend/src/main/resources/application.yml
```

默认读取以下环境变量，也提供了本地默认值：

```yaml
spring:
  datasource:
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:123456}
```

如果你的 MySQL 用户名或密码不同，请修改环境变量或直接调整配置文件。

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端默认运行在：

```text
http://localhost:8080
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在：

```text
http://localhost:3000
```

Vite 已配置 `/api` 和 `/uploads` 代理到后端 `http://localhost:8080`。

## 默认账号

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 管理员 | admin | admin |
| 测试用户 | test | 123456 |

## 常用页面

- 首页：`/`
- 登录：`/login`
- 注册：`/register`
- 领养猫详情：`/cat/:id`
- 猫咪市场：`/market`
- 用品商城：`/shop`
- 我的发布：`/my-cats`
- 我的领养：`/my-adoptions`
- 私信聊天：`/chat`
- 管理后台：`/admin`
- 领养审核：`/adoption-review`

## 注意事项

- `backend/log.txt` 是本地运行日志，不建议提交到 Git。
- 上传文件默认存放在 `uploads/` 目录，后端通过 `/uploads/**` 对外提供访问。
- 如果前端无法访问接口，请确认后端已启动，并检查 `frontend/vite.config.js` 中的代理配置。
- 如果数据库连接失败，请检查 MySQL 服务、数据库名、账号密码和 `application.yml` 配置。
