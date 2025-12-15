<template>
  <div style="padding: 20px;">
    <h2>🤖 机器人任务管理</h2>

    <el-row :gutter="20">
      
      <el-col :span="6">
        <el-card shadow="hover" style="margin-bottom: 20px;">
          <template #header>
            <div class="card-header">
              <span>机器人状态概览 ({{ availableRobots.length }} 台)</span>
              <el-button type="primary" @click="loadRobots" size="small">刷新</el-button>
            </div>
          </template>
          
          <el-table :data="availableRobots" :show-header="false" size="small" :row-style="getRowStyle">
            <el-table-column prop="robotCode" label="编码" width="80" />
            <el-table-column prop="robotName" label="名称" min-width="80" />
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template #default="scope">
                <el-tag :type="getRobotStatusTagType(scope.row.status)" size="small">
                  {{ getRobotStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="18">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>任务列表 ({{ total }} 条)</span>
              <el-button type="primary" @click="loadData(1)" size="small">刷新任务</el-button>
            </div>
          </template>

          <el-table :data="tableData" border stripe v-loading="loading">
            <el-table-column prop="taskNo" label="任务编号" width="120" />
            <el-table-column prop="taskType" label="类型" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.taskType === 'INBOUND' ? 'success' : 'warning'" size="small">
                  {{ scope.row.taskType === 'INBOUND' ? '入库' : '出库' }}
                </el-tag>
              </template>
            </el-table-column>
            
            <el-table-column prop="goodsName" label="处理货物" min-width="120" />
            
            <el-table-column prop="robotName" label="分配机器人" width="100">
              <template #default="scope">
                <span v-if="scope.row.robotName">{{ scope.row.robotName }}</span>
                <span v-else class="text-secondary">未分配</span>
              </template>
            </el-table-column>

            <el-table-column prop="status" label="任务状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="createTime" label="创建时间" width="160" />

            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                
                <el-button 
                  v-if="scope.row.status === 'PENDING'" 
                  size="small" 
                  type="primary" 
                  @click="handleAssign(scope.row)"
                >
                  分配机器人
                </el-button>
                
                <el-button 
                  v-if="scope.row.status === 'ASSIGNED'" 
                  size="small" 
                  type="success" 
                  @click="handleComplete(scope.row.id)"
                >
                  完成任务
                </el-button>
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
        </el-card>
      </el-col>
    </el-row>


    <el-dialog v-model="assignDialogVisible" title="分配机器人" width="30%">
      <div style="margin-bottom: 10px;">
        当前任务编号: **{{ currentTaskNo }}**
      </div>
      <el-select v-model="selectedRobotId" placeholder="请选择空闲机器人" style="width: 100%;">
        <el-option
          v-for="robot in availableRobots.filter(r => r.status === 'IDLE')"
          :key="robot.id"
          :label="`${robot.robotCode} (${robot.robotName})`"
          :value="robot.id"
        />
      </el-select>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="assignDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAssignment" :disabled="!selectedRobotId">确认分配</el-button>
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

// --- 机器人列表状态 ---
const availableRobots = ref([]) // 所有机器人列表
const assignDialogVisible = ref(false)
const currentTaskId = ref(null)
const currentTaskNo = ref('')
const selectedRobotId = ref(null)

// --- 状态映射函数 ---
const getStatusTagType = (status) => {
  switch (status) {
    case 'PENDING': return 'info';
    case 'ASSIGNED': return 'primary';
    case 'COMPLETED': return 'success';
    case 'CANCELED': return 'danger';
    default: return '';
  }
}
const getStatusText = (status) => {
  switch (status) {
    case 'PENDING': return '待分配';
    case 'ASSIGNED': return '已分配/执行中';
    case 'COMPLETED': return '已完成';
    case 'CANCELED': return '已取消';
    default: return '未知';
  }
}

// 机器人状态映射
const getRobotStatusTagType = (status) => {
  switch (status) {
    case 'IDLE': return 'success'; // 空闲
    case 'BUSY': return 'warning'; // 忙碌
    case 'FAULT': return 'danger'; // 故障
    default: return 'info';
  }
}
const getRobotStatusText = (status) => {
  switch (status) {
    case 'IDLE': return '空闲';
    case 'BUSY': return '忙碌';
    case 'FAULT': return '故障';
    default: return '未知';
  }
}

const getRowStyle = ({row}) => {
    if (row.status === 'BUSY') {
        return { backgroundColor: '#fdf6ec' }; // 忙碌的机器人显示浅黄色背景
    }
    if (row.status === 'FAULT') {
        return { backgroundColor: '#fef0f0' }; // 故障的机器人显示浅红色背景
    }
    return {};
}

// 1. 加载任务列表数据
const loadData = (pageNum = 1) => {
  currentPage.value = pageNum
  loading.value = true
  
  const params = {
    pageNum,
    pageSize: pageSize.value,
  }

  request.get('/task/list', { params }).then(res => {
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.msg || '加载任务失败')
    }
  }).finally(() => {
    loading.value = false
  })
}

// 2. 加载所有机器人列表 (在页面初始化时加载，并用于侧栏显示)
const loadRobots = () => {
  request.get('/task/robots').then(res => {
    if (res.code === 200) {
      availableRobots.value = res.data
    } else {
      ElMessage.error(res.msg || '加载机器人列表失败')
    }
  })
}

// 3. 打开分配对话框
const handleAssign = (row) => {
  currentTaskId.value = row.id;
  currentTaskNo.value = row.taskNo;
  selectedRobotId.value = null; // 清空上次选择
  // 此时 robots 列表已加载在侧栏，只需要在弹窗中过滤出 IDLE 的即可
  assignDialogVisible.value = true; 
}

// 4. 提交分配
const submitAssignment = () => {
  // ... (分配逻辑保持不变)
  if (!selectedRobotId.value || !currentTaskId.value) {
    ElMessage.warning('请选择一个机器人');
    return;
  }
  
  const payload = {
    taskId: currentTaskId.value,
    robotId: selectedRobotId.value
  };

  // 假设后端分配接口为 /task/assign
  request.post('/task/assign', payload).then(res => {
    if (res.code === 200 && res.data) {
      ElMessage.success('任务分配成功，机器人已开始执行！');
      assignDialogVisible.value = false;
      loadData(currentPage.value); // 刷新任务列表
      loadRobots(); // 刷新机器人状态
    } else {
      ElMessage.error(res.msg || '分配失败，请检查机器人状态');
    }
  }).catch(() => {
    ElMessage.error('请求失败，请检查后端服务是否启动');
  });
}

// 5. 完成任务
const handleComplete = (taskId) => {
  // ... (完成逻辑保持不变)
  ElMessageBox.confirm(
    '确认该任务已完成，并释放机器人吗？',
    '提示',
    {
      confirmButtonText: '确定完成',
      cancelButtonText: '取消',
      type: 'success',
    }
  ).then(() => {
    // 调用后端完成接口 /task/complete
    request.post(`/task/complete?taskId=${taskId}`).then(res => {
      if (res.code === 200 && res.data) {
        ElMessage.success('任务状态已更新为[已完成]，机器人已空闲！');
        loadData(currentPage.value); // 刷新任务列表
        loadRobots(); // 刷新机器人状态
      } else {
        ElMessage.error(res.msg || '任务完成失败');
      }
    });
  }).catch(() => {
    // 用户取消
  });
}


onMounted(() => {
  loadRobots(); // 页面加载时立即加载机器人列表
  loadData();   // 页面加载时加载任务列表
})
</script>

<style scoped>
.text-secondary {
    color: #909399; 
    font-style: italic;
    font-size: 0.9em;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>