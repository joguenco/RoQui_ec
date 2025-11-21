<template>
  <AppHeader />
  <AppNotification
    :type="notification.type"
    :message="notification.message"
    v-if="showNotification"
  />

  <article class="panel is-info m-6">
    <p class="panel-heading">Menú</p>

    <a class="panel-block is-active">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      <router-link to="/invoice" class="navbar-item" active-class="is-active">
        <img src="@/assets/invoice.svg" alt="invoice" />
        <strong class="has-text-grey-dark">Facturas</strong></router-link
      >
    </a>
    <a class="panel-block">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      <router-link to="/credit/note" class="navbar-item" active-class="is-active">
        <img src="@/assets/receipt-refund.svg" alt="refund" />
        <strong class="has-text-grey-dark">Notas de Crédito</strong></router-link
      >
    </a>
  </article>
</template>
<script>
import AppHeader from '@/components/layout/AppHeader.vue'
import AppNotification from '@/components/shared/AppNotification.vue'
import certificateService from '@/services/certificate-service'

export default {
  components: {
    AppHeader,
    AppNotification,
  },

  data: () => ({
    // For certificate expiration
    daysToExpiry: null,
    notification: {
      message: '',
      type: 'is-link',
    },
    showNotification: false,
  }),

  methods: {
    getDaysToExpireCertificate(token) {
      certificateService
        .getDaysToExpire(token)
        .then((response) => {
          this.daysToExpiry = response.data.daysToExpiry
          if (this.daysToExpiry !== null && this.daysToExpiry <= 30) {
            if (this.daysToExpiry > 0) {
              this.notification.message = `Su certificado digital expirará en ${this.daysToExpiry} días.`
            } else if (this.daysToExpiry === 0) {
              this.notification.message = `Su certificado digital expira hoy.`
            } else {
              this.notification.message = `Su certificado digital ha expirado hace ${Math.abs(this.daysToExpiry)} días.`
            }
            this.notification.type = 'is-danger'
            this.showNotification = true
          }
        })
        .catch(() => {
          this.daysToExpire = null
        })
    },
  },

  beforeMount() {
    if (localStorage.getItem('user')) {
      const user = JSON.parse(localStorage.getItem('user'))
      this.getDaysToExpireCertificate(user.accessToken)
    } else {
      this.$router.push('/')
    }
  },
}
</script>
