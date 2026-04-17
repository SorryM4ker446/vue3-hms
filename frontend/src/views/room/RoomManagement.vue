<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import apiClient from '@/api/client'

const rooms = ref([])
const departments = ref([]) // 用于选择所属科室
const roomDialogVisible = ref(false)
const roomForm = ref({
  roomId: null,
  roomNumber: '',
  type: '普通', // 默认
  deptId: null
})
const isEditRoom = ref(false)

const bedDialogVisible = ref(false)
const bedForm = ref({
  bedId: null,
  bedNumber: '',
  status: 0, // 默认空闲
  roomId: null
})
const isEditBed = ref(false)
const currentRoomId = ref(null); // 当前操作的病房ID

// 初始化
onMounted(() => {
  fetchRooms()
  fetchDepartments()
})

const fetchRooms = async () => {
  try {
    const res = await apiClient.get('/rooms')
    rooms.value = res.data
    // 为每个病房加载其床位信息
    for (let room of rooms.value) {
      await fetchBedsByRoomId(room);
    }
  } catch (err) {
    ElMessage.error('获取病房数据失败')
  }
}

const fetchDepartments = async () => {
  try {
    const res = await apiClient.get('/departments')
    departments.value = res.data
  } catch (err) {
    ElMessage.error('获取科室列表失败')
  }
}

const fetchBedsByRoomId = async (room) => {
  try {
    const res = await apiClient.get(`/rooms/${room.roomId}/beds`);
    room.beds = res.data; // 将床位信息挂载到对应的病房对象上
  } catch (err) {
    ElMessage.error(`获取病房 ${room.roomNumber} 的床位失败`);
    room.beds = [];
  }
};

const getDeptName = (deptId) => {
  const dept = departments.value.find(d => d.deptId === deptId);
  return dept ? dept.deptName : '未知';
}

const getBedStatusText = (status) => {
  switch (status) {
    case 0: return '空闲';
    case 1: return '占用';
    case 2: return '维修';
    default: return '未知';
  }
}

// === 病房操作 ===
const showAddRoomDialog = () => {
  isEditRoom.value = false
  roomForm.value = { roomId: null, roomNumber: '', type: '普通', deptId: null }
  roomDialogVisible.value = true
}

const showEditRoomDialog = (row) => {
  isEditRoom.value = true
  roomForm.value = { ...row }
  roomDialogVisible.value = true
}

const submitRoomForm = async () => {
  try {
    if (isEditRoom.value) {
      await apiClient.put('/rooms', roomForm.value)
      ElMessage.success('病房信息更新成功')
    } else {
      await apiClient.post('/rooms', roomForm.value)
      ElMessage.success('病房添加成功')
    }
    roomDialogVisible.value = false
    fetchRooms() // 刷新列表
  } catch (err) {
    ElMessage.error('操作失败，请检查房号是否重复')
  }
}

const handleDeleteRoom = async (id) => {
  try {
    await ElMessageBox.confirm('此操作将永久删除该病房及其下所有床位, 是否继续?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await apiClient.delete(`/rooms/${id}`)
    ElMessage.success('删除成功')
    fetchRooms()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败，可能存在关联数据')
    }
  }
}

// === 床位操作 ===
const showAddBedDialog = (roomId) => {
  isEditBed.value = false
  currentRoomId.value = roomId; // 记录当前要添加床位的病房ID
  bedForm.value = { bedId: null, bedNumber: '', status: 0, roomId: roomId } // 默认绑定到当前病房
  bedDialogVisible.value = true
}

// 修改床位状态（例如：设为维修）
const updateBedStatus = async (bedId, status) => {
  try {
    await apiClient.put(`/rooms/beds/${bedId}/status`, { status });
    ElMessage.success('床位状态更新成功');
    fetchRooms(); // 刷新所有病房和床位
  } catch (error) {
    ElMessage.error('更新床位状态失败');
  }
};

const handleAddBed = async () => {
  try {
    // 假设添加床位也是通过roomController的PUT/POST方法
    // 实际项目中，通常是 POST /api/beds 接口
    // 这里为了简化，我们暂时用一个辅助方法，或者直接在前端模拟
    // 实际应调用后端 /api/beds 的POST接口
    const newBed = {
      bedNumber: bedForm.value.bedNumber,
      status: bedForm.value.status,
      roomId: currentRoomId.value // 使用当前病房ID
    };
    await apiClient.post('/beds', newBed); // 假设有一个 /api/beds 的新增接口
    ElMessage.success('床位添加成功');
    bedDialogVisible.value = false;
    fetchRooms();
  } catch (error) {
    ElMessage.error('添加床位失败');
  }
};
</script>

<template>
  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span>病房与床位管理</span>
        <el-button type="primary" @click="showAddRoomDialog">
          + 添加病房
        </el-button>
      </div>
    </template>

    <el-table :data="rooms" border style="width: 100%; margin-bottom: 20px;" row-key="roomId">
      <el-table-column type="expand">
        <template #default="props">
          <div style="padding: 0 20px;">
            <el-row :gutter="20">
              <el-col :span="6" v-for="bed in props.row.beds" :key="bed.bedId" style="margin-bottom: 10px;">
                <el-card shadow="hover" :body-style="{ padding: '10px' }">
                  <div class="bed-item">
                    <span class="bed-number">{{ bed.bedNumber }}号床</span>
                    <el-tag :type="bed.status === 0 ? 'success' : (bed.status === 1 ? 'warning' : 'info')">
                      {{ getBedStatusText(bed.status) }}
                    </el-tag>
                    <el-button 
                      v-if="bed.status !== 2" 
                      type="warning" size="small" 
                      style="margin-left: 10px;" 
                      @click="updateBedStatus(bed.bedId, 2)"
                    >
                      设为维修
                    </el-button>
                    <el-button 
                      v-if="bed.status === 2" 
                      type="success" size="small" 
                      style="margin-left: 10px;" 
                      @click="updateBedStatus(bed.bedId, 0)"
                    >
                      恢复空闲
                    </el-button>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-button type="success" size="small" plain @click="showAddBedDialog(props.row.roomId)">
                  + 添加床位
                </el-button>
              </el-col>
            </el-row>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="roomNumber" label="病房号" width="120" />
      <el-table-column prop="type" label="病房类型" width="120" />
      <el-table-column prop="deptId" label="所属科室" >
        <template #default="scope">
          {{ getDeptName(scope.row.deptId) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button size="small" @click="showEditRoomDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDeleteRoom(scope.row.roomId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑病房弹窗 -->
    <el-dialog v-model="roomDialogVisible" :title="isEditRoom ? '编辑病房' : '添加病房'" width="400px">
      <el-form :model="roomForm" label-width="100px">
        <el-form-item label="病房号">
          <el-input v-model="roomForm.roomNumber" />
        </el-form-item>
        <el-form-item label="病房类型">
          <el-select v-model="roomForm.type" style="width: 100%;">
            <el-option label="普通" value="普通"></el-option>
            <el-option label="ICU" value="ICU"></el-option>
            <el-option label="单人间" value="单人间"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属科室">
          <el-select v-model="roomForm.deptId" placeholder="选择科室" style="width: 100%;">
            <el-option 
              v-for="dept in departments" 
              :key="dept.deptId" 
              :label="dept.deptName" 
              :value="dept.deptId"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roomDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRoomForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加床位弹窗 (简化处理，假设直接添加) -->
    <el-dialog v-model="bedDialogVisible" title="添加床位" width="300px">
      <el-form :model="bedForm" label-width="80px">
        <el-form-item label="床位号">
          <el-input v-model="bedForm.bedNumber" />
        </el-form-item>
        <!-- 床位状态默认空闲，无需用户选择 -->
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="bedDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddBed">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.bed-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.bed-number {
  font-weight: bold;
  margin-right: 10px;
}
</style>
