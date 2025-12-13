<template>
  <div class="container">
    <table class="table is-bordered is-striped is-hoverable">
      <thead>
        <tr>
          <th class="titulo">
            <a title="Número de Documento" class="button">Factura</a>
          </th>
          <th class="titulo">
            <a title="Fecha de Documento ( aaaa-mm-dd )" class="button">Fecha</a>
          </th>
          <th class="titulo">Total</th>
          <th class="titulo">Documento</th>
          <th class="titulo">Razón Social</th>
          <th class="titulo">Correo</th>
          <th class="titulo">Estado</th>
          <th class="titulo">PDF</th>
          <th class="titulo">XML</th>
          <th class="titulo">
            <a title="Autorizar" class="button">A</a>
          </th>
        </tr>
      </thead>
      <tfoot>
        <tr>
          <th class="titulo">
            <a title="Número de Documento" class="button">Factura</a>
          </th>
          <th class="titulo">
            <a title="Fecha de Documento ( aaaa-mm-dd )" class="button">Fecha</a>
          </th>
          <th class="titulo">Total</th>
          <th class="titulo">Documento</th>
          <th class="titulo">Razón Social</th>
          <th class="titulo">Correo</th>
          <th class="titulo">Estado</th>
          <th class="titulo">PDF</th>
          <th class="titulo">XML</th>
          <th class="titulo">
            <a title="Autorizar" class="button">A</a>
          </th>
        </tr>
      </tfoot>
      <tbody>
        <tr v-for="(d, index) in localDetails" :key="d.id">
          <td class="no_enviado">
            <p>{{ d.code }}</p>
            <p>{{ d.number }}</p>
          </td>
          <td>{{ d.date }}</td>
          <td class="numero">{{ d.total }}</td>
          <td class="numero">{{ d.identification }}</td>
          <td>{{ d.legalName }}</td>
          <td>
            <a class="button is-fullwidth is-loading" v-show="d.isSending">{{ d.email }}</a>
            <a
              class="button is-text is-outlined is-fullwidth"
              @click="sendEmail(d.code, d.number, index)"
              v-show="!d.isSending"
              >{{ d.email }}</a
            >
          </td>
          <td>
            <a
              class="button is-link is-outlined"
              @click="showDocument(d.code, d.number)"
              v-bind:class="'status_' + d.status.toLowerCase().replaceAll(' ', '_')"
              >{{ d.status }}</a
            >
          </td>
          <td>
            <a
              v-bind:href="`${pdf}/${d.accessKey}`"
              target="_blank"
              class="button is-danger is-light"
              v-bind:class="'status_' + d.status.toLowerCase().replaceAll(' ', '_')"
              title="Ver archivo PDF"
            >
              PDF</a
            >
          </td>
          <td>
            <a
              v-bind:href="`${xml}/${d.accessKey}`"
              target="_blank"
              class="button is-success is-light"
              v-bind:class="'status_' + d.status.toLowerCase().replaceAll(' ', '_')"
              title="Ver archivo XML"
            >
              XML</a
            >
          </td>
          <td>
            <a class="button is-info is-loading" v-show="d.isLoading">A</a>
            <a
              class="button is-info"
              @click="authorize(d.code, d.number, index)"
              v-bind:class="'button_' + d.status.toLowerCase()"
              v-show="!d.isLoading"
              title="Autorizar"
              >A</a
            >
          </td>
        </tr>
      </tbody>
    </table>
  </div>

  <!-- Modal Document Status -->
  <div class="modal" v-bind:class="{ 'is-active': isActive }">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p><strong>Estado del Documento Electrónico</strong></p>
      </header>
      <section class="modal-card-body">
        <p><strong>Documento: </strong> {{ document.code }} {{ document.number }}</p>
        <p><strong>Autorización: </strong> {{ document.authorization }}</p>
        <p><strong>Fecha de Autorización: </strong> {{ document.authorizationDate }}</p>
        <p><strong>Estado: </strong> {{ document.status }}</p>
        <p><strong>Observación: </strong> {{ document.observation }}</p>
      </section>
      <footer class="modal-card-foot">
        <div class="buttons">
          <button class="button is-warning" @click="closeModal">Cerrar</button>
        </div>
      </footer>
    </div>
  </div>
</template>
<script>
import invoiceService from '@/services/invoice-service'
import documentService from '@/services/document-service'
import { emailService } from '@/services/email-client-service'
import { format } from '@formkit/tempo'

export default {
  data: () => ({
    user: {},
    isActive: false,
    document: {
      code: '',
      number: '',
      authorization: '',
      authorizationDate: '',
      status: '',
      observation: '',
    },
    // For not mutating the prop directly
    localDetails: [],
  }),

  mounted() {
    if (localStorage.getItem('user')) {
      this.user = JSON.parse(localStorage.getItem('user'))
      document.addEventListener('keyup', this.handleEscapeKey)
    } else {
      this.$router.push('/')
    }
  },

  beforeUnmount() {
    document.removeEventListener('keyup', this.handleEscapeKey)
  },

  props: {
    details: { type: Array, required: true },
  },

  watch: {
    // Sync localDetails with prop changes
    details: {
      immediate: true,
      handler(newVal) {
        // Deep clone to avoid mutating prop
        this.localDetails = JSON.parse(JSON.stringify(newVal))
      },
    },
  },

  computed: {
    pdf() {
      return documentService.pdf()
    },
    xml() {
      return documentService.xml()
    },
  },

  methods: {
    authorize(code, number, index) {
      this.localDetails[index].isLoading = true
      invoiceService
        .authorize(this.user.accessToken, code, number)
        .then((response) => {
          this.localDetails[index].status = response.data.status
          this.localDetails[index].isLoading = false
        })
        .catch((error) => {
          if (error.response) {
            alert('Error: ' + error.response.data)
          } else {
            console.error('Error message:', error.message)
          }
          this.localDetails[index].isLoading = false
        })
    },

    sendEmail(code, number, index) {
      this.localDetails[index].isSending = true
      emailService
        .send(this.user.accessToken, code, number)
        .then(() => {
          this.localDetails[index].isSending = false
        })
        .catch((error) => {
          if (error.response) {
            console.error('Error: ' + error.response.data)
          } else {
            console.error('Error message:', error.message)
          }
          this.localDetails[index].isSending = false
        })
    },

    showDocument(code, number) {
      documentService
        .find(this.user.accessToken, code, number)
        .then((response) => {
          this.isActive = true
          this.document.code = response.data.code
          this.document.number = response.data.number
          this.document.authorization = response.data.authorization
          if (response.data.authorizationDate === null) {
            this.document.authorizationDate = ''
          } else {
            this.document.authorizationDate = format(
              new Date(response.data.authorizationDate),
              'YYYY-MM-DD HH:mm',
            )
          }
          this.document.status = response.data.status
          this.document.observation = response.data.observation
        })
        .catch((error) => {
          console.error('Error fetching document:', error)
        })
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
a.button_autorizado {
  pointer-events: none;
  cursor: default;
  opacity: 0.4;
}

a.status_no_enviado {
  pointer-events: none;
  cursor: default;
  opacity: 0.4;
}
.modal-card-head {
  background-color: #66d1ff;
}
</style>
