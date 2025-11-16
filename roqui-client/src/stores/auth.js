import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import loginService from '@/services/login-service'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  // State
  const accessToken = ref(localStorage.getItem('accessToken') || null)
  const refreshToken = ref(localStorage.getItem('refreshToken') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  // Getters
  const isAuthenticated = computed(() => !!accessToken.value)

  // Actions
  function setTokens(tokens) {
    accessToken.value = tokens.accessToken
    refreshToken.value = tokens.refreshToken

    // Store in localStorage
    if (tokens.accessToken) {
      localStorage.setItem('accessToken', tokens.accessToken)
    }
    if (tokens.refreshToken) {
      localStorage.setItem('refreshToken', tokens.refreshToken)
    }
  }

  function setUser(userData) {
    user.value = userData
    localStorage.setItem('user', JSON.stringify(userData))
  }

  async function login(username, password) {
    try {
      const response = await loginService.login(username, password)
      const data = response.data

      // Store tokens
      if (data.accessToken) {
        setTokens({
          accessToken: data.accessToken,
          refreshToken: data.refreshToken || refreshToken.value,
        })
      }

      // Store user data (excluding tokens for security)
      const userData = { ...data }
      delete userData.accessToken
      delete userData.refreshToken
      setUser(userData)

      return response
    } catch (error) {
      throw error
    }
  }

  async function refreshAccessToken() {
    if (!refreshToken.value) {
      throw new Error('No refresh token available')
    }

    try {
      const response = await loginService.refreshToken(refreshToken.value)
      const data = response.data

      if (data.accessToken) {
        setTokens({
          accessToken: data.accessToken,
          refreshToken: data.refreshToken || refreshToken.value,
        })
      }

      return data.accessToken
    } catch (error) {
      // If refresh fails, logout user
      logout()
      throw error
    }
  }

  function logout() {
    accessToken.value = null
    refreshToken.value = null
    user.value = null

    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')

    router.push('/')
  }

  function getAccessToken() {
    return accessToken.value
  }

  function getRefreshToken() {
    return refreshToken.value
  }

  // Initialize from localStorage on store creation
  function initialize() {
    const storedAccessToken = localStorage.getItem('accessToken')
    const storedRefreshToken = localStorage.getItem('refreshToken')
    const storedUser = localStorage.getItem('user')

    if (storedAccessToken) {
      accessToken.value = storedAccessToken
    }
    if (storedRefreshToken) {
      refreshToken.value = storedRefreshToken
    }
    if (storedUser) {
      try {
        user.value = JSON.parse(storedUser)
      } catch (e) {
        user.value = null
      }
    }
  }

  return {
    // State
    accessToken,
    refreshToken,
    user,
    // Getters
    isAuthenticated,
    // Actions
    login,
    logout,
    refreshAccessToken,
    setTokens,
    setUser,
    getAccessToken,
    getRefreshToken,
    initialize,
  }
})
