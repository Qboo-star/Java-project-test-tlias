<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import {
  getEmpGenderData,
  getEmpJobData,
  getStudentDegreeData,
  getStudentCountData,
  type PieItem,
  type BarData,
} from '@/api/report'
import { Refresh } from '@element-plus/icons-vue'

// 图表实例
let genderChart: echarts.ECharts | null = null
let jobChart: echarts.ECharts | null = null
let degreeChart: echarts.ECharts | null = null
let countChart: echarts.ECharts | null = null

const loading = ref(false)
const genderRef = ref<HTMLElement>()
const jobRef = ref<HTMLElement>()
const degreeRef = ref<HTMLElement>()
const countRef = ref<HTMLElement>()

// 墨青主题色板
const palette = ['#0d9488', '#14b8a6', '#5eead4', '#f59e0b', '#ef4444', '#8b5cf6']

function initGenderChart(data: PieItem[]) {
  if (!genderRef.value) return
  genderChart = echarts.init(genderRef.value)
  genderChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 8, icon: 'circle', textStyle: { color: '#475569' } },
    color: ['#0ea5e9', '#f43f5e'],
    series: [
      {
        type: 'pie',
        radius: ['45%', '72%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 16, fontWeight: 'bold' },
        },
        data,
      },
    ],
  })
}

function initJobChart(data: BarData) {
  if (!jobRef.value) return
  jobChart = echarts.init(jobRef.value)
  jobChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 50, right: 30, top: 30, bottom: 40 },
    xAxis: {
      type: 'category',
      data: data.jobList,
      axisLine: { lineStyle: { color: '#cbd5e1' } },
      axisLabel: { color: '#64748b', interval: 0, rotate: data.jobList!.length > 5 ? 25 : 0 },
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f1f5f9' } },
      axisLabel: { color: '#64748b' },
    },
    series: [
      {
        type: 'bar',
        data: data.dataList,
        barWidth: '45%',
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#14b8a6' },
            { offset: 1, color: '#0d9488' },
          ]),
        },
        label: { show: true, position: 'top', color: '#0f766e', fontWeight: 600 },
      },
    ],
  })
}

function initDegreeChart(data: PieItem[]) {
  if (!degreeRef.value) return
  degreeChart = echarts.init(degreeRef.value)
  degreeChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
    legend: { bottom: 8, icon: 'circle', textStyle: { color: '#475569' } },
    color: palette,
    series: [
      {
        type: 'pie',
        radius: '65%',
        center: ['50%', '45%'],
        roseType: 'radius',
        itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
        label: { color: '#475569' },
        data,
      },
    ],
  })
}

function initCountChart(data: BarData) {
  if (!countRef.value) return
  countChart = echarts.init(countRef.value)
  countChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 50, right: 30, top: 30, bottom: 60 },
    xAxis: {
      type: 'category',
      data: data.clazzList,
      axisLine: { lineStyle: { color: '#cbd5e1' } },
      axisLabel: { color: '#64748b', interval: 0, rotate: 30 },
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f1f5f9' } },
      axisLabel: { color: '#64748b' },
    },
    series: [
      {
        type: 'bar',
        data: data.dataList,
        barWidth: '50%',
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#fbbf24' },
            { offset: 1, color: '#f59e0b' },
          ]),
        },
        label: { show: true, position: 'top', color: '#b45309', fontWeight: 600 },
      },
    ],
  })
}

function handleResize() {
  genderChart?.resize()
  jobChart?.resize()
  degreeChart?.resize()
  countChart?.resize()
}

async function loadAll() {
  loading.value = true
  try {
    const [gender, job, degree, count] = await Promise.all([
      getEmpGenderData(),
      getEmpJobData(),
      getStudentDegreeData(),
      getStudentCountData(),
    ])
    await nextTick()
    initGenderChart(gender)
    initJobChart(job)
    initDegreeChart(degree)
    initCountChart(count)
  } catch {
    // 各接口单独失败时不抛错，保持空图
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAll()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  genderChart?.dispose()
  jobChart?.dispose()
  degreeChart?.dispose()
  countChart?.dispose()
})
</script>

<template>
  <div class="report-page" v-loading="loading">
    <div class="page-head">
      <div>
        <div class="page-title tlias-title">数据统计</div>
        <div class="page-sub">员工与学员核心指标可视化分析</div>
      </div>
      <el-button :icon="Refresh" circle @click="loadAll" />
    </div>

    <div class="chart-grid">
      <el-card shadow="never" class="chart-card">
        <div class="chart-head">
          <span class="chart-tag tag-teal">员工</span>
          <span class="chart-name">员工性别占比</span>
        </div>
        <div ref="genderRef" class="chart-box"></div>
      </el-card>

      <el-card shadow="never" class="chart-card">
        <div class="chart-head">
          <span class="chart-tag tag-teal">员工</span>
          <span class="chart-name">职位人数分布</span>
        </div>
        <div ref="jobRef" class="chart-box"></div>
      </el-card>

      <el-card shadow="never" class="chart-card">
        <div class="chart-head">
          <span class="chart-tag tag-amber">学员</span>
          <span class="chart-name">学员学历构成</span>
        </div>
        <div ref="degreeRef" class="chart-box"></div>
      </el-card>

      <el-card shadow="never" class="chart-card">
        <div class="chart-head">
          <span class="chart-tag tag-amber">学员</span>
          <span class="chart-name">班级人数统计</span>
        </div>
        <div ref="countRef" class="chart-box"></div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.report-page {
  padding: 0;
}
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.page-title {
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
}
.page-sub {
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
}
.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
.chart-card {
  border-radius: 14px;
  border: none;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.04);
}
.chart-head {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 4px 16px;
}
.chart-tag {
  font-size: 12px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 6px;
}
.tag-teal {
  color: #0f766e;
  background: #ccfbf1;
}
.tag-amber {
  color: #b45309;
  background: #fef3c7;
}
.chart-name {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}
.chart-box {
  width: 100%;
  height: 320px;
}
@media (max-width: 1100px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }
}
</style>
