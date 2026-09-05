<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Refresh, Edit, Delete, Search } from '@element-plus/icons-vue'
import { confirmDelete } from '@/utils/confirm'
import {
  getEmpPage,
  getEmpById,
  addEmp,
  updateEmp,
  deleteEmp,
  type Emp,
  type EmpQueryParam,
} from '@/api/emp'
import { getDeptList, type Dept } from '@/api/dept'

// ===== 列表与查询 =====
const list = ref<Emp[]>([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref<number[]>([])

const query = reactive<EmpQueryParam>({
  page: 1,
  pageSize: 10,
  name: '',
  gender: undefined,
  begin: '',
  end: '',
})
const dateRange = ref<[string, string] | null>(null)

// 部门下拉
const deptOptions = ref<Dept[]>([])

// 职位映射
const jobMap: Record<number, string> = {
  1: '班主任',
  2: '讲师',
  3: '学工主管',
  4: '教研主管',
  5: '咨询师',
}
const jobOptions = Object.entries(jobMap).map(([v, label]) => ({
  value: Number(v),
  label,
}))

function formatTime(t?: string) {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 16)
}

async function loadList() {
  loading.value = true
  try {
    query.begin = dateRange.value?.[0] || ''
    query.end = dateRange.value?.[1] || ''
    const res = await getEmpPage(query)
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
  query.gender = undefined
  dateRange.value = null
  query.begin = ''
  query.end = ''
  handleQuery()
}

function handleSelectionChange(rows: Emp[]) {
  selectedIds.value = rows.map((r) => r.id!)
}

async function handleBatchDelete() {
  if (!selectedIds.value.length) {
    ElMessage.warning('请先选择要删除的员工')
    return
  }
  await confirmDelete({
    title: '批量删除员工',
    message: `确认删除选中的 ${selectedIds.value.length} 名员工吗？删除后数据将无法恢复。`,
    confirmText: '确认删除',
    type: 'danger',
  })
  await deleteEmp(selectedIds.value)
  ElMessage.success('删除成功')
  if (list.value.length === selectedIds.value.length && query.page! > 1) {
    query.page!--
  }
  loadList()
}

async function handleDelete(row: Emp) {
  await confirmDelete({
    title: '确认删除员工',
    message: `确认删除员工「${row.name}」吗？删除后数据将无法恢复。`,
    confirmText: '确认删除',
    type: 'danger',
  })
  await deleteEmp([row.id!])
  ElMessage.success('删除成功')
  loadList()
}

// ===== 抽屉表单 =====
const drawerVisible = ref(false)
const drawerTitle = ref('新增员工')
const submitting = ref(false)
const formRef = ref()
const empForm = reactive<Emp>({
  username: '',
  name: '',
  gender: 1,
  phone: '',
  job: undefined,
  salary: undefined,
  image: '',
  entryDate: '',
  deptId: undefined,
  exprList: [],
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
}

function resetForm() {
  Object.assign(empForm, {
    id: undefined,
    username: '',
    name: '',
    gender: 1,
    phone: '',
    job: undefined,
    salary: undefined,
    image: '',
    entryDate: '',
    deptId: undefined,
    exprList: [],
  })
  formRef.value?.clearValidate()
}

function openAdd() {
  drawerTitle.value = '新增员工'
  resetForm()
  drawerVisible.value = true
}

async function openEdit(row: Emp) {
  drawerTitle.value = '编辑员工'
  resetForm()
  drawerVisible.value = true
  const data = await getEmpById(row.id!)
  Object.assign(empForm, data)
}

// 工作经历动态行
function addExpr() {
  empForm.exprList = empForm.exprList || []
  empForm.exprList.push({ company: '', job: '', begin: '', end: '' })
}

function removeExpr(index: number) {
  empForm.exprList?.splice(index, 1)
}

// 头像上传（el-upload 原生请求，on-success 取 Result.data）
function handleUploadSuccess(res: any) {
  // 后端按文档应返回 data 为 URL；若实际返回 null，用空串占位
  empForm.image = res?.data || ''
  if (!res?.data) {
    ElMessage.warning('后端未返回图片地址，已记录但无法预览')
  }
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

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    submitting.value = true
    try {
      if (empForm.id) {
        await updateEmp(empForm)
        ElMessage.success('修改成功')
      } else {
        await addEmp(empForm)
        ElMessage.success('新增成功')
      }
      drawerVisible.value = false
      loadList()
    } finally {
      submitting.value = false
    }
  })
}

onMounted(async () => {
  deptOptions.value = await getDeptList()
  loadList()
})
</script>

<template>
  <div class="emp-page">
    <!-- 查询条件 -->
    <el-card shadow="never" class="card query-card">
      <el-form inline :model="query" @submit.prevent>
        <el-form-item label="姓名">
          <el-input
            v-model="query.name"
            placeholder="请输入姓名"
            clearable
            style="width: 160px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="性别">
          <el-select
            v-model="query.gender"
            placeholder="请选择"
            clearable
            style="width: 120px"
          >
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职日期">
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
        <div class="toolbar-title tlias-title">员工管理</div>
        <div class="toolbar-actions">
          <el-button
            type="danger"
            plain
            :icon="Delete"
            :disabled="!selectedIds.length"
            @click="handleBatchDelete"
          >
            批量删除
          </el-button>
          <el-button :icon="Refresh" circle @click="loadList" />
          <el-button type="primary" :icon="Plus" @click="openAdd">
            新增员工
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="list"
        stripe
        class="data-table"
        :header-cell-style="{ background: '#f8fafc', color: '#334155' }"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="头像" width="70" align="center">
          <template #default="{ row }">
            <el-avatar
              :size="36"
              :src="row.image"
              shape="square"
            />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="110" />
        <el-table-column prop="name" label="姓名" min-width="90" />
        <el-table-column label="性别" width="70" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.gender === 1 ? '' : 'danger'"
              size="small"
              effect="light"
            >
              {{ row.gender === 1 ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="职位" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small" effect="plain">
              {{ jobMap[row.job] || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="salary" label="薪资" width="100" align="right">
          <template #default="{ row }">
            {{ row.salary ? '¥' + row.salary : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="entryDate" label="入职日期" width="120" />
        <el-table-column prop="deptName" label="部门" width="100" />
        <el-table-column label="最后修改" min-width="160">
          <template #default="{ row }">
            {{ formatTime(row.updateTime) }}
          </template>
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
          <el-empty description="暂无员工数据" />
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

    <!-- 新增/编辑抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      :title="drawerTitle"
      size="640px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="empForm"
        :rules="rules"
        label-width="90px"
      >
        <div class="form-grid">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="empForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input v-model="empForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="empForm.gender">
              <el-radio :value="1">男</el-radio>
              <el-radio :value="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="empForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="职位">
            <el-select
              v-model="empForm.job"
              placeholder="请选择职位"
              clearable
              style="width: 100%"
            >
              <el-option
                v-for="o in jobOptions"
                :key="o.value"
                :label="o.label"
                :value="o.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="薪资">
            <el-input-number
              v-model="empForm.salary"
              :min="0"
              :step="500"
              controls-position="right"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="入职日期">
            <el-date-picker
              v-model="empForm.entryDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="部门">
            <el-select
              v-model="empForm.deptId"
              placeholder="请选择部门"
              clearable
              style="width: 100%"
            >
              <el-option
                v-for="d in deptOptions"
                :key="d.id"
                :label="d.name"
                :value="d.id"
              />
            </el-select>
          </el-form-item>
        </div>

        <el-form-item label="头像">
          <el-upload
            action="/api/upload"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            accept="image/jpeg,image/png"
          >
            <div v-if="empForm.image" class="avatar-box">
              <img :src="empForm.image" class="avatar" />
              <div class="avatar-mask">点击更换</div>
            </div>
            <div v-else class="avatar-uploader">
              <el-icon size="24"><Plus /></el-icon>
              <span>点击上传</span>
            </div>
          </el-upload>
        </el-form-item>

        <!-- 工作经历子表 -->
        <div class="expr-section">
          <div class="expr-header">
            <span class="tlias-title">工作经历</span>
            <el-button type="primary" link :icon="Plus" @click="addExpr">
              添加经历
            </el-button>
          </div>
          <el-table
            v-if="empForm.exprList && empForm.exprList.length"
            :data="empForm.exprList"
            border
            size="small"
          >
            <el-table-column label="公司" min-width="160">
              <template #default="{ row }">
                <el-input v-model="row.company" placeholder="公司名称" />
              </template>
            </el-table-column>
            <el-table-column label="职位" min-width="120">
              <template #default="{ row }">
                <el-input v-model="row.job" placeholder="担任职位" />
              </template>
            </el-table-column>
            <el-table-column label="开始日期" width="150">
              <template #default="{ row }">
                <el-date-picker
                  v-model="row.begin"
                  type="date"
                  placeholder="开始"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                  size="small"
                />
              </template>
            </el-table-column>
            <el-table-column label="结束日期" width="150">
              <template #default="{ row }">
                <el-date-picker
                  v-model="row.end"
                  type="date"
                  placeholder="结束"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                  size="small"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="70" align="center">
              <template #default="{ $index }">
                <el-button
                  type="danger"
                  link
                  :icon="Delete"
                  @click="removeExpr($index)"
                />
              </template>
            </el-table-column>
          </el-table>
          <el-empty
            v-else
            description="暂无工作经历，点击上方添加"
            :image-size="60"
          />
        </div>
      </el-form>

      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">
          确定
        </el-button>
      </template>
    </el-drawer>
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
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 16px;
}
.avatar-box {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
}
.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.avatar-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(15, 23, 42, 0.5);
  color: #fff;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.2s;
}
.avatar-box:hover .avatar-mask {
  opacity: 1;
}
.avatar-uploader {
  width: 100px;
  height: 100px;
  border: 1px dashed #94a3b8;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #94a3b8;
  transition: border-color 0.2s, color 0.2s;
}
.avatar-uploader:hover {
  border-color: #0d9488;
  color: #0d9488;
}
.expr-section {
  margin-top: 8px;
}
.expr-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
}
</style>
