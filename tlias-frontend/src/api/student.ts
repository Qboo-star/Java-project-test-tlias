import request, { type PageResult } from './request'

export interface Student {
  id?: number
  name: string
  no: string
  gender: number
  phone: string
  idCard: string
  isCollege: number
  address: string
  degree: number
  graduationDate: string
  violationCount?: number
  violationScore?: number
  clazzId: number
  clazzName?: string
  createTime?: string
  updateTime?: string
}

export interface StudentQueryParam {
  page: number
  pageSize: number
  name?: string
  degree?: number
  clazzId?: number
}

// 学员分页条件查询
export function getStudentPage(params: StudentQueryParam) {
  return request.get<PageResult<Student>, PageResult<Student>>('/students', {
    params,
  })
}

// 根据 ID 查询学员
export function getStudentById(id: number) {
  return request.get<Student, Student>(`/students/${id}`)
}

// 新增学员
export function addStudent(data: Student) {
  return request.post('/students', data)
}

// 修改学员
export function updateStudent(data: Student) {
  return request.put('/students', data)
}

// 批量删除学员（请求体 JSON 数组，对应后端 @DeleteMapping + @RequestBody List<Integer>）
export function deleteStudent(ids: number[]) {
  return request.delete('/students', { data: ids })
}

// 违纪处理（扣分）
export function handleViolation(id: number, score: number) {
  return request.put(`/students/violation/${id}/${score}`)
}

// 学历映射
export const degreeMap: Record<number, string> = {
  1: '初中',
  2: '高中',
  3: '大专',
  4: '本科',
  5: '硕士',
  6: '博士',
}
