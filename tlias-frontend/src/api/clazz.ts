import request, { type PageResult } from './request'

export interface Clazz {
  id?: number
  name: string
  room: string
  beginDate: string
  endDate: string
  masterId: number
  masterName?: string
  subject: number
  status?: string
  createTime?: string
  updateTime?: string
}

export interface ClazzQueryParam {
  page: number
  pageSize: number
  name?: string
  begin?: string
  end?: string
}

// 班级分页条件查询
export function getClazzPage(params: ClazzQueryParam) {
  return request.get<PageResult<Clazz>, PageResult<Clazz>>('/clazzs', { params })
}

// 根据 ID 查询班级
export function getClazzById(id: number) {
  return request.get<Clazz, Clazz>(`/clazzs/${id}`)
}

// 新增班级
export function addClazz(data: Clazz) {
  return request.post('/clazzs', data)
}

// 修改班级
export function updateClazz(data: Clazz) {
  return request.put('/clazzs', data)
}

// 删除班级
export function deleteClazz(id: number) {
  return request.delete(`/clazzs/${id}`)
}

// 查询所有班级（用于下拉选择）
export function getClazzList() {
  return request.get<Clazz[], Clazz[]>('/clazzs/list')
}

// 学科映射
export const subjectMap: Record<number, string> = {
  1: 'Java',
  2: '前端',
  3: '大数据',
  4: 'Python',
  5: '测试',
  6: '运维',
}
