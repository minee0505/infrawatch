import { fetchAuditLogs } from '../api/auditLogs'
import { Empty, ErrorMessage, Loading } from '../components/Feedback'
import PageHeader from '../components/PageHeader'
import { useAsync } from '../hooks/useAsync'
import { formatDateTime } from '../utils/format'
import styles from './AuditLogPage.module.scss'

export default function AuditLogPage() {
  const { data, loading, error, reload } = useAsync(fetchAuditLogs, [])

  if (loading) {
    return <Loading />
  }
  if (error) {
    return <ErrorMessage text={error} onRetry={reload} />
  }

  const logs = data ?? []

  return (
    <div>
      <PageHeader title="감사 로그" description="관리 대상에 대한 변경 이력을 최신순으로 표시합니다." />

      {logs.length === 0 ? (
        <Empty>기록된 로그가 없습니다.</Empty>
      ) : (
        <div className={styles.tableWrap}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th scope="col">시각</th>
                <th scope="col">수행자</th>
                <th scope="col">작업</th>
                <th scope="col">대상</th>
                <th scope="col">상세</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr key={log.id}>
                  <td className={styles.time}>{formatDateTime(log.createdAt)}</td>
                  <td>{log.actorName}</td>
                  <td>{log.action}</td>
                  <td>
                    {log.resourceType}
                    {log.resourceId && ` #${log.resourceId}`}
                  </td>
                  <td className={styles.detail}>{log.detail ?? '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
