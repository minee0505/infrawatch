import { api } from './client'
import type { AlertResponse, AlertStatus } from './types'

export function fetchAlerts(status?: AlertStatus): Promise<AlertResponse[]> {
  const query = status ? `?status=${status}` : ''
  return api.get<AlertResponse[]>(`/api/v1/alerts${query}`)
}

export function acknowledgeAlert(alertId: number): Promise<AlertResponse> {
  return api.patch<AlertResponse>(`/api/v1/alerts/${alertId}/acknowledge`)
}

export function resolveAlert(alertId: number): Promise<AlertResponse> {
  return api.patch<AlertResponse>(`/api/v1/alerts/${alertId}/resolve`)
}
