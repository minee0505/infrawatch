import { useState } from 'react'
import type { FormEvent } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { ErrorMessage } from '../components/Feedback'
import { useAuthStore } from '../store/authStore'
import styles from './LoginPage.module.scss'

type Mode = 'login' | 'register'

export default function LoginPage() {
  const status = useAuthStore((state) => state.status)
  const signIn = useAuthStore((state) => state.signIn)
  const signUp = useAuthStore((state) => state.signUp)
  const navigate = useNavigate()

  const [mode, setMode] = useState<Mode>('login')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  if (status === 'authenticated') {
    return <Navigate to="/dashboard" replace />
  }

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    setError(null)
    setSubmitting(true)

    try {
      if (mode === 'login') {
        await signIn({ email, password })
      } else {
        await signUp({ email, password, name })
      }
      navigate('/dashboard', { replace: true })
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : '처리하지 못했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className={styles.page}>
      <section className={styles.intro}>
        <span className={styles.mark} aria-hidden="true">
          <svg viewBox="0 0 64 64">
            <path
              d="M14 40 L26 24 L36 32 L50 16"
              fill="none"
              stroke="currentColor"
              strokeWidth="6"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
            <circle cx="50" cy="16" r="6" fill="currentColor" />
          </svg>
        </span>

        <h1 className={styles.title}>infrawatch</h1>
        <p className={styles.tagline}>시설물 안전을 실시간으로 지켜보는 곳</p>
      </section>

      <section className={styles.formBox}>
        <div className={styles.tabs}>
          <button
            type="button"
            className={`${styles.tab} ${mode === 'login' ? styles.tabActive : ''}`}
            onClick={() => setMode('login')}
          >
            로그인
          </button>
          <button
            type="button"
            className={`${styles.tab} ${mode === 'register' ? styles.tabActive : ''}`}
            onClick={() => setMode('register')}
          >
            회원가입
          </button>
        </div>

        <form className={styles.form} onSubmit={handleSubmit}>
          {mode === 'register' && (
            <label className={styles.field} htmlFor="name">
              <span className={styles.label}>이름</span>
              <input
                id="name"
                className={styles.input}
                type="text"
                value={name}
                onChange={(event) => setName(event.target.value)}
                required
              />
            </label>
          )}

          <label className={styles.field} htmlFor="email">
            <span className={styles.label}>이메일</span>
            <input
              id="email"
              className={styles.input}
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </label>

          <label className={styles.field} htmlFor="password">
            <span className={styles.label}>비밀번호</span>
            <input
              id="password"
              className={styles.input}
              type="password"
              minLength={8}
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
            {mode === 'register' && <p className={styles.help}>8자 이상 입력해 주세요.</p>}
          </label>

          {error && <ErrorMessage text={error} />}

          <button type="submit" className={styles.submit} disabled={submitting}>
            {submitting ? '처리하는 중' : mode === 'login' ? '로그인' : '가입하고 시작하기'}
          </button>
        </form>
      </section>

      <p className={styles.notice}>데모 관리자 계정: admin@infrawatch.io / admin1234!</p>
    </div>
  )
}
