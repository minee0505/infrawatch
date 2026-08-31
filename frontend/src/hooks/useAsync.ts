import { useEffect, useState } from 'react'
import type { DependencyList } from 'react'

interface AsyncState<T> {
  data: T | null
  loading: boolean
  error: string | null
  reload: () => void
}

/**
 * 화면에 들어올 때 API 를 한 번 부르고 로딩, 오류, 결과를 함께 관리한다.
 * 목록 화면마다 같은 코드를 반복하게 되어 하나로 묶었다.
 *
 * load 함수는 렌더링마다 새로 만들어지므로 의존성 목록에 넣지 않는다.
 * 다시 부를 조건은 호출하는 쪽이 deps 로 직접 지정한다.
 */
export function useAsync<T>(load: () => Promise<T>, deps: DependencyList): AsyncState<T> {
  const [data, setData] = useState<T | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [reloadCount, setReloadCount] = useState(0)

  useEffect(() => {
    let cancelled = false

    setLoading(true)
    setError(null)

    load()
      .then((result) => {
        if (!cancelled) {
          setData(result)
        }
      })
      .catch((cause: unknown) => {
        if (!cancelled) {
          setError(cause instanceof Error ? cause.message : '요청을 처리하지 못했습니다.')
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false)
        }
      })

    return () => {
      cancelled = true
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, reloadCount])

  return { data, loading, error, reload: () => setReloadCount((count) => count + 1) }
}
