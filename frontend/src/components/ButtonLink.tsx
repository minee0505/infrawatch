import { Link } from 'react-router-dom'
import type { ReactNode } from 'react'
import styles from './ButtonLink.module.scss'

interface Props {
  to: string
  children: ReactNode
}

export default function ButtonLink({ to, children }: Props) {
  return (
    <Link to={to} className={styles.button}>
      {children}
    </Link>
  )
}
