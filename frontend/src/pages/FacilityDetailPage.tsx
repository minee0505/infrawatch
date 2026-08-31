import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, Navigate, useNavigate, useParams } from 'react-router-dom'
import { deleteFacility, fetchFacility, updateFacility } from '../api/facilities'
import { createReading, fetchRecentReadings } from '../api/readings'
import { createSensor, deleteSensor, fetchSensors, updateSensor } from '../api/sensors'
import type { FacilityRequest, FacilityType, SensorRequest, SensorResponse, SensorType } from '../api/types'
import { SensorStatusBadge } from '../components/Badge'
import { Empty, ErrorMessage, Loading } from '../components/Feedback'
import Modal from '../components/Modal'
import PageHeader from '../components/PageHeader'
import SensorTrendChart from '../components/SensorTrendChart'
import { useAsync } from '../hooks/useAsync'
import { isAdmin, useAuthStore } from '../store/authStore'
import { facilityTypeLabel, formatDateTime, formatValue, sensorTypeLabel } from '../utils/format'
import styles from './FacilityDetailPage.module.scss'

const FACILITY_TYPES: FacilityType[] = ['BRIDGE', 'RETAINING_WALL', 'TUNNEL', 'SLOPE', 'BUILDING']
const SENSOR_TYPES: SensorType[] = ['TILT', 'VIBRATION', 'CRACK', 'HUMIDITY', 'FLOOD']

export default function FacilityDetailPage() {
  const { facilityId = '' } = useParams()
  const id = Number(facilityId)
  const navigate = useNavigate()
  const member = useAuthStore((state) => state.member)
  const admin = isAdmin(member)

  const { data, loading, error, reload } = useAsync(async () => {
    const [facility, sensors] = await Promise.all([fetchFacility(id), fetchSensors(id)])
    return { facility, sensors }
  }, [id])

  const [editing, setEditing] = useState(false)
  const [deleting, setDeleting] = useState(false)
  const [deleteError, setDeleteError] = useState<string | null>(null)
  const [creatingSensor, setCreatingSensor] = useState(false)

  if (!facilityId || Number.isNaN(id)) {
    return <Navigate to="/facilities" replace />
  }
  if (loading) {
    return <Loading />
  }
  if (error) {
    return <ErrorMessage text={error} onRetry={reload} />
  }
  if (!data) {
    return <Empty>시설물을 찾을 수 없습니다.</Empty>
  }

  const { facility, sensors } = data

  const handleDelete = async () => {
    if (!window.confirm('시설물을 삭제하면 소속 센서와 측정 기록도 함께 사라집니다. 삭제할까요?')) {
      return
    }
    setDeleteError(null)
    setDeleting(true)
    try {
      await deleteFacility(id)
      navigate('/facilities', { replace: true })
    } catch (deleteFailure) {
      setDeleteError(deleteFailure instanceof Error ? deleteFailure.message : '삭제하지 못했습니다.')
      setDeleting(false)
    }
  }

  return (
    <div>
      <p className={styles.back}>
        <Link to="/facilities">시설물 목록으로</Link>
      </p>

      <PageHeader
        title={facility.name}
        description={`${facilityTypeLabel(facility.type)}${facility.location ? ` · ${facility.location}` : ''}`}
        actions={
          admin && (
            <>
              <button type="button" className={styles.secondaryButton} onClick={() => setEditing(true)}>
                수정
              </button>
              <button
                type="button"
                className={styles.dangerButton}
                onClick={handleDelete}
                disabled={deleting}
              >
                {deleting ? '삭제하는 중' : '삭제'}
              </button>
            </>
          )
        }
      />

      {deleteError && <ErrorMessage text={deleteError} />}

      {facility.description && <p className={styles.description}>{facility.description}</p>}

      <section className={styles.sensors}>
        <div className={styles.sectionHead}>
          <h2 className={styles.sectionTitle}>센서</h2>
          {admin && (
            <button
              type="button"
              className={styles.secondaryButton}
              onClick={() => setCreatingSensor(true)}
            >
              센서 등록
            </button>
          )}
        </div>

        {sensors.length === 0 ? (
          <Empty>등록된 센서가 없습니다.</Empty>
        ) : (
          <ul className={styles.sensorList}>
            {sensors.map((sensor) => (
              <SensorRow key={sensor.id} sensor={sensor} admin={admin} onChanged={reload} />
            ))}
          </ul>
        )}
      </section>

      {editing && (
        <EditFacilityModal
          facilityId={id}
          initial={facility}
          onClose={() => setEditing(false)}
          onSaved={() => {
            setEditing(false)
            reload()
          }}
        />
      )}

      {creatingSensor && (
        <SensorFormModal
          title="센서 등록"
          initial={null}
          onClose={() => setCreatingSensor(false)}
          onSubmit={(request) => createSensor(id, request)}
          onSaved={() => {
            setCreatingSensor(false)
            reload()
          }}
        />
      )}
    </div>
  )
}

function EditFacilityModal({
  facilityId,
  initial,
  onClose,
  onSaved,
}: {
  facilityId: number
  initial: { name: string; type: FacilityType; location: string | null; description: string | null }
  onClose: () => void
  onSaved: () => void
}) {
  const [name, setName] = useState(initial.name)
  const [type, setType] = useState<FacilityType>(initial.type)
  const [location, setLocation] = useState(initial.location ?? '')
  const [description, setDescription] = useState(initial.description ?? '')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    setError(null)
    setSubmitting(true)

    const request: FacilityRequest = { name, type, location, description }
    try {
      await updateFacility(facilityId, request)
      onSaved()
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : '수정하지 못했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Modal title="시설물 수정" onClose={onClose}>
      <form className={styles.form} onSubmit={handleSubmit}>
        <label className={styles.field} htmlFor="edit-facility-name">
          <span className={styles.label}>이름</span>
          <input
            id="edit-facility-name"
            className={styles.input}
            value={name}
            onChange={(event) => setName(event.target.value)}
            required
          />
        </label>

        <label className={styles.field} htmlFor="edit-facility-type">
          <span className={styles.label}>유형</span>
          <select
            id="edit-facility-type"
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

        <label className={styles.field} htmlFor="edit-facility-location">
          <span className={styles.label}>위치</span>
          <input
            id="edit-facility-location"
            className={styles.input}
            value={location}
            onChange={(event) => setLocation(event.target.value)}
          />
        </label>

        <label className={styles.field} htmlFor="edit-facility-description">
          <span className={styles.label}>설명</span>
          <textarea
            id="edit-facility-description"
            className={styles.textarea}
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            rows={3}
          />
        </label>

        {error && <ErrorMessage text={error} />}

        <button type="submit" className={styles.submit} disabled={submitting}>
          {submitting ? '저장하는 중' : '저장'}
        </button>
      </form>
    </Modal>
  )
}

function SensorRow({
  sensor,
  admin,
  onChanged,
}: {
  sensor: SensorResponse
  admin: boolean
  onChanged: () => void
}) {
  const [expanded, setExpanded] = useState(false)
  const [editing, setEditing] = useState(false)
  const [deleting, setDeleting] = useState(false)
  const [deleteError, setDeleteError] = useState<string | null>(null)

  const handleDelete = async () => {
    if (!window.confirm(`'${sensor.name}' 센서를 삭제할까요? 측정 기록도 함께 사라집니다.`)) {
      return
    }
    setDeleteError(null)
    setDeleting(true)
    try {
      await deleteSensor(sensor.id)
      onChanged()
    } catch (deleteFailure) {
      setDeleteError(deleteFailure instanceof Error ? deleteFailure.message : '삭제하지 못했습니다.')
      setDeleting(false)
    }
  }

  return (
    <li className={styles.sensorItem}>
      <button type="button" className={styles.sensorHead} onClick={() => setExpanded((v) => !v)}>
        <span className={styles.sensorType}>{sensorTypeLabel(sensor.type)}</span>
        <span className={styles.sensorName}>{sensor.name}</span>
        <span className={styles.sensorValue}>{formatValue(sensor.latestValue, sensor.unit)}</span>
        <SensorStatusBadge status={sensor.status} />
        <span className={styles.chevron}>{expanded ? '▲' : '▼'}</span>
      </button>

      {expanded && (
        <div className={styles.sensorBody}>
          <dl className={styles.sensorMeta}>
            <div>
              <dt>임계값 하한</dt>
              <dd>{sensor.thresholdMin === null ? '-' : `${sensor.thresholdMin} ${sensor.unit}`}</dd>
            </div>
            <div>
              <dt>임계값 상한</dt>
              <dd>{sensor.thresholdMax === null ? '-' : `${sensor.thresholdMax} ${sensor.unit}`}</dd>
            </div>
            <div>
              <dt>최근 측정 시각</dt>
              <dd>{sensor.latestMeasuredAt ? formatDateTime(sensor.latestMeasuredAt) : '-'}</dd>
            </div>
          </dl>

          {admin && (
            <div className={styles.sensorActions}>
              <button type="button" className={styles.secondaryButton} onClick={() => setEditing(true)}>
                수정
              </button>
              <button
                type="button"
                className={styles.dangerButton}
                onClick={handleDelete}
                disabled={deleting}
              >
                {deleting ? '삭제하는 중' : '삭제'}
              </button>
            </div>
          )}
          {deleteError && <ErrorMessage text={deleteError} />}

          <SensorTrendPanel sensor={sensor} onReadingAdded={onChanged} />
        </div>
      )}

      {editing && (
        <SensorFormModal
          title="센서 수정"
          initial={sensor}
          onClose={() => setEditing(false)}
          onSubmit={(request) => updateSensor(sensor.id, request)}
          onSaved={() => {
            setEditing(false)
            onChanged()
          }}
        />
      )}
    </li>
  )
}

function SensorTrendPanel({
  sensor,
  onReadingAdded,
}: {
  sensor: SensorResponse
  onReadingAdded: () => void
}) {
  const { data, loading, error, reload } = useAsync(() => fetchRecentReadings(sensor.id), [sensor.id])
  const [value, setValue] = useState('')
  const [submitError, setSubmitError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    setSubmitError(null)

    const numericValue = Number(value)
    if (!Number.isFinite(numericValue)) {
      setSubmitError('숫자를 입력해 주세요.')
      return
    }

    setSubmitting(true)
    try {
      await createReading(sensor.id, { value: numericValue })
      setValue('')
      reload()
      onReadingAdded()
    } catch (submitFailure) {
      setSubmitError(submitFailure instanceof Error ? submitFailure.message : '입력하지 못했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className={styles.trendPanel}>
      {loading ? (
        <Loading text="측정값을 불러오는 중입니다." />
      ) : error ? (
        <ErrorMessage text={error} onRetry={reload} />
      ) : (
        <SensorTrendChart
          readings={[...(data ?? [])].reverse()}
          unit={sensor.unit}
          thresholdMin={sensor.thresholdMin}
          thresholdMax={sensor.thresholdMax}
        />
      )}

      <form className={styles.readingForm} onSubmit={handleSubmit}>
        <span className={styles.readingLabel}>측정값 수동 입력</span>
        <input
          className={styles.readingInput}
          type="number"
          step="any"
          placeholder={`값 (${sensor.unit})`}
          value={value}
          onChange={(event) => setValue(event.target.value)}
          required
        />
        <button type="submit" className={styles.readingSubmit} disabled={submitting}>
          {submitting ? '입력하는 중' : '입력'}
        </button>
      </form>
      {submitError && <ErrorMessage text={submitError} />}
    </div>
  )
}

function SensorFormModal({
  title,
  initial,
  onClose,
  onSubmit,
  onSaved,
}: {
  title: string
  initial: SensorResponse | null
  onClose: () => void
  onSubmit: (request: SensorRequest) => Promise<SensorResponse>
  onSaved: () => void
}) {
  const [type, setType] = useState<SensorType>(initial?.type ?? 'TILT')
  const [name, setName] = useState(initial?.name ?? '')
  const [unit, setUnit] = useState(initial?.unit ?? '')
  const [thresholdMin, setThresholdMin] = useState(
    initial?.thresholdMin === null || initial?.thresholdMin === undefined
      ? ''
      : String(initial.thresholdMin),
  )
  const [thresholdMax, setThresholdMax] = useState(
    initial?.thresholdMax === null || initial?.thresholdMax === undefined
      ? ''
      : String(initial.thresholdMax),
  )
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    setError(null)

    if (thresholdMin.trim() === '' && thresholdMax.trim() === '') {
      setError('임계값 하한, 상한 중 하나는 입력해야 합니다.')
      return
    }

    setSubmitting(true)
    try {
      await onSubmit({
        type,
        name,
        unit,
        thresholdMin: thresholdMin.trim() === '' ? null : Number(thresholdMin),
        thresholdMax: thresholdMax.trim() === '' ? null : Number(thresholdMax),
        status: initial?.status,
      })
      onSaved()
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : '저장하지 못했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Modal title={title} onClose={onClose}>
      <form className={styles.form} onSubmit={handleSubmit}>
        <label className={styles.field} htmlFor="sensor-type">
          <span className={styles.label}>유형</span>
          <select
            id="sensor-type"
            className={styles.input}
            value={type}
            onChange={(event) => setType(event.target.value as SensorType)}
          >
            {SENSOR_TYPES.map((sensorType) => (
              <option key={sensorType} value={sensorType}>
                {sensorTypeLabel(sensorType)}
              </option>
            ))}
          </select>
        </label>

        <label className={styles.field} htmlFor="sensor-name">
          <span className={styles.label}>이름</span>
          <input
            id="sensor-name"
            className={styles.input}
            value={name}
            onChange={(event) => setName(event.target.value)}
            required
          />
        </label>

        <label className={styles.field} htmlFor="sensor-unit">
          <span className={styles.label}>단위</span>
          <input
            id="sensor-unit"
            className={styles.input}
            placeholder="예: mm, %, deg"
            value={unit}
            onChange={(event) => setUnit(event.target.value)}
            required
          />
        </label>

        <div className={styles.thresholdRow}>
          <label className={styles.field} htmlFor="sensor-threshold-min">
            <span className={styles.label}>임계값 하한</span>
            <input
              id="sensor-threshold-min"
              className={styles.input}
              type="number"
              step="any"
              value={thresholdMin}
              onChange={(event) => setThresholdMin(event.target.value)}
            />
          </label>

          <label className={styles.field} htmlFor="sensor-threshold-max">
            <span className={styles.label}>임계값 상한</span>
            <input
              id="sensor-threshold-max"
              className={styles.input}
              type="number"
              step="any"
              value={thresholdMax}
              onChange={(event) => setThresholdMax(event.target.value)}
            />
          </label>
        </div>

        {error && <ErrorMessage text={error} />}

        <button type="submit" className={styles.submit} disabled={submitting}>
          {submitting ? '저장하는 중' : '저장'}
        </button>
      </form>
    </Modal>
  )
}
