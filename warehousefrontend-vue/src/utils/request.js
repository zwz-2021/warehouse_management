import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: '/api', // 对应 vite.config.js 里的代理
    timeout: 5000
})

// 响应拦截器
request.interceptors.response.use(
    response => {
        const res = response.data
        // 如果后端返回 code 不是 200，说明业务逻辑报错
        // 注意：你的后端 Result 类里成功的 code 是 200 吗？如果不是请修改这里
        if (res.code !== 200) {
            ElMessage.error(res.msg || '系统错误')
            return Promise.reject(new Error(res.msg || 'Error'))
        }
        return res
    },
    error => {
        console.error('Request Error:', error)
        ElMessage.error(error.message || '网络请求失败')
        return Promise.reject(error)
    }
)

export default request