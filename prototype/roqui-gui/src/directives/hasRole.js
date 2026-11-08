import keycloak from '@/services/keycloak'

export const vHasRole = {
  mounted(el, binding) {
    const requiredRoles = Array.isArray(binding.value) ? binding.value : [binding.value]
    const userRoles = keycloak.realmAccess?.roles || []
    const hasRole = requiredRoles.some((role) => userRoles.includes(role))

    if (!hasRole) {
      el.style.display = 'none'
    }
  },
}
