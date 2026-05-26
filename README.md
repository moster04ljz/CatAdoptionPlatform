# 🐱 流浪猫领养平台

## 项目简介
一个基于 SpringBoot + Vue3 的流浪猫领养平台，用户可以浏览待领养的猫咪信息，申请领养，管理员可以管理猫咪和审核领养申请。

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- MyBatis
- MySQL 8.x

### 前端
- Vue 3
- Vite
- Element Plus
- Axios

## 项目结构

```
CatAdoptionPlatform/
├── backend/           # SpringBoot后端
│   ├── src/main/java/com/catadoption/
│   │   ├── controller/  # 控制层
│   │   ├── service/    # 业务层
│   │   ├── dao/        # 数据访问
│   │   └── entity/     # 实体类
│   └── src/main/resources/
├── frontend/           # Vue3前端
│   ├── src/
│   │   ├── api/       # 接口
│   │   ├── components/# 组件
│   │   ├── router/    # 路由
│   │   └── views/     # 页面
│   └── public/
└── database.sql       # 数据库脚本
```

## 快速开始

### 1. 初始化数据库
```bash
mysql -u root -p < database.sql
```

### 2. 启动后端
```bash
cd backend
mvn spring-boot:run
```
后端运行在 http://localhost:8080

### 3. 启动前端
```bash
cd frontend
npm install
npm run dev
```
前端运行在 http://localhost:3000

## 功能列表

- [x] 猫咪浏览
- [x] 用户登录/注册
- [x] 申请领养
- [x] 领养审核（管理员）
- [x] 猫咪管理（管理员）

## 默认账号

- 管理员: admin / admin
- 测试用户: test / 123456