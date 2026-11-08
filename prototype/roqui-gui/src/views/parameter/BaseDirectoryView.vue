<template>
  <AppHeader />
  <AppNotification
    :type="notification.type"
    :message="notification.message"
    v-if="showNotification"
  />
  <article class="message is-info m-6">
    <div class="message-header">
      <p>Configuración de directorio base para los archivos de la aplicación</p>
    </div>
    <div class="message-body">
      <p><strong>Directorio base: </strong></p>
      <input
        class="input is-primary"
        type="text"
        placeholder="C:\app\RoQui o /app/RoQui"
        v-model="parameter.baseDirectory"
        ref="baseDirectory"
      />
      <div class="buttons mt-3">
        <button class="button is-primary" @click="setBaseDirectory">Guardar</button>
        <button class="button is-warning" @click="clean">&times;</button>
      </div>
    </div>
  </article>
</template>
<script>
import parameterService from '@/services/parameter-service'
import AppNotification from '@/components/shared/AppNotification.vue'
import AppHeader from '@/components/layout/AppHeader.vue'

export default {
  components: {
    AppHeader,
    AppNotification,
  },

  data: () => ({
    parameter: {},
    notification: {
      message: '',
      type: 'is-link',
    },
    showNotification: false,
  }),

  mounted() {
    this.getBaseDirectory()
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
    getBaseDirectory() {
      parameterService.getBaseDirectory().then((response) => {
        this.parameter = response.data
      })
    },
    setBaseDirectory() {
      if (this.parameter.baseDirectory === '') {
        this.notification.message = 'El directorio base no puede estar vacío'
        this.notification.type = 'is-danger'
        this.showNotification = true
        return
      }

      parameterService
        .setBaseDirectory(this.parameter.baseDirectory)
        .then((response) => {
          if (response.status === 200) {
            this.notification.message = 'Directorio base guardado con éxito'
            this.notification.type = 'is-success'
            this.showNotification = true
          }
        })
        .catch((error) => {
          this.notification.type = 'is-danger'
          if (error.response.data.message === 'Directory does not exist') {
            this.notification.message = 'El directorio base no existe'
          } else {
            this.notification.message = error.response.data.message
          }
          this.showNotification = true
        })
    },
    clean() {
      this.parameter.baseDirectory = ''
      this.$refs.baseDirectory.focus()
    },
  },
}
</script>
