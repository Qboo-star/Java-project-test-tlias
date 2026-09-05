<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Refresh, Edit, Delete } from '@element-plus/icons-vue'
import { confirmDelete } from '@/utils/confirm'
import {
  getDeptList,
  getDeptById,
  addDept,
  updateDept,
  deleteDept,
  type Dept,
} from '@/api/dept'

const list = ref<Dept[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const submitting = ref(false)
const formRef = ref()
const form = reactive<{ id?: number; name: string }>({ name: '' })

const rules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
}

// 时间格式化
function formatTime(t?: string) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 16)
}

async function loadList() {
  loading.value = true
  try {
    list.value = await getDeptList()
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.id = undefined
  form.name = ''
  formRef.value?.clearValidate()
}

function openAdd() {
  dialogTitle.value = '新增部门'
  resetForm()
  dialogVisible.value = true
}

async function openEdit(row: Dept) {
  dialogTitle.value = '编辑部门'
  resetForm()
  dialogVisible.value = true
  const data = await getDeptById(row.id)
  form.id = data.id
  form.name = data.name
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    submitting.value = true
    try {
      if (form.id) {
        await updateDept({ id: form.id, name: form.name })
        ElMessage.success('修改成功')
      } else {
        await addDept({ name: form.name })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadList()
    } finally {
      submitting.value = false
    }
  })
}

async function handleDelete(row: Dept) {
  await confirmDelete({
    title: '确认删除部门',
    message: `确认删除部门「${row.name}」吗？删除后相关数据将无法恢复。`,
    confirmText: '确认删除',
    type: 'danger',
  })
  await deleteDept(row.id)
  ElMessage.success('删除成功')
  loadList()
}

onMounted(loadList)
</script>

<template>
  <div class="dept-page">
    <el-card shadow="never" class="card">
      <div class="toolbar">
        <div class="toolbar-title tlias-title">部门管理</div>
        <div class="toolbar-actions">
          <el-button :icon="Refresh" circle @click="loadList" />
          <el-button type="primary" :icon="Plus" @click="openAdd">
            新增部门
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="list"
        stripe
        class="data-table"
        :header-cell-style="{ background: '#f8fafc', color: '#334155' }"
      >
        <el-table-column type="index" label="序号" width="70" align="center" />
        <el-table-column prop="name" label="部门名称" min-width="160" />
        <el-table-column label="创建时间" min-width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="最后修改时间" min-width="170">
          <template #default="{ row }">{{ formatTime(row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              :icon="Edit"
              @click="openEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              link
              :icon="Delete"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无部门数据" />
        </template>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="420px">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent
      >
        <el-form-item label="部门名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入部门名称"
            maxlength="20"
            clearable
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.card {
  border-radius: 12px;
  border: none;
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
.toolbar-actions {
  display: flex;
  gap: 8px;
}
.data-table {
  width: 100%;
}
:deep(.data-table .el-table__row:hover > td) {
  background-color: #f0fdfa !important;
}
</style>
