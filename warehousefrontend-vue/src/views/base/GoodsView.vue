<template>
  <div style="padding: 20px;">
    <div style="margin-bottom: 20px;">
      <el-input 
        v-model="searchQuery" 
        placeholder="搜索货物名称 / 编码" 
        style="width: 250px; margin-right: 10px;" 
        clearable
        @clear="loadData"
        @keyup.enter="loadData"
      />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button type="success" @click="openAddDialog">新增货物</el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="70" sortable />
      
      <el-table-column prop="goodsCode" label="货物编码" width="120">
         <template #default="scope">
            <el-tag type="info">{{ scope.row.goodsCode }}</el-tag>
         </template>
      </el-table-column>
      
      <el-table-column prop="goodsName" label="货物名称" min-width="150" />
      
      <el-table-column prop="spec" label="规格型号" width="120" />
      
      <el-table-column prop="price" label="单价 (元)" width="100" />
      
      <el-table-column prop="unit" label="单位" width="70" align="center" />
      
      <el-table-column prop="warnThreshold" label="库存预警值" width="100" align="center" />

      <el-table-column label="操作" width="100" fixed="right" align="center">
        <template #default="scope">
          <el-popconfirm 
            title="确认删除该货物吗？" 
            @confirm="handleDelete(scope.row.id)"
            confirm-button-text="删除"
            cancel-button-text="取消"
          >
            <template #reference>
              <el-button type="danger" size="small" link>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 20px; display: flex; justify-content: flex-end;">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadData"
      />
    </div>

    <el-dialog v-model="dialogVisible" title="新增货物" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="货物编码" required>
          <el-input v-model="form.goodsCode" placeholder="例如：G001" />
        </el-form-item>
        <el-form-item label="货物名称" required>
          <el-input v-model="form.goodsName" placeholder="例如：显卡" />
        </el-form-item>
        <el-form-item label="规格型号">
          <el-input v-model="form.spec" placeholder="例如：RTX 4090" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input v-model="form.price" type="number" placeholder="0.00" />
        </el-form-item>
        <el-form-item label="单位">
          <el-radio-group v-model="form.unit">
            <el-radio-button label="个" />
            <el-radio-button label="箱" />
            <el-radio-button label="件" />
          </el-radio-group>
        </el-form-item>
        <el-form-item label="预警阈值">
          <el-input-number v-model="form.warnThreshold" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const dialogVisible = ref(false)

// 表单数据，严格对应 BaseGoods 实体
const form = reactive({
  goodsCode: '',
  goodsName: '',
  spec: '',
  unit: '个',
  price: '',
  warnThreshold: 10
})

// 加载数据
const loadData = () => {
  loading.value = true
  request.get('/goods/list', {
    params: {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      name: searchQuery.value
    }
  }).then(res => {
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.msg)
    }
  }).finally(() => {
    loading.value = false
  })
}

// 打开新增
const openAddDialog = () => {
  // 重置表单
  form.goodsCode = 'G' + Date.now().toString().slice(-4) // 自动生成个简短编码
  form.goodsName = ''
  form.spec = ''
  form.unit = '个'
  form.price = ''
  form.warnThreshold = 10
  dialogVisible.value = true
}

// 提交新增
const submitAdd = () => {
  if(!form.goodsCode || !form.goodsName) return ElMessage.warning('编码和名称必填')
  
  request.post('/goods/add', form).then(res => {
    if (res.code === 200) {
      ElMessage.success('添加成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 删除
const handleDelete = (id) => {
  request.delete(`/goods/delete/${id}`).then(res => {
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

onMounted(() => {
  loadData()
})
</script>