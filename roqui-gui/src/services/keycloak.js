import Keycloak from 'keycloak-js'

const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL,
  realm: import.meta.env.VITE_KEYCLOAK_REALM,
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
})

// Refresh token when tab regains focus
document.addEventListener('visibilitychange', async () => {
  if (document.visibilityState === 'visible' && keycloak.authenticated) {
    try {
      await keycloak.updateToken(60)
    } catch {
      console.warn('Token refresh on focus failed')
    }
  }
})

// Handle token expiration events
keycloak.onTokenExpired = () => {
  keycloak.updateToken(60).catch(() => {
    console.warn('Token expired and refresh failed')
    keycloak.logout()
  })
}

export default keycloak
