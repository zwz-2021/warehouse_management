<template>
  <div style="padding: 20px;">
    <h2>🚚 运输管理 (TMS)</h2>
    <el-button @click="loadData">刷新列表</el-button>

    <el-table :data="tableData" border style="margin-top: 20px;">
      <el-table-column prop="trackingNo" label="运单号" />
      <el-table-column prop="orderNo" label="关联订单号" />
      <el-table-column prop="logisticsCompany" label="物流公司" />
      <el-table-column prop="status" label="状态">
         <template #default="scope">
           <el-tag type="warning" v-if="scope.row.status === 'TRANSIT'">运输中</el-tag>
           <el-tag type="success" v-else>已签收</el-tag>
         </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="scope.row.status === 'TRANSIT'" type="primary" @click="sign(scope.row)">
             确认签收
          </el-button>
          <span v-else style="color: green;">✔ 已完成</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const tableData = ref([])

const loadData = () => {
  // 假设后端有个 list 接口
   request.get('/transport/list').then(res => tableData.value = res.data.records)
}

const sign = (row) => {
  request.post(`/transport/sign?id=${row.id}`).then(res => {
    ElMessage.success('签收成功！订单状态已更新。')
    loadData()
  })
}

onMounted(loadData)
</script>