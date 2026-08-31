import { api } from './client'
import type { MemberResponse, TokenResponse } from './types'

export interface RegisterRequest {
  email: string
  password: string
  name: string
}

export interface LoginRequest {
  email: string
  password: string
}

export function register(request: RegisterRequest): Promise<TokenResponse> {
  return api.post<TokenResponse>('/api/v1/auth/register', request)
}

export function login(request: LoginRequest): Promise<TokenResponse> {
  return api.post<TokenResponse>('/api/v1/auth/login', request)
}

export function fetchMe(): Promise<MemberResponse> {
  return api.get<MemberResponse>('/api/v1/auth/me')
}
