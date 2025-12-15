import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 1. 新增 Login 路由
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/',
      component: Layout,
      redirect: '/user', // 默认进用户页
      children: [
         { path: 'user', component: () => import('@/views/system/UserView.vue') },
         { path: 'goods', component: () => import('@/views/base/GoodsView.vue') },
         { path: 'inbound', component: () => import('@/views/inbound/InboundView.vue') },
         { path: 'robot-task', component: () => import('@/views/robot/TaskView.vue') },
         { path: 'order', component: () => import('@/views/outbound/OrderView.vue') },
         { path: 'transport', component: () => import('@/views/transport/TransportView.vue') },
         { path: 'inventory', component: () => import('@/views/base/InventoryView.vue') },
         { path: 'location', component: () => import('@/views/base/LocationView.vue') },
      ]
    }
  ]
})

// 2. 路由守卫 (关键！)
router.beforeEach((to, from, next) => {
  // 获取缓存的用户信息
  const user = localStorage.getItem('user')
  
  // 如果要去的地方是 login，直接放行
  if (to.path === '/login') {
    next()
  } else {
    // 如果要去别的地方，检查有没有登录
    if (!user) {
      // 没登录，强制跳回 login
      next('/login')
    } else {
      // 登录了，放行
      next()
    }
  }
})

export default router