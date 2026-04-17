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
- MySQL 8+

## 快速开始
1. 创建数据库并导入 SQL：`database/hms_db.sql`。
2. 按需设置后端环境变量（不设置则使用开发默认值）：
   - `SPRING_PROFILES_ACTIVE`（默认 `dev`）
   - `DB_URL` / `DB_USERNAME` / `DB_PASSWORD`
   - `JWT_SECRET`
   - `CORS_ALLOWED_ORIGINS`
3. 启动后端：

```bash
cd backend
.\mvnw.cmd spring-boot:run
```

4. 启动前端：

```bash
cd frontend
npm install
# 可选：通过环境变量覆盖后端地址
# set VITE_API_BASE_URL=http://localhost:8080/api
npm run dev
```

## 默认地址
- 前端开发地址: `http://localhost:5173`
- 后端接口地址: `http://localhost:8080`
- 前端当前接口前缀: `http://localhost:8080/api`

## API 模块概览
- 认证: `/api/login`
- 刷新令牌: `/api/refresh`
- 仪表盘: `/api/dashboard`
- 病人管理: `/api/patients`
- 科室管理: `/api/departments`
- 用户管理: `/api/users`
- 病房/床位: `/api/rooms`, `/api/beds`

## 安全与架构优化（已完成）
- 登录改为 `BCrypt` 校验，兼容历史明文密码并在首次登录后自动升级哈希。
- 登录响应不再返回 `password` 字段。
- 增加 JWT 双令牌机制：`accessToken + refreshToken`。
- 后端新增鉴权拦截器，默认保护 `/api/**`（登录与刷新接口除外）。
- CORS 改为环境变量白名单，不再使用全开放 `*`。
- 前端新增 `apiClient`（统一请求、自动携带 token、401 自动刷新重试）。
- 前端路由改为懒加载，减小首屏包体积。

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
