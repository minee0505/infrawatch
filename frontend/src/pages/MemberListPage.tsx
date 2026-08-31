import { useState } from 'react'
import { fetchMembers, updateMemberRole } from '../api/members'
import type { MemberRole } from '../api/types'
import { ErrorMessage, Loading } from '../components/Feedback'
import PageHeader from '../components/PageHeader'
import { useAsync } from '../hooks/useAsync'
import { memberRoleLabel } from '../utils/format'
import styles from './MemberListPage.module.scss'

export default function MemberListPage() {
  const { data, loading, error, reload } = useAsync(fetchMembers, [])
  const [updating, setUpdating] = useState<number | null>(null)
  const [updateError, setUpdateError] = useState<string | null>(null)

  if (loading) {
    return <Loading />
  }
  if (error) {
    return <ErrorMessage text={error} onRetry={reload} />
  }

  const members = data ?? []

  const handleChangeRole = async (memberId: number, role: MemberRole) => {
    setUpdateError(null)
    setUpdating(memberId)
    try {
      await updateMemberRole(memberId, role === 'ROLE_ADMIN')
      reload()
    } catch (cause) {
      setUpdateError(cause instanceof Error ? cause.message : '변경하지 못했습니다.')
    } finally {
      setUpdating(null)
    }
  }

  return (
    <div>
      <PageHeader title="구성원" description="계정 권한을 변경할 수 있습니다." />

      {updateError && <ErrorMessage text={updateError} />}

      <div className={styles.tableWrap}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th scope="col">이름</th>
              <th scope="col">이메일</th>
              <th scope="col">권한</th>
            </tr>
          </thead>
          <tbody>
            {members.map((member) => (
              <tr key={member.id}>
                <td>{member.name}</td>
                <td>{member.email}</td>
                <td>
                  <select
                    className={styles.select}
                    value={member.role}
                    disabled={updating === member.id}
                    onChange={(event) => handleChangeRole(member.id, event.target.value as MemberRole)}
                  >
                    <option value="ROLE_ADMIN">{memberRoleLabel('ROLE_ADMIN')}</option>
                    <option value="ROLE_FIELD_MANAGER">{memberRoleLabel('ROLE_FIELD_MANAGER')}</option>
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
