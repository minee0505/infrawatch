import { useEffect } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import { setUnauthorizedHandler } from './api/client'
import Layout from './components/Layout'
import RequireAuth from './components/RequireAuth'
import AlertListPage from './pages/AlertListPage'
import AuditLogPage from './pages/AuditLogPage'
import DashboardPage from './pages/DashboardPage'
import FacilityDetailPage from './pages/FacilityDetailPage'
import FacilityListPage from './pages/FacilityListPage'
import LoginPage from './pages/LoginPage'
import MemberListPage from './pages/MemberListPage'
import NotFoundPage from './pages/NotFoundPage'
import { useAuthStore } from './store/authStore'

export default function App() {
  const restore = useAuthStore((state) => state.restore)

  useEffect(() => {
    // 토큰이 만료되면 어느 화면에서 요청했든 로그인 상태를 정리한다.
    setUnauthorizedHandler(() => useAuthStore.getState().signOut())
    void restore()
  }, [restore])

  return (
    <Routes>
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="/login" element={<LoginPage />} />

      <Route element={<RequireAuth />}>
        <Route element={<Layout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/facilities" element={<FacilityListPage />} />
          <Route path="/facilities/:facilityId" element={<FacilityDetailPage />} />
          <Route path="/alerts" element={<AlertListPage />} />
        </Route>
      </Route>

      <Route element={<RequireAuth adminOnly />}>
        <Route element={<Layout />}>
          <Route path="/members" element={<MemberListPage />} />
          <Route path="/audit-logs" element={<AuditLogPage />} />
        </Route>
      </Route>

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}
