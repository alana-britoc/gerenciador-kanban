import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// manda o /api pro back em Spring Boot
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
