<template>
  <div style="padding: 20px;">
    <h2>📊 实时库存查询</h2>
    
    <div style="margin-bottom: 20px; display: flex; align-items: center;">
      <el-input-number 
        v-model="searchGoodsId" 
        :min="1" 
        placeholder="按货物ID搜索" 
        style="width: 150px; margin-right: 10px;"
        clearable
      />
      <el-input-number 
        v-model="searchLocationId" 
        :min="1" 
        placeholder="按货位ID搜索" 
        style="width: 150px; margin-right: 20px;"
        clearable
      />
      <el-button type="primary" @click="loadData(1)">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="记录ID" width="80" />
      
      <el-table-column prop="goodsName" label="货物名称" min-width="150">
         <template #default="scope">
            {{ scope.row.goodsName }} 
            <el-tag type="info" size="small" effect="plain">(ID: {{ scope.row.goodsId }})</el-tag>
         </template>
      </el-table-column>
      
      <el-table-column prop="locationCode" label="所在货位" min-width="150">
         <template #default="scope">
            {{ scope.row.locationCode }} 
            <el-tag type="warning" size="small" effect="plain">(ID: {{ scope.row.locationId }})</el-tag>
         </template>
      </el-table-column>
      
      <el-table-column prop="totalQty" label="物理库存" />
      <el-table-column prop="lockedQty" label="锁定库存" />
      <el-table-column prop="updateTime" label="更新时间" width="180" />
    </el-table>

    <div style="margin-top: 20px; text-align: right;">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :page-size="pageSize"
          @current-change="loadData"
        />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)

// 搜索变量
// 注意：即使搜索框是输入数字，我们传递给后端的参数名和类型必须保持不变 (Long goodsId, Long locationId)
const searchGoodsId = ref(null)
const searchLocationId = ref(null)

// 1. 加载数据
const loadData = (pageNum = 1) => {
  currentPage.value = pageNum
  loading.value = true
  
  // 构造查询参数 (与后端 Controller 的 @RequestParam 保持一致)
  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
  }
  
  // 只有当值是有效数字时才传递
  if (searchGoodsId.value > 0) params.goodsId = searchGoodsId.value
  if (searchLocationId.value > 0) params.locationId = searchLocationId.value
  
  request.get('/inventory/list', { params }).then(res => {
    if (res.code === 200) {
      // 后端返回的 records 现在是 InventoryDetailVO 数组
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.msg)
    }
  }).finally(() => {
    loading.value = false
  })
}

// 2. 重置搜索
const resetSearch = () => {
    searchGoodsId.value = null
    searchLocationId.value = null
    loadData(1)
}

onMounted(() => {
  loadData()
})
</script>