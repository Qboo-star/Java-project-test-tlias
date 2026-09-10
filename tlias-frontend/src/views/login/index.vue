<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '@/api/request'

const router = useRouter()
const loading = ref(false)
const formRef = ref()

const form = reactive({
  username: 'admin',
  password: '123456',
})

// 登录成功后后端返回的员工信息（含 token）
interface LoginResult {
  id: number
  username: string
  name: string
  token: string
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
  ],
}

async function handleLogin() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    loading.value = true
    try {
      // 调用登录接口，成功后后端返回 { id, username, name, token }
      const res = await request.post<LoginResult, LoginResult>('/login', form)
      const token = res?.token
      if (!token) {
        ElMessage.error('登录失败，请稍后重试')
        return
      }
      localStorage.setItem('tlias_token', token)
      localStorage.setItem('tlias_user', res?.name || form.username)
      ElMessage.success('登录成功')
      router.replace('/')
    } catch {
      // 账号密码错误等提示已由响应拦截器统一弹出，留在登录页
    } finally {
      loading.value = false
    }
  })
}
</script>

<template>
  <div class="login-page tlias-login-bg">
    <div class="login-card">
      <!-- 品牌区 -->
      <div class="brand">
        <div class="brand-mark">T</div>
        <div class="brand-text">
          <div class="brand-title tlias-title">Tlias 管理系统</div>
          <div class="brand-sub">智能教学运营中后台</div>
        </div>
      </div>

      <div class="welcome">
        <h2 class="welcome-title tlias-title">欢迎回来</h2>
        <p class="welcome-desc">请使用您的账号密码登录</p>
      </div>

      <!-- 表单 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        size="large"
        @submit.prevent
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <div class="form-extra">
          <el-checkbox>记住我</el-checkbox>
          <a class="forget" href="javascript:;">忘记密码？</a>
        </div>
        <el-button
          type="primary"
          class="login-btn"
          :loading="loading"
          @click="handleLogin"
        >
          登 录
        </el-button>
      </el-form>

      <div class="tip">演示账号：admin / 123456</div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f172a 0%, #134e4a 100%);
  padding: 24px;
}
.login-card {
  position: relative;
  z-index: 1;
  width: 420px;
  background: rgba(255, 255, 255, 0.97);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px 36px 32px;
  box-shadow: 0 25px 60px -15px rgba(0, 0, 0, 0.4);
}
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
}
.brand-mark {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #0d9488, #14b8a6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 800;
  font-size: 22px;
  font-family: "Sora", sans-serif;
  box-shadow: 0 8px 16px -4px rgba(13, 148, 136, 0.5);
}
.brand-title {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}
.brand-sub {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}
.welcome {
  margin-bottom: 24px;
}
.welcome-title {
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 6px;
}
.welcome-desc {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}
.form-extra {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  font-size: 13px;
}
.forget {
  color: #0d9488;
  text-decoration: none;
}
.forget:hover {
  text-decoration: underline;
}
.login-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #0d9488, #14b8a6);
  border: none;
  box-shadow: 0 8px 16px -4px rgba(13, 148, 136, 0.4);
}
.login-btn:hover {
  background: linear-gradient(135deg, #0f766e, #0d9488);
}
.tip {
  text-align: center;
  margin-top: 20px;
  font-size: 12px;
  color: #94a3b8;
}
:deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 12px;
}
</style>
