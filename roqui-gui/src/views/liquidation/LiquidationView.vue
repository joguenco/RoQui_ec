<template>
  <AppHeader />
  <AppLoader v-show="isLoading" />
  <AppNotification
    :type="notification.type"
    :message="notification.message"
    v-if="showNotification"
  />

  <section class="section" v-show="!isLoading">
    <!-- contenedores fluidos: usan todo el ancho con el mismo margen lateral
         (px-3), en vez de quedar centrados con el ancho fijo de Bulma -->
    <div class="container is-fluid px-3">
      <h1 class="title">Liquidaciones de Compra</h1>

      <!-- las dos fechas juntas en una fila; en movil se acomodan una debajo
           de otra por el is-grouped-multiline -->
      <div class="field is-grouped is-grouped-multiline my-3">
        <div class="control date-field">
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
                @keyup.enter="findLiquidations"
              />
            </p>
          </div>
        </div>

        <div class="control date-field">
          <div class="field has-addons">
            <p class="control">
              <a class="button is-static"> Fecha Final </a>
            </p>
            <p class="control is-expanded">
              <input
                class="input"
                placeholder="2025-02-31"
                v-model="endDate"
                @keyup.enter="findLiquidations"
              />
            </p>
          </div>
        </div>
      </div>

      <div class="field is-horizontal my-3">
        <div class="radios">
          <label class="radio">
            <input type="radio" value="All" v-model="status" @keyup.enter="findLiquidations" />
            Todos
          </label>
          <label class="radio">
            <input
              type="radio"
              value="Unauthorized"
              v-model="status"
              @keyup.enter="findLiquidations"
            />
            No Autorizados
          </label>
          <label class="radio">
            <input
              type="radio"
              value="Authorized"
              v-model="status"
              @keyup.enter="findLiquidations"
            />
            Autorizados
          </label>
        </div>
      </div>

      <div class="field is-grouped is-grouped-multiline my-3">
        <button class="button is-primary" @click="findLiquidations">Buscar</button>
        <button class="button is-link" @click="authorizeAll">Autorizar</button>
        <button class="button is-success" @click="checkAll">Verificar</button>
        <button class="button is-warning" @click="setDefault" title="Añadir fecha actual">
          <img src="@/assets/calendar-star.svg" alt="calentar star" />
        </button>
      </div>
    </div>
    <div class="container is-fluid px-3 py-2">Encontrados: {{ finderMessage }}</div>
    <AppDetail v-bind:details="liquidation.details" />
  </section>
</template>
<script>
import liquidationService from '@/services/liquidation-service'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppLoader from '@/components/shared/AppLoader.vue'
import AppNotification from '@/components/shared/AppNotification.vue'
import AppDetail from './LiquidationDetailView.vue'
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
    liquidation: {
      details: [],
    },

    notification: {
      message: '',
      type: 'is-link',
    },
    showNotification: false,
    isLoading: false,
  }),

  mounted() {
    this.$refs.startDate.focus()
  },

  computed: {
    finderMessage() {
      if (this.liquidation.details && this.liquidation.details.length > 0) {
        return `${this.liquidation.details.length}`
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
    findLiquidations() {
      if (!this.isValidDates()) return

      this.isLoading = true

      liquidationService
        .find(this.startDate, this.endDate, this.status)
        .then((response) => {
          this.liquidation.details = response.data.map((detail) => ({
            ...detail,
            date: format(new Date(detail.date), 'YYYY-MM-DD'),
            isLoading: false,
            isSending: false,
          }))

          this.isLoading = false
        })
        .catch((error) => {
          console.error('Error fetching liquidation:', error)
        })
    },

    authorizeAll() {
      if (!this.isValidDates()) return

      this.isLoading = true

      liquidationService
        .authorizeAll(this.startDate, this.endDate)
        .then(() => {
          this.findLiquidations()
        })
        .catch((error) => {
          if (error.response.status === 400) {
            this.findLiquidations()
            if (error.response.data.message === 'The password certificate could not be decrypted') {
              this.notification.message = 'Certificado de contraseña no puede ser descifrado'
            } else {
              this.notification.message = error.response.data.message
            }
            this.notification.type = 'is-danger'
            this.showNotification = true
          } else {
            console.error('Error to authorize all liquidations:', error)
          }
        })
    },

    checkAll() {
      if (!this.isValidDates()) return

      this.isLoading = true

      liquidationService
        .checkAll(this.startDate, this.endDate)
        .then(() => {
          this.findLiquidations()
        })
        .catch((error) => {
          console.error('Error to check all liquidations:', error)
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
<style scoped>
/* los dos campos de fecha: ancho comodo y fijo, que en pantallas chicas se
   reduce al 100% y se apilan sin desbordar */
.date-field {
  width: 20rem;
  max-width: 100%;
}
</style>
