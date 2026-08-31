import type { ReactNode } from 'react'
import styles from './Feedback.module.scss'

// 목록을 불러오는 화면마다 로딩, 오류, 빈 목록 표시가 반복되어 한곳에 모았다.

export function Loading({ text = '불러오는 중입니다.' }: { text?: string }) {
  return <p className={styles.loading}>{text}</p>
}

export function ErrorMessage({ text, onRetry }: { text: string; onRetry?: () => void }) {
  return (
    <div className={styles.error}>
      <p>{text}</p>
      {onRetry && (
        <button type="button" className={styles.retry} onClick={onRetry}>
          다시 시도
        </button>
      )}
    </div>
  )
}

export function Empty({ children }: { children: ReactNode }) {
  return <div className={styles.empty}>{children}</div>
}
