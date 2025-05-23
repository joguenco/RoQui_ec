<template>
  <AppHeader />
  <AppNotification
    :type="notification.type"
    :message="notification.message"
    v-if="showNotification"
  />
  <article class="message is-info m-6">
    <div class="message-header">
      <p>Certificado para firmar los documentos electrónicos</p>
    </div>
    <div class="message-body">
      <p><strong>Entidad Dueña del Certificado: </strong>{{ certificate.ownerCertificate }}</p>
      <p><strong>Entidad Emisora: </strong>{{ certificate.issuerCertificate }}</p>
      <p><strong>Fecha de Emisión: </strong>{{ certificate.dateIssued }}</p>
      <p><strong>Fecha de Caducidad: </strong>{{ certificate.dateExpiry }}</p>
      <p><strong>El certificado caducará en: </strong>{{ certificate.daysToExpiry }}</p>
      <div class="buttons mt-3">
        <button class="button is-primary" @click="showModal">Cargar Certificado</button>
      </div>
    </div>
  </article>

  <div class="modal" v-bind:class="{ 'is-active': isActive }">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p><strong>Cargar Certificado</strong></p>
      </header>
      <section class="modal-card-body">
        <p><strong>Archivo P12: </strong></p>
        <div class="file">
          <label class="file-label">
            <input
              class="file-input"
              type="file"
              name="resume"
              accept=".p12"
              @change="onFileSelected"
            />
            <span class="file-cta">
              <span class="file-label">Cargar Certificado</span>
            </span>
          </label>
        </div>
        <p><strong>Contraseña: </strong></p>
        <input class="input" type="password" v-model="passwordFile" />
      </section>
      <footer class="modal-card-foot">
        <div class="buttons">
          <button class="button is-success" @click="uploadFile">Guardar</button>
          <button class="button is-warning" @click="closeModal">Cancelar</button>
        </div>
      </footer>
    </div>
  </div>
</template>
<script>
import certificateService from '@/services/certificate-service'
import AppNotification from '@/components/shared/AppNotification.vue'
import AppHeader from '@/components/layout/AppHeader.vue'
import { format } from '@formkit/tempo'

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
    isActive: false,
    selectedFile: null,
    passwordFile: '',
  }),

  mounted() {
    if (localStorage.getItem('user')) {
      this.user = JSON.parse(localStorage.getItem('user'))
      this.getCertificate(this.user.accessToken)
      document.addEventListener('keyup', this.handleEscapeKey)
    } else {
      this.$router.push('/')
    }
  },

  beforeUnmount() {
    document.removeEventListener('keyup', this.handleEscapeKey)
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
          this.certificate.dateIssued = format(new Date(this.certificate.dateIssued), 'YYYY-MM-DD')
          this.certificate.dateExpiry = format(new Date(this.certificate.dateExpiry), 'YYYY-MM-DD')
        })
        .catch((error) => {
          this.notification.message = error.response.data.message
          this.notification.type = 'is-danger'
          this.showNotification = true
        })
    },

    onFileSelected(event) {
      this.selectedFile = event.target.files[0]
    },

    uploadFile() {
      if (this.selectedFile != null) {
        const formData = new FormData()
        formData.append('file', this.selectedFile, this.selectedFile.name)
        formData.append('password', this.passwordFile)
        certificateService.uploadCertificate(this.user.accessToken, formData).then((res) => {
          if (res.status === 200) {
            this.getCertificate(this.user.accessToken)
          }
          this.isActive = false
        })
      }
    },

    showModal() {
      this.isActive = true
    },

    closeModal() {
      this.isActive = false
    },

    handleEscapeKey(event) {
      if (event.key === 'Escape' && this.isActive) {
        this.closeModal()
      }
    },
  },
}
</script>

<style scoped>
.modal-card-head {
  background-color: #66d1ff;
}
</style>
