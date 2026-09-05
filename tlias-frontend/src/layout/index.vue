<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  OfficeBuilding,
  User,
  Upload,
  Fold,
  Expand,
  Refresh,
  School,
  Avatar,
  TrendCharts,
  SwitchButton,
  Setting,
} from '@element-plus/icons-vue'
import { confirmDelete } from '@/utils/confirm'

const route = useRoute()
const router = useRouter()

// 折叠侧边栏
const isCollapse = ref(false)

// 菜单项
const menus = [
  { path: '/depts', title: '部门管理', icon: OfficeBuilding },
  { path: '/emps', title: '员工管理', icon: User },
  { path: '/clazzs', title: '班级管理', icon: School },
  { path: '/students', title: '学员管理', icon: Avatar },
  { path: '/reports', title: '数据统计', icon: TrendCharts },
  { path: '/upload', title: '文件上传', icon: Upload },
]

// 当前激活菜单
const activeMenu = computed(() => route.path)

// 面包屑标题
const currentTitle = computed(() => (route.meta.title as string) || '')

// 当前用户名
const username = computed(() => localStorage.getItem('tlias_user') || '管理员')

function handleRefresh() {
  router.replace({ path: '/redirect' + route.fullPath })
}

async function handleLogout() {
  await confirmDelete({
    title: '确认退出登录',
    message: '退出后需要重新登录才能继续访问系统。',
    confirmText: '退出登录',
    type: 'warning',
  })
  localStorage.removeItem('tlias_token')
  localStorage.removeItem('tlias_user')
  ElMessage.success('已退出登录')
  router.replace('/login')
}
</script>

<template>
  <el-container class="h-screen">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <span class="logo-mark">T</span>
        <span v-show="!isCollapse" class="logo-text">Tlias 管理</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#0f172a"
        text-color="#cbd5e1"
        active-text-color="#5eead4"
        router
      >
        <el-menu-item v-for="m in menus" :key="m.path" :index="m.path">
          <el-icon><component :is="m.icon" /></el-icon>
          <template #title>{{ m.title }}</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <component :is="isCollapse ? Expand : Fold" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-tooltip content="刷新" placement="bottom">
            <el-icon class="header-icon" @click="handleRefresh">
              <Refresh />
            </el-icon>
          </el-tooltip>
          <el-dropdown trigger="click">
            <div class="user-box">
              <div class="avatar">{{ username.charAt(0).toUpperCase() }}</div>
              <span class="user-name">{{ username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :icon="Setting">个人设置</el-dropdown-item>
                <el-dropdown-item :icon="SwitchButton" divided @click="handleLogout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.sidebar {
  background-color: #0f172a;
  transition: width 0.28s ease;
  overflow-x: hidden;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 18px;
  color: #f1f5f9;
}
.logo-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: linear-gradient(135deg, #0d9488, #14b8a6);
  color: #fff;
  font-weight: 700;
  font-family: "Sora", sans-serif;
}
.logo-text {
  font-family: "Sora", "Noto Sans SC", sans-serif;
  font-weight: 600;
  font-size: 16px;
  white-space: nowrap;
}
:deep(.el-menu) {
  border-right: none;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  padding: 0 20px;
  height: 56px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 18px;
}
.collapse-btn,
.header-icon {
  font-size: 20px;
  color: #475569;
  cursor: pointer;
  transition: color 0.2s;
}
.collapse-btn:hover,
.header-icon:hover {
  color: #0d9488;
}
.user-box {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}
.user-box:hover {
  background: #f1f5f9;
}
.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0d9488, #14b8a6);
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}
.user-name {
  font-size: 13px;
  color: #334155;
  font-weight: 500;
}
.main {
  background-color: #f8fafc;
  padding: 20px;
}
</style>
