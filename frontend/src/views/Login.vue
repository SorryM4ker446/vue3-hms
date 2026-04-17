<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const API_URL = 'http://localhost:8080/api'

const form = ref({
  username: '',
  password: ''
})
const loading = ref(false)

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    return ElMessage.warning('请输入用户名和密码')
  }

  loading.value = true
  try {
    const res = await axios.post(`${API_URL}/login`, form.value)
    
    if (res.data.success) {
      ElMessage.success('登录成功')
      // 1. 将用户信息存入 localStorage
      localStorage.setItem('user', JSON.stringify(res.data.user))
      // 2. 跳转到 Dashboard
      router.push('/dashboard')
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('连接服务器失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">🏥 医院病房管理系统</h2>
      <el-form :model="form" label-width="0">
        <el-form-item>
          <el-input 
            v-model="form.username" 
            placeholder="用户名 (admin/doctor)" 
            size="large" 
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item>
          <el-input 
            v-model="form.password" 
            placeholder="密码 (123456)" 
            type="password" 
            size="large" 
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-button 
          type="primary" 
          class="login-btn" 
          size="large" 
          :loading="loading"
          @click="handleLogin"
        >
          登 录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
/* 1. 最外层容器：占满全屏，背景设为浅灰色，用 flex 让内容居中 */
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center;     /* 垂直居中 */
  
  /* 背景：浅灰偏蓝，看起来干净专业 */
  background-color: #eef0f3; 
  /* 可选：加一点渐变效果 */
  background-image: linear-gradient(135deg, #eef0f3 0%, #dce4eb 100%);
  
  overflow: hidden;
}

/* 2. 登录框：这就是你要的那个“框” */
.login-box {
  width: 400px; /* 宽度固定 */
  padding: 40px; /* 内部留白 */
  
  /* 核心：纯白背景，让它从灰色背景中凸显出来 */
  background-color: #ffffff; 
  
  /* 核心：圆角 */
  border-radius: 12px;
  
  /* 核心：阴影效果，制造立体感 (这是现代网页的框框做法) */
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
  
  /* 如果你喜欢明显的线条边框，可以把上面那行 box-shadow 删掉，换成下面这行：*/
  /* border: 2px solid #333; */
  
  text-align: center;
}

.title {
  margin-bottom: 30px;
  color: #333; /* 标题颜色变深，增强对比 */
  font-weight: bold;
  letter-spacing: 1px;
}

.login-btn {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
  margin-top: 10px;
  border-radius: 6px;
}
</style>