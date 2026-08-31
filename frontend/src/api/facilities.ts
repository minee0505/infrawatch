import { api } from './client'
import type { FacilityRequest, FacilityResponse } from './types'

export function fetchFacilities(): Promise<FacilityResponse[]> {
  return api.get<FacilityResponse[]>('/api/v1/facilities')
}

export function fetchFacility(facilityId: number): Promise<FacilityResponse> {
  return api.get<FacilityResponse>(`/api/v1/facilities/${facilityId}`)
}

export function createFacility(request: FacilityRequest): Promise<FacilityResponse> {
  return api.post<FacilityResponse>('/api/v1/facilities', request)
}

export function updateFacility(facilityId: number, request: FacilityRequest): Promise<FacilityResponse> {
  return api.put<FacilityResponse>(`/api/v1/facilities/${facilityId}`, request)
}

export function deleteFacility(facilityId: number): Promise<void> {
  return api.delete<void>(`/api/v1/facilities/${facilityId}`)
}
