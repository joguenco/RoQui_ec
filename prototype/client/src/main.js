import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'
import { vHasRole } from './directives/hasRole'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.directive('has-role', vHasRole)

const authStore = useAuthStore()

authStore.init().then(() => {
  app.mount('#app')
})
