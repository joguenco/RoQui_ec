<template>
  <AppHeader />
  <AppNotification
    :type="notification.type"
    :message="notification.message"
    v-if="showNotification"
  />
  <article class="message m-6">
    <div class="card">
      <header class="card-header">
        <p class="card-header-title">Configuración de Servidor de Correo</p>
      </header>
      <div class="card-content">
        <div class="content">
          <p><strong>Servidor: </strong>{{ mail.server }}</p>
          <p><strong>Puerto: </strong>{{ mail.port }}</p>
          <p><strong>Correo Electrónico: </strong>{{ mail.email }}</p>
          <p><strong>Plantilla: </strong>{{ mail.template }}</p>
        </div>
      </div>
      <footer class="card-footer">
        <div class="buttons p-3 m-3">
          <div class="buttons">
            <button class="button is-success" @click="showModal">Editar</button>

            <label class="file-label">
              <input
                class="file-input"
                type="file"
                name="resume"
                accept=".html"
                @change="onFileSelected"
              />
              <span class="button is-info">
                <span class="file-label">Cargar Plantilla</span>
              </span>
            </label>

            <button class="button is-warning" @click="closeModal">Probar</button>
          </div>
        </div>
      </footer>
    </div>
  </article>

  <div class="modal" v-bind:class="{ 'is-active': isActive }">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p><strong>Servidor de Correo</strong></p>
      </header>
      <section class="modal-card-body">
        <p><strong>Archivo P12: </strong></p>
        <input type="file" accept=".p12" class="input" @change="onFileSelected" />
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
import mailServerService from '@/services/mail-server-service'
import AppNotification from '@/components/shared/AppNotification.vue'
import AppHeader from '@/components/layout/AppHeader.vue'

export default {
  components: {
    AppHeader,
    AppNotification,
  },

  data: () => ({
    mail: {},
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
      mailServerService
        .getInformation(token)
        .then((response) => {
          this.mail = response.data
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
        mailServerService.uploadCertificate(this.user.accessToken, formData).then((res) => {
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
.card-header {
  background-color: #66d1ff;
}
.modal-card-head {
  background-color: #66d1ff;
}
.card-footer {
  background-color: #f9fafb;
}
</style>
