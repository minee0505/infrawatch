import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { isAdmin, useAuthStore } from '../store/authStore'
import { memberRoleLabel } from '../utils/format'
import styles from './Layout.module.scss'

export default function Layout() {
  const member = useAuthStore((state) => state.member)
  const signOut = useAuthStore((state) => state.signOut)
  const navigate = useNavigate()

  const admin = isAdmin(member)

  const handleSignOut = () => {
    signOut()
    navigate('/login', { replace: true })
  }

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    isActive ? `${styles.link} ${styles.active}` : styles.link

  return (
    <div className={styles.shell}>
      <aside className={styles.sidebar}>
        <div className={styles.brand}>infrawatch</div>

        <nav className={styles.nav}>
          <NavLink to="/dashboard" className={linkClass}>
            대시보드
          </NavLink>
          <NavLink to="/facilities" className={linkClass}>
            시설물
          </NavLink>
          <NavLink to="/alerts" className={linkClass}>
            알림
          </NavLink>
          {admin && (
            <>
              <NavLink to="/members" className={linkClass}>
                구성원
              </NavLink>
              <NavLink to="/audit-logs" className={linkClass}>
                감사 로그
              </NavLink>
            </>
          )}
        </nav>

        <div className={styles.account}>
          {member && (
            <div className={styles.identity}>
              <span className={styles.name}>{member.name}</span>
              <span className={styles.email}>{member.email}</span>
              <span className={styles.role}>{memberRoleLabel(member.role)}</span>
            </div>
          )}
          <button type="button" className={styles.signOut} onClick={handleSignOut}>
            로그아웃
          </button>
        </div>
      </aside>

      <main className={styles.main}>
        <Outlet />
      </main>
    </div>
  )
}
