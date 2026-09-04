import axios, { type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

// 统一响应结构
export interface Result<T = any> {
  code: number
  msg: string
  data: T
}

// 分页响应结构
export interface PageResult<T> {
  total: number
  rows: T[]
}

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 预留 token 鉴权位（后端目前未启用）
    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截器：统一拆解 Result，业务失败抛错
service.interceptors.response.use(
  (response) => {
    const res = response.data as Result
    // 非 JSON（如文件流）直接返回
    if (res === null || typeof res !== 'object' || res.code === undefined) {
      return response.data
    }
    if (res.code === 1) {
      return res.data
    }
    // 业务失败
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      ElMessage.error('未登录或登录已过期')
    } else if (status && status >= 500) {
      ElMessage.error('服务器异常，请稍后重试')
    } else {
      ElMessage.error(error.message || '网络异常')
    }
    return Promise.reject(error)
  },
)

export default service
