import type {
  AlertLevel,
  AlertStatus,
  FacilityType,
  MemberRole,
  SensorStatus,
  SensorType,
} from '../api/types'

/** 서버가 내려주는 ISO 시각 문자열을 화면용으로 바꾼다. */
export function formatDateTime(value: string): string {
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }

  const pad = (n: number) => String(n).padStart(2, '0')
  return `${date.getFullYear()}. ${pad(date.getMonth() + 1)}. ${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

export function formatDate(value: string): string {
  return formatDateTime(value).slice(0, 12)
}

export function formatTime(value: string): string {
  return formatDateTime(value).slice(13)
}

export function formatValue(value: number | null, unit: string): string {
  if (value === null) {
    return '-'
  }
  return `${value} ${unit}`
}

const FACILITY_TYPE_LABEL: Record<FacilityType, string> = {
  BRIDGE: '교량',
  RETAINING_WALL: '옹벽',
  TUNNEL: '터널',
  SLOPE: '사면',
  BUILDING: '건축물',
}

export function facilityTypeLabel(type: FacilityType): string {
  return FACILITY_TYPE_LABEL[type]
}

const SENSOR_TYPE_LABEL: Record<SensorType, string> = {
  TILT: '기울기',
  VIBRATION: '진동',
  CRACK: '균열',
  HUMIDITY: '습도',
  FLOOD: '침수',
}

export function sensorTypeLabel(type: SensorType): string {
  return SENSOR_TYPE_LABEL[type]
}

const SENSOR_STATUS_LABEL: Record<SensorStatus, string> = {
  ACTIVE: '가동중',
  INACTIVE: '중지',
}

export function sensorStatusLabel(status: SensorStatus): string {
  return SENSOR_STATUS_LABEL[status]
}

const ALERT_LEVEL_LABEL: Record<AlertLevel, string> = {
  WARNING: '경고',
  CRITICAL: '위험',
}

export function alertLevelLabel(level: AlertLevel): string {
  return ALERT_LEVEL_LABEL[level]
}

const ALERT_STATUS_LABEL: Record<AlertStatus, string> = {
  OPEN: '열림',
  ACKNOWLEDGED: '확인됨',
  RESOLVED: '해결됨',
}

export function alertStatusLabel(status: AlertStatus): string {
  return ALERT_STATUS_LABEL[status]
}

const MEMBER_ROLE_LABEL: Record<MemberRole, string> = {
  ROLE_ADMIN: '총관리자',
  ROLE_FIELD_MANAGER: '현장 관리자',
}

export function memberRoleLabel(role: MemberRole): string {
  return MEMBER_ROLE_LABEL[role]
}
