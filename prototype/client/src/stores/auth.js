import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import keycloak from '@/services/keycloak'

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref(false)
  const user = ref(null)
  const token = ref(undefined)
  const tokenParsed = ref(undefined)
  const roles = ref([])
  const isLoading = ref(true)

  const userName = computed(() => {
    if (user.value) {
      return `${user.value.firstName} ${user.value.lastName}`
    }
    return tokenParsed.value?.preferred_username || 'Unknown'
  })

  const hasRole = (role) => {
    return roles.value.includes(role)
  }

  const isAdmin = computed(() => hasRole('admin'))

  async function init() {
    try {
      const authenticated = await keycloak.init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        checkLoginIframe: false,
      })

      isAuthenticated.value = authenticated

      if (authenticated) {
        token.value = keycloak.token
        tokenParsed.value = keycloak.tokenParsed
        roles.value = keycloak.realmAccess?.roles || []

        /*
        TODO: Load user profile from Keycloak if available
        try {
          const profile = await keycloak.loadUserProfile()
          user.value = profile
        } catch (err) {
          console.warn('Could not load user profile:', err)
        }
        */

        startTokenRefresh()
      }

      isLoading.value = false
      return authenticated
    } catch (error) {
      console.error('Keycloak init failed:', error)
      isLoading.value = false
      return false
    }
  }

  function login(redirectUri) {
    keycloak.login({
      redirectUri: redirectUri || window.location.origin,
    })
  }

  function logout() {
    keycloak.logout({
      redirectUri: window.location.origin,
    })
  }

  function startTokenRefresh() {
    setInterval(async () => {
      try {
        const refreshed = await keycloak.updateToken(60)
        if (refreshed) {
          token.value = keycloak.token
          tokenParsed.value = keycloak.tokenParsed
        }
      } catch {
        console.warn('Token refresh failed, logging out')
        logout()
      }
    }, 30000)
  }

  return {
    isAuthenticated,
    user,
    token,
    tokenParsed,
    roles,
    isLoading,
    userName,
    isAdmin,
    hasRole,
    init,
    login,
    logout,
  }
})
