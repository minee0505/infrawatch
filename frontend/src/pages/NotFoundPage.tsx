import { Link } from 'react-router-dom'
import styles from './NotFoundPage.module.scss'

export default function NotFoundPage() {
  return (
    <div className={styles.page}>
      <p className={styles.code}>404</p>
      <p className={styles.message}>요청한 화면을 찾을 수 없습니다.</p>
      <Link to="/dashboard" className={styles.link}>
        대시보드로 이동
      </Link>
    </div>
  )
}
