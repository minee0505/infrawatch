// 서버 응답 형태를 그대로 옮겨 둔 타입.
// 백엔드 DTO 가 바뀌면 이 파일만 고치면 되고, 화면에서 잘못된 필드를 쓰면 빌드가 깨진다.

export type MemberRole = 'ROLE_ADMIN' | 'ROLE_FIELD_MANAGER'

export type FacilityType = 'BRIDGE' | 'RETAINING_WALL' | 'TUNNEL' | 'SLOPE' | 'BUILDING'

export type SensorType = 'TILT' | 'VIBRATION' | 'CRACK' | 'HUMIDITY' | 'FLOOD'

export type SensorStatus = 'ACTIVE' | 'INACTIVE'

export type AlertLevel = 'WARNING' | 'CRITICAL'

export type AlertStatus = 'OPEN' | 'ACKNOWLEDGED' | 'RESOLVED'

export interface MemberResponse {
  id: number
  email: string
  name: string
  role: MemberRole
}

export interface TokenResponse {
  accessToken: string
  member: MemberResponse
}

export interface FacilityResponse {
  id: number
  name: string
  type: FacilityType
  location: string | null
  description: string | null
  sensorCount: number
  openAlertCount: number
}

export interface FacilityRequest {
  name: string
  type: FacilityType
  location: string
  description: string
}

export interface SensorResponse {
  id: number
  facilityId: number
  facilityName: string
  type: SensorType
  name: string
  unit: string
  thresholdMin: number | null
  thresholdMax: number | null
  status: SensorStatus
  latestValue: number | null
  latestMeasuredAt: string | null
}

export interface SensorRequest {
  type: SensorType
  name: string
  unit: string
  thresholdMin: number | null
  thresholdMax: number | null
  status?: SensorStatus
}

export interface ReadingResponse {
  id: number
  sensorId: number
  value: number
  measuredAt: string
}

export interface ReadingRequest {
  value: number
  measuredAt?: string
}

export interface AlertResponse {
  id: number
  sensorId: number
  sensorName: string
  facilityId: number
  facilityName: string
  level: AlertLevel
  message: string
  status: AlertStatus
  readingValue: number | null
  createdAt: string
  resolvedAt: string | null
  resolvedByName: string | null
}

export interface DashboardSummaryResponse {
  facilityCount: number
  sensorCount: number
  activeSensorCount: number
  openAlertCount: number
  criticalAlertCount: number
  recentAlerts: AlertResponse[]
}

export interface AuditLogResponse {
  id: number
  actorName: string
  action: string
  resourceType: string
  resourceId: string | null
  detail: string | null
  createdAt: string
}
