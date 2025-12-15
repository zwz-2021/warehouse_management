<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="220px" class="aside-menu">
        <div class="logo">WMS 智能仓储</div>
        <el-menu
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          :default-active="$route.path"
          :router="true" 
          style="border-right: none;"
        >
          <el-sub-menu index="1">
            <template #title>
              <el-icon><User /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/user">用户管理</el-menu-item>
            </el-sub-menu>

          <el-sub-menu index="2">
            <template #title>
              <el-icon><Box /></el-icon>
              <span>基础档案</span>
            </template>
            <el-menu-item index="/goods">货物管理</el-menu-item>
            <el-menu-item index="/location">货位管理</el-menu-item>
            <el-menu-item index="/inventory">库存查询</el-menu-item>
            </el-sub-menu>

          <el-sub-menu index="3">
            <template #title>
              <el-icon><Download /></el-icon>
              <span>入库管理</span>
            </template>
            <el-menu-item index="/inbound">入库单据</el-menu-item>
            </el-sub-menu>

          <el-sub-menu index="4">
            <template #title>
              <el-icon><Upload /></el-icon>
              <span>出库管理</span>
            </template>
            <el-menu-item index="/order">销售订单</el-menu-item>
            </el-sub-menu>
          
           <el-menu-item index="/robot-task">
            <el-icon><Monitor /></el-icon>
            <span>机器人任务监控</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div style="flex: 1"></div>
          
          <div class="user-info">
            <el-dropdown>
              <span class="el-dropdown-link">
                <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" style="margin-right: 8px;"/>
                {{ user.realName || user.username || '管理员' }}
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>个人中心</el-dropdown-item>
                  <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main style="background-color: #f0f2f5;">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
// 引入需要的图标
import { User, Box, Download, Upload, Monitor, Van, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()

// 从本地缓存获取用户信息
const userStr = localStorage.getItem('user')
// 如果解析失败或没有，给个默认空对象防止报错
const user = ref(userStr ? JSON.parse(userStr) : {})

// 退出登录逻辑
const logout = () => {
  // 1. 清除本地缓存
  localStorage.removeItem('user')
  // 2. 跳转回登录页
  router.push('/login')
}
</script>

<style scoped>
.common-layout {
  height: 100vh;
  display: flex;
}
.el-container {
  height: 100%;
}
.aside-menu {
  background-color: #304156;
  color: white;
  transition: width 0.3s;
  overflow-x: hidden;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-weight: bold;
  font-size: 20px;
  color: white;
  background-color: #2b3649;
  overflow: hidden;
}
.header {
  background-color: white;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #ddd;
  padding: 0 20px;
  height: 60px;
}
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.el-dropdown-link {
  display: flex;
  align-items: center;
  color: #333;
  font-weight: 500;
}
/* 覆盖 Element Plus 默认的菜单背景色 */
:deep(.el-menu) {
  border-right: none;
}
</style>