import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import '@/assets/global.css'
import App from './App.vue'
import router from './router'

Vue.use(ElementUI)
Vue.config.productionTip = false

// 已由 axios 拦截器 Message 提示过的错误，不再作为「未处理的运行时错误」弹出全屏遮罩
window.addEventListener('unhandledrejection', event => {
  const r = event.reason
  if (r && r.__requestError) {
    event.preventDefault()
  }
})

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
