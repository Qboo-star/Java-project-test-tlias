import request from './request'

export interface Dept {
  id: number
  name: string
  createTime: string
  updateTime: string
}

// 查询全部部门
export function getDeptList() {
  return request.get<Dept[], Dept[]>('/depts')
}

// 根据 ID 查询部门
export function getDeptById(id: number) {
  return request.get<Dept, Dept>(`/depts/${id}`)
}

// 新增部门
export function addDept(data: { name: string }) {
  return request.post('/depts', data)
}

// 修改部门
export function updateDept(data: { id: number; name: string }) {
  return request.put('/depts', data)
}

// 删除部门
export function deleteDept(id: number) {
  return request.delete('/depts', { params: { id } })
}
