import { api } from './client'
import type { ReadingRequest, ReadingResponse } from './types'

/** 최근 20건, 최신순 */
export function fetchRecentReadings(sensorId: number): Promise<ReadingResponse[]> {
  return api.get<ReadingResponse[]>(`/api/v1/sensors/${sensorId}/readings`)
}

export function createReading(sensorId: number, request: ReadingRequest): Promise<ReadingResponse> {
  return api.post<ReadingResponse>(`/api/v1/sensors/${sensorId}/readings`, request)
}
