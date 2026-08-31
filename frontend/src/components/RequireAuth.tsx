import { Navigate, Outlet } from 'react-router-dom'
import { isAdmin, useAuthStore } from '../store/authStore'
import { Loading } from './Feedback'

interface Props {
  /** 총관리자만 들어갈 수 있는 화면인지 여부 */
  adminOnly?: boolean
}

/**
 * 화면 진입을 막는 것은 편의일 뿐이고 실제 권한 판단은 서버가 한다.
 * 주소를 직접 입력해 들어와도 API 가 401, 403 을 돌려준다.
 */
export default function RequireAuth({ adminOnly = false }: Props) {
  const status = useAuthStore((state) => state.status)
  const member = useAuthStore((state) => state.member)

  if (status === 'checking') {
    return <Loading text="로그인 상태를 확인하고 있습니다." />
  }
  if (status === 'anonymous') {
    return <Navigate to="/login" replace />
  }
  if (adminOnly && !isAdmin(member)) {
    return <Navigate to="/dashboard" replace />
  }
  return <Outlet />
}
