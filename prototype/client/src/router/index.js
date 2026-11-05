import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true },
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/unauthorized',
      name: 'unauthorized',
      component: () => import('../views/UnauthorizedView.vue'),
    },
  ],
})

router.beforeEach(async (to, _from) => {
  const authStore = useAuthStore()

  // Wait for Keycloak initialization
  if (authStore.isLoading) {
    await new Promise((resolve) => {
      const unwatch = authStore.$subscribe(() => {
        if (!authStore.isLoading) {
          unwatch()
          resolve()
        }
      })
    })
  }

  const requiresAuth = to.meta.requiresAuth
  const requiredRoles = to.meta.roles

  if (requiresAuth && !authStore.isAuthenticated) {
    authStore.login(window.location.origin + to.fullPath)
    return false
  }

  if (requiredRoles && requiredRoles.length > 0) {
    const hasRequiredRole = requiredRoles.some((role) => authStore.hasRole(role))
    if (!hasRequiredRole) {
      return { name: 'unauthorized' }
    }
  }

  return true
})

export default router
