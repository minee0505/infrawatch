import { clearToken, readToken } from './token'

/** 서버가 내려주는 오류 응답(ErrorResponse) 형태 */
interface ErrorBody {
  code: string
  message: string
}

export class ApiError extends Error {
  readonly status: number
  readonly code: string

  constructor(status: number, code: string, message: string) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.code = code
  }
}

// 토큰이 만료되면 모든 화면에서 같은 처리를 해야 한다.
// 여기서 스토어를 직접 부르면 스토어와 서로를 참조하게 되므로 처리 함수를 밖에서 등록받는다.
let unauthorizedHandler: (() => void) | null = null

export function setUnauthorizedHandler(handler: () => void): void {
  unauthorizedHandler = handler
}

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const token = readToken()
  const headers = new Headers(init.headers)
  headers.set('Accept', 'application/json')

  if (init.body) {
    headers.set('Content-Type', 'application/json')
  }
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(path, { ...init, headers })

  if (response.status === 401) {
    clearToken()
    unauthorizedHandler?.()
    throw new ApiError(401, 'A001', '로그인이 필요합니다.')
  }

  if (!response.ok) {
    throw new ApiError(response.status, ...(await readError(response)))
  }

  if (response.status === 204) {
    return undefined as T
  }
  return (await response.json()) as T
}

/** 오류 응답 본문이 JSON 이 아닌 경우(프록시 오류 등)까지 감안해 읽는다. */
async function readError(response: Response): Promise<[string, string]> {
  try {
    const body = (await response.json()) as ErrorBody
    return [body.code, body.message]
  } catch {
    return ['C002', `요청을 처리하지 못했습니다. (HTTP ${response.status})`]
  }
}

export const api = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, body?: unknown) =>
    request<T>(path, { method: 'POST', body: body === undefined ? undefined : JSON.stringify(body) }),
  put: <T>(path: string, body: unknown) =>
    request<T>(path, { method: 'PUT', body: JSON.stringify(body) }),
  patch: <T>(path: string, body?: unknown) =>
    request<T>(path, { method: 'PATCH', body: body === undefined ? undefined : JSON.stringify(body) }),
  delete: <T>(path: string) => request<T>(path, { method: 'DELETE' }),
}
