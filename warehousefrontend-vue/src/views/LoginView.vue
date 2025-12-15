<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Box, InfoFilled } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)

const form = ref({ username: 'admin', password: '123456' })

const handleLogin = () => {
  if (!form.value.username || !form.value.password) return ElMessage.warning('请输入账号密码')
  loading.value = true
  setTimeout(() => {
    ElMessage.success('登录成功')
    localStorage.setItem('user', JSON.stringify({ username: form.value.username, role: 'ADMIN' }))
    router.push('/')
    loading.value = false
  }, 600)
}

// 快速填单
const fill = (u) => { form.value.username = u; form.value.password = '123456'; }
</script>

<template>
  <div class="login-wrapper">
    <div class="login-bg-decoration"></div>

    <div class="login-card">
      <div class="header">
        <div class="logo-box">
          <el-icon :size="32" color="#fff"><Box /></el-icon>
        </div>
        <h1 class="system-name">WMS 智能仓储系统</h1>
      </div>

      <div class="form-area">
        <h3 class="welcome-text">账号登录</h3>
        
        <el-form size="large" class="login-form">
          <el-form-item>
            <el-input 
              v-model="form.username" 
              placeholder="请输入用户名" 
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item>
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码" 
              :prefix-icon="Lock" 
              show-password 
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          
          <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form>
      </div>
      
      <div class="footer">
        © 2024 WMS System v1.0
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 1. 外层容器：Flex 居中，背景色柔和 */
.login-wrapper {
  min-height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  position: relative;
  overflow: hidden;
}

/* 2. 背景装饰：使用 CSS 绘制网格，增加科技感但完全不影响布局 */
.login-bg-decoration {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background-image: 
    linear-gradient(#e4e7ed 1px, transparent 1px),
    linear-gradient(90deg, #e4e7ed 1px, transparent 1px);
  background-size: 30px 30px; /* 网格大小 */
  z-index: 0;
}

/* 3. 核心登录卡片：黄金尺寸 420px */
.login-card {
  position: relative;
  z-index: 1;
  width: 420px; /* 锁定宽度，不大不小 */
  /* 高度不写死，由内容撑开 */
  background: transparent;
}

/* 头部样式：深色背景，突出品牌 */
.header {
  text-align: center;
  margin-bottom: 25px;
}
.logo-box {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 56px;
  height: 56px;
  background: #409EFF;
  border-radius: 12px;
  margin-bottom: 15px;
  box-shadow: 0 4px 10px rgba(64, 158, 255, 0.4);
}
.system-name {
  font-size: 26px;
  color: #303133;
  font-weight: 600;
  letter-spacing: 1px;
  margin: 0;
}

/* 表单区域：白色卡片 */
.form-area {
  background: white;
  padding: 35px 40px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05); /* 极轻的阴影，干净 */
}

.welcome-text {
  font-size: 18px;
  color: #303133;
  margin-bottom: 25px;
  font-weight: 500;
  text-align: center;
}

/* 输入框微调 */
:deep(.el-input__wrapper) {
  background-color: #f5f7fa;
  box-shadow: none;
  border: 1px solid #e4e7ed;
  padding: 10px 15px; /* 增加内衬 */
}
:deep(.el-input__wrapper.is-focus) {
  border-color: #409EFF;
  background-color: white;
}

.submit-btn {
  width: 100%;
  padding: 20px 0; /* 按钮高度适中 */
  font-size: 16px;
  margin-top: 10px;
  letter-spacing: 2px;
}

/* 演示账号样式 */
.demo-account {
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
.demo-title {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 5px;
}
.tags {
  display: flex;
  gap: 10px;
  justify-content: center;
}
.tag {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 4px;
  cursor: pointer;
  background: #f4f4f5;
  color: #909399;
  transition: all 0.2s;
}
.tag:hover { transform: translateY(-1px); }
.tag.admin:hover { background: #ecf5ff; color: #409EFF; }
.tag.operator:hover { background: #f0f9eb; color: #67C23A; }
.tag.client:hover { background: #fdf6ec; color: #E6A23C; }

/* 底部版权 */
.footer {
  text-align: center;
  margin-top: 25px;
  font-size: 12px;
  color: #909399;
}
</style>