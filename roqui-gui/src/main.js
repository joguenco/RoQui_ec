import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from '@/stores/auth'
import { vHasRole } from './directives/hasRole'

const app = createApp(App)
const pinia = createPinia()

app.use(router)
app.use(pinia)
app.directive('has-role', vHasRole)

const authStore = useAuthStore()

authStore.init().then(() => {
  app.mount('#app')
})
