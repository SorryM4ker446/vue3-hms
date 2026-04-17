<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const API_URL = 'http://localhost:8080/api'

const tableData = ref([])
const dialogVisible = ref(false)
const form = ref({
  deptId: null,
  deptName: '',
  deptPhone: ''
})
const isEdit = ref(false)

onMounted(() => {
  fetchDepartments()
})

const fetchDepartments = async () => {
  try {
    const res = await axios.get(`${API_URL}/departments`)
    tableData.value = res.data
  } catch (err) {
    ElMessage.error('获取科室数据失败')
  }
}

const showAddDialog = () => {
  isEdit.value = false
  form.value = { deptId: null, deptName: '', deptPhone: '' }
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row } // 复制当前行数据
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    if (isEdit.value) {
      await axios.put(`${API_URL}/departments`, form.value)
      ElMessage.success('科室信息更新成功')
    } else {
      await axios.post(`${API_URL}/departments`, form.value)
      ElMessage.success('科室添加成功')
    }
    dialogVisible.value = false
    fetchDepartments() // 刷新列表
  } catch (err) {
    ElMessage.error('操作失败，请检查科室名称是否重复或存在关联数据')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('此操作将永久删除该科室, 是否继续?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await axios.delete(`${API_URL}/departments/${id}`)
    ElMessage.success('删除成功')
    fetchDepartments()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败，可能存在关联数据')
    }
  }
}
</script>

<template>
  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span>科室管理</span>
        <el-button type="primary" @click="showAddDialog">
          + 添加科室
        </el-button>
      </div>
    </template>

    <el-table :data="tableData" border style="width: 100%" stripe>
      <el-table-column prop="deptId" label="ID" width="80" align="center" />
      <el-table-column prop="deptName" label="科室名称" />
      <el-table-column prop="deptPhone" label="联系电话" />
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button size="small" @click="showEditDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.deptId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑科室' : '添加科室'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="科室名称">
          <el-input v-model="form.deptName" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.deptPhone" />
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