<template>
  <AppNotification
    :type="notification.type"
    :message="notification.message"
    v-if="showNotification"
  />

  <section class="hero is-primary">
    <div class="hero-body">
      <h1 class="title has-text-centered is-size-3 has-text-primary-15">Documentos Electrónicos</h1>
      <div class="columns is-centered">
        <div class="column is-half">
          <div class="notification is-light">
            <div class="field">
              <input
                class="input"
                type="username"
                placeholder="Usuario"
                v-model="username"
                ref="username"
              />
            </div>
            <div class="field">
              <input
                class="input"
                type="password"
                placeholder="Contraseña"
                v-model="password"
                @keyup.enter="login"
              />
            </div>
            <div class="field">
              <p class="control">
                <button class="button is-success" @click="login">Acceder</button>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  <AppFooter />
</template>

<script>
import AppFooter from '@/components/layout/AppFooter.vue'
import AppNotification from '@/components/shared/AppNotification.vue'
import loginService from '@/services/login-service'

export default {
  components: {
    AppFooter,
    AppNotification,
  },

  data() {
    return {
      username: '',
      password: '',
      notification: {
        message: '',
        type: 'is-link',
      },
      showNotification: false,
    }
  },

  mounted() {
    this.$refs.username.focus()
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
    async login() {
      if (this.username === '' || this.password === '') {
        this.notification.type = 'is-danger'
        this.notification.message = 'Ingrese usuario y contraseña'
        this.showNotification = true
        return
      }

      try {
        const res = await loginService.login(this.username, this.password)
        if (res.status === 200) {
          localStorage.setItem('user', JSON.stringify(res.data))
          this.$router.push('/home')
        }
      } catch (error) {
        if (error.response && error.response.status === 403) {
          this.notification.type = 'is-danger'
          this.notification.message = 'Usuario o contraseña incorrectos'
        } else {
          this.notification.type = 'is-danger'
          this.notification.message = 'Error de conexión'
        }
        this.showNotification = true
      }
    },
  },
}
</script>
