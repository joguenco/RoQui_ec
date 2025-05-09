<template>
  <AppHeader />
  <section class="section">
    <div class="container">
      <h1 class="title">Facturas Electrónicas</h1>
      <div class="field is-grouped">
        <p class="control is-expanded">
          <label class="label">Fecha Inicial</label>
          <input class="input" type="text" placeholder="2025-02-31" v-model="startDate" />
          <label class="label">Fecha Final</label>
          <input class="input" type="text" placeholder="2025-02-31" v-model="endDate" />

          <button class="button is-primary" @click="getInvoice">Buscar</button>
        </p>
      </div>
    </div>
  </section>
</template>
<script>
import invoiceService from '@/services/invoice-service'
import AppHeader from '@/components/layout/AppHeader.vue'
import { format } from '@formkit/tempo'

export default {
  components: {
    AppHeader,
  },

  data: () => ({
    startDate: format(new Date(), 'YYYY-MM-DD'),
    endDate: format(new Date(), 'YYYY-MM-DD'),
    invoice: {},
    user: {},
  }),

  mounted() {
    if (localStorage.getItem('user')) {
      this.user = JSON.parse(localStorage.getItem('user'))
    } else {
      this.$router.push('/')
    }
  },

  methods: {
    getInvoice() {
      invoiceService
        .find(this.user.accessToken, this.startDate, this.endDate)
        .then((response) => {
          this.invoice = response.data
          console.log('Invoice data:', this.invoice)
        })
        .catch((error) => {
          console.error('Error fetching invoice:', error)
        })
    },
  },
}
</script>
