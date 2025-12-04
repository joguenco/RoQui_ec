import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import loginService from '@/services/login-service'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  // State
  const accessToken = ref(localStorage.getItem('accessToken') || null)
  const refreshToken = ref(localStorage.getItem('refreshToken') || null)
  const role = ref(localStorage.getItem('role') || null)
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
    // Decode and store role from access token (if present)
    if (tokens.accessToken) {
      const payload = decodeJwt(tokens.accessToken)
      // Try common claim names for role
      const resolvedRole = payload && (payload.role || payload.roles || payload.sub || null)
      // Normalize when roles is an array
      let finalRole = null
      if (Array.isArray(resolvedRole)) {
        finalRole = resolvedRole.length > 0 ? resolvedRole[0] : null
      } else if (typeof resolvedRole === 'string') {
        finalRole = resolvedRole
      }
      role.value = finalRole
      if (finalRole) {
        localStorage.setItem('role', finalRole)
      } else {
        localStorage.removeItem('role')
      }
    }
  }

  function setUser(userData) {
    user.value = userData
    localStorage.setItem('user', JSON.stringify(userData))
  }

  async function login(username, password) {
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
    } catch {
      // If refresh fails, logout user
      logout()
      // throw error
    }
  }

  // Decode a JWT and return its payload object, or null on failure
  function decodeJwt(token) {
    try {
      const parts = token.split('.')
      if (parts.length < 2) return null
      const payload = parts[1]
      // base64url -> base64
      const base64 =
        payload.replace(/-/g, '+').replace(/_/g, '/') + '='.repeat((4 - (payload.length % 4)) % 4)
      const json = atob(base64)
      return JSON.parse(json)
    } catch {
      return null
    }
  }

  function logout() {
    accessToken.value = null
    refreshToken.value = null
    user.value = null
    role.value = null

    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
    localStorage.removeItem('role')

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
    const storedRole = localStorage.getItem('role')

    if (storedAccessToken) {
      accessToken.value = storedAccessToken
      // ensure role is in sync with token
      const payload = decodeJwt(storedAccessToken)
      const resolvedRole = payload && (payload.role || payload.roles || payload.sub || null)
      if (Array.isArray(resolvedRole)) {
        role.value = resolvedRole.length > 0 ? resolvedRole[0] : null
      } else {
        role.value = resolvedRole
      }
    }
    if (storedRefreshToken) {
      refreshToken.value = storedRefreshToken
    }
    if (storedUser) {
      try {
        user.value = JSON.parse(storedUser)
      } catch {
        user.value = null
      }
    }
    if (storedRole && !role.value) {
      role.value = storedRole
    }
  }

  return {
    // State
    accessToken,
    refreshToken,
    role,
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
