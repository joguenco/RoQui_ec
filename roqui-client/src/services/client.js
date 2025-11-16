import configService from '@/services/config'
import axios from 'axios'

const client = axios.create({
  baseURL: configService.API_URL,
})

// Store reference to auth store (will be set in main.js)
let authStore = null

export function setAuthStore(store) {
  authStore = store
}

// Request interceptor: Add access token to requests
client.interceptors.request.use(
  (config) => {
    if (authStore && authStore.getAccessToken()) {
      config.headers.Authorization = `Bearer ${authStore.getAccessToken()}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// Response interceptor: Handle token refresh on 401
let isRefreshing = false
let failedQueue = []

const processQueue = (error, token = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  failedQueue = []
}

client.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // If error is 401 and we haven't tried to refresh yet
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // If already refreshing, queue this request
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`
            return client(originalRequest)
          })
          .catch((err) => {
            return Promise.reject(err)
          })
      }

      originalRequest._retry = true
      isRefreshing = true

      if (!authStore || !authStore.getRefreshToken()) {
        // No refresh token, logout user
        if (authStore) {
          authStore.logout()
        }
        processQueue(error, null)
        isRefreshing = false
        return Promise.reject(error)
      }

      try {
        const newAccessToken = await authStore.refreshAccessToken()
        processQueue(null, newAccessToken)
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`
        isRefreshing = false
        return client(originalRequest)
      } catch (refreshError) {
        processQueue(refreshError, null)
        isRefreshing = false
        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  },
)

export default client
