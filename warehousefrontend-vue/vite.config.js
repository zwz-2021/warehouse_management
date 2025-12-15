import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173, // 前端端口
    proxy: {
      '/api': { // 只要请求以 /api 开头
        target: 'http://localhost:8080', // 就转发给后端 8080
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '') // 发送给后端时去掉 /api
      }
    }
  }
})