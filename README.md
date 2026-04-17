# HMS 医院病房管理系统

一个前后端分离的医院病房管理系统，前端使用 Vue 3 + Vite + Element Plus，后端使用 Spring Boot + MyBatis-Plus + MySQL。

## 技术栈
- Frontend: Vue 3, Vue Router, Pinia, Axios, Element Plus, TypeScript, Vite
- Backend: Spring Boot 3, MyBatis-Plus, MySQL, Lombok
- Database: MySQL (`database/hms_db.sql`)

## 项目结构
```text
vue3-hms/
├── backend/                    # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/hms/
│       │   ├── controller/
│       │   ├── entity/
│       │   ├── mapper/
│       │   └── config/
│       └── resources/application.yml
├── frontend/                   # Vue 3 前端
│   ├── package.json
│   ├── public/
│   └── src/
│       ├── router/
│       └── views/
└── database/
    └── hms_db.sql              # 初始化数据库脚本
```

## 环境要求
- Node.js 20+
- npm 10+
- Java 17
- Maven 3.9+
- MySQL 8+

## 快速开始
1. 创建数据库并导入 SQL：`database/hms_db.sql`。
2. 修改后端数据库连接配置：`backend/src/main/resources/application.yml`。
3. 启动后端：

```bash
cd backend
.\mvnw.cmd spring-boot:run
```

4. 启动前端：

```bash
cd frontend
npm install
npm run dev
```

## 默认地址
- 前端开发地址: `http://localhost:5173`
- 后端接口地址: `http://localhost:8080`
- 前端当前接口前缀: `http://localhost:8080/api`

## API 模块概览
- 认证: `/api/login`
- 仪表盘: `/api/dashboard`
- 病人管理: `/api/patients`
- 科室管理: `/api/departments`
- 用户管理: `/api/users`
- 病房/床位: `/api/rooms`, `/api/beds`

## 提交到 GitHub（首次）
```bash
cd D:\vue3-hms
git init
git add .
git commit -m "chore: reorganize project structure and docs"
git branch -M main
git remote add origin <你的仓库地址>
git push -u origin main
```

## 说明
- 当前仓库已整理为单仓结构（`backend` + `frontend` + `database`），便于协作与部署。
- 根目录 `.gitignore` 已包含前后端常见忽略项。
