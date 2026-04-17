# Frontend (Vue 3 + Vite)

该目录是医院管理系统前端项目。

## 运行命令

```bash
npm install
# 可选：设置后端 API 基址（默认 http://localhost:8080/api）
# set VITE_API_BASE_URL=http://localhost:8080/api
npm run dev
```

## 构建命令

```bash
npm run build
```

## 相关说明
- 默认后端接口前缀为 `http://localhost:8080/api`（由 `src/api/client.ts` 统一管理）。
- 鉴权方案为 JWT（`accessToken + refreshToken`），401 会自动尝试刷新并重试请求。
- 项目完整说明请查看仓库根目录的 `README.md`。
