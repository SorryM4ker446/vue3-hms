import { createRouter, createWebHistory } from 'vue-router'
// 手动引入 ElMessage (解决报错 1 和 3)
import { ElMessage } from 'element-plus'

import Layout from '../views/layout/Layout.vue'
import Login from '../views/Login.vue'
import DashboardOverview from '../views/DashboardOverview.vue'
import PatientManagement from '../views/patient/PatientManagement.vue'
import DepartmentManagement from '../views/department/DepartmentManagement.vue'
import UserManagement from '../views/user/UserManagement.vue'
import RoomManagement from '../views/room/RoomManagement.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: { title: '登录' }
    },
    {
      path: '/',
      name: 'Layout',
      component: Layout,
      redirect: '/dashboard',
      meta: { requiresAuth: true },
      children: [
        {
          path: 'dashboard',
          name: 'DashboardOverview',
          component: DashboardOverview,
          meta: { title: '系统概览', icon: 'House' }
        },
        {
          path: 'patients',
          name: 'PatientManagement',
          component: PatientManagement,
          meta: { title: '病人管理', icon: 'UserFilled' }
        },
        {
          path: 'departments',
          name: 'DepartmentManagement',
          component: DepartmentManagement,
          meta: { title: '科室管理', icon: 'OfficeBuilding', roles: [1] }
        },
        {
          path: 'users',
          name: 'UserManagement',
          component: UserManagement,
          meta: { title: '员工管理', icon: 'Avatar', roles: [1] }
        },
        {
          path: 'rooms',
          name: 'RoomManagement',
          component: RoomManagement,
          meta: { title: '病房床位', icon: 'MessageBox' }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard'
    }
  ]
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : null;

  if (to.meta.requiresAuth && !user) {
    ElMessage.warning('请先登录！');
    next('/login')
  } else if (to.path === '/login' && user) {
    next('/dashboard')
  } else if (to.meta.roles && user) {
    // 核心修复：使用类型断言 (as number[]) 解决报错 2
    const requiredRoles = to.meta.roles as number[];
    if (!requiredRoles.includes(user.role)) {
        ElMessage.error('您没有权限访问该页面！');
        next('/dashboard')
    } else {
        next()
    }
  } else {
    next()
  }
})

export default router