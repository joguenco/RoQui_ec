import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { setAuthStore } from '@/services/client'
import { useAuthStore } from '@/stores/auth'

const app = createApp(App)
const pinia = createPinia()

app.use(router)
app.use(pinia)

// Initialize auth store and set it in client for interceptors
const authStore = useAuthStore()
authStore.initialize()
setAuthStore(authStore)

app.mount('#app')
