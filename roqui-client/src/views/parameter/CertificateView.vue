<template>
  <AppNotification
    :type="notification.type"
    :message="notification.message"
    v-if="showNotification"
  />
  <AppHeader />
  <article class="message is-info m-6">
    <div class="message-header">
      <p>Certificado para firmar los comprobantes electrónicos</p>
    </div>
    <div class="message-body">
      <p><strong>Entidad Dueña del Certificado: </strong></p>
      <p><strong>Entidad Emisora: </strong></p>
      <p><strong>Formato de Fechas: </strong></p>
      <p><strong>Fecha de Caducidad: </strong></p>
      <p><strong>El certificado caducará en: </strong></p>
      <div class="buttons mt-3">
        <button class="button is-primary" @click="">Cargar</button>
      </div>
    </div>
  </article>
</template>
<script>
import certificateService from '@/services/certificate-service'
import AppNotification from '@/components/shared/AppNotification.vue'
import AppHeader from '@/components/layout/AppHeader.vue'

export default {
  components: {
    AppHeader,
    AppNotification,
  },

  data: () => ({
    certificate: {},
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
      this.getCertificate(this.user.accessToken)
    } else {
      this.$router.push('/')
    }
  },

  watch: {
    showNotification() {
      if (this.showNotification) {
        setTimeout(() => {
          this.showNotification = false
        }, 3000)
      }
    },
  },

  methods: {
    getCertificate(token) {
      certificateService
        .getCertificate(token)
        .then((response) => {
          this.certificate = response.data
          console.log(this.certificate)
        })
        .catch((error) => {
          this.notification.message = error.response.data.message
          this.notification.type = 'is-danger'
          this.showNotification = true
        })
    },
  },
}
</script>
