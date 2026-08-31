import type { ReactNode } from 'react'
import styles from './Modal.module.scss'

interface Props {
  title: string
  onClose: () => void
  children: ReactNode
}

export default function Modal({ title, onClose, children }: Props) {
  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.dialog} onClick={(event) => event.stopPropagation()}>
        <div className={styles.head}>
          <h2 className={styles.title}>{title}</h2>
          <button type="button" className={styles.close} onClick={onClose} aria-label="닫기">
            ×
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}
