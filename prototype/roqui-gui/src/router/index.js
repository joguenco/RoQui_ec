import { createRouter, createWebHistory } from 'vue-router'
import AboutView from '@/views/AboutView.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/taxpayer',
      name: 'taxpayer',
      component: () => import('@/views/TaxpayerView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/parameter',
      name: 'parameter',
      component: () => import('@/views/ParameterView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/subscription',
      name: 'subscription',
      component: () => import('@/views/SubscriptionView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView,
      meta: { requiresAuth: true },
    },
    {
      path: '/exit',
      name: 'exit',
      component: () => import('@/views/ExitView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/parameter/base/directory',
      name: 'directory',
      component: () => import('@/views/parameter/BaseDirectoryView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/parameter/certificate',
      name: 'certificate',
      component: () => import('@/views/parameter/CertificateView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/parameter/logo',
      name: 'logo',
      component: () => import('@/views/parameter/LogoView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/parameter/mail/smtp',
      name: 'mail-smtp',
      component: () => import('@/views/parameter/EmailClientSmtpView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/parameter/mail/http',
      name: 'mail-http',
      component: () => import('@/views/parameter/EmailClientHttpView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/invoice',
      name: 'invoice',
      component: () => import('@/views/invoice/InvoiceView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/credit/note',
      name: 'credit-note',
      component: () => import('@/views/credit/note/CreditNoteView.vue'),
      meta: { requiresAuth: true },
    },

    // Ruta del módulo de Notas de Débito: al entrar a /debit/note carga (lazy)
    // el componente DebitNoteView.vue
    {
      path: '/debit/note',
      name: 'debit-note',
      component: () => import('@/views/debit/note/DebitNoteView.vue'),
      meta: { requiresAuth: true },
    },

    // Ruta del modulo de Liquidaciones de Compra: al entrar a /liquidation carga (lazy)
    // el componente LiquidationView.vue
    {
      path: '/liquidation',
      name: 'liquidation',
      component: () => import('@/views/liquidation/LiquidationView.vue'),
      meta: { requiresAuth: true },
    },

    // Ruta del modulo de Comprobantes de Retencion: al entrar a /withhold carga
    // (lazy) el componente WithholdView.vue
    {
      path: '/withhold',
      name: 'withhold',
      component: () => import('@/views/withhold/WithholdView.vue'),
      meta: { requiresAuth: true },
    },

    // Ruta del modulo de Guias de Remision: al entrar a /delivery-note carga
    // (lazy) el componente DeliveryNoteView.vue
    {
      path: '/delivery-note',
      name: 'delivery-note',
      component: () => import('@/views/delivery/DeliveryNoteView.vue'),
      meta: { requiresAuth: true },
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
