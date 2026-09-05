import request from './request'

// 饼图数据项（性别统计、学历统计）
export interface PieItem {
  name: string
  value: number
}

// 柱状图数据（职位人数、班级人数）
export interface BarData {
  jobList?: string[] // 职位名
  clazzList?: string[] // 班级名
  dataList: number[]
}

// 员工性别统计
export function getEmpGenderData() {
  return request.get<PieItem[], PieItem[]>('/report/empGenderData')
}

// 员工职位人数统计
export function getEmpJobData() {
  return request.get<BarData, BarData>('/report/empJobData')
}

// 学员学历统计
export function getStudentDegreeData() {
  return request.get<PieItem[], PieItem[]>('/report/studentDegreeData')
}

// 班级人数统计
export function getStudentCountData() {
  return request.get<BarData, BarData>('/report/studentCountData')
}
