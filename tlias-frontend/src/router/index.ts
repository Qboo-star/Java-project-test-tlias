import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

// 定义路由配置
const routes = [
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

export default router
