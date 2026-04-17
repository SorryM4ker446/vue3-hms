<script setup>
import { ref, onMounted, watch } from 'vue' // 1. 引入 watch
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, Timer, InfoFilled } from '@element-plus/icons-vue'
import apiClient from '@/api/client'

const tableData = ref([])
const availableBeds = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const keyword = ref('')
let searchTimer = null // 2. 定义一个定时器变量用于防抖

// 表单数据
const form = ref({
  patientId: null,
  name: '',
  gender: 'M',
  age: 30,
  idCard: '',
  bedId: null
})

onMounted(() => {
  fetchPatientList()
})

// 3. 核心修改：监听 keyword 变化，实现实时搜索（防抖）
watch(keyword, (newVal) => {
  // 如果之前有定时器，清除它（取消上一次未完成的请求）
  if (searchTimer) clearTimeout(searchTimer)

  // 设置一个新的定时器，300毫秒后执行查询
  searchTimer = setTimeout(() => {
    fetchPatientList()
  }, 300)
})

// 获取列表
const fetchPatientList = async () => {
  try {
    const res = await apiClient.get('/patients', {
      params: { keyword: keyword.value }
    })
    tableData.value = res.data
  } catch (err) {
    ElMessage.error('获取病人数据失败')
  }
}

// 获取空闲床位
const fetchBeds = async () => {
  try {
    const res = await apiClient.get('/patients/beds/available')
    availableBeds.value = res.data
  } catch (err) {
    ElMessage.error('无法获取空闲床位')
  }
}

// 打开“办理入院”弹窗
const showAdmitDialog = () => {
  isEdit.value = false
  fetchBeds()
  form.value = { patientId: null, name: '', gender: 'M', age: 30, idCard: '', bedId: null }
  dialogVisible.value = true
}

// 打开“编辑信息”弹窗
const showEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

// 提交表单 (新增或修改)
const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await apiClient.put('/patients', form.value)
      ElMessage.success('修改成功')
    } else {
      if(!form.value.bedId) return ElMessage.warning('请选择床位')
      const res = await apiClient.post('/patients/admit', form.value)
      if (!res.data.success) {
        ElMessage.error(res.data.message)
        return
      }
      ElMessage.success(res.data.message)
    }

    dialogVisible.value = false
    fetchPatientList()
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

// 办理出院
const handleDischarge = async (patientId) => {
  try {
    await ElMessageBox.confirm('确定为该病人办理出院吗？床位将自动释放。', '提示', { type: 'warning' })
    const res = await apiClient.post(`/patients/discharge/${patientId}`)
    if(res.data.success) {
      ElMessage.success(res.data.message)
      fetchPatientList()
    }
  } catch (err) {
    if(err !== 'cancel') ElMessage.error('出院办理失败')
  }
}

// 删除病人
const handleDelete = async (id) => {
  try {
    await apiClient.delete(`/patients/${id}`)
    ElMessage.success('删除成功')
    fetchPatientList()
  } catch (err) {
    ElMessage.error('删除失败')
  }
}
</script>

<template>
  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span style="font-weight: bold; font-size: 16px;">病人管理</span>

        <div class="header-right">
          <!-- 4. 修改 input：移除了 @keyup.enter 和 @clear，完全由 watch 接管 -->
          <el-input
              v-model="keyword"
              placeholder="输入姓名或身份证"
              style="width: 240px; margin-right: 12px;"
              clearable
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>

          <el-button type="primary" @click="showAdmitDialog">
            <el-icon style="margin-right: 5px"><Plus /></el-icon> 办理入院
          </el-button>
        </div>
      </div>
    </template>

    <el-table :data="tableData" border style="width: 100%" stripe>
      <el-table-column prop="patientId" label="ID" width="80" align="center" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="gender" label="性别" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.gender === 'M' ? '' : 'danger'">
            {{ row.gender === 'M' ? '男' : '女' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="age" label="年龄" width="80" align="center" />
      <el-table-column prop="idCard" label="身份证号" width="200" />
      <el-table-column prop="bedId" label="床位ID" width="100" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.bedId" type="success" effect="dark">{{ row.bedId }}</el-tag>
          <el-tag v-else type="info">已出院</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="280" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" :icon="Edit" @click="showEditDialog(row)">编辑</el-button>
          <el-popconfirm v-if="row.status === 1" title="确认办理出院?" confirm-button-text="确认" cancel-button-text="取消" @confirm="handleDischarge(row.patientId)">
            <template #reference><el-button type="warning" size="small" :icon="Timer">出院</el-button></template>
          </el-popconfirm>
          <el-tag v-else type="info" style="margin: 0 10px">已出院</el-tag>
          <el-popconfirm title="确认删除该记录?" confirm-button-text="删除" confirm-button-type="danger" cancel-button-text="取消" @confirm="handleDelete(row.patientId)">
            <template #reference><el-button type="danger" size="small" :icon="Delete">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '修改病人信息' : '入院登记'" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名"><el-input v-model="form.name" placeholder="请输入姓名"/></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio label="M">男</el-radio>
            <el-radio label="F">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="1" :max="120"/></el-form-item>
        <el-form-item label="身份证"><el-input v-model="form.idCard" placeholder="请输入身份证号"/></el-form-item>
        <el-form-item label="床位">
          <el-select v-model="form.bedId" placeholder="选择床位" :disabled="isEdit" style="width: 100%">
            <el-option v-for="b in availableBeds" :key="b.bedId" :label="b.bedNumber" :value="b.bedId"/>
            <el-option v-if="isEdit && form.bedId" :label="form.bedId + ' (当前)'" :value="form.bedId"/>
          </el-select>
          <div v-if="isEdit" style="font-size: 12px; color: #999; margin-top: 5px;">
            <el-icon><InfoFilled /></el-icon> 如需更换床位，请先办理出院，再重新入院。
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit">确定</el-button>
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
.header-right {
  display: flex;
  align-items: center;
}
</style>
