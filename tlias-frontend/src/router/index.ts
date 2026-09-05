import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

// 定义路由配置
const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/',
    component: Layout,
    redirect: '/depts',
    children: [
      {
        path: 'depts',
        name: 'depts',
        component: () => import('@/views/dept/index.vue'),
        meta: { title: '部门管理', icon: 'OfficeBuilding' },
      },
      {
        path: 'emps',
        name: 'emps',
        component: () => import('@/views/emp/index.vue'),
        meta: { title: '员工管理', icon: 'User' },
      },
      {
        path: 'clazzs',
        name: 'clazzs',
        component: () => import('@/views/clazz/index.vue'),
        meta: { title: '班级管理', icon: 'School' },
      },
      {
        path: 'students',
        name: 'students',
        component: () => import('@/views/student/index.vue'),
        meta: { title: '学员管理', icon: 'Avatar' },
      },
      {
        path: 'reports',
        name: 'reports',
        component: () => import('@/views/report/index.vue'),
        meta: { title: '数据统计', icon: 'TrendCharts' },
      },
      {
        path: 'upload',
        name: 'upload',
        component: () => import('@/views/upload/index.vue'),
        meta: { title: '文件上传', icon: 'Upload' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'notFound',
    component: () => import('@/views/NotFound.vue'),
  },
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫：未登录跳转登录页
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('tlias_token')
  if (to.path === '/login') {
    next()
    return
  }
  if (!token) {
    next('/login')
    return
  }
  next()
})

export default router
