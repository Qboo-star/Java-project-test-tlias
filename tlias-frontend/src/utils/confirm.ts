import { ElMessageBox } from 'element-plus'
import { WarningFilled } from '@element-plus/icons-vue'
import { createVNode } from 'vue'

interface ConfirmOptions {
  /** 主标题 */
  title?: string
  /** 描述内容 */
  message?: string
  /** 确认按钮文字 */
  confirmText?: string
  /** 类型：danger / warning / info */
  type?: 'danger' | 'warning' | 'info'
}

/**
 * 精致版确认框：大圆形图标 + 居中标题描述 + 渐变按钮。
 * 用于所有删除/危险操作的二次确认。
 */
export function confirmDelete(options: ConfirmOptions = {}): Promise<void> {
  const {
    title = '确认删除',
    message = '删除后数据将无法恢复，请谨慎操作。',
    confirmText = '确认删除',
    type = 'danger',
  } = options

  const palette =
    type === 'danger'
      ? { main: '#ef4444', dark: '#dc2626', light: '#fee2e2', glow: 'rgba(239,68,68,0.35)' }
      : type === 'warning'
        ? { main: '#f59e0b', dark: '#d97706', light: '#fef3c7', glow: 'rgba(245,158,11,0.35)' }
        : { main: '#0ea5e9', dark: '#0284c7', light: '#e0f2fe', glow: 'rgba(14,165,233,0.35)' }

  return ElMessageBox({
    title,
    message: () =>
      createVNode('div', { class: 'tlias-confirm-body' }, [
        // 顶部彩色装饰条
        createVNode('div', {
          class: 'tlias-confirm-bar',
          style: { background: `linear-gradient(90deg, ${palette.main}, ${palette.dark})` },
        }),
        // 大圆形图标
        createVNode(
          'div',
          {
            class: 'tlias-confirm-icon-wrap',
          },
          [
            createVNode(
              'div',
              {
                class: 'tlias-confirm-icon',
                style: {
                  background: `linear-gradient(135deg, ${palette.main}, ${palette.dark})`,
                  boxShadow: `0 12px 28px -6px ${palette.glow}`,
                },
              },
              [createVNode(WarningFilled)],
            ),
          ],
        ),
        // 标题
        createVNode('div', { class: 'tlias-confirm-title' }, title),
        // 描述
        createVNode('div', { class: 'tlias-confirm-desc' }, message),
      ]),
    confirmButtonText: confirmText,
    cancelButtonText: '取消',
    type,
    confirmButtonClass: `tlias-confirm-btn tlias-confirm-btn-${type}`,
    cancelButtonClass: 'tlias-confirm-cancel',
    customClass: 'tlias-confirm-box',
    showCancelButton: true,
    closeOnClickModal: true,
    draggable: false,
    buttonSize: 'default',
    modal: true,
  }).then(() => undefined)
}
