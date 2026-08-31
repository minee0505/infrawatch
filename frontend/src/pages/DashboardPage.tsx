import { Link } from 'react-router-dom'
import { fetchDashboardSummary } from '../api/dashboard'
import { AlertLevelBadge } from '../components/Badge'
import { Empty, ErrorMessage, Loading } from '../components/Feedback'
import PageHeader from '../components/PageHeader'
import { useAsync } from '../hooks/useAsync'
import { formatDateTime } from '../utils/format'
import styles from './DashboardPage.module.scss'

export default function DashboardPage() {
  const { data, loading, error, reload } = useAsync(fetchDashboardSummary, [])

  if (loading) {
    return <Loading />
  }
  if (error) {
    return <ErrorMessage text={error} onRetry={reload} />
  }
  if (!data) {
    return null
  }

  const cards = [
    { label: '시설물 수', value: data.facilityCount },
    { label: '센서 수', value: data.sensorCount },
    { label: '가동 센서 수', value: data.activeSensorCount },
    { label: '열린 알림', value: data.openAlertCount },
    { label: '위험 알림', value: data.criticalAlertCount, critical: true },
  ]

  return (
    <div>
      <PageHeader title="대시보드" description="전체 시설물과 센서의 현재 상태를 한눈에 확인합니다." />

      <div className={styles.cards}>
        {cards.map((card) => (
          <div key={card.label} className={`${styles.card} ${card.critical ? styles.critical : ''}`}>
            <span className={styles.cardLabel}>{card.label}</span>
            <span className={styles.cardValue}>{card.value}</span>
          </div>
        ))}
      </div>

      <section className={styles.recent}>
        <h2 className={styles.sectionTitle}>최근 알림</h2>

        {data.recentAlerts.length === 0 ? (
          <Empty>최근 발생한 알림이 없습니다.</Empty>
        ) : (
          <ul className={styles.list}>
            {data.recentAlerts.map((alert) => (
              <li key={alert.id}>
                <Link to={`/facilities/${alert.facilityId}`} className={styles.item}>
                  <AlertLevelBadge level={alert.level} />
                  <span className={styles.itemBody}>
                    <span className={styles.itemMessage}>{alert.message}</span>
                    <span className={styles.itemMeta}>
                      {alert.facilityName} · {alert.sensorName}
                    </span>
                  </span>
                  <span className={styles.itemDate}>{formatDateTime(alert.createdAt)}</span>
                </Link>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  )
}
