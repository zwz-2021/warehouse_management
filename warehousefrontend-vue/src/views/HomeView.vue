<template>
  <div style="padding: 50px; text-align: center;">
    <h1>🤖 前端环境搭建成功！</h1>
    <p>点击下方按钮测试连接后端接口</p>
    
    <div style="margin-top: 20px;">
      <el-button type="primary" size="large" @click="testConnection">
        连接后端 (Get User List)
      </el-button>
    </div>

    <div style="margin-top: 20px;" v-if="result">
      <el-alert title="请求成功" type="success" :description="result" show-icon :closable="false" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import request from '@/utils/request'

const result = ref('')

const testConnection = () => {
  // 发送请求给后端
  request.get('/user/list?pageNum=1&pageSize=1').then(res => {
    // 你的后端返回格式: { code: 200, msg: "success", data: {...} }
    if (res.code === 200) {
       result.value = `连接成功！后端返回数据: 总用户数 ${res.data.total}，第一个用户: ${res.data.records[0].username}`
    } else {
       result.value = `连接成功，但数据状态不对: ${res.msg}`
    }
  }).catch(err => {
    result.value = '连接失败，请检查后端是否启动 (8080端口)'
  })
}
</script>