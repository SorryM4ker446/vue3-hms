<script setup>
import { ref, onMounted, watch } from 'vue' // 引入 watch
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'

const API_URL = 'http://localhost:8080/api'
const tableData = ref([])
const keyword = ref('')
let searchTimer = null // 新增：定时器变量

onMounted(() => {
  fetchDashboardView()
})

// 修改：监听 keyword 变化，添加防抖
watch(keyword, (newVal) => {
  // 清除之前的定时器
  if (searchTimer) clearTimeout(searchTimer)

  // 设置新的定时器
  searchTimer = setTimeout(() => {
    fetchDashboardView() // 300ms 后再发送请求
  }, 300)
})

const fetchDashboardView = async () => {
  try {
    const res = await axios.get(`${API_URL}/dashboard`, {
      params: { keyword: keyword.value } // 关键字带给后端
    })
    tableData.value = res.data
  } catch (err) {
    console.error(err)
    ElMessage.error('获取数据失败')
  }
}
</script>

<template>
  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span style="font-weight: bold; font-size: 16px;">全院病人概览</span>

        <!-- 右侧操作区：搜索框和刷新按钮 -->
        <div class="header-right">
          <el-input
              v-model="keyword"
              placeholder="搜索姓名或身份证"
              style="width: 220px; margin-right: 12px;"
              clearable
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>

          <el-button type="primary" plain @click="fetchDashboardView">
            <el-icon style="margin-right: 5px"><Refresh /></el-icon> 刷新列表
          </el-button>
        </div>
      </div>
    </template>

    <el-table :data="tableData" border style="width: 100%" height="600" stripe>
      <el-table-column prop="patient_id" label="ID" width="80" align="center" />
      <el-table-column prop="patient_name" label="姓名" width="120" />
      <el-table-column prop="id_card" label="身份证号" width="200" />
      <el-table-column prop="dept_name" label="所属科室" width="150" />
      <el-table-column prop="room_number" label="房号" width="100" />
      <el-table-column prop="bed_number" label="床号" width="100" />
    </el-table>
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