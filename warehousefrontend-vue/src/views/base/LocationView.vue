<template>
  <div style="padding: 20px;">
    <h2>🏠 货位管理</h2>

    <div style="margin-bottom: 20px; display: flex; align-items: center;">
      <el-input 
        v-model="searchCode" 
        placeholder="按货位编码搜索" 
        style="width: 200px; margin-right: 10px;" 
        clearable 
        @clear="loadData(1)"
        @keyup.enter="loadData(1)"
      />
      
      <el-button type="primary" @click="loadData(1)">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
      
      <el-button type="success" @click="handleEdit" style="margin-left: 20px;">
        新增货位
      </el-button>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="locationCode" label="货位编码" width="150" />
      <el-table-column prop="areaZone" label="所属区域" width="120" />
      
      <el-table-column prop="temperature" label="温度 (°C)" width="100" align="center">
        <template #default="scope">
          <span v-if="scope.row.temperature">{{ scope.row.temperature }}</span>
          <span v-else class="text-secondary">无数据</span>
        </template>
      </el-table-column>

      <el-table-column prop="humidity" label="湿度 (%)" width="100" align="center">
        <template #default="scope">
          <span v-if="scope.row.humidity">{{ scope.row.humidity }}</span>
          <span v-else class="text-secondary">无数据</span>
        </template>
      </el-table-column>

      <el-table-column prop="isAlarm" label="报警状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.isAlarm === 1 ? 'danger' : 'success'" effect="dark">
            {{ scope.row.isAlarm === 1 ? '报警' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="status" label="货位状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
            {{ scope.row.status === 0 ? '空闲' : '占用' }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
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

    <el-dialog v-model="dialogVisible" title="新增货位" width="30%">
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="货位编码" prop="locationCode">
          <el-input v-model="form.locationCode" placeholder="如：A-01-01" />
        </el-form-item>
        <el-form-item label="所属区域" prop="areaZone">
          <el-input v-model="form.areaZone" placeholder="如：A区" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">空闲</el-radio>
            <el-radio :label="1">占用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)

// 搜索变量
const searchCode = ref('')
const searchArea = ref('') // 暂未使用，保留
const dialogVisible = ref(false)
const formRef = ref(null)
const form = ref({})

const rules = {
  locationCode: [{ required: true, message: '请输入货位编码', trigger: 'blur' }],
  areaZone: [{ required: true, message: '请输入所属区域', trigger: 'blur' }],
}

// 1. 加载数据 (现在调用的是带传感器数据的 list 接口)
const loadData = (pageNum = currentPage.value) => {
  currentPage.value = pageNum
  loading.value = true
  
  const params = {
    pageNum,
    pageSize: pageSize.value,
  }
  // 传递货位编码搜索参数给后端
  if (searchCode.value) params.locationCode = searchCode.value

  // 注意：请求的接口是 /location/list，它在后端 Controller 里被修改为执行联表查询
  request.get('/location/list', { params }).then(res => {
    if (res.code === 200) {
      // 接收 LocationSensorVO 数组
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
    searchCode.value = ''
    searchArea.value = ''
    loadData(1)
}

// 3. 打开新增对话框
const handleEdit = () => {
  // 初始化新增表单
  form.value = { locationCode: '', areaZone: '', status: 0 };
  dialogVisible.value = true
  if (formRef.value) formRef.value.resetFields()
}

// 4. 保存 (新增逻辑)
const save = () => {
  formRef.value.validate(valid => {
    if (valid) {
      // POST /location/add 接口
      request.post('/location/add', form.value).then(res => {
        if (res.code === 200) {
          ElMessage.success(res.msg)
          dialogVisible.value = false
          loadData() // 刷新列表
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}

// 5. 删除 (调用Service进行库存检查)
const handleDelete = (id) => {
  ElMessageBox.confirm(
    '确定要删除该货位吗？只有空货位才能被删除。',
    '警告',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    // DELETE /location/delete/{id} 接口
    request.delete(`/location/delete/${id}`).then(res => {
      if (res.code === 200) {
        ElMessage.success(res.msg)
        loadData()
      } else {
        // 接收后端返回的删除失败（库存非空）信息
        ElMessage.error(res.msg || '删除失败，请检查库存')
      }
    }).catch(() => {
        ElMessage.error('请求失败，请检查网络或后端日志')
    })
  }).catch(() => {
    // 用户取消删除
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.text-secondary {
    color: #909399; /* 灰色 */
    font-style: italic;
    font-size: 0.8em;
}
</style>