import { createRouter, createWebHistory } from 'vue-router'
import AboutView from '@/views/AboutView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: () => import('@/views/LoginVue.vue'),
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
      path: '/parameter/mail',
      name: 'mail',
      component: () => import('@/views/parameter/EmailServerView.vue'),
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
  ],
})

export default router
