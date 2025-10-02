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
      </div>
      <footer class="card-footer">
        <div class="buttons p-3 m-3">
          <div class="buttons">
            <button class="button is-success" @click="showModalEdit">Editar</button>
            <button class="button is-link" @click="showModalResource">Recursos</button>
            <button class="button is-warning" @click="closeModalEdit">Probar</button>
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
          <button class="button is-warning" @click="closeModalEdit">Cancelar</button>
        </div>
      </footer>
    </div>
  </div>

  <!-- Resorces Modal -->
  <div class="modal" v-bind:class="{ 'is-active': isActiveResorce }">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p><strong>Recursos</strong></p>
      </header>
      <section class="modal-card-body">
        <p class="pt-3"><strong>Plantilla: </strong></p>
        <div class="file">
          <label class="file-label">
            <input
              class="file-input"
              type="file"
              name="template"
              accept=".html"
              @change="onFileTemplateSelected"
            />
            <span class="file-cta">
              <span class="file-label">Cargar Plantilla</span>
            </span>
          </label>
        </div>
        <p class="pt-3"><strong>Logotipo PNG: </strong></p>
        <div class="file">
          <label class="file-label">
            <input
              class="file-input"
              type="file"
              name="logo"
              accept=".png"
              @change="onFileLogoSelected"
            />
            <span class="file-cta">
              <span class="file-label">Cargar Logotipo</span>
            </span>
          </label>
        </div>
      </section>
      <footer class="modal-card-foot">
        <div class="buttons">
          <button class="button is-success" @click="">Guardar</button>
          <button class="button is-warning" @click="closeModalResource">Cancelar</button>
        </div>
      </footer>
    </div>
  </div>
</template>
<script>
import { emailClientSmtpService } from '@/services/email-client-service'
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
    selectedFileTemplate: null,
    selectedFileLogo: null,
    showNotification: false,
    isActiveEdit: false,
    isActiveResorce: false,
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
        }, 3000)
      }
    },
  },

  methods: {
    getConfiguration(token) {
      emailClientSmtpService
        .getConfiguration(token)
        .then((response) => {
          this.emailConfiguration = response.data
        })
        .catch((error) => {
          this.notification.message = error.response.data.message
          this.notification.type = 'is-danger'
          this.showNotification = true
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

    onFileLogoSelected(event) {
      this.selectedFileLogo = event.target.files[0]
    },

    showModalEdit() {
      this.isActiveEdit = true
    },

    showModalResource() {
      this.isActiveResorce = true
    },

    closeModalEdit() {
      this.isActiveEdit = false
    },

    closeModalResource() {
      this.isActiveResorce = false
    },

    handleEscapeKey(event) {
      if (event.key === 'Escape' && this.isActiveEdit) {
        this.closeModalEdit()
      } else if (event.key === 'Escape' && this.isActiveResorce) {
        this.closeModalResource()
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
