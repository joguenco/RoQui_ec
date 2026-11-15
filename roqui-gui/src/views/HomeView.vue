<template>
  <AppHeader />
  <AppNotification
    :type="notification.type"
    :message="notification.message"
    v-if="showNotification"
  />

  <!-- Menú principal: cada panel-block es un enlace a un módulo del sistema -->
  <article class="panel is-link m-6">
    <p class="panel-heading">Menú</p>

    <!-- Módulo de Facturas -->
    <a class="panel-block is-active">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      <router-link to="/invoice" class="navbar-item" active-class="is-active">
        <img src="@/assets/invoice.svg" alt="invoice" />
        <strong class="has-text-grey-dark">Facturas</strong></router-link
      >
    </a>

    <!--
      Módulo de Notas de Crédito.
      Esta estructura (panel-block + router-link) es la plantilla que se
      replicó para agregar el módulo de Notas de Débito de abajo.
    -->
    <a class="panel-block">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      <router-link to="/credit/note" class="navbar-item" active-class="is-active">
        <img src="@/assets/receipt-refund.svg" alt="refund" />
        <strong class="has-text-grey-dark">Notas de Crédito</strong></router-link
      >
    </a>

    <!-- Módulo de Notas de Débito (copiado del bloque de Notas de Crédito) -->
    <a class="panel-block">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      <router-link to="/debit/note" class="navbar-item" active-class="is-active">
        <img src="@/assets/receipt-refund.svg" alt="refund" />
        <strong class="has-text-grey-dark">Notas de Débito</strong></router-link
      >
    </a>

    <!-- Modulo de Liquidaciones de Compra (copiado del bloque de Notas de Debito) -->
    <a class="panel-block">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      <router-link to="/liquidation" class="navbar-item" active-class="is-active">
        <img src="@/assets/invoice.svg" alt="liquidation" />
        <strong class="has-text-grey-dark">Liquidaciones de Compra</strong></router-link
      >
    </a>

    <!-- Modulo de Comprobantes de Retencion -->
    <a class="panel-block">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      <router-link to="/withhold" class="navbar-item" active-class="is-active">
        <img src="@/assets/star.svg" alt="withhold" />
        <strong class="has-text-grey-dark">Comprobantes de Retención</strong></router-link
      >
    </a>

    <a class="panel-block">
      <span class="panel-icon">
        <i class="fas fa-book" aria-hidden="true"></i>
      </span>
      <router-link to="/delivery-note" class="navbar-item" active-class="is-active">
        <img src="@/assets/star.svg" alt="delivery note" />
        <strong class="has-text-grey-dark">Guías de Remisión</strong></router-link
      >
    </a>

    <!-- Indicador de ambiente actual (producción/pruebas) -->
    <div class="panel-block">
      <button class="button is-fullwidth is-static">Ambiente: {{ environment }}</button>
    </div>
  </article>
</template>
<script>
import AppHeader from '@/components/layout/AppHeader.vue'
import AppNotification from '@/components/shared/AppNotification.vue'
import certificateService from '@/services/certificate-service'
import assetService from '@/services/asset-service'

export default {
  components: {
    AppHeader,
    AppNotification,
  },

  data: () => ({
    // For certificate expiration
    daysToExpiry: null,
    environment: '',
    notification: {
      message: '',
      type: 'is-link',
    },
    showNotification: false,
  }),

  methods: {
    getDaysToExpireCertificate() {
      certificateService
        .getDaysToExpire()
        .then((response) => {
          this.daysToExpiry = response.data.daysToExpiry
          if (this.daysToExpiry !== null && this.daysToExpiry <= 15) {
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
        .catch((error) => {
          if (error.response?.status === 404) {
            this.notification.message = 'Certificado digital no encontrado.'
            this.notification.type = 'is-warning'
            this.showNotification = true
          }
          this.daysToExpiry = null
        })
    },
  },

  beforeMount() {
    this.getDaysToExpireCertificate()
  },

  mounted() {
    assetService
      .getEnvironment()
      .then((response) => {
        this.environment = response.data.value
      })
      .catch(() => {
        this.environment = ''
      })
  },
}
</script>
