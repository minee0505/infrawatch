import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { createFacility, fetchFacilities } from '../api/facilities'
import type { FacilityRequest, FacilityType } from '../api/types'
import { Empty, ErrorMessage, Loading } from '../components/Feedback'
import Modal from '../components/Modal'
import PageHeader from '../components/PageHeader'
import { useAsync } from '../hooks/useAsync'
import { isAdmin, useAuthStore } from '../store/authStore'
import { facilityTypeLabel } from '../utils/format'
import styles from './FacilityListPage.module.scss'

const FACILITY_TYPES: FacilityType[] = ['BRIDGE', 'RETAINING_WALL', 'TUNNEL', 'SLOPE', 'BUILDING']

export default function FacilityListPage() {
  const member = useAuthStore((state) => state.member)
  const { data, loading, error, reload } = useAsync(fetchFacilities, [])
  const [creating, setCreating] = useState(false)

  if (loading) {
    return <Loading />
  }
  if (error) {
    return <ErrorMessage text={error} onRetry={reload} />
  }

  const facilities = data ?? []

  return (
    <div>
      <PageHeader
        title="시설물"
        description="관리 중인 교량, 옹벽, 터널, 사면, 건축물 목록입니다."
        actions={
          isAdmin(member) && (
            <button type="button" className={styles.createButton} onClick={() => setCreating(true)}>
              시설물 등록
            </button>
          )
        }
      />

      {facilities.length === 0 ? (
        <Empty>등록된 시설물이 없습니다.</Empty>
      ) : (
        <div className={styles.grid}>
          {facilities.map((facility) => (
            <Link key={facility.id} to={`/facilities/${facility.id}`} className={styles.card}>
              <div className={styles.cardHead}>
                <span className={styles.name}>{facility.name}</span>
                <span className={styles.type}>{facilityTypeLabel(facility.type)}</span>
              </div>
              {facility.location && <p className={styles.location}>{facility.location}</p>}
              <div className={styles.cardFoot}>
                <span>센서 {facility.sensorCount}개</span>
                {facility.openAlertCount > 0 ? (
                  <span className={styles.alertBadge}>열린 알림 {facility.openAlertCount}건</span>
                ) : (
                  <span className={styles.noAlert}>열린 알림 없음</span>
                )}
              </div>
            </Link>
          ))}
        </div>
      )}

      {creating && (
        <CreateFacilityModal
          onClose={() => setCreating(false)}
          onCreated={() => {
            setCreating(false)
            reload()
          }}
        />
      )}
    </div>
  )
}

function CreateFacilityModal({ onClose, onCreated }: { onClose: () => void; onCreated: () => void }) {
  const [name, setName] = useState('')
  const [type, setType] = useState<FacilityType>('BRIDGE')
  const [location, setLocation] = useState('')
  const [description, setDescription] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    setError(null)
    setSubmitting(true)

    const request: FacilityRequest = { name, type, location, description }
    try {
      await createFacility(request)
      onCreated()
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : '등록하지 못했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Modal title="시설물 등록" onClose={onClose}>
      <form className={styles.form} onSubmit={handleSubmit}>
        <label className={styles.field} htmlFor="facility-name">
          <span className={styles.label}>이름</span>
          <input
            id="facility-name"
            className={styles.input}
            value={name}
            onChange={(event) => setName(event.target.value)}
            required
          />
        </label>

        <label className={styles.field} htmlFor="facility-type">
          <span className={styles.label}>유형</span>
          <select
            id="facility-type"
            className={styles.input}
            value={type}
            onChange={(event) => setType(event.target.value as FacilityType)}
          >
            {FACILITY_TYPES.map((facilityType) => (
              <option key={facilityType} value={facilityType}>
                {facilityTypeLabel(facilityType)}
              </option>
            ))}
          </select>
        </label>

        <label className={styles.field} htmlFor="facility-location">
          <span className={styles.label}>위치</span>
          <input
            id="facility-location"
            className={styles.input}
            value={location}
            onChange={(event) => setLocation(event.target.value)}
          />
        </label>

        <label className={styles.field} htmlFor="facility-description">
          <span className={styles.label}>설명</span>
          <textarea
            id="facility-description"
            className={styles.textarea}
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            rows={3}
          />
        </label>

        {error && <ErrorMessage text={error} />}

        <button type="submit" className={styles.submit} disabled={submitting}>
          {submitting ? '등록하는 중' : '등록'}
        </button>
      </form>
    </Modal>
  )
}
