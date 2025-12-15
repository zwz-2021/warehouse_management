<template>
  <div style="padding: 20px;">
    <div style="margin-bottom: 20px;">
      <el-input 
        v-model="searchName" 
        placeholder="搜索用户名或姓名" 
        style="width: 200px; margin-right: 10px;" 
        clearable
        @clear="loadData"
        @keyup.enter="loadData" 
      />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button type="success" @click="openAddDialog">新增用户</el-button>
    </div>

    <el-table :data="tableData" border style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="真实姓名" />
      <el-table-column prop="role" label="角色">
        <template #default="scope">
          <el-tag :type="scope.row.role === 'CLIENT' ? 'warning' : 'info'">
            {{ scope.row.role === 'CLIENT' ? '客户' : '管理员' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="电话" />
      <el-table-column prop="createTime" label="创建时间" width="180" />

      <el-table-column label="操作" width="100" fixed="right">
        <template #default="scope">
          <el-popconfirm 
            title="确定要删除该用户吗？" 
            @confirm="handleDelete(scope.row.id)"
            confirm-button-text="是"
            cancel-button-text="否"
          >
            <template #reference>
              <el-button 
                type="danger" 
                size="small" 
                :disabled="scope.row.username === 'admin'"
              >
                删除
              </el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 20px; text-align: right;">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          @current-change="handlePageChange"
        />
    </div>

    <el-dialog v-model="dialogVisible" title="新增用户" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="登录账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="留空默认 123456" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="普通管理员" value="ADMIN" />
            <el-option label="客户 (CLIENT)" value="CLIENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名/客户名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input v-model="form.address" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request' // 确保你之前创建了这个文件
import { ElMessage } from 'element-plus'

// 数据定义
const tableData = ref([])
const loading = ref(false)
const searchName = ref('')
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)

const dialogVisible = ref(false)
const form = ref({ username: '', password: '', role: 'CLIENT', realName: '', phone: '', address: '' })

// 1. 加载数据 (查询)
const loadData = (pageNum = 1) => {
  currentPage.value = pageNum
  loading.value = true
  request.get('/user/list', { 
    params: { 
      pageNum: currentPage.value, 
      pageSize: pageSize.value, 
      name: searchName.value 
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

// 2. 翻页
const handlePageChange = (val) => {
  loadData(val)
}

// 3. 打开新增弹窗
const openAddDialog = () => {
  // 重置表单
  form.value = { username: '', password: '', role: 'CLIENT', realName: '', phone: '', address: '' }
  dialogVisible.value = true
}

// 4. 提交新增
const submitAdd = () => {
  if(!form.value.username) return ElMessage.warning("请输入用户名")
  
  request.post('/user/add', form.value).then(res => {
    if (res.code === 200) {
      ElMessage.success('添加成功')
      dialogVisible.value = false
      loadData() // 刷新列表
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// ★ 5. 删除用户逻辑
const handleDelete = (id) => {
  request.delete(`/user/delete/${id}`).then(res => {
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData(currentPage.value) // 刷新当前页
    } else {
      ElMessage.error(res.msg) // 如果是删admin报错，这里会显示后端返回的信息
    }
  })
}

// 页面加载时自动查询
onMounted(() => {
  loadData()
})
</script>