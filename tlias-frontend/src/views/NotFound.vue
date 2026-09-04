<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div class="flex h-screen flex-col items-center justify-center bg-slate-50">
    <div class="tlias-title text-[120px] font-bold text-brand-600">404</div>
    <p class="mb-8 text-lg text-slate-500">页面走丢了</p>
    <el-button type="primary" @click="router.push('/')">返回首页</el-button>
  </div>
</template>
