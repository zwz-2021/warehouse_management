<template>
  <div style="padding: 20px;">
    <h2>📥 入库单管理</h2>
    <el-button type="primary" size="large" @click="openDialog" style="margin-bottom: 20px;">
      + 新建入库单
    </el-button>

    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="单号ID" width="80" align="center" />
      <el-table-column prop="poNo" label="来源单号" width="180" />
      
      <el-table-column prop="goodsName" label="货物名称" min-width="180">
         <template #default="scope">
            <span style="font-weight: bold;">{{ scope.row.goodsName }}</span>
            <el-tag type="info" size="small" effect="plain" style="margin-left: 5px;">
                ID: {{ scope.row.goodsId }}
            </el-tag>
         </template>
      </el-table-column>

      <el-table-column prop="locationCode" label="目标货位" min-width="150">
         <template #default="scope">
            <div v-if="scope.row.locationCode">
                {{ scope.row.locationCode }} 
                <el-tag type="warning" size="small" effect="plain" style="margin-left: 5px;">
                    ID: {{ scope.row.targetLocationId }}
                </el-tag>
            </div>
            <div v-else>
                <span class="text-secondary">未分配</span>
            </div>
         </template>
      </el-table-column>

      <el-table-column prop="actualQty" label="数量" width="100" align="center" />
      
      <el-table-column prop="qcStatus" label="质检状态" width="120" align="center">
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.qcStatus === 1" effect="dark">合格</el-tag>
          <el-tag type="danger" v-else effect="dark">不合格</el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="putawayStatus" label="上架状态" width="120" align="center">
        <template #default="scope">
          <el-tag type="info" v-if="scope.row.putawayStatus === 0">不上架</el-tag>
          <el-tag type="warning" v-if="scope.row.putawayStatus === 1">待上架</el-tag>
          <el-tag type="success" v-if="scope.row.putawayStatus === 2">已完成</el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="createTime" label="创建时间" width="180" />
    </el-table>

    <el-dialog v-model="dialogVisible" title="快速入库录入" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="选择货物">
          <el-select v-model="form.goodsId" placeholder="请选择货物" filterable style="width: 100%;">
            <el-option
              v-for="item in goodsList"
              :key="item.id"
              :label="item.goodsName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="入库数量">
          <el-input-number v-model="form.actualQty" :min="1" style="width: 100%;" />
        </el-form-item>
        
        <el-form-item label="分配方式">
          <el-radio-group v-model="form.assignMethod">
            <el-radio :label="1">🖐 手动分配</el-radio>
            <el-radio :label="2">🤖 自动分配 </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="质检结果">
           <el-radio-group v-model="form.qcStatus">
              <el-radio :label="1">✅ 合格 (自动分配)</el-radio>
              <el-radio :label="2">❌ 不合格</el-radio>
           </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitInbound">提交入库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const dialogVisible = ref(false)
const goodsList = ref([]) 
// 表单数据初始化
const form = ref({ 
    goodsId: null, 
    actualQty: 10, 
    qcStatus: 1, 
    assignMethod: 1 // 默认为手动
})

// 1. 加载入库单列表
const loadData = () => {
  request.get('/inbound/list', { params: { pageNum: 1, pageSize: 10 } }).then(res => {
    if (res.code === 200) {
        // 后端现在返回的是 InboundNoteVO，包含 goodsName 和 locationCode
        tableData.value = res.data.records || []
    } else {
        ElMessage.error(res.msg || '加载入库单列表失败')
    }
  })
}

// 2. 加载货物列表
const loadGoodsList = () => {
    request.get('/goods/all').then(res => {
        if (res.code === 200) {
            goodsList.value = res.data || []
        }
    })
}

const openDialog = () => {
    // 重置表单
    form.value = { goodsId: null, actualQty: 10, qcStatus: 1, assignMethod: 1 }
    // 确保货物列表已加载
    if (goodsList.value.length === 0) {
        loadGoodsList()
    }
    dialogVisible.value = true
}

const submitInbound = () => {
  if (!form.value.goodsId) {
      ElMessage.warning("请选择货物")
      return
  }

  request.post('/inbound/add-direct', form.value).then(res => {
    if (res.code === 200) {
        if (form.value.qcStatus === 1) {
            ElMessage.success('入库单已生成！系统已自动分配货位。')
        } else {
            ElMessage.warning('入库单已生成（不合格），未分配货位。')
        }
        dialogVisible.value = false
        loadData() 
    } else {
        ElMessage.error(res.msg || '提交入库单失败')
    }
  })
}

onMounted(() => {
    loadData()
    loadGoodsList()
})
</script>

<style scoped>
.text-secondary {
    color: #909399;
    font-size: 0.85em;
    font-style: italic;
}
</style>