import { defineStore } from 'pinia'

export interface UploadRecord {
  name: string
  size: number
  time: string
  url: string
}

// 上传历史记录（会话级，刷新后清空）
export const useUploadStore = defineStore('upload', {
  state: () => ({
    records: [] as UploadRecord[],
  }),
  actions: {
    add(record: UploadRecord) {
      this.records.unshift(record)
      // 仅保留最近 20 条
      if (this.records.length > 20) {
        this.records.pop()
      }
    },
    remove(url: string) {
      this.records = this.records.filter((r) => r.url !== url)
    },
    clear() {
      this.records = []
    },
  },
})
