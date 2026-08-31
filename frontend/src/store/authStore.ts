import { create } from 'zustand'
import { fetchMe, login, register } from '../api/auth'
import type { LoginRequest, RegisterRequest } from '../api/auth'
import { clearToken, readToken, writeToken } from '../api/token'
import type { MemberResponse } from '../api/types'

type AuthStatus = 'checking' | 'authenticated' | 'anonymous'

interface AuthState {
  status: AuthStatus
  member: MemberResponse | null

  /** 저장된 토큰이 아직 쓸 수 있는지 확인한다. 앱이 처음 뜰 때 한 번 부른다. */
  restore: () => Promise<void>
  signIn: (request: LoginRequest) => Promise<void>
  signUp: (request: RegisterRequest) => Promise<void>
  signOut: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  status: 'checking',
  member: null,

  restore: async () => {
    if (!readToken()) {
      set({ status: 'anonymous', member: null })
      return
    }

    try {
      set({ status: 'authenticated', member: await fetchMe() })
    } catch {
      // 토큰이 만료됐거나 계정이 사라진 경우
      clearToken()
      set({ status: 'anonymous', member: null })
    }
  },

  signIn: async (request) => {
    const { accessToken, member } = await login(request)
    writeToken(accessToken)
    set({ status: 'authenticated', member })
  },

  signUp: async (request) => {
    const { accessToken, member } = await register(request)
    writeToken(accessToken)
    set({ status: 'authenticated', member })
  },

  signOut: () => {
    clearToken()
    set({ status: 'anonymous', member: null })
  },
}))

export function isAdmin(member: MemberResponse | null): boolean {
  return member?.role === 'ROLE_ADMIN'
}
