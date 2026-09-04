import request from './request'

// 上传文件，返回可访问的图片 URL
export function uploadFile(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<string, string>('/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
