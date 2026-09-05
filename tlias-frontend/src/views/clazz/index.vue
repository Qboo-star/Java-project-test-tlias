<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Refresh, Edit, Delete, Search } from '@element-plus/icons-vue'
import { confirmDelete } from '@/utils/confirm'
import {
  getClazzPage,
  getClazzById,
  addClazz,
  updateClazz,
  deleteClazz,
  subjectMap,
  type Clazz,
  type ClazzQueryParam,
} from '@/api/clazz'
import { getEmpPage, type Emp } from '@/api/emp'

const list = ref<Clazz[]>([])
const total = ref(0)
const loading = ref(false)

const query = reactive<ClazzQueryParam>({
  page: 1,
  pageSize: 10,
  name: '',
  begin: '',
  end: '',
})
const dateRange = ref<[string, string] | null>(null)

// 班主任下拉（员工列表）
const empOptions = ref<Emp[]>([])

const subjectOptions = Object.entries(subjectMap).map(([v, label]) => ({
  value: Number(v),
  label,
}))

function formatTime(t?: string) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 16)
}

function subjectLabel(s?: number) {
  return s ? subjectMap[s] || '-' : '-'
}

async function loadList() {
  loading.value = true
  try {
    query.begin = dateRange.value?.[0] || ''
    query.end = dateRange.value?.[1] || ''
    const res = await getClazzPage(query)
    list.value = res.rows
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  query.page = 1
  loadList()
}

function handleReset() {
  query.name = ''
  dateRange.value = null
  query.begin = ''
  query.end = ''
  handleQuery()
}

async function handleDelete(row: Clazz) {
  await confirmDelete({
    title: '确认删除班级',
    message: `确认删除班级「${row.name}」吗？删除后数据将无法恢复。`,
    confirmText: '确认删除',
    type: 'danger',
  })
  await deleteClazz(row.id!)
  ElMessage.success('删除成功')
  if (list.value.length === 1 && query.page! > 1) {
    query.page!--
  }
  loadList()
}

// ===== 对话框 =====
const dialogVisible = ref(false)
const dialogTitle = ref('新增班级')
const submitting = ref(false)
const formRef = ref()
const form = reactive<Clazz>({
  name: '',
  room: '',
  beginDate: '',
  endDate: '',
  masterId: undefined as unknown as number,
  subject: 1,
})

const rules = {
  name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  beginDate: [{ required: true, message: '请选择开班日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  masterId: [{ required: true, message: '请选择班主任', trigger: 'change' }],
  subject: [{ required: true, message: '请选择学科', trigger: 'change' }],
}

function resetForm() {
  Object.assign(form, {
    id: undefined,
    name: '',
    room: '',
    beginDate: '',
    endDate: '',
    masterId: undefined,
    subject: 1,
  })
  formRef.value?.clearValidate()
}

function openAdd() {
  dialogTitle.value = '新增班级'
  resetForm()
  dialogVisible.value = true
}

async function openEdit(row: Clazz) {
  dialogTitle.value = '编辑班级'
  resetForm()
  dialogVisible.value = true
  const data = await getClazzById(row.id!)
  Object.assign(form, data)
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    submitting.value = true
    try {
      if (form.id) {
        await updateClazz(form)
        ElMessage.success('修改成功')
      } else {
        await addClazz(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadList()
    } finally {
      submitting.value = false
    }
  })
}

onMounted(async () => {
  // 拉取员工列表作为班主任候选
  try {
    const res = await getEmpPage({ page: 1, pageSize: 1000 })
    empOptions.value = res.rows
  } catch {
    empOptions.value = []
  }
  loadList()
})
</script>

<template>
  <div class="clazz-page">
    <!-- 查询条件 -->
    <el-card shadow="never" class="card query-card">
      <el-form inline :model="query" @submit.prevent>
        <el-form-item label="班级名称">
          <el-input
            v-model="query.name"
            placeholder="请输入"
            clearable
            style="width: 160px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="开班日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始"
            end-placeholder="结束"
            value-format="YYYY-MM-DD"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">
            查询
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 列表 -->
    <el-card shadow="never" class="card">
      <div class="toolbar">
        <div class="toolbar-title tlias-title">班级管理</div>
        <div class="toolbar-actions">
          <el-button :icon="Refresh" circle @click="loadList" />
          <el-button type="primary" :icon="Plus" @click="openAdd">
            新增班级
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
        <el-table-column prop="name" label="班级名称" min-width="160" />
        <el-table-column prop="room" label="教室" width="90" align="center" />
        <el-table-column label="学科" width="90" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small" effect="plain">
              {{ subjectLabel(row.subject) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="开班日期" width="120">
          <template #default="{ row }">{{ row.beginDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="结束日期" width="120">
          <template #default="{ row }">{{ row.endDate || '-' }}</template>
        </el-table-column>
        <el-table-column prop="masterName" label="班主任" width="100" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.status === '已开班' ? 'success' : 'info'"
              size="small"
              effect="light"
            >
              {{ row.status || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后修改" min-width="160">
          <template #default="{ row }">{{ formatTime(row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="openEdit(row)">
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
          <el-empty description="暂无班级数据" />
        </template>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="total"
          background
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadList"
          @current-change="loadList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        @submit.prevent
      >
        <el-form-item label="班级名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="教室">
          <el-input v-model="form.room" placeholder="请输入教室" />
        </el-form-item>
        <el-form-item label="学科" prop="subject">
          <el-select
            v-model="form.subject"
            placeholder="请选择学科"
            style="width: 100%"
          >
            <el-option
              v-for="o in subjectOptions"
              :key="o.value"
              :label="o.label"
              :value="o.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开班日期" prop="beginDate">
          <el-date-picker
            v-model="form.beginDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="form.endDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="班主任" prop="masterId">
          <el-select
            v-model="form.masterId"
            placeholder="请选择班主任"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="e in empOptions"
              :key="e.id"
              :label="e.name"
              :value="e.id"
            />
          </el-select>
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
  margin-bottom: 16px;
}
.query-card :deep(.el-card__body) {
  padding-bottom: 2px;
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
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
