<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Plus,
  Refresh,
  Edit,
  Delete,
  Search,
  Warning,
} from '@element-plus/icons-vue'
import { confirmDelete } from '@/utils/confirm'
import {
  getStudentPage,
  getStudentById,
  addStudent,
  updateStudent,
  deleteStudent,
  handleViolation,
  degreeMap,
  type Student,
  type StudentQueryParam,
} from '@/api/student'
import { getClazzList, type Clazz } from '@/api/clazz'

// ===== 列表与查询 =====
const list = ref<Student[]>([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref<number[]>([])

const query = reactive<StudentQueryParam>({
  page: 1,
  pageSize: 10,
  name: '',
  degree: undefined,
  clazzId: undefined,
})

// 班级下拉
const clazzOptions = ref<Clazz[]>([])

const degreeOptions = Object.entries(degreeMap).map(([v, label]) => ({
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
    const res = await getStudentPage(query)
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
  query.degree = undefined
  query.clazzId = undefined
  handleQuery()
}

function handleSelectionChange(rows: Student[]) {
  selectedIds.value = rows.map((r) => r.id!)
}

async function handleBatchDelete() {
  if (!selectedIds.value.length) {
    ElMessage.warning('请先选择要删除的学员')
    return
  }
  await confirmDelete({
    title: '批量删除学员',
    message: `确认删除选中的 ${selectedIds.value.length} 名学员吗？删除后数据将无法恢复。`,
    confirmText: '确认删除',
    type: 'danger',
  })
  await deleteStudent(selectedIds.value)
  ElMessage.success('删除成功')
  if (list.value.length === selectedIds.value.length && query.page! > 1) {
    query.page!--
  }
  loadList()
}

async function handleDelete(row: Student) {
  await confirmDelete({
    title: '确认删除学员',
    message: `确认删除学员「${row.name}」吗？删除后数据将无法恢复。`,
    confirmText: '确认删除',
    type: 'danger',
  })
  await deleteStudent([row.id!])
  ElMessage.success('删除成功')
  loadList()
}

// ===== 违纪处理 =====
const violationVisible = ref(false)
const violationForm = reactive({ id: 0, name: '', score: 1 })

function openViolation(row: Student) {
  violationForm.id = row.id!
  violationForm.name = row.name
  violationForm.score = 1
  violationVisible.value = true
}

async function submitViolation() {
  if (!violationForm.score || violationForm.score <= 0) {
    ElMessage.warning('扣分数必须大于 0')
    return
  }
  await handleViolation(violationForm.id, violationForm.score)
  ElMessage.success('违纪处理成功')
  violationVisible.value = false
  loadList()
}

// ===== 抽屉表单 =====
const drawerVisible = ref(false)
const drawerTitle = ref('新增学员')
const submitting = ref(false)
const formRef = ref()
const stuForm = reactive<Student>({
  name: '',
  no: '',
  gender: 1,
  phone: '',
  idCard: '',
  isCollege: 0,
  address: '',
  degree: 1,
  graduationDate: '',
  clazzId: undefined as unknown as number,
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  no: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  degree: [{ required: true, message: '请选择学历', trigger: 'change' }],
  clazzId: [{ required: true, message: '请选择班级', trigger: 'change' }],
}

function resetForm() {
  Object.assign(stuForm, {
    id: undefined,
    name: '',
    no: '',
    gender: 1,
    phone: '',
    idCard: '',
    isCollege: 0,
    address: '',
    degree: 1,
    graduationDate: '',
    clazzId: undefined,
  })
  formRef.value?.clearValidate()
}

function openAdd() {
  drawerTitle.value = '新增学员'
  resetForm()
  drawerVisible.value = true
}

async function openEdit(row: Student) {
  drawerTitle.value = '编辑学员'
  resetForm()
  drawerVisible.value = true
  const data = await getStudentById(row.id!)
  Object.assign(stuForm, data)
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    submitting.value = true
    try {
      if (stuForm.id) {
        await updateStudent(stuForm)
        ElMessage.success('修改成功')
      } else {
        await addStudent(stuForm)
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
  try {
    clazzOptions.value = await getClazzList()
  } catch {
    clazzOptions.value = []
  }
  loadList()
})
</script>

<template>
  <div class="student-page">
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
        <el-form-item label="学历">
          <el-select
            v-model="query.degree"
            placeholder="请选择"
            clearable
            style="width: 120px"
          >
            <el-option
              v-for="o in degreeOptions"
              :key="o.value"
              :label="o.label"
              :value="o.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select
            v-model="query.clazzId"
            placeholder="请选择"
            clearable
            filterable
            style="width: 180px"
          >
            <el-option
              v-for="c in clazzOptions"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
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
        <div class="toolbar-title tlias-title">学员管理</div>
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
            新增学员
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
        <el-table-column prop="name" label="姓名" min-width="90" />
        <el-table-column prop="no" label="学号" min-width="120" />
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
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="学历" width="80" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small" effect="plain">
              {{ degreeMap[row.degree] || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="是否上过大学" width="110" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.isCollege === 1 ? 'success' : 'info'"
              size="small"
            >
              {{ row.isCollege === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="clazzName" label="班级" min-width="130" />
        <el-table-column label="违纪次数" width="90" align="center">
          <template #default="{ row }">{{ row.violationCount || 0 }}</template>
        </el-table-column>
        <el-table-column label="违纪扣分" width="90" align="center">
          <template #default="{ row }">
            <span :class="{ 'score-warn': (row.violationScore || 0) > 0 }">
              {{ row.violationScore || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              type="warning"
              link
              :icon="Warning"
              @click="openViolation(row)"
            >
              违纪
            </el-button>
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
          <el-empty description="暂无学员数据" />
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
      size="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="stuForm"
        :rules="rules"
        label-width="100px"
      >
        <div class="form-grid">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="stuForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="学号" prop="no">
            <el-input v-model="stuForm.no" placeholder="请输入学号" />
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="stuForm.gender">
              <el-radio :value="1">男</el-radio>
              <el-radio :value="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="stuForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="身份证号">
            <el-input v-model="stuForm.idCard" placeholder="请输入身份证号" />
          </el-form-item>
          <el-form-item label="是否上过大学">
            <el-radio-group v-model="stuForm.isCollege">
              <el-radio :value="1">是</el-radio>
              <el-radio :value="0">否</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="学历" prop="degree">
            <el-select
              v-model="stuForm.degree"
              placeholder="请选择学历"
              style="width: 100%"
            >
              <el-option
                v-for="o in degreeOptions"
                :key="o.value"
                :label="o.label"
                :value="o.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="班级" prop="clazzId">
            <el-select
              v-model="stuForm.clazzId"
              placeholder="请选择班级"
              filterable
              style="width: 100%"
            >
              <el-option
                v-for="c in clazzOptions"
                :key="c.id"
                :label="c.name"
                :value="c.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="毕业日期">
            <el-date-picker
              v-model="stuForm.graduationDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        <el-form-item label="家庭住址">
          <el-input
            v-model="stuForm.address"
            type="textarea"
            :rows="2"
            placeholder="请输入家庭住址"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">
          确定
        </el-button>
      </template>
    </el-drawer>

    <!-- 违纪处理对话框 -->
    <el-dialog v-model="violationVisible" title="违纪处理" width="420px">
      <el-form label-width="90px">
        <el-form-item label="学员姓名">
          <el-input :model-value="violationForm.name" disabled />
        </el-form-item>
        <el-form-item label="扣分数">
          <el-input-number
            v-model="violationForm.score"
            :min="1"
            :max="100"
            :step="1"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="violationVisible = false">取消</el-button>
        <el-button type="warning" @click="submitViolation">确认扣分</el-button>
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
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 16px;
}
.score-warn {
  color: #f59e0b;
  font-weight: 600;
}
</style>
