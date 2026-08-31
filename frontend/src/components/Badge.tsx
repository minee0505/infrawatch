import type { AlertLevel, AlertStatus, SensorStatus } from '../api/types'
import { alertLevelLabel, alertStatusLabel, sensorStatusLabel } from '../utils/format'
import styles from './Badge.module.scss'

export function AlertLevelBadge({ level }: { level: AlertLevel }) {
  const className = level === 'CRITICAL' ? styles.critical : styles.warning
  return <span className={`${styles.badge} ${className}`}>{alertLevelLabel(level)}</span>
}

export function AlertStatusBadge({ status }: { status: AlertStatus }) {
  const className =
    status === 'OPEN' ? styles.critical : status === 'ACKNOWLEDGED' ? styles.warning : styles.ok
  return <span className={`${styles.badge} ${className}`}>{alertStatusLabel(status)}</span>
}

export function SensorStatusBadge({ status }: { status: SensorStatus }) {
  const className = status === 'ACTIVE' ? styles.ok : styles.inactive
  return <span className={`${styles.badge} ${className}`}>{sensorStatusLabel(status)}</span>
}
