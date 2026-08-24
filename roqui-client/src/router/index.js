import { createRouter, createWebHistory } from 'vue-router'
import AboutView from '@/views/AboutView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/taxpayer',
      name: 'taxpayer',
      component: () => import('@/views/TaxpayerView.vue'),
    },
    {
      path: '/parameter',
      name: 'parameter',
      component: () => import('@/views/ParameterView.vue'),
    },
    {
      path: '/subscription',
      name: 'subscription',
      component: () => import('@/views/SubscriptionView.vue'),
    },
    {
      path: '/about',
      name: 'about',
      component: AboutView,
    },
    {
      path: '/exit',
      name: 'exit',
      component: () => import('@/views/ExitView.vue'),
    },
    {
      path: '/parameter/base/directory',
      name: 'directory',
      component: () => import('@/views/parameter/BaseDirectoryView.vue'),
    },
    {
      path: '/parameter/certificate',
      name: 'certificate',
      component: () => import('@/views/parameter/CertificateView.vue'),
    },
    {
      path: '/parameter/logo',
      name: 'logo',
      component: () => import('@/views/parameter/LogoView.vue'),
    },
    {
      path: '/parameter/mail/smtp',
      name: 'mail-smtp',
      component: () => import('@/views/parameter/EmailClientSmtpView.vue'),
    },
    {
      path: '/parameter/mail/http',
      name: 'mail-http',
      component: () => import('@/views/parameter/EmailClientHttpView.vue'),
    },
    {
      path: '/invoice',
      name: 'invoice',
      component: () => import('@/views/invoice/InvoiceView.vue'),
    },
    {
      path: '/credit/note',
      name: 'credit-note',
      component: () => import('@/views/credit/note/CreditNoteView.vue'),
    },

    // Ruta del módulo de Notas de Débito: al entrar a /debit/note carga (lazy)
    // el componente DebitNoteView.vue
    {
      path: '/debit/note',
      name: 'debit-note',
      component: () => import('@/views/debit/note/DebitNoteView.vue'),
    },

    // Ruta del modulo de Liquidaciones de Compra: al entrar a /liquidation carga (lazy)
    // el componente LiquidationView.vue
    {
      path: '/liquidation',
      name: 'liquidation',
      component: () => import('@/views/liquidation/LiquidationView.vue'),
    },

    // Ruta del modulo de Comprobantes de Retencion: al entrar a /withhold carga
    // (lazy) el componente WithholdView.vue
    {
      path: '/withhold',
      name: 'withhold',
      component: () => import('@/views/withhold/WithholdView.vue'),
    },
  ],
})

export default router
