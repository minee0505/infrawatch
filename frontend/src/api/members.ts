import { api } from './client'
import type { MemberResponse } from './types'

export function fetchMembers(): Promise<MemberResponse[]> {
  return api.get<MemberResponse[]>('/api/v1/members')
}

export function updateMemberRole(memberId: number, admin: boolean): Promise<MemberResponse> {
  return api.patch<MemberResponse>(`/api/v1/members/${memberId}/role`, { admin })
}
