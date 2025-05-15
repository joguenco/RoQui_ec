<template>
  <AppHeader />
  <AppNotification
    :type="notification.type"
    :message="notification.message"
    v-if="showNotification"
  />
  <article class="message is-info m-6">
    <div class="message-header">
      <p>Logotipo</p>
    </div>
    <div class="message-body">
      <div class="columns is-vcentered">
        <div class="column">
          <div class="container p-3">
            <li>El logotipo debe tener una resolución de 270 x 108 aproximadamente.</li>
            <li>El tamaño del archivo se recomienda que tenga 100 KB aproximadamente.</li>
            <li>El fondo debe ser blanco y puede contener información adicional.</li>
            <li>El logotipo debe ser en formato JPEG (.jpeg).</li>
          </div>
          <div class="container p-3">
            <div class="file">
              <label class="file-label">
                <input
                  class="file-input"
                  type="file"
                  name="resume"
                  accept=".jpeg"
                  @change="onFileSelected"
                />
                <span class="file-cta">
                  <span class="file-label"> Cargar </span>
                </span>
              </label>
            </div>
          </div>
        </div>
        <div class="column">
          <figure>
            <img :src="blobUrl" @load="loaded" />
          </figure>
        </div>
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
    blobUrl: null,
    user: {},
    notification: {
      message: '',
      type: 'is-link',
    },
    showNotification: false,
    selectedFile: null,
  }),

  mounted() {
    if (localStorage.getItem('user')) {
      this.user = JSON.parse(localStorage.getItem('user'))
      this.getLogo(this.user.accessToken)
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
    getLogo() {
      parameterService
        .getLogo(this.user.accessToken)
        .then((response) => {
          const blob = new Blob([response.data], { type: 'image/jpeg' })
          this.blobUrl = URL.createObjectURL(blob)
        })
        .catch(() => {
          this.notification.type = 'is-danger'
          this.notification.message = 'Error al obtener el logotipo'
          this.showNotification = true
        })
    },

    loaded() {
      if (this.blobUrl) URL.revokeObjectURL(this.blobUrl)
    },

    onFileSelected(event) {
      this.selectedFile = event.target.files[0]

      if (this.selectedFile) {
        const formData = new FormData()
        formData.append('file', this.selectedFile, this.selectedFile.name)
        parameterService.uploadLogo(this.user.accessToken, formData).then((res) => {
          if (res.status === 200) {
            this.getLogo()
          }
          this.isActive = false
        })
      }
    },
  },
}
</script>
