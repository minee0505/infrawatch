import { api } from './client'
import type { DashboardSummaryResponse } from './types'

export function fetchDashboardSummary(): Promise<DashboardSummaryResponse> {
  return api.get<DashboardSummaryResponse>('/api/v1/dashboard/summary')
}
