import './assets/main.css' // 如果报错找不到这个文件，可以注释掉这行

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// Element Plus 及其样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

// 引入 Element Plus Icons
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 全局注册 Element Plus Icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
})

app.mount('#app')