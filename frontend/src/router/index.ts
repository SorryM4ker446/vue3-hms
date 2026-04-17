import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { clearAuthSession, getAuthUser, hasValidSession } from '@/utils/auth'

const Layout = () => import('../views/layout/Layout.vue')
const Login = () => import('../views/Login.vue')
const DashboardOverview = () => import('../views/DashboardOverview.vue')
const PatientManagement = () => import('../views/patient/PatientManagement.vue')
const DepartmentManagement = () => import('../views/department/DepartmentManagement.vue')
const UserManagement = () => import('../views/user/UserManagement.vue')
const RoomManagement = () => import('../views/room/RoomManagement.vue')

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

router.beforeEach((to, from, next) => {
  const user = getAuthUser()
  const loggedIn = hasValidSession()

  if (to.meta.requiresAuth && !loggedIn) {
    clearAuthSession()
    ElMessage.warning('请先登录！')
    next('/login')
    return
  }

  if (to.path === '/login' && loggedIn) {
    next('/dashboard')
    return
  }

  if (to.meta.roles && user) {
    const requiredRoles = to.meta.roles as number[]
    if (!requiredRoles.includes(user.role)) {
      ElMessage.error('您没有权限访问该页面！')
      next('/dashboard')
      return
    }
  }

  next()
})

export default router
