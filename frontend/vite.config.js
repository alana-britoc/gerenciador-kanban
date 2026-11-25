import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// manda o /api e o WebSocket do quadro pro back em Spring Boot
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
      '/ws': { target: 'ws://localhost:8080', ws: true },
    },
  },
})
