import { api } from './client'
import type { SensorRequest, SensorResponse } from './types'

export function fetchSensors(facilityId: number): Promise<SensorResponse[]> {
  return api.get<SensorResponse[]>(`/api/v1/facilities/${facilityId}/sensors`)
}

export function createSensor(facilityId: number, request: SensorRequest): Promise<SensorResponse> {
  return api.post<SensorResponse>(`/api/v1/facilities/${facilityId}/sensors`, request)
}

export function fetchSensor(sensorId: number): Promise<SensorResponse> {
  return api.get<SensorResponse>(`/api/v1/sensors/${sensorId}`)
}

export function updateSensor(sensorId: number, request: SensorRequest): Promise<SensorResponse> {
  return api.put<SensorResponse>(`/api/v1/sensors/${sensorId}`, request)
}

export function deleteSensor(sensorId: number): Promise<void> {
  return api.delete<void>(`/api/v1/sensors/${sensorId}`)
}
