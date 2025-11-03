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
        </div>
      </div>
      <footer class="card-footer">
        <div class="buttons p-3 m-3">
          <div class="buttons">
            <button class="button is-success" @click="showModal">Editar</button>
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
        <p><strong>URL: </strong></p>
        <input class="input" v-model="emailConfiguration.url" />
        <p><strong>Puerto: </strong></p>
        <input class="input" v-model="emailConfiguration.port" />
        <p><strong>Cuenta de Correo: </strong></p>
        <input class="input" v-model="emailConfiguration.account" />
        <p><strong>Contraseña: </strong></p>
        <input class="input" type="password" v-model="emailConfiguration.password" />
      </section>
      <footer class="modal-card-foot">
        <div class="buttons">
          <button class="button is-success" @click="updateConfiguration">Guardar</button>
          <button class="button is-warning" @click="closeModal">Cancelar</button>
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
    showNotification: false,
    isActive: false,
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
        )
        .then((res) => {
          if (res.status === 200) {
            this.getConfiguration(this.user.accessToken)
          }
          this.isActive = false
        })
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
