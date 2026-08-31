import { api } from './client'
import type { AuditLogResponse } from './types'

export function fetchAuditLogs(): Promise<AuditLogResponse[]> {
  return api.get<AuditLogResponse[]>('/api/v1/audit-logs')
}
