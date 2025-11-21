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
        <p class="card-header-title">Configuración del Cliente de Correo SMTP</p>
      </header>
      <div class="card-content">
        <div class="content">
          <p><strong>URL: </strong>{{ emailConfiguration.url }}</p>
          <p><strong>Puerto: </strong>{{ emailConfiguration.port }}</p>
          <p><strong>Cuenta de Correo: </strong>{{ emailConfiguration.account }}</p>
          <p>
            <strong>Cifrado: </strong
            >{{
              emailConfiguration.encryption === 'None'
                ? 'Ninguno'
                : emailConfiguration.encryption || '-'
            }}
          </p>
        </div>
        <div name="logo" class="mb-3">
          <div class="file is-info has-name">
            <label class="file-label">
              <input
                class="file-input"
                type="file"
                name="resume"
                accept=".png"
                @change="onFileSelected"
              />
              <span class="file-cta">
                <span class="file-icon">
                  <img src="@/assets/upload.svg" />
                </span>
                <span class="file-label"> Logotipo PNG&nbsp;&nbsp; </span>
              </span>
              <span class="file-name"> {{ emailConfiguration.logo }} </span>
            </label>
          </div>
        </div>

        <div name="template" class="mb-3">
          <div class="file is-info has-name">
            <label class="file-label">
              <input
                class="file-input"
                type="file"
                name="resume"
                accept=".html"
                @change="onFileSelected"
              />
              <span class="file-cta">
                <span class="file-icon">
                  <img src="@/assets/upload.svg" />
                </span>
                <span class="file-label"> Plantilla HTML </span>
              </span>
              <span class="file-name"> {{ emailConfiguration.template }} </span>
            </label>
          </div>
        </div>
      </div>
      <footer class="card-footer">
        <div class="buttons p-3 m-3">
          <div class="buttons">
            <button class="button is-success" @click="showModalEdit">Editar</button>
            <button class="button is-warning" @click="showModalSend">Probar</button>
          </div>
        </div>
      </footer>
    </div>
  </article>

  <!-- Edit Modal -->
  <div class="modal" v-bind:class="{ 'is-active': isActiveEdit }">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p><strong>Servidor de Correo</strong></p>
      </header>
      <section class="modal-card-body">
        <p class="pt-3"><strong>URL: </strong></p>
        <input class="input" v-model="emailConfiguration.url" />
        <p class="pt-3"><strong>Puerto: </strong></p>
        <input class="input" v-model="emailConfiguration.port" />
        <p class="pt-3"><strong>Cuenta de Correo: </strong></p>
        <input class="input" v-model="emailConfiguration.account" />
        <p class="pt-3"><strong>Contraseña: </strong></p>
        <input class="input" type="password" v-model="emailConfiguration.password" />
        <p class="pt-3"><strong>Cifrado: </strong></p>
        <div class="field is-narrow">
          <div class="control">
            <label class="radio mr-4">
              <input
                type="radio"
                value="None"
                name="member"
                v-model="emailConfiguration.encryption"
              />
              Ninguno
            </label>
            <label class="radio mr-4">
              <input
                type="radio"
                value="SSL/TLS"
                name="member"
                v-model="emailConfiguration.encryption"
              />
              SSL/TLS
            </label>
          </div>
        </div>
      </section>
      <footer class="modal-card-foot">
        <div class="buttons">
          <button class="button is-success" @click="updateConfiguration">Guardar</button>
          <button class="button is-warning" @click="closeModal">Cancelar</button>
        </div>
      </footer>
    </div>
  </div>

  <!-- Send Modal -->
  <div class="modal" v-bind:class="{ 'is-active': isActiveSend }">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p><strong>Prueba de Envío de Correo</strong></p>
      </header>
      <section class="modal-card-body">
        <p class="pt-3"><strong>Dirección de Correo: </strong></p>
        <input class="input" placeholder="hola@tu.correo" v-model="emailAddress" />
      </section>
      <footer class="modal-card-foot">
        <div class="buttons">
          <button class="button is-success" @click="sendEmail">Enviar</button>
          <button class="button is-warning" @click="closeModal">Cancelar</button>
        </div>
      </footer>
    </div>
  </div>
</template>
<script>
import { emailService, emailClientSmtpService } from '@/services/email-client-service'
import parameterService from '@/services/parameter-service'
import AppNotification from '@/components/shared/AppNotification.vue'
import AppHeader from '@/components/layout/AppHeader.vue'

export default {
  components: {
    AppHeader,
    AppNotification,
  },

  data: () => ({
    emailConfiguration: {},
    user: {},
    notification: {
      message: '',
      type: 'is-link',
    },
    emailAddress: '',
    selectedFile: null,
    showNotification: false,
    isActiveEdit: false,
    isActiveSend: false,
  }),

  mounted() {
    if (localStorage.getItem('user')) {
      this.user = JSON.parse(localStorage.getItem('user'))
      this.getConfiguration(this.user.accessToken)
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
        }, 3600)
      }
    },
  },

  methods: {
    async getConfiguration(token) {
      await emailClientSmtpService
        .getConfiguration(token)
        .then((response) => {
          this.emailConfiguration = response.data
        })
        .catch((error) => {
          this.notification.message = error.response.data.message
          this.notification.type = 'is-danger'
          this.showNotification = true
        })

      await parameterService
        .getResource(token, 'Logo PNG')
        .then((response) => {
          this.emailConfiguration.logo = response.data.value
        })
        .catch(() => {
          this.emailConfiguration.logo = 'No está cargado'
        })

      await parameterService
        .getResource(token, 'Template Email')
        .then((response) => {
          this.emailConfiguration.template = response.data.value
        })
        .catch(() => {
          this.emailConfiguration.template = 'No está cargado'
        })
    },

    updateConfiguration() {
      emailClientSmtpService
        .update(
          this.user.accessToken,
          this.emailConfiguration.url,
          this.emailConfiguration.port,
          this.emailConfiguration.account,
          this.emailConfiguration.password,
          this.emailConfiguration.encryption,
        )
        .then((res) => {
          if (res.status === 200) {
            this.getConfiguration(this.user.accessToken)
          }
          this.isActiveEdit = false
        })
    },

    onFileTemplateSelected(event) {
      this.selectedFileTemplate = event.target.files[0]
    },

    onFileSelected(event) {
      this.selectedFile = event.target.files[0]

      if (this.selectedFile) {
        const fileExtension = this.selectedFile.name.split('.').pop()
        const formData = new FormData()
        formData.append('file', this.selectedFile, this.selectedFile.name)
        if (fileExtension === 'png') {
          parameterService.uploadLogoPng(this.user.accessToken, formData).then((res) => {
            if (res.status === 200) {
              parameterService
                .getResource(this.user.accessToken, 'Logo PNG')
                .then((response) => {
                  this.emailConfiguration.logo = response.data.value
                })
                .catch(() => {
                  this.emailConfiguration.logo = 'No está cargado'
                })
            }
            this.isActive = false
          })
        } else if (fileExtension === 'html') {
          parameterService.uploadEmailTemplateHtml(this.user.accessToken, formData).then((res) => {
            if (res.status === 200) {
              parameterService
                .getResource(this.user.accessToken, 'Template Email')
                .then((response) => {
                  this.emailConfiguration.template = response.data.value
                })
                .catch(() => {
                  this.emailConfiguration.template = 'No está cargado'
                })
            }
            this.isActive = false
          })
        } else {
          this.notification.type = 'is-danger'
          this.notification.message = 'Tipo de archivo no soportado'
          this.showNotification = true
        }
      }
    },

    sendEmail() {
      if (this.emailAddress === '') {
        this.isActiveSend = false
        this.notification.type = 'is-danger'
        this.notification.message = 'Ingrese una dirección de correo válida.'
        this.showNotification = true
        return
      }
      emailService
        .sendTest(this.user.accessToken, this.emailAddress)
        .then((res) => {
          if (res.status === 200) {
            this.isActiveSend = false
            this.notification.type = 'is-success'
            this.notification.message = 'Correo de prueba enviado correctamente.'
            this.showNotification = true
          }
        })
        .catch(() => {
          this.isActiveSend = false
          this.notification.type = 'is-danger'
          this.notification.message = 'Error al enviar el correo de prueba.'
          this.showNotification = true
        })
    },

    showModalEdit() {
      this.isActiveEdit = true
    },

    showModalSend() {
      this.isActiveSend = true
    },

    closeModal() {
      this.isActiveEdit = false
      this.isActiveSend = false
    },

    handleEscapeKey(event) {
      if (event.key === 'Escape' && this.isActiveEdit) {
        this.closeModal()
      } else if (event.key === 'Escape' && this.isActiveSend) {
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
