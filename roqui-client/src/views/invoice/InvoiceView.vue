<template>
  <AppHeader />
  <AppNotification
    :type="notification.type"
    :message="notification.message"
    v-if="showNotification"
  />

  <section class="section">
    <div class="container">
      <h1 class="title">Facturas Electrónicas</h1>
      <div class="field">
        <label class="label">Fecha Inicial</label>
        <input class="input" type="text" placeholder="2025-02-31" v-model="startDate" />
      </div>
      <div class="field">
        <label class="label">Fecha Final</label>
        <input class="input" type="text" placeholder="2025-02-31" v-model="endDate" />
      </div>
      <div class="container">
        {{ finderMessage }}
      </div>
      <button class="button is-primary" @click="getInvoice">Buscar</button>
    </div>
    <AppDetail v-bind:details="invoice.details" />
  </section>
</template>
<script>
import invoiceService from '@/services/invoice-service'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppNotification from '@/components/shared/AppNotification.vue'
import AppDetail from './InvoiceDetailView.vue'
import { format } from '@formkit/tempo'

export default {
  components: {
    AppHeader,
    AppNotification,
    AppDetail,
  },

  data: () => ({
    startDate: format(new Date(), 'YYYY-MM-DD'),
    endDate: format(new Date(), 'YYYY-MM-DD'),
    invoice: {
      details: [],
    },
    user: {},
    notification: {
      message: '',
      type: 'is-link',
    },
    showNotification: false,
  }),

  mounted() {
    if (localStorage.getItem('user')) {
      this.user = JSON.parse(localStorage.getItem('user'))
    } else {
      this.$router.push('/')
    }
  },

  computed: {
    finderMessage() {
      if (this.invoice.details && this.invoice.details.length > 0) {
        return `Encontrados: ${this.invoice.details.length}`
      }
      return 'Encontrados: 0'
    },
  },

  methods: {
    getInvoice() {
      invoiceService
        .find(this.user.accessToken, this.startDate, this.endDate)
        .then((response) => {
          this.invoice.details = response.data.map((detail) => ({
            ...detail,
            date: format(new Date(detail.date), 'YYYY-MM-DD'),
          }))
        })
        .catch((error) => {
          console.error('Error fetching invoice:', error)
        })
    },
  },
}
</script>
