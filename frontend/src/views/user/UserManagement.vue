<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const API_URL = 'http://localhost:8080/api'

const tableData = ref([])
const departments = ref([]) // 用于选择科室
const dialogVisible = ref(false)
const form = ref({
  userId: null,
  username: '',
  password: '123456', // 默认密码
  realName: '',
  role: 3, // 默认护士
  deptId: null
})
const isEdit = ref(false)

onMounted(() => {
  fetchUsers()
  fetchDepartments()
})

const fetchUsers = async () => {
  try {
    const res = await axios.get(`${API_URL}/users`)
    tableData.value = res.data
  } catch (err) {
    ElMessage.error('获取用户数据失败')
  }
}

const fetchDepartments = async () => {
  try {
    const res = await axios.get(`${API_URL}/departments`)
    departments.value = res.data
  } catch (err) {
    ElMessage.error('获取科室列表失败')
  }
}

const showAddDialog = () => {
  isEdit.value = false
  form.value = { userId: null, username: '', password: '123456', realName: '', role: 3, deptId: null }
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    if (isEdit.value) {
      await axios.put(`${API_URL}/users`, form.value)
      ElMessage.success('用户信息更新成功')
    } else {
      await axios.post(`${API_URL}/users`, form.value)
      ElMessage.success('用户添加成功')
    }
    dialogVisible.value = false
    fetchUsers()
  } catch (err) {
    ElMessage.error('操作失败，请检查用户名是否重复')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('此操作将永久删除该用户, 是否继续?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await axios.delete(`${API_URL}/users/${id}`)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getRoleText = (role) => {
  switch (role) {
    case 1: return '管理员';
    case 2: return '医生';
    case 3: return '护士';
    default: return '未知';
  }
}

const getDeptName = (deptId) => {
  const dept = departments.value.find(d => d.deptId === deptId);
  return dept ? dept.deptName : '无';
}
</script>

<template>
  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span>员工管理</span>
        <el-button type="primary" @click="showAddDialog">
          + 添加员工
        </el-button>
      </div>
    </template>

    <el-table :data="tableData" border style="width: 100%" stripe>
      <el-table-column prop="userId" label="ID" width="80" align="center" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column prop="role" label="角色" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.role === 1 ? 'danger' : scope.row.role === 2 ? 'warning' : ''">
            {{ getRoleText(scope.row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="deptId" label="所属科室">
        <template #default="scope">
          {{ getDeptName(scope.row.deptId) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button size="small" @click="showEditDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.userId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑员工' : '添加员工'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" placeholder="选择角色" style="width: 100%;">
            <el-option label="管理员" :value="1"></el-option>
            <el-option label="医生" :value="2"></el-option>
            <el-option label="护士" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所属科室">
          <el-select v-model="form.deptId" placeholder="选择科室" style="width: 100%;" clearable>
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
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
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
</style>