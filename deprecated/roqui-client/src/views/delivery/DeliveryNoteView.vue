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
      <h1 class="title">Guías de Remisión</h1>

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
                    @keyup.enter="findDeliveryNotes"
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
                    @keyup.enter="findDeliveryNotes"
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
            <input type="radio" value="All" v-model="status" @keyup.enter="findDeliveryNotes" />
            Todos
          </label>
          <label class="radio">
            <input
              type="radio"
              value="Unauthorized"
              v-model="status"
              @keyup.enter="findDeliveryNotes"
            />
            No Autorizados
          </label>
          <label class="radio">
            <input
              type="radio"
              value="Authorized"
              v-model="status"
              @keyup.enter="findDeliveryNotes"
            />
            Autorizados
          </label>
        </div>
      </div>

      <div class="field is-grouped m-3">
        <button class="button is-primary" @click="findDeliveryNotes">Buscar</button>
        <button class="button is-link" @click="authorizeAll">Autorizar</button>
        <button class="button is-success" @click="checkAll">Verificar</button>
        <button class="button is-warning" @click="setDefault" title="Añadir fecha actual">
          <img src="@/assets/calendar-star.svg" alt="calentar star" />
        </button>
      </div>
    </div>
    <div class="container p-2">Encontrados: {{ finderMessage }}</div>
    <AppDetail v-bind:details="deliveryNote.details" />
  </section>
</template>
<script>
import deliveryNoteService from '@/services/delivery-note-service'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppLoader from '@/components/shared/AppLoader.vue'
import AppNotification from '@/components/shared/AppNotification.vue'
import AppDetail from './DeliveryNoteDetailView.vue'
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
    deliveryNote: {
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
      if (this.deliveryNote.details && this.deliveryNote.details.length > 0) {
        return `${this.deliveryNote.details.length}`
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
    findDeliveryNotes() {
      if (!this.isValidDates()) return

      this.isLoading = true

      deliveryNoteService
        .find(this.user.accessToken, this.startDate, this.endDate, this.status)
        .then((response) => {
          this.deliveryNote.details = response.data.map((detail) => ({
            ...detail,
            date: format(new Date(detail.date), 'YYYY-MM-DD'),
            isLoading: false,
            isSending: false,
          }))

          this.isLoading = false
        })
        .catch((error) => {
          console.error('Error fetching delivery notes:', error)
        })
    },

    authorizeAll() {
      if (!this.isValidDates()) return

      this.isLoading = true

      deliveryNoteService
        .authorizeAll(this.user.accessToken, this.startDate, this.endDate)
        .then(() => {
          this.findDeliveryNotes()
        })
        .catch((error) => {
          console.error('Error to authorize all delivery notes:', error)
        })
    },

    checkAll() {
      if (!this.isValidDates()) return

      this.isLoading = true

      deliveryNoteService
        .checkAll(this.user.accessToken, this.startDate, this.endDate)
        .then(() => {
          this.findDeliveryNotes()
        })
        .catch((error) => {
          console.error('Error to check all delivery notes:', error)
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
<style scoped></style>
