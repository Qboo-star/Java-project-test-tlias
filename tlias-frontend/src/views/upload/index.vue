<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled, CopyDocument, Delete } from '@element-plus/icons-vue'
import { useUploadStore, type UploadRecord } from '@/stores/upload'

const uploadStore = useUploadStore()

// 最近一次上传结果
const lastUrl = ref('')
const lastName = ref('')

function handleSuccess(res: any, file: File) {
  // 后端按文档应返回 data 为图片 URL
  const url = res?.data || ''
  if (!url) {
    ElMessage.warning('上传成功，但后端未返回图片地址')
    return
  }
  lastUrl.value = url
  lastName.value = file.name
  uploadStore.add({
    name: file.name,
    size: file.size,
    time: new Date().toLocaleString('zh-CN', { hour12: false }),
    url,
  })
  ElMessage.success('上传成功')
}

function beforeUpload(file: File) {
  const isImg = ['image/jpeg', 'image/png'].includes(file.type)
  const lt10M = file.size / 1024 / 1024 < 10
  if (!isImg) {
    ElMessage.error('仅支持 jpg / png 格式')
    return false
  }
  if (!lt10M) {
    ElMessage.error('图片大小不能超过 10MB')
    return false
  }
  return true
}

async function copyUrl(url: string) {
  try {
    await navigator.clipboard.writeText(url)
    ElMessage.success('已复制链接')
  } catch {
    ElMessage.warning('复制失败，请手动复制')
  }
}

function formatSize(size: number) {
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + ' KB'
  return (size / 1024 / 1024).toFixed(2) + ' MB'
}

function removeRecord(row: UploadRecord) {
  uploadStore.remove(row.url)
}
</script>

<template>
  <div class="upload-page">
    <el-card shadow="never" class="card">
      <div class="toolbar">
        <div class="toolbar-title tlias-title">文件上传</div>
      </div>

      <el-upload
        action="/api/upload"
        drag
        multiple
        :show-file-list="false"
        :before-upload="beforeUpload"
        :on-success="handleSuccess"
        accept="image/jpeg,image/png"
        class="uploader"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          将图片拖到此处，或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            仅支持 jpg / png 格式，单文件不超过 10MB
          </div>
        </template>
      </el-upload>
    </el-card>

    <!-- 最近上传结果 -->
    <el-card v-if="lastUrl" shadow="never" class="card result-card">
      <div class="toolbar">
        <div class="toolbar-title tlias-title">最近上传</div>
      </div>
      <div class="result">
        <img :src="lastUrl" class="result-img" :alt="lastName" />
        <div class="result-info">
          <div class="result-name">{{ lastName }}</div>
          <div class="result-url">{{ lastUrl }}</div>
          <el-button
            type="primary"
            plain
            size="small"
            :icon="CopyDocument"
            @click="copyUrl(lastUrl)"
          >
            复制链接
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 上传历史 -->
    <el-card shadow="never" class="card">
      <div class="toolbar">
        <div class="toolbar-title tlias-title">上传历史</div>
        <el-button
          v-if="uploadStore.records.length"
          type="danger"
          plain
          size="small"
          :icon="Delete"
          @click="uploadStore.clear()"
        >
          清空
        </el-button>
      </div>
      <el-table
        v-if="uploadStore.records.length"
        :data="uploadStore.records"
        stripe
        :header-cell-style="{ background: '#f8fafc', color: '#334155' }"
      >
        <el-table-column label="预览" width="80" align="center">
          <template #default="{ row }">
            <el-image
              :src="row.url"
              fit="cover"
              class="thumb"
              :preview-src-list="[row.url]"
              preview-teleported
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="文件名" min-width="180" />
        <el-table-column label="大小" width="100" align="right">
          <template #default="{ row }">{{ formatSize(row.size) }}</template>
        </el-table-column>
        <el-table-column prop="time" label="上传时间" width="180" />
        <el-table-column label="链接" min-width="200">
          <template #default="{ row }">
            <span class="link-text">{{ row.url }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              :icon="CopyDocument"
              @click="copyUrl(row.url)"
            >
              复制
            </el-button>
            <el-button
              type="danger"
              link
              :icon="Delete"
              @click="removeRecord(row)"
            >
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无上传记录" />
    </el-card>
  </div>
</template>

<style scoped>
.card {
  border-radius: 12px;
  border: none;
  margin-bottom: 16px;
}
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.toolbar-title {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
}
.uploader :deep(.el-upload-dragger) {
  width: 100%;
  padding: 40px 20px;
  border-color: #cbd5e1;
  transition: border-color 0.2s, background-color 0.2s;
}
.uploader :deep(.el-upload-dragger:hover) {
  border-color: #0d9488;
  background-color: #f0fdfa;
}
.uploader :deep(.el-icon--upload) {
  color: #0d9488;
}
.uploader :deep(.el-upload__text em) {
  color: #0d9488;
  font-style: normal;
}
.result {
  display: flex;
  gap: 20px;
  align-items: center;
}
.result-img {
  width: 140px;
  height: 140px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}
.result-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
  min-width: 0;
}
.result-name {
  font-weight: 600;
  color: #0f172a;
}
.result-url {
  word-break: break-all;
  color: #64748b;
  font-size: 13px;
  background: #f8fafc;
  padding: 8px 10px;
  border-radius: 6px;
}
.thumb {
  width: 40px;
  height: 40px;
  border-radius: 6px;
}
.link-text {
  color: #64748b;
  font-size: 13px;
  word-break: break-all;
}
</style>
