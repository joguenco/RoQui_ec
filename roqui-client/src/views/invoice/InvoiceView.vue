<template>
  <AppHeader />
  <AppLoader v-show="isLoading" />
  <AppNotification
    :type="notification.type"
    :message="notification.message"
    v-if="showNotification"
  />

  <section class="section" v-show="!isLoading">
    <div class="container">
      <h1 class="title">Facturas Electrónicas</h1>

      <div class="field is-horizontal m-2">
        <div class="field p-3">
          <div class="field-label"></div>
          <div class="field-body">
            <div class="field is-expanded">
              <div class="field has-addons">
                <p class="control">
                  <a class="button is-static"> Fecha Inicial </a>
                </p>
                <p class="control is-expanded">
                  <input
                    class="input"
                    placeholder="2025-02-31"
                    v-model="startDate"
                    ref="startDate"
                    @keyup.enter="findInvoices"
                  />
                </p>
              </div>
            </div>
          </div>
        </div>

        <div class="field p-3">
          <div class="field-label"></div>
          <div class="field-body">
            <div class="field is-expanded">
              <div class="field has-addons">
                <p class="control">
                  <a class="button is-static"> Fecha Final </a>
                </p>
                <p class="control is-expanded">
                  <input
                    class="input"
                    placeholder="2025-02-31"
                    v-model="endDate"
                    @keyup.enter="findInvoices"
                  />
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="field is-horizontal m-3">
        <div class="radios">
          <label class="radio">
            <input type="radio" value="All" v-model="status" @keyup.enter="findInvoices" />
            Todos
          </label>
          <label class="radio">
            <input type="radio" value="Unauthorized" v-model="status" @keyup.enter="findInvoices" />
            No Autorizados
          </label>
          <label class="radio">
            <input type="radio" value="Authorized" v-model="status" @keyup.enter="findInvoices" />
            Autorizados
          </label>
        </div>
      </div>

      <div class="field is-grouped m-3">
        <button class="button is-primary" @click="findInvoices">Buscar</button>
        <button class="button is-link" @click="authorizeAll">Autorizar</button>
        <button class="button is-success" @click="checkAll">Verificar</button>
        <button class="button is-warning" @click="setDefault" title="Añadir fecha actual">
          <img src="@/assets/calendar-star.svg" alt="calentar star" />
        </button>
      </div>
    </div>
    <div class="container p-2">Encontrados: {{ finderMessage }}</div>
    <AppDetail v-bind:details="invoice.details" />
  </section>
</template>
<script>
import invoiceService from '@/services/invoice-service'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppLoader from '@/components/shared/AppLoader.vue'
import AppNotification from '@/components/shared/AppNotification.vue'
import AppDetail from './InvoiceDetailView.vue'
import { format } from '@formkit/tempo'
import validate from '@/utils/validate'

export default {
  components: {
    AppHeader,
    AppLoader,
    AppNotification,
    AppDetail,
  },

  data: () => ({
    startDate: format(new Date(), 'YYYY-MM-DD'),
    endDate: format(new Date(), 'YYYY-MM-DD'),
    status: 'All',
    invoice: {
      details: [],
    },
    user: {},
    notification: {
      message: '',
      type: 'is-link',
    },
    showNotification: false,
    isLoading: false,
  }),

  mounted() {
    if (localStorage.getItem('user')) {
      this.user = JSON.parse(localStorage.getItem('user'))
      this.$refs.startDate.focus()
    } else {
      this.$router.push('/')
    }
  },

  computed: {
    finderMessage() {
      if (this.invoice.details && this.invoice.details.length > 0) {
        return `${this.invoice.details.length}`
      }
      return '0'
    },
  },

  watch: {
    showNotification() {
      if (this.showNotification) {
        setTimeout(() => {
          this.showNotification = false
        }, 3600)
      }
    },
  },

  methods: {
    findInvoices() {
      if (!this.isValidDates()) return

      this.isLoading = true

      invoiceService
        .find(this.user.accessToken, this.startDate, this.endDate, this.status)
        .then((response) => {
          this.invoice.details = response.data.map((detail) => ({
            ...detail,
            date: format(new Date(detail.date), 'YYYY-MM-DD'),
            isLoading: false,
            isSending: false,
          }))
          this.isLoading = false
        })
        .catch((error) => {
          console.error('Error fetching invoice:', error)
        })
    },

    authorizeAll() {
      if (!this.isValidDates()) return

      this.isLoading = true

      invoiceService
        .authorizeAll(this.user.accessToken, this.startDate, this.endDate)
        .then(() => {
          this.findInvoices()
        })
        .catch((error) => {
          console.error('Error to authorize all invoices:', error)
        })
    },

    checkAll() {
      if (!this.isValidDates()) return

      this.isLoading = true

      invoiceService
        .checkAll(this.user.accessToken, this.startDate, this.endDate)
        .then(() => {
          this.findInvoices()
        })
        .catch((error) => {
          console.error('Error to check all invoices:', error)
        })
    },

    setDefault() {
      this.$refs.startDate.focus()
      this.startDate = format(new Date(), 'YYYY-MM-DD')
      this.endDate = format(new Date(), 'YYYY-MM-DD')
    },

    isValidDates() {
      if (!validate.isDate(this.startDate) || !validate.isDate(this.endDate)) {
        this.notification.message = 'Rango de fechas no válido'
        this.notification.type = 'is-danger'
        this.showNotification = true
        return false
      }
      return true
    },
  },
}
</script>
