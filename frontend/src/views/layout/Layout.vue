<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  House,
  UserFilled,
  OfficeBuilding,
  Avatar,
  MessageBox,
  ArrowDown,
  ArrowRight,
  Expand,
  Fold
} from '@element-plus/icons-vue'
import { clearAuthSession, getAuthUser, hasValidSession } from '@/utils/auth'

const router = useRouter()
const isCollapse = ref(false)
const currentUser = ref({})
const userMenus = ref([])

const iconMap = {
  House,
  UserFilled,
  OfficeBuilding,
  Avatar,
  MessageBox
}

onMounted(() => {
  if (!hasValidSession()) {
    router.push('/login')
    return
  }

  const user = getAuthUser()
  if (!user) {
    router.push('/login')
    return
  }

  currentUser.value = user
  filterMenusByRole()
})

const filterMenusByRole = () => {
  const layoutRoute = router.options.routes.find((r) => r.name === 'Layout')
  const allRoutes = layoutRoute?.children || []
  userMenus.value = allRoutes.filter((route) => {
    return !route.meta?.roles || route.meta.roles.includes(currentUser.value.role)
  })
}

const logout = () => {
  clearAuthSession()
  router.push('/login')
  ElMessage.success('已退出登录')
}
</script>

<template>
  <el-container class="common-layout">
    <el-header class="main-header">
      <div class="header-left">
        <el-icon :size="24" @click="isCollapse = !isCollapse" class="collapse-icon">
          <component :is="isCollapse ? Expand : Fold"></component>
        </el-icon>
        <span class="system-title">医院病房管理系统</span>
      </div>
      <div class="header-right">
        <span>欢迎，{{ currentUser.realName }} ({{ currentUser.role == 1 ? '管理员' : currentUser.role == 2 ? '医生' : '护士' }})</span>
        <el-dropdown trigger="click" style="margin-left: 20px;">
          <span class="el-dropdown-link user-dropdown">
            <el-avatar :size="30" style="margin-right: 8px;">{{ currentUser.realName ? currentUser.realName.charAt(0) : 'U' }}</el-avatar>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人中心</el-dropdown-item>
              <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container class="main-body">
      <el-aside :width="isCollapse ? '64px' : '200px'" class="main-aside">
        <el-menu
          :default-active="$route.path"
          :collapse="isCollapse"
          :router="true"
          class="el-menu-vertical-demo"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
        >
          <el-menu-item
            v-for="menu in userMenus"
            :key="menu.path"
            :index="menu.path.startsWith('/') ? menu.path : '/' + menu.path"
          >
            <el-icon><component :is="iconMap[menu.meta.icon] || House"></component></el-icon>
            <template #title>{{ menu.meta.title }}</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main-content-view">
        <el-breadcrumb :separator-icon="ArrowRight" style="margin-bottom: 20px;">
          <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
        </el-breadcrumb>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.common-layout {
  height: 100vh;
}
.main-header {
  background-color: #409EFF;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.header-left {
  display: flex;
  align-items: center;
}
.collapse-icon {
  cursor: pointer;
  margin-right: 15px;
}
.system-title {
  font-size: 20px;
  font-weight: bold;
}
.header-right {
  display: flex;
  align-items: center;
}
.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: white;
}
.main-body {
  height: calc(100vh - 60px);
}
.main-aside {
  background-color: #545c64;
  transition: width 0.3s;
  overflow-x: hidden;
}
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
.el-menu-vertical-demo {
  border-right: none;
  height: 100%;
}
.main-content-view {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
