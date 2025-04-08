<template>
  <AppHeader />
  <br />
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
        <button class="button is-primary">Guardar</button>
        <button class="button is-warning" @click="clean">&times;</button>
      </div>
    </div>
  </article>
</template>
<script>
import parameterService from '@/services/parameter-service'
import AppHeader from '@/components/layout/AppHeader.vue'

export default {
  components: {
    AppHeader,
  },

  data: () => ({
    parameter: {},
    user: {},
  }),

  mounted() {
    if (localStorage.getItem('user')) {
      this.user = JSON.parse(localStorage.getItem('user'))
      this.getBaseDirectory(this.user.accessToken)
    } else {
      this.$router.push('/')
    }
  },
  methods: {
    getBaseDirectory(token) {
      parameterService.getBaseDirectory(token).then((response) => {
        this.parameter = response.data
        console.log(this.parameter)
      })
    },
    clean() {
      this.parameter.baseDirectory = ''
      this.$refs.baseDirectory.focus()
    },
  },
}
</script>
