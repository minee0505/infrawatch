import { useState } from 'react'
import { Link } from 'react-router-dom'
import { acknowledgeAlert, fetchAlerts, resolveAlert } from '../api/alerts'
import type { AlertStatus } from '../api/types'
import { AlertLevelBadge, AlertStatusBadge } from '../components/Badge'
import { Empty, ErrorMessage, Loading } from '../components/Feedback'
import PageHeader from '../components/PageHeader'
import { useAsync } from '../hooks/useAsync'
import { formatDateTime } from '../utils/format'
import styles from './AlertListPage.module.scss'

const TABS: { label: string; status: AlertStatus | undefined }[] = [
  { label: '전체', status: undefined },
  { label: '열림', status: 'OPEN' },
  { label: '확인됨', status: 'ACKNOWLEDGED' },
  { label: '해결됨', status: 'RESOLVED' },
]

export default function AlertListPage() {
  const [tab, setTab] = useState<AlertStatus | undefined>(undefined)
  const { data, loading, error, reload } = useAsync(() => fetchAlerts(tab), [tab])
  const [processing, setProcessing] = useState<number | null>(null)
  const [actionError, setActionError] = useState<string | null>(null)

  const handleAcknowledge = async (alertId: number) => {
    setActionError(null)
    setProcessing(alertId)
    try {
      await acknowledgeAlert(alertId)
      reload()
    } catch (cause) {
      setActionError(cause instanceof Error ? cause.message : '처리하지 못했습니다.')
    } finally {
      setProcessing(null)
    }
  }

  const handleResolve = async (alertId: number) => {
    setActionError(null)
    setProcessing(alertId)
    try {
      await resolveAlert(alertId)
      reload()
    } catch (cause) {
      setActionError(cause instanceof Error ? cause.message : '처리하지 못했습니다.')
    } finally {
      setProcessing(null)
    }
  }

  return (
    <div>
      <PageHeader title="알림" description="센서 측정값이 임계값을 벗어나면 알림이 자동으로 생성됩니다." />

      <div className={styles.tabs}>
        {TABS.map((item) => (
          <button
            key={item.label}
            type="button"
            className={`${styles.tab} ${tab === item.status ? styles.tabActive : ''}`}
            onClick={() => setTab(item.status)}
          >
            {item.label}
          </button>
        ))}
      </div>

      {actionError && <ErrorMessage text={actionError} />}

      {loading ? (
        <Loading />
      ) : error ? (
        <ErrorMessage text={error} onRetry={reload} />
      ) : !data || data.length === 0 ? (
        <Empty>표시할 알림이 없습니다.</Empty>
      ) : (
        <ul className={styles.list}>
          {data.map((alert) => (
            <li key={alert.id} className={styles.item}>
              <div className={styles.head}>
                <AlertLevelBadge level={alert.level} />
                <AlertStatusBadge status={alert.status} />
                <Link to={`/facilities/${alert.facilityId}`} className={styles.facility}>
                  {alert.facilityName} · {alert.sensorName}
                </Link>
                <span className={styles.createdAt}>{formatDateTime(alert.createdAt)}</span>
              </div>

              <p className={styles.message}>{alert.message}</p>

              {alert.status === 'RESOLVED' && alert.resolvedByName && (
                <p className={styles.resolvedInfo}>
                  {alert.resolvedByName} 님이 {alert.resolvedAt && formatDateTime(alert.resolvedAt)}에
                  해결 처리했습니다.
                </p>
              )}

              {alert.status !== 'RESOLVED' && (
                <div className={styles.actions}>
                  {alert.status === 'OPEN' && (
                    <button
                      type="button"
                      className={styles.acknowledge}
                      disabled={processing === alert.id}
                      onClick={() => handleAcknowledge(alert.id)}
                    >
                      {processing === alert.id ? '처리하는 중' : '확인'}
                    </button>
                  )}
                  <button
                    type="button"
                    className={styles.resolve}
                    disabled={processing === alert.id}
                    onClick={() => handleResolve(alert.id)}
                  >
                    {processing === alert.id ? '처리하는 중' : '해결'}
                  </button>
                </div>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
