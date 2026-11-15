import axios from 'axios'
import keycloak from '@/services/keycloak'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
})

http.interceptors.request.use(
  async (config) => {
    if (keycloak.authenticated) {
      try {
        await keycloak.updateToken(30)
        config.headers.Authorization = `Bearer ${keycloak.token}`
      } catch {
        keycloak.logout()
      }
    }
    return config
  },
  (error) => Promise.reject(error),
)

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      keycloak.logout()
    }
    return Promise.reject(error)
  },
)

export default http
