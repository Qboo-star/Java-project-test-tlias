import request, { type PageResult } from './request'

export interface EmpExpr {
  id?: number
  empId?: number
  company: string
  job: string
  begin: string
  end: string
}

export interface Emp {
  id?: number
  username: string
  password?: string
  name: string
  gender: number
  phone?: string
  job?: number
  salary?: number
  image?: string
  entryDate?: string
  deptId?: number
  deptName?: string
  createTime?: string
  updateTime?: string
  exprList?: EmpExpr[]
}

export interface EmpQueryParam {
  page: number
  pageSize: number
  name?: string
  gender?: number
  begin?: string
  end?: string
}

// 分页条件查询
export function getEmpPage(params: EmpQueryParam) {
  return request.get<PageResult<Emp>, PageResult<Emp>>('/emps', { params })
}

// 根据 ID 查询员工（含工作经历）
export function getEmpById(id: number) {
  return request.get<Emp, Emp>(`/emps/${id}`)
}

// 新增员工
export function addEmp(data: Emp) {
  return request.post('/emps', data)
}

// 修改员工
export function updateEmp(data: Emp) {
  return request.put('/emps', data)
}

// 批量删除员工
export function deleteEmp(ids: number[]) {
  return request.delete('/emps', { params: { ids: ids.join(',') } })
}
