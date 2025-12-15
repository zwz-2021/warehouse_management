<template>
  <div style="padding: 20px;">
    <div style="margin-bottom: 20px;">
        <h2>📊 销售与出库监控看板</h2>
    </div>

    <el-row :gutter="20">
      
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>🛒 销售订单 (客户端)</span>
              <el-button type="primary" size="small" @click="openDialog">+ 客户下单</el-button>
            </div>
          </template>
          
          <el-table :data="orderList" border stripe height="500">
            <el-table-column prop="orderNo" label="订单号" width="140" show-overflow-tooltip />
            
            <el-table-column label="客户" width="100">
              <template #default="scope">
                {{ getCustomerName(scope.row.customerId) }}
              </template>
            </el-table-column>
            
            <el-table-column prop="goodsName" label="商品" show-overflow-tooltip />
            <el-table-column prop="qty" label="数量" width="60" align="center" />
            
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template #default="scope">
                 <el-tag v-if="scope.row.status === 0">已下单</el-tag>
                 <el-tag type="success" v-else-if="scope.row.status === 4">完成</el-tag>
                 <el-tag type="info" v-else>处理中</el-tag>
              </template>
            </el-table-column>
          </el-table>
          </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>🚚 仓库出库单 (WMS端)</span>
              <el-button icon="Refresh" circle size="small" @click="loadOutboundData" />
            </div>
          </template>

          <el-table :data="outboundList" border stripe height="500">
            <el-table-column prop="orderNo" label="来源订单" width="140" show-overflow-tooltip />
            
            <el-table-column prop="goodsName" label="出库商品" show-overflow-tooltip />
            
            <el-table-column prop="qty" label="数量" width="60" align="center" />
            
            <el-table-column prop="pickStatus" label="拣货状态" width="100" align="center">
              <template #default="scope">
                <el-tag type="warning" v-if="scope.row.pickStatus === 0">🤖 拣选中</el-tag>
                <el-tag type="success" v-else-if="scope.row.pickStatus === 2">✅ 已出库</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="创建订单" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="选择客户">
           <el-select v-model="form.customerId" placeholder="请选择客户" filterable style="width: 100%">
              <el-option 
                v-for="item in clientList" 
                :key="item.id" 
                :label="item.username" 
                :value="item.id" 
              />
           </el-select>
        </el-form-item>
        <el-form-item label="购买货物">
           <el-select v-model="form.goodsId" placeholder="请选择商品" filterable style="width: 100%">
              <el-option 
                v-for="item in goodsList" 
                :key="item.id" 
                :label="item.goodsName" 
                :value="item.id" 
              />
           </el-select>
        </el-form-item>
        <el-form-item label="数量">
           <el-input-number v-model="form.qty" :min="1" style="width: 100%" />
        </el-form-item>
        <el-divider>收货信息</el-divider>
        <el-form-item label="收货人">
           <el-input v-model="form.receiverName" />
        </el-form-item>
        <el-form-item label="电话">
           <el-input v-model="form.receiverPhone" />
        </el-form-item>
        <el-form-item label="地址">
           <el-input v-model="form.receiverAddress" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
         <el-button @click="dialogVisible = false">取消</el-button>
         <el-button type="primary" @click="submitOrder">立即下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

// 数据定义
const orderList = ref([])
const outboundList = ref([])

// 资源定义
const clientList = ref([])
const goodsList = ref([])
const dialogVisible = ref(false)

const form = ref({ 
  customerId: null, goodsId: null, qty: 1, 
  receiverName: '', receiverPhone: '', receiverAddress: '' 
})

// 1. 加载销售订单
const loadOrderData = () => {
  request.get('/order/list').then(res => {
     if(res.code === 200) orderList.value = res.data.records || []
  })
}

// 2. 加载出库单 (新加的)
const loadOutboundData = () => {
  request.get('/outbound/list').then(res => {
     if(res.code === 200) outboundList.value = res.data.records || []
  })
}

// 3. 加载下拉框资源
const loadResources = () => {
  request.get('/user/clients').then(res => {
    if (res.code === 200) clientList.value = res.data || []
  })
  request.get('/goods/all').then(res => {
    if (res.code === 200) goodsList.value = res.data || []
  })
}

const openDialog = () => {
  form.value = { customerId: null, goodsId: null, qty: 1, receiverName: '', receiverPhone: '', receiverAddress: '' }
  if (clientList.value.length === 0) loadResources()
  dialogVisible.value = true
}

const submitOrder = () => {
  if (!form.value.customerId || !form.value.goodsId) {
      ElMessage.warning('请选择客户和商品')
      return
  }
  request.post('/order/create', form.value).then(res => {
    if (res.code === 200) {
        ElMessage.success('下单成功！出库单已自动生成。')
        dialogVisible.value = false
        // 刷新两边的列表，看到联动效果
        loadOrderData()
        loadOutboundData()
    } else {
        ElMessage.error(res.msg || '下单失败')
    }
  })
}

const getCustomerName = (id) => {
    const u = clientList.value.find(item => item.id === id)
    return u ? u.username : id
}

onMounted(() => {
  loadOrderData()
  loadOutboundData()
  loadResources() // 预加载资源，方便列表显示名字
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>