import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],

  server: {
    port: 5173,
    // 개발 중에는 프론트(5173)와 API(8080) 의 포트가 다르다.
    // 개발 서버가 대신 전달하면 브라우저 입장에서는 같은 출처라 CORS 설정이 필요 없다.
    // 배포할 때는 빌드 결과물을 스프링이 함께 서빙하므로 이 프록시가 쓰이지 않는다.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
